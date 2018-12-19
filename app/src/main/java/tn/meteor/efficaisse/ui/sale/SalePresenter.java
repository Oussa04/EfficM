package tn.meteor.efficaisse.ui.sale;

import java.util.List;

import tn.meteor.efficaisse.data.repository.CategoryRepository;
import tn.meteor.efficaisse.data.repository.ProductIngredientRepository;
import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 20/01/2018.
 */

public class SalePresenter extends BasePresenter implements SaleContract.Presenter {


    private SaleContract.View view;
    private Customer customer;

    public SalePresenter(SaleContract.View view) {


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
    public void addProductToTicket(Product product, List<DetailCommande> detailCommandeList) {


        boolean productExists = false;
        for (DetailCommande t : detailCommandeList) {
            if (t.getProductId() == product.getId()) {
                t.setQuantity(t.getQuantity() + 1);
//                t.update();
                productExists = true;
            }
        }
        if (!productExists) {
            DetailCommande detailCommande = new DetailCommande();
            detailCommande.setQuantity(1);
            detailCommande.setProduct(product);
            detailCommande.setCommande(null);
            detailCommande.setCommandeNumber(-1);
            detailCommande.setProductId(product.getId());
//            detailCommande.save();

            detailCommandeList.add(detailCommande);

        }
        view.updateCommande();
        view.updateCommandeUI();

    }


    @Override
    public void decreseItemQuantity(DetailCommande detailCommande, List<DetailCommande> detailCommandeList) {
        if (detailCommande.getQuantity() > 1) {
            detailCommande.setQuantity(detailCommande.getQuantity() - 1);
//            detailCommande.update();
            view.updateCommande();
            view.updateCommandeUI();
        }


    }

    @Override
    public void increseItemQuantity(DetailCommande detailCommande, List<DetailCommande> detailCommandeList) {
        detailCommande.setQuantity(detailCommande.getQuantity() + 1);
//        detailCommande.update();

        view.updateCommande();
        view.updateCommandeUI();
    }

    @Override
    public void removeItem(DetailCommande detailCommande, List<DetailCommande> detailCommandeList) {
        detailCommandeList.remove(detailCommandeList.indexOf(detailCommande));
//        detailCommande.delete();
        view.updateCommande();
        view.updateCommandeUI();

    }

    @Override
    public void emptyTicket(List<DetailCommande> detailCommandeList) {

        detailCommandeList.clear();

        view.updateCommande();
        view.updateCommandeUI();


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


    @Override
    public float calculateSum(List<DetailCommande> ticketList) {
        float ticketSum = 0;

        for (DetailCommande t : ticketList) {

            ticketSum += t.getQuantity() * t.getProduct().getDiscounted(customer);


        }
        return ticketSum;
    }

    @Override
    public void setCustomer(Customer c) {
        customer = c ;
    }

    @Override
    public int countItems(List<DetailCommande> ticketList) {
        int ticketCountProducts = 0;
        for (DetailCommande t : ticketList) {

            ticketCountProducts += t.getQuantity();


        }
        return ticketCountProducts;
    }


}
