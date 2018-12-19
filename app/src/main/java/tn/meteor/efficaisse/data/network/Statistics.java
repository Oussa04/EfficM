

package tn.meteor.efficaisse.data.network;

import java.util.Date;

/**
 * Created by SKIIN on 11/03/2018
 */
public class Statistics {
    private int id;
    private int quantity;
    private String productName;
    private Date date;
    private double montant;
    private String category;

    public int getQuantity() {
        return quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Statistics{" + "quantity=" + quantity + ", productName='" + productName + '\'' + ", date=" + date + '}';
    }
}

