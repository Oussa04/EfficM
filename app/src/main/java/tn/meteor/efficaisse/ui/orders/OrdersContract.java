package tn.meteor.efficaisse.ui.orders;

import java.util.List;

import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 20/01/2018.
 */

public interface OrdersContract {

    interface View extends BaseContract.View {

        void updateUI();

    }


    interface Presenter extends BaseContract.Presenter {


        void initPaidList(List<Commande> paidList);

        void initNotPaidList(List<Commande> notPaidList);
    }
}
