package tn.meteor.efficaisse.ui.items;

import java.util.List;

import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.ui.base.BaseContract;
import tn.meteor.efficaisse.model.Product;

/**
 * Created by lilk on 20/01/2018.
 */

public interface ItemContract {

    interface View extends BaseContract.View {


        void updateProducts();

        void updateCategories();

        void showProductDetail(Product product);


    }


    interface Presenter extends BaseContract.Presenter {
        void initCategoriesList(List<Category> categories);

        void initProductList(List<Product> productsList);

        void getProductByCategory(Category category, List<Product> productList);

        void getFavoriteProducts(List<Product> productList);


    }
}
