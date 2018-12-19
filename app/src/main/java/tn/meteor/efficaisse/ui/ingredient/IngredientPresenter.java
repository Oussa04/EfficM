package tn.meteor.efficaisse.ui.ingredient;

import tn.meteor.efficaisse.data.repository.IngredientRepository;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 27/01/2018.
 */

public class IngredientPresenter extends BasePresenter implements IngredientContract.Presenter {


    private IngredientContract.View view;

    public IngredientPresenter(IngredientContract.View view) {
        this.view = view;
    }

    @Override
    public void addIngredient(Ingredient ingredient) {
        IngredientRepository ingredientRepository = new IngredientRepository();
        ingredientRepository.save(ingredient);
        view.updateUI();
    }
}

