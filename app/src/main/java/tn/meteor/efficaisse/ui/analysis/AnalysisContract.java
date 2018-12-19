package tn.meteor.efficaisse.ui.analysis;

import java.util.List;

import tn.meteor.efficaisse.model.Prediction;
import tn.meteor.efficaisse.ui.base.BaseContract;

public interface AnalysisContract{

    interface View extends BaseContract.View {
        void updateUI(List<Prediction> predictions);
    }


    interface Presenter extends BaseContract.Presenter {
        void  getPredictions();
    }
}
