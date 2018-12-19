package tn.meteor.efficaisse.ui.stock;

import java.util.List;

import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 20/01/2018.
 */

public interface StockContract {

    interface View extends BaseContract.View {

        void updateUI();
        void showIngredientDetails(Ingredient ingredient);
    }


    interface Presenter extends BaseContract.Presenter {


        void initIngredientList(List<Ingredient> ingredientList);


    }
}
