package tn.meteor.efficaisse.ui.lounge;

import java.util.List;

import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 20/01/2018.
 */

public interface LoungeContract {

    interface View extends BaseContract.View {

        void updateUI();


    }


    interface Presenter extends BaseContract.Presenter {

        void initTables(List<LoungeTable> tables);

    }
}
