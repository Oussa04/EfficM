package tn.meteor.efficaisse.ui.cashier;

import tn.meteor.efficaisse.data.repository.CashierRepository;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.ui.base.BasePresenter;
import tn.meteor.efficaisse.utils.Common;

/**
 * Created by lilk on 27/01/2018.
 */

public class CashierPresenter extends BasePresenter implements CashierContract.Presenter {


    private CashierContract.View view;

    public CashierPresenter(CashierContract.View view) {
        this.view = view;

    }

    @Override
    public void addCashier(Cashier cashier) {






        Common.changeProgressText("Ajout en cours");

        if(CashierRepository.save(cashier)){
            view.updateUI();
        }else{
            view.showMessage("Nom d'utilisateur existe deja");
        }




    }
}

