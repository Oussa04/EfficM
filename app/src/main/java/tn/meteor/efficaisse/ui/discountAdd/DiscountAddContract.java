package tn.meteor.efficaisse.ui.discountAdd;

import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.ui.base.BaseContract;

public interface DiscountAddContract {
    interface View extends BaseContract.View {

        void bindComponent();

        void updateUI();


    }


    interface Presenter extends BaseContract.Presenter {

        void addDiscount(Discount discount);


    }
}
