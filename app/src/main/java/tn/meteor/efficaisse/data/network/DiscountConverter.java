package tn.meteor.efficaisse.data.network;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.data.repository.ProductIngredientRepository;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.model.Discount_Group;
import tn.meteor.efficaisse.model.Discount_Product;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Table;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;

public class DiscountConverter{
    private String store;
    private double discount;
    private String name;
    private Date dateBegin;
    private Date dateEnd;
    private List<subClass> discountGroups;
    private List<subClass> discountProducts;


    public  Discount toDbModel(){
        ProductIngredientRepository productRepo = new ProductIngredientRepository();
        Discount discount = new Discount();
        discount.setDateend(dateEnd);
        discount.setDatebegin(dateBegin);
        discount.setDiscount(this.discount);
        discount.setName(name);

        Set<Discount_Product> listProduct = new HashSet<>();
        for (subClass product :
                discountProducts) {
            try {
                Discount_Product dp = new Discount_Product();
                dp.setProduct_id(SQLite.select().from(Product.class).where(Product_Table.name.eq(product.name)).querySingle().getId());

                listProduct.add(dp);
            }catch(Exception e){

            }
        }
        discount.setDiscount_products(listProduct);

        Set<Discount_Group> listGroup = new HashSet<>();
        for (subClass product :
                discountGroups) {

                Discount_Group dp = new Discount_Group();
                dp.setGroup_name(product.name);

                listGroup.add(dp);

        }
        discount.setDiscount_groups(listGroup);

        return discount;
    }
    public static DiscountConverter fromDbModel(Discount discount){
        DiscountConverter dc = new DiscountConverter();
        dc.dateBegin = discount.getDatebegin();
        dc.dateEnd = discount.getDateend();
        dc.discount = discount.getDiscount();
        dc.name = discount.getName();
        PreferencesHelper preferences = new AppPreferencesHelper(EfficaisseApplication.getInstance().getContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);

        dc.store = preferences.getStoreRDC();
        List<subClass> listProduct = new ArrayList<>();
        List<subClass> listGroup = new ArrayList<>();
        for (Discount_Group dg:
             discount.getDiscount_groups()) {
            DiscountConverter.subClass discountGroup = dc.new subClass();
            discountGroup.name = dg.getGroup_name();
            listGroup.add(discountGroup);
        }
        dc.discountGroups = listGroup;
        for (Discount_Product dp :
                discount.getDiscount_products()) {
            DiscountConverter.subClass discountGroup = dc.new subClass();
            discountGroup.name = dp.getProduct().getName();
            listProduct.add(discountGroup);

        }
        dc.discountProducts = listProduct;
        return dc;
    }




    private class subClass {
     private String name;



    }

}
