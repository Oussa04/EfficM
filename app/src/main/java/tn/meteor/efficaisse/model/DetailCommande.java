package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by SKIIN on 28/01/2018.
 */

@Table(database = EfficaisseDatabase.class)
public class DetailCommande extends BaseModel{


    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private int commandeNumber;


    @Column
    private int productId;


    @Column
    private int quantity;

    @Column
    private String productName;

    @Column
    private double price;

    @Column
    private Double cost;



    private Product product;
    private Commande commande;


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCommandeNumber() {
        return commandeNumber;
    }

    public void setCommandeNumber(int commandeNumber) {
        this.commandeNumber = commandeNumber;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        if(product == null)
            product = SQLite.select().from(Product.class).where(Product_Table.id.eq(productId)).querySingle();
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Commande getCommande() {
        if(commande == null)
            commande = SQLite.select().from(Commande.class).where(Commande_Table.commandeNumber.eq(commandeNumber)).querySingle();
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "DetailCommande{" +
                "id=" + id +
                ", commandeNumber=" + commandeNumber +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", cost=" + cost +
                ", product=" + product +
                ", commande=" + commande +
                '}';
    }
}
