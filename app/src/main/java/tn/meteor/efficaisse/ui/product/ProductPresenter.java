package tn.meteor.efficaisse.ui.product;

import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.data.repository.ProductIngredientRepository;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 27/01/2018.
 */

public class ProductPresenter extends BasePresenter implements ProductContract.Presenter {


    private ProductContract.View view;

    public ProductPresenter(ProductContract.View view) {
        this.view = view;
    }

    @Override
    public void addProduct(Product product) {
        ProductIngredientRepository repository = new ProductIngredientRepository();
        repository.save(product);
        view.updateUI();
        }
    }

