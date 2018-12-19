package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;


@Table(database = EfficaisseDatabase.class)
public class LoungeTable extends BaseModel {



    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private String libelle;

    @Column
    private String descr;

    @Column
    private Integer commande_number;


    private Commande commande;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String desc) {
        this.descr = desc;
    }

    public Integer getCommande_number() {
        return commande_number;
    }

    public void setCommande_number(Integer commande_number) {
        this.commande_number = commande_number;
    }

    public Commande getCommande() {
        if (commande == null)
            commande = SQLite.select().from(Commande.class).where(Commande_Table.commandeNumber.eq(commande_number)).querySingle();
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }
}
