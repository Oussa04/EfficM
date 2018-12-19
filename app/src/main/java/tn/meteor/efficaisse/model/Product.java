package tn.meteor.efficaisse.model;



import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;


import java.util.List;
import java.util.Objects;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;
import tn.meteor.efficaisse.data.repository.DiscountRepository;
import tn.meteor.efficaisse.utils.CategoryConverter;



@Table(database = EfficaisseDatabase.class)
public class Product extends BaseModel  {


    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private String name;

    @Column
    private Double cost;

    @Column
    private double price;

    @Column
    private String photo;

    @Column(typeConverter = CategoryConverter.class)
    private Category category;

    @Column
    private Double stockQuantity;

    @Column
    private boolean favoris;


    private List<Product_Ingredient> ingredients ;
    private List<DetailCommande> commandes;




    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "commandes")
    public List<DetailCommande> getCommandes() {
        if (commandes == null || commandes.isEmpty()) {
            commandes = SQLite.select()
                    .from(DetailCommande.class)
                    .where(DetailCommande_Table.productId.eq(id))
                    .queryList();
        }
        return commandes;
    }

    public void setCommandes(List<DetailCommande> commandes) {
        this.commandes = commandes;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "ingredients")
    public List<Product_Ingredient> getIngredients() {
        if (ingredients == null || ingredients.isEmpty()) {
            ingredients = SQLite.select()
                    .from(Product_Ingredient.class)
                    .where(Product_Ingredient_Table.product_id.eq(id))
                    .queryList();
        }
        return ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                Objects.equals(name, product.name) &&
                Objects.equals(category, product.category);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, category);
    }

    public Product(){}


    public Product(String name, double cost, double price, String photo, Category category, boolean favoris,double stock) {
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.photo = photo;
        this.category = category;
        this.favoris = favoris;
        this.stockQuantity = stock;
    }

    public Product(int id, String name, double cost, double price, String photo, Category category, boolean favoris,double stock) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.photo = photo;
        this.category = category;
        this.favoris = favoris;
        this.stockQuantity = stock;
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

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public boolean isFavoris() {
        return favoris;
    }

    public void setFavoris(boolean favoris) {
        this.favoris = favoris;
    }

    public Double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setIngredients(List<Product_Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
    public double getDiscounted(Customer customer){

        double discount = DiscountRepository.getDiscount(this,customer);
        Log.d("test1230",discount+"");
        return price * (1-discount/100);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                ", price=" + price +
                ", photo='" + photo + '\'' +
                ", category=" + category +
                ", stockQuantity=" + stockQuantity +
                ", favoris=" + favoris +
                '}';
    }
}



