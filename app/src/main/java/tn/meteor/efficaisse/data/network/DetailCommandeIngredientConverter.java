package tn.meteor.efficaisse.data.network;

import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.DetailCommandeIngredient;
import tn.meteor.efficaisse.model.Unit;
import tn.meteor.efficaisse.utils.CommandeConverter;

/**
 * Created by ahmed on 02/17/18.
 */

public class DetailCommandeIngredientConverter {

    private double quantity;
    private String name;
    private double price;
    private Unit unit;


    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
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

    public DetailCommandeIngredient toDbModel(Commande c) {
        DetailCommandeIngredient dc = new DetailCommandeIngredient();

        dc.setIngredientName(name);
        dc.setQuantity(quantity);
        dc.setPrice(price);
        dc.setUnit(unit);
        dc.setCommandeNumber(c.getCommandeNumber());
        dc.setCommande(c);

        return dc;
    }

    public static DetailCommandeIngredientConverter fromDbModel(DetailCommandeIngredient dc) {
        DetailCommandeIngredientConverter dcic = new DetailCommandeIngredientConverter();
        dcic.setName(dc.getIngredientName());
        dcic.setPrice(dc.getPrice());
        dcic.setQuantity(dc.getQuantity());
        dcic.setUnit(dc.getUnit());


        return dcic;
    }

}
