package tn.meteor.efficaisse.ui.product;

import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.ui.base.BaseContract;

public interface ProductContract {
    interface View extends BaseContract.View {

        void bindComponent();
        void updateUI();


    }


    interface Presenter extends BaseContract.Presenter {

        void addProduct(Product product);



    }
}
