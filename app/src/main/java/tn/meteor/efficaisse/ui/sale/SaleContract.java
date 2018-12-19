package tn.meteor.efficaisse.ui.sale;

import java.util.List;

import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.ui.base.BaseContract;

/**
 * Created by lilk on 20/01/2018.
 */

public interface SaleContract {

    interface View extends BaseContract.View {


        void updateProducts();

        void updateCategories();

        void updateCommande();

        void updateCommandeUI();


    }


    interface Presenter extends BaseContract.Presenter {
        void initCategoriesList(List<Category> categories);

        void initProductList(List<Product> productsList);

        void getProductByCategory(Category category, List<Product> productList);

        void getFavoriteProducts(List<Product> productList);


        void addProductToTicket(Product product, List<DetailCommande> ticket);

        void decreseItemQuantity(DetailCommande ticket, List<DetailCommande> tickets);

        void increseItemQuantity(DetailCommande ticket, List<DetailCommande> tickets);

        void removeItem(DetailCommande ticket, List<DetailCommande> tickets);

        void emptyTicket(List<DetailCommande> tickets);

        float calculateSum(List<DetailCommande> ticketList);

        void setCustomer(Customer c);
        int countItems(List<DetailCommande> ticketList);
    }
}
