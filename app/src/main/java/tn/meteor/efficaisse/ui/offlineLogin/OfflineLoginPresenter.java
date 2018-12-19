package tn.meteor.efficaisse.ui.offlineLogin;

import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.User;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 27/01/2018.
 */

public class OfflineLoginPresenter extends BasePresenter implements OfflineLoginContract.Presenter {


    private OfflineLoginContract.View view;

    public OfflineLoginPresenter(OfflineLoginContract.View view) {
        this.view = view;
    }

    @Override
    public void addCashier(Cashier cashier) {




    }
}

