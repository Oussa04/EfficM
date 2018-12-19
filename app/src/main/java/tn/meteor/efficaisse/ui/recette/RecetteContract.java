package tn.meteor.efficaisse.ui.recette;

import java.util.List;

import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 20/01/2018.
 */

public interface RecetteContract {

    interface View extends BaseContract.View {

        void updateUI();

    }


    interface Presenter extends BaseContract.Presenter {


        void initPaidList(List<Commande> paidList);

        void initNotPaidList(List<Commande> notPaidList);
    }
}
