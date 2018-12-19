package tn.meteor.efficaisse.ui.ingredientDetails;

import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Prediction;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 27/01/2018.
 */

public interface IngredientDetailsContract {
    interface View extends BaseContract.View {




    }


    interface Presenter extends BaseContract.Presenter {

        Prediction getPredictionByIngredient(Ingredient ingredient);



    }
}
