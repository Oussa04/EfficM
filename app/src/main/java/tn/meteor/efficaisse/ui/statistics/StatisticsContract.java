package tn.meteor.efficaisse.ui.statistics;


import tn.meteor.efficaisse.ui.base.BaseContract;


public interface StatisticsContract {

    interface View extends BaseContract.View {
        void bindComponent(android.view.View view);
        void load();
    }


    interface Presenter extends BaseContract.Presenter {

    }
}
