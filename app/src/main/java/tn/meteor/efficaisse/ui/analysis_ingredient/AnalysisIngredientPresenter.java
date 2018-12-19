package tn.meteor.efficaisse.ui.analysis_ingredient;

public class AnalysisIngredientPresenter implements AnalysisIngredientContract.Presenter {

    private AnalysisIngredientContract.View view;


    AnalysisIngredientPresenter(AnalysisIngredientContract.View view) {
        this.view = view;
    }


}
