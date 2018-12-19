package tn.meteor.efficaisse.data.repository;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.property.PropertyFactory;

import java.util.Date;
import java.util.List;
import java.util.Set;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.model.Discount_Group;
import tn.meteor.efficaisse.model.Discount_Group_Table;
import tn.meteor.efficaisse.model.Discount_Product;
import tn.meteor.efficaisse.model.Discount_Product_Table;
import tn.meteor.efficaisse.model.Discount_Table;
import tn.meteor.efficaisse.model.Product;


/**
 * Created by lilk on 28/01/2018.
 */

public class DiscountRepository {

    private SyncRepository syncRepository = new SyncRepository();

    public Discount find(Discount discount) {
        return SQLite.select().from(Discount.class).where(Discount_Table.id.eq(discount.getId())).querySingle();
    }


    public void delete(Discount discount) {
        Discount ing = find(discount);
        if (ing != null) {
            ing.delete();

            LogSystem.persist(discount, "delete", "Discount", false);
        }

    }

    public List<Discount> findAll() {
        return SQLite.select().from(Discount.class).queryList();
    }

    public void save(Discount discount) {
        if (discount != null) {
            discount.save();


            for (CustomerGroup customerGroup : discount.getCustomerGroups()) {

                Discount_Group discount_group = new Discount_Group();
                discount_group.setDiscount_id(discount.getId());
                discount_group.setGroup_name(customerGroup.getName());
                discount_group.save();
            }


            for (Product product : discount.getProductList()) {


                Discount_Product discount_product = new Discount_Product();
                discount_product.setDiscount_id(discount.getId());
                discount_product.setProduct_id(product.getId());
                discount_product.save();
            }
            LogSystem.persist(discount, "save", "Discount", false);

        }


    }

    public static double getDiscount(Product p, Customer c) {

        CustomerGroup cg;
        if (c != null) {
            cg = c.getCustomerGroup();
        } else {
            cg = null;
        }
        List<Discount> discounts = SQLite.select().from(Discount.class).where(PropertyFactory.from(new Date()).between(Discount_Table.datebegin).and(Discount_Table.dateend)).queryList();
        Log.e("test1230", "" + discounts.size());
        double best = 0;
        for (Discount discount :
                discounts) {
            Discount_Product dp = new Discount_Product();
            dp.setDiscount_id(discount.getId());
            dp.setProduct_id(p.getId());
            if (cg != null) {
                if ((discount.getCustomerGroups().isEmpty() || discount.getCustomerGroups().contains(cg)) && (discount.getDiscount_products().isEmpty() || discount.getDiscount_products().contains(dp))) {
                    if (discount.getDiscount() > best) {
                        best = discount.getDiscount();
                    }
                }
            } else {
                if (discount.getCustomerGroups().isEmpty() && (discount.getDiscount_products().isEmpty() || discount.getDiscount_products().contains(dp))) {

                    if (discount.getDiscount() > best) {
                        best = discount.getDiscount();
                    }
                }
            }
        }

        return best;


    }

    public void update(Discount discount) {


        Set<Discount_Group> discount_groups = discount.getDiscount_groups();
        Set<Discount_Product> discount_products = discount.getDiscount_products();
        SQLite.delete().from(Discount_Product.class).where(Discount_Product_Table.discount_id.eq(discount.getId())).execute();
        SQLite.delete().from(Discount_Group.class).where(Discount_Group_Table.discount_id.eq(discount.getId())).execute();
        for (Discount_Group customerGroup : discount_groups) {
            customerGroup.setDiscount_id(discount.getId());
            if (SQLite.select().from(Discount_Group.class).where(Discount_Group_Table.discount_id.eq(customerGroup.getDiscount_id())).and(Discount_Group_Table.group_name.eq(customerGroup.getGroup_name())).querySingle() == null)

                customerGroup.save();

        }


        for (Discount_Product product : discount_products) {

            product.setDiscount_id(discount.getId());
            if (SQLite.select().from(Discount_Product.class).where(Discount_Product_Table.discount_id.eq(product.getDiscount_id())).and(Discount_Product_Table.product_id.eq(product.getProduct_id())).querySingle() == null)

                product.save();
        }
        LogSystem.persist(discount, "update", "Discount", false);

    }

}
