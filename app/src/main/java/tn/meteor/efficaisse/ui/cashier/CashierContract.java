package tn.meteor.efficaisse.ui.cashier;

import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.User;
import tn.meteor.efficaisse.ui.base.BaseContract;

public interface CashierContract {
    interface View extends BaseContract.View {

        void bindComponent();

        void updateUI();


    }


    interface Presenter extends BaseContract.Presenter {

        void addCashier(Cashier cashier);


    }
}
