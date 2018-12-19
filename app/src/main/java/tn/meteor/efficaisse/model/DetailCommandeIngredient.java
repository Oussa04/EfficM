package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by ahmed on 02/16/18.
 */
@Table(database = EfficaisseDatabase.class)
public class DetailCommandeIngredient extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private int commandeNumber;


    @Column
    private Unit unit;

    @Column
    private double quantity;

    @Column
    private String ingredientName;

    @Column
    private double price;


    private Ingredient ingredient;
    private Commande commande;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommandeNumber() {
        return commandeNumber;
    }

    public void setCommandeNumber(int commandeNumber) {
        this.commandeNumber = commandeNumber;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        this.price = ingredient.getPrice();
        this.ingredientName = ingredient.getName();


    }

    public Commande getCommande() {
        if (commande == null)
            commande = SQLite.select().from(Commande.class).where(Commande_Table.commandeNumber.eq(commandeNumber)).querySingle();
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
        this.commandeNumber = commande.getCommandeNumber();
    }
}
