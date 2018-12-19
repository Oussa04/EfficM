package tn.meteor.efficaisse.data.network;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.model.Category;
import tn.meteor.efficaisse.model.Category_Table;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Product_Ingredient;
import tn.meteor.efficaisse.model.Unit;

/**
 * Created by ahmed on 02/12/18.
 */

public class ProductConverterModel {
    private Id id;
    private String name;
    private double price;
    private String photo;
    private Double cost;
    private Double stockQuantity;
    private boolean favoris;
    private List<IngredientProduct> ingredients;
    private CategoryId category;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isFavoris() {
        return favoris;
    }

    public void setFavoris(boolean favoris) {
        this.favoris = favoris;
    }

    public List<IngredientProduct> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientProduct> ingredients) {
        this.ingredients = ingredients;
    }

    public CategoryId getCategory() {
        return category;
    }

    public void setCategory(CategoryId category) {
        this.category = category;
    }

    public class CategoryId{
        private CategoryId() {
            id = new IdCategory();
        }

        public IdCategory getId() {
            return id;
        }

        public void setId(IdCategory id) {
            this.id = id;
        }

        private IdCategory id;
        public class IdCategory{
            String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }

    public ProductConverterModel() {
        category = new CategoryId();
        id = new Id();
        ingredients = new ArrayList<>();
    }

    public class Id{
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


    }
    public Product toDbModel(){
        Product product = new Product();
        product.setId(id.id);
        product.setStockQuantity(stockQuantity);
        product.setFavoris(favoris);
        product.setName(name);
        product.setCost(cost);
        Log.i("test123",getPhoto()+" ");
        Log.i("test123",category.id.getName()+" ");

        product.setCategory(SQLite.select().from(Category.class).where(Category_Table.name.eq(category.id.name)).querySingle());
        product.setPhoto(photo);
        product.setPrice(price);
        List<Product_Ingredient> pi = new ArrayList<>();
        for(IngredientProduct ip : ingredients){
            pi.add(ip.toDbModel());
        }
        product.setIngredients(pi);
        return product;
    }
}
