package tn.meteor.efficaisse.ui.base;

import android.support.annotation.StringRes;


public interface BaseContract {



    interface View {

        void showLoading();
        void hideLoading();
        void onError(@StringRes int resId);
        void onError(String message);
        void showMessage(String message);
        void showMessage(@StringRes int resId);
        boolean isNetworkConnected();
        void recreate();


    }


    interface Presenter{





    }





}
