package tn.meteor.efficaisse.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.List;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import io.fabric.sdk.android.Fabric;
import tn.meteor.efficaisse.data.network.Credential;
import tn.meteor.efficaisse.data.network.Credential_Table;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.data.repository.SessionRepository;
import tn.meteor.efficaisse.model.ErrorSync;
import tn.meteor.efficaisse.ui.login.LoginActivity;


/**
 * Created by SKIIN on 26/01/2018.
 */

public class EfficaisseApplication extends Application implements SyncPushListener {

    private static EfficaisseApplication instance;

    public static final boolean ENCRYPTED = true;
    public static LoadToast toast;
    private  PreferencesHelper preferencesHelper;

    public static EfficaisseApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
        FlowManager.init(this);
        instance = this;

        CaocConfig.Builder.create().errorActivity(ErrorActivity.class).errorDrawable(null).apply();
        preferencesHelper = new AppPreferencesHelper(getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this)
                                .withFolder(getCacheDir())
                                .withMetaTables()
                                .withDescendingOrder()
                                .withLimit(100000)
                                .build())
                        .build());

    }

    public void logout(String token) {


        List<Credential> credentials = SQLite.select().from(Credential.class).where(Credential_Table.access_token.eq(token)).queryList();
        for (Credential credential : credentials) {
            credential.delete();
        }
        preferencesHelper.setRefreshToken(null);
        preferencesHelper.setAccesToken(null);
        SessionRepository.close();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public Context getContext() {
        return instance.getApplicationContext();
    }


    @Override
    public void onSyncPushFinished(List<ErrorSync> errors) {

        if (errors.isEmpty()) {

            preferencesHelper.setSyncing(false);
            toast.success();
        } else {
            preferencesHelper.setSyncing(false);
            toast.error();
        }

    }
}
