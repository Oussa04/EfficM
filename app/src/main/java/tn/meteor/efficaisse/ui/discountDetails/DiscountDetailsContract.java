package tn.meteor.efficaisse.ui.discountDetails;

import java.util.List;

import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 20/01/2018.
 */

public interface DiscountDetailsContract {

    interface View extends BaseContract.View {

        void updateUI();
        void showDiscountDetails(Discount discount);
    }


    interface Presenter extends BaseContract.Presenter {


        void initDicountList(List<Discount> discountList);


    }
}
