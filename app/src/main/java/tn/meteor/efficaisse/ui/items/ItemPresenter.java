package tn.meteor.efficaisse.ui.items;

import java.util.List;

import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.data.repository.CategoryRepository;
import tn.meteor.efficaisse.data.repository.ProductIngredientRepository;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 20/01/2018.
 */

public class ItemPresenter extends BasePresenter implements ItemContract.Presenter {


    private ItemContract.View view;

    public ItemPresenter(ItemContract.View view) {
        this.view = view;
    }


    @Override
    public void initProductList(List<Product> productsList) {
        ProductIngredientRepository repo = new ProductIngredientRepository();
        productsList.clear();
        productsList.addAll(repo.findAll());
        view.updateProducts();
    }

    @Override
    public void getProductByCategory(Category category, List<Product> productList) {
        productList.clear();
        productList.addAll(category.getProducts());
        view.updateProducts();
    }


    @Override
    public void getFavoriteProducts(List<Product> productList) {
        CategoryRepository categoryRepository = new CategoryRepository();
        productList.clear();
        productList.addAll(categoryRepository.findFavoriteProducts());
        view.updateProducts();

    }

    @Override
    public void initCategoriesList(List<Category> categories) {
        CategoryRepository categoryRepository = new CategoryRepository();
        categories.clear();

        Category favoris = new Category();
        favoris.setName("Favoris");
        favoris.setPhoto("");
        categories.add(favoris);
        categories.addAll(categoryRepository.findAll());
        view.updateCategories();

    }


}
