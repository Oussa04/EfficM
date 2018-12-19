package tn.meteor.efficaisse.ui.offlineLogin;

import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.User;
import tn.meteor.efficaisse.ui.base.BaseContract;

public interface OfflineLoginContract {
    interface View extends BaseContract.View {

        void bindComponent();

        void updateUI(Cashier cashier);


    }


    interface Presenter extends BaseContract.Presenter {

        void addCashier(Cashier cashier);


    }
}
