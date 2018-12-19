package tn.meteor.efficaisse.ui.stock;

import java.util.List;

import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.data.repository.IngredientRepository;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 20/01/2018.
 */

public class StockPresenter extends BasePresenter implements StockContract.Presenter {


    private StockContract.View view;

    public StockPresenter(StockContract.View view) {


        this.view = view;


    }


    @Override
    public void initIngredientList(List<Ingredient> ingredientList) {
        IngredientRepository repo = new IngredientRepository();
        ingredientList.clear();
        ingredientList.addAll(repo.findAll());
        view.updateUI();
    }
}
