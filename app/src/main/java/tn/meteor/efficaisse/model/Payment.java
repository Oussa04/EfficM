package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;
import tn.meteor.efficaisse.utils.CommandeConverter;

/**
 * Created by SKIIN on 05/02/2018
 */
@Table(database = EfficaisseDatabase.class)
public class Payment extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private Integer id;

    @Column
    private double montant;

    @Column
    private String type;

    @Column
    private String commentaire;

    @Column
    private int quantity;

    @Column(typeConverter = CommandeConverter.class)
    private Commande commande;
    private ContreBon contreBon;

    public ContreBon getContreBon() {
        return contreBon;
    }

    public void setContreBon(ContreBon contreBon) {
        this.contreBon = contreBon;
    }

    public Payment(Integer id, float montant, String type, String commentaire, int quantity, Commande commande) {
        this.id = id;
        this.montant = montant;
        this.type = type;
        this.commentaire = commentaire;
        this.quantity = quantity;
        this.commande = commande;
    }

    public Payment() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

}
