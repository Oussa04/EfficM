package tn.meteor.efficaisse.ui.home;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tn.meteor.efficaisse.data.network.Message;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.service.ServiceFactory;
import tn.meteor.efficaisse.service.ServiceProduct;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by SKIIN on 27/01/2018.
 */

public class HomePresenter extends BasePresenter implements HomeContract.Presenter {

    private HomeContract.View view;
    private ServiceProduct serviceProduct;


    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }


    @Override
    public void testGet() {

    }

    @Override
    public void addProducts(List<Product> products) {
/*
        serviceProduct = ServiceFactory.getApiClient().create(ServiceProduct.class);

        Observable<JsonObject> addProducts = serviceProduct.addProduct(products);
        addProducts.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        Gson gson = new Gson();
                        Message message = gson.fromJson(jsonObject,Message.class);
                        view.showMessage(message.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {

                        view.onError(e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }
                });

*/

    }
}
