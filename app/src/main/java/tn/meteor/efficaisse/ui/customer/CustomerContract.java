package tn.meteor.efficaisse.ui.customer;

import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.ui.base.BaseContract;

public interface CustomerContract {
    interface View extends BaseContract.View {

        void bindComponent();

        void updateUI();


    }


    interface Presenter extends BaseContract.Presenter {

        void addCustomer(Customer customer);


    }
}
