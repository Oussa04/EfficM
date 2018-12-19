package tn.meteor.efficaisse.ui.payement;

import java.util.List;

import tn.meteor.efficaisse.model.Payment;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 16/01/2018.
 */

public interface PayementContract {


    interface View extends BaseContract.View{

        void updateUI();

    }


    interface Presenter extends BaseContract.Presenter{

        void addPayement(Payment payment, List<Payment> payments);


    }



}
