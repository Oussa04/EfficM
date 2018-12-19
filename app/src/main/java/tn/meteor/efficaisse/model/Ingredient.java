package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;
import java.util.Objects;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;
import tn.meteor.efficaisse.utils.UnitConverter;

/**
 * Created by SKIIN on 27/01/2018.
 */

@Table(database = EfficaisseDatabase.class)
public class Ingredient extends BaseModel {


    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private String name;

    @Column
    private double price;

    @Column(typeConverter = UnitConverter.class)
    private Unit unit;

    @Column
    private double stockQuantity;

    @Column
    private String photo;
    @Column
    private double seuil;


    public Ingredient(int id) {
        this.id = id;
    }

    private List<Product_Ingredient> products;

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "products")
    public List<Product_Ingredient> getIngredient() {
        if (products == null || products.isEmpty()) {
            products = SQLite.select()
                    .from(Product_Ingredient.class)
                    .where(Product_Ingredient_Table.ingredient_id.eq(id))
                    .queryList();
        }
        return products;
    }

    public void setProducts(List<Product_Ingredient> products) {
        this.products = products;
    }


    public Ingredient(){}

    public Ingredient(String name, double price, Unit unit, String photo, double stockQuantity) {
        super();
        this.photo = photo;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ingredient that = (Ingredient) o;

        if (Double.compare(that.price, price) != 0) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (unit != that.unit) return false;
        return photo != null ? photo.equals(that.photo) : that.photo == null;
    }


    @Override
    public int hashCode() {

        return Objects.hash(name, price, unit, photo);
    }

    public double getSeuil() {
        return seuil;
    }




    public void setSeuil(double seuil) {
        this.seuil = seuil;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", unit=" + unit +
                ", stockQuantity=" + stockQuantity +
                ", photo='" + photo + '\'' +
                ", products=" + products +
                '}';
    }
}
