package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by ahmed on 01/28/18.
 */

@Table(database = EfficaisseDatabase.class)
public class Product_Ingredient extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private int product_id;
    @Column
    private int ingredient_id;
    @Column
    private double quantity;

    private Product product;
    private Ingredient ingredient;

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getIngredient_id() {
        return ingredient_id;
    }

    public void setIngredient_id(int ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public Product  getProduct(){
        if(product == null)
        product =  SQLite.select().from(Product.class).where(Product_Table.id.eq(product_id)).querySingle();
        return product;
    }
    public Ingredient  getIngredient(){
        if (ingredient == null)
        ingredient =  SQLite.select().from(Ingredient.class).where(Ingredient_Table.id.eq(ingredient_id)).querySingle();
        return ingredient;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
