package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.Category;

import tn.meteor.efficaisse.model.Category_Table;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Table;


/**
 * Created by SKIIN on 04/02/2018.
 */

public class CategoryRepository {

    public Category find(Category category) {
        return SQLite.select().from(Category.class).where(Category_Table.name.eq(category.getName())).querySingle();
    }


    public void delete(Category category) {
        Category cat = find(category);
        if (cat != null){
            cat.delete();
            LogSystem.persist(category,"delete","Category",false);

        }

    }


    public List<Category> findAll() {
        return SQLite.select().from(Category.class).queryList();
    }


    public void save(Category category) {
        LogSystem.persist(category, "save", "Category", true);
        category.save();
    }

    public List<Product> findFavoriteProducts() {
        return SQLite.select().from(Product.class).where(Product_Table.favoris.eq(true)).queryList();
    }

    public void update(Category category, boolean imageUpdated) {
        if (imageUpdated)
            LogSystem.persist(category, "update", "Category", true);
        category.update();


    }

}
