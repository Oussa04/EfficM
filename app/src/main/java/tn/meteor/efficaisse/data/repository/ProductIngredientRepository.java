package tn.meteor.efficaisse.data.repository;


import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.data.Repository;
import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.History;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Ingredient;
import tn.meteor.efficaisse.model.Product_Ingredient_Table;
import tn.meteor.efficaisse.model.Product_Table;

/**
 * Created by ahmed on 01/28/18.
 */

public class ProductIngredientRepository implements Repository<Product, Ingredient> {




    public Product findByName(String productName){
        return SQLite.select().from(Product.class).where(Product_Table.name.eq(productName)).querySingle();
    }


    @Override
    public Product find(Product parent) {
        return SQLite.select().from(Product.class).where(Product_Table.id.eq(parent.getId())).querySingle();
    }

    @Override
    public Product find(int id) {
        return SQLite.select().from(Product.class).where(Product_Table.id.eq(id)).querySingle();
    }

    @Override
    public void delete(Product parent) {
        Product product = find(parent);
        for (Product_Ingredient ingredientP : product.getIngredients()) {
            ingredientP.delete();
        }
        LogSystem.persist(product, "delete", "Product", false);
        product.delete();

    }

    @Override
    public void delete(Product parent, Ingredient child) {

        Product product = find(parent);
        for (Product_Ingredient ingredientP : product.getIngredients()) {
            if (ingredientP.getIngredient_id() == child.getId()) {

                ingredientP.delete();

            }
        }

    }

    @Override
    public List<Product> findAll() {
        return SQLite.select().from(Product.class).queryList();
    }

    public void save(Product parent, Ingredient ingredient, float quantity) {

        Product_Ingredient relation = SQLite.select().from(Product_Ingredient.class).where(Product_Ingredient_Table.ingredient_id.eq(ingredient.getId())).and(Product_Ingredient_Table.product_id.eq(parent.getId())).querySingle();
        if (relation == null) {
            relation = new Product_Ingredient();
            relation.setIngredient_id(ingredient.getId());
            relation.setProduct_id(parent.getId());
            relation.setQuantity(quantity);
            relation.save();
            LogSystem.persist(relation, "save", "Product_Ingredient", false);
        } else {
            relation.setQuantity(quantity);
            relation.update();

        }


    }

    @Override
    public void save(Product parent) {
        List<Product_Ingredient> ingredients = parent.getIngredients();
        parent.setIngredients(null);
        for (Product_Ingredient ingredientP : ingredients) {
            if (ingredientP.getIngredient().getId() == 0) {
                ingredientP.getIngredient().save();
            }
            ingredientP.setIngredient_id(ingredientP.getIngredient().getId());
        }
        parent.save();
        LogSystem.persist(parent, "save", "Product", true);
        for (Product_Ingredient ingredientP : ingredients) {
            ingredientP.setProduct_id(parent.getId());
            ingredientP.save();
        }


        if (parent.getStockQuantity() != null && parent.getCost() != null) {
            History history = new History();
            history.setDate(new Date());
            history.setName(parent.getName());
            history.setQuantity(parent.getStockQuantity());
            history.setType("Product");
            HistoryRepository historyRepository = new HistoryRepository();
            historyRepository.save(history);
        }

    }

    @Override
    public void save(List<Product> parents) {

        for (Product parent : parents) {
            save(parent);
        }
    }


    public List<Product_Ingredient> findProductsWithIngredient(Ingredient ingredient) {


        return SQLite.select().from(Product_Ingredient.class).where(Product_Ingredient_Table.ingredient_id.eq(ingredient.getId())).queryList();

    }


    public void updateProduct(Product product, boolean imageUpdated) {
        product.update();
        LogSystem.persist(product, "update", "Product", imageUpdated);
    }

    public void updateProductIngreient(Product_Ingredient productIngredient) {
        productIngredient.update();
        LogSystem.persist(productIngredient, "update", "Product_Ingredient", false);
    }

    public void deleteProductIngredient(Product_Ingredient productIngredient) {
        productIngredient.delete();
        LogSystem.persist(productIngredient, "delete", "Product_Ingredient", false);
    }
}
