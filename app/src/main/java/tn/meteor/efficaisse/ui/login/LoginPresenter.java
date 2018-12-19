package tn.meteor.efficaisse.ui.login;

import android.content.Context;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import tn.meteor.efficaisse.data.db.DataBaseManager;
import tn.meteor.efficaisse.data.db.PullDb;
import tn.meteor.efficaisse.data.network.Credential;
import tn.meteor.efficaisse.data.network.Message;
import tn.meteor.efficaisse.data.network.Response;
import tn.meteor.efficaisse.data.network.UserConverterModel;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.repository.CashierRepository;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.ErrorSync;
import tn.meteor.efficaisse.model.User;
import tn.meteor.efficaisse.service.ServiceCategory;
import tn.meteor.efficaisse.service.ServiceFactory;
import tn.meteor.efficaisse.service.ServiceIngredient;
import tn.meteor.efficaisse.service.ServiceProduct;
import tn.meteor.efficaisse.service.ServiceUser;
import tn.meteor.efficaisse.ui.base.BasePresenter;
import tn.meteor.efficaisse.utils.CacheStore;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.SyncPullListener;
import tn.meteor.efficaisse.utils.SyncPushListener;


public class LoginPresenter extends BasePresenter implements LoginContract.Presenter, SyncPushListener, SyncPullListener {


    private LoginContract.View view;
    private ServiceUser serviceUser;

    private ServiceProduct serviceProduct;
    private ServiceIngredient serviceIngredient;
    private ServiceCategory serviceCategory;

    private String username = "efficaiseAuthorization", pass = "eff20186978";
    private AppPreferencesHelper preferences;
    public Credential credentialUser;
    DataBaseManager dataBaseManager = DataBaseManager.getInstance();
    private CacheStore cacheStore = CacheStore.getInstance();


    public LoginPresenter(LoginContract.View view, Context context) {
        this.view = view;
        preferences = new AppPreferencesHelper(context.getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
    }


    @Override
    public void login(String email, String password) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        serviceUser = ServiceFactory.getApiClient().create(ServiceUser.class);
        String credential = username + ":" + pass;
        final String basic =
                "Basic " + Base64.encodeToString(credential.getBytes(), Base64.NO_WRAP);

        credentialUser = new Credential();
        Observable<JsonObject> credentials = serviceUser.credentials(email, password, "password", basic);
        Observable<JsonObject> getUser = serviceUser.getCurrentUser();


        view.showLoading();
        credentials.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        cn -> {

                            Gson cGson = new Gson();
                            Response response = cGson.fromJson(cn, Response.class);
                            preferences.setAccesToken(response.getAccess_token());
                            preferences.setRefreshToken(response.getRefresh_token());
                            credentialUser.setAccess_token(response.getAccess_token());
                            credentialUser.setRefresh_token(response.getRefresh_token());

                            getUser.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            gn -> {

                                                Gson Ggson = new Gson();
                                                UserConverterModel userc = Ggson.fromJson(gn, UserConverterModel.class);
                                                User user = userc.toDbModel();
                                                user.getCashierStore().save();
                                                user.save();
                                                user.getCashierStore().getLicence().save();
                                                credentialUser.setUsername(user.getUsername());
                                                credentialUser.save();
                                                preferences.setStoreRDC(user.getCashierStore().getRegisterDC());
                                                preferences.setUsername(user.getUsername());

                                                dataBaseManager.synchronizationData(this);

                                            });

                        }, e -> {
                            view.hideLoading();
                            if (e instanceof HttpException) {
                                view.showMessage(((HttpException) e).response().errorBody().string());
                            }
                        });

    }

    @Override
    public void onSyncPushFinished(List<ErrorSync> errors) {
        if (errors.isEmpty()) {
            Observable<JsonObject> getUser = serviceUser.getCurrentUser();
            dataBaseManager.clearData();
            cacheStore.cleanCacheStart();
            getUser.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            gn -> {

                                Gson gson = new Gson();
                                UserConverterModel userc = gson.fromJson(gn, UserConverterModel.class);
                                User user = userc.toDbModel();
                                user.getCashierStore().save();

                                user.getCashierStore().getLicence().save();
                                credentialUser.setUsername(user.getUsername());
                                credentialUser.save();
                                preferences.setStoreRDC(user.getCashierStore().getRegisterDC());
                                preferences.setUsername(user.getUsername());

                                preferences.setStoreType(user.getCashierStore().getType());
                                serviceUser.getKey().subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(k -> {
                                            preferences.setCipherKey(gson.fromJson(k, Message.class).getMessage());
                                            serviceUser.getUsers().subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(u -> {
                                                        TypeToken<List<Cashier>> ut = new TypeToken<List<Cashier>>() {
                                                        };
                                                        List<Cashier> ucms = gson.fromJson(u, ut.getType());
                                                        for (Cashier ucm :
                                                                ucms) {
                                                            ucm.save();
                                                        }
                                                        PullDb.getInstance().startPulling(this);
                                                    });
                                        });

                            });
        } else {
            for (ErrorSync error : errors
                    ) {
                Log.e("errorSync", error.getErrorMessage());

            }
            view.hideLoading();
            view.showMessage("Login echoué car il y a des erreurs dans la synchronisation");
        }
    }

    @Override
    public void onSyncPullFinished() {


        view.updateUI(CashierRepository.find(preferences.getUsername()));
    }
}
