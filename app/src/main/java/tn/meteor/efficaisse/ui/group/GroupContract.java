package tn.meteor.efficaisse.ui.group;

import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.ui.base.BaseContract;

public interface GroupContract {
    interface View extends BaseContract.View {

        void bindComponent();

        void updateUI();


    }


    interface Presenter extends BaseContract.Presenter {

        void addCustomerGroup(CustomerGroup customerGroup);


    }
}
