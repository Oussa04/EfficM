package tn.meteor.efficaisse.data.network;


import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Payment;
/**
 * Created by ahmed on 02/11/18.
 */

public class PaymentModel {

    private Id id;
    private double montant;


    private String type;


    private String commentaire;


    private int quantity;

    private PaymentModel() {
        id = new Id();
    }

    private class Id {
        String storeId;
        int id;

    }

    public Payment toDbModel(Commande c) {
        Payment  p = new Payment();
        p.setId(id.id);
        p.setCommentaire(commentaire);
        p.setMontant(montant);
        p.setQuantity(quantity);
        p.setType(type);
        p.setCommande(c);
        return p;
    }


    public static PaymentModel fromDbModel(Payment pi,String store) {
        PaymentModel pm = new PaymentModel();
        pm.id.storeId = store;
        pm.id.id = pi.getId();
        pm.commentaire = pi.getCommentaire();
        pm.montant = pi.getMontant();
        pm.quantity = pi.getQuantity();
        pm.type = pi.getType();
        return pm;


    }
}
