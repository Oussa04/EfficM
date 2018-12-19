package tn.meteor.efficaisse.ui.events;

import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by Oussa on 22-Apr-18.
 */

public interface EventContract  {
    interface View extends BaseContract.View {

        void bindComponent();
    }

    interface Presenter extends BaseContract.Presenter {

    }
}
