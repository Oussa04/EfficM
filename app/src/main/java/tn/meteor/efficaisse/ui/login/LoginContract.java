package tn.meteor.efficaisse.ui.login;

import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 16/01/2018.
 */

public interface LoginContract {


    interface View extends BaseContract.View{

        void bindComponent();
        void updateUI(Cashier c);

    }


    interface Presenter extends BaseContract.Presenter{
        void login(String email,String password);
    }



}
