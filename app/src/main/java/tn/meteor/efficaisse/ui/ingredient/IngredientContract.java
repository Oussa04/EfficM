package tn.meteor.efficaisse.ui.ingredient;

import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.ui.base.BaseContract;

public interface IngredientContract {
    interface View extends BaseContract.View {

        void bindComponent();
        void updateUI();


    }


    interface Presenter extends BaseContract.Presenter {

        void addIngredient(Ingredient ingredient);



    }
}
