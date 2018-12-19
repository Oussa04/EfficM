package tn.meteor.efficaisse.data.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.DetailCommande;
import tn.meteor.efficaisse.model.DetailCommandeIngredient;
import tn.meteor.efficaisse.model.Payment;

/**
 * Created by ahmed on 02/12/18.
 */

public class CommandeConverterModel {
    private Id id;
    private class Id{
        private int commandeNumber;
    }
    private boolean status;
    private Cashier cashier;
    private List<PaymentModel> payments;
    private List<CommandeDetailModel>detailsCommandes;
    private List<DetailCommandeIngredientConverter> ingredients;
    private CustomerGroupConverter.CustomerConverter client;
    private Date date;

    public CommandeConverterModel() {
        id = new Id();
    }
    public Commande toDbModel(){
        Commande commande = new Commande();
        commande.setCommandeNumber(id.commandeNumber);
        List<DetailCommande> details = new ArrayList<>();
        commande.setDate(date);
        List<Payment> payment = new ArrayList<>();
        List<DetailCommandeIngredient> ingredientss = new ArrayList<>();
        for(CommandeDetailModel cmd:detailsCommandes){
            details.add(cmd.toDbModel());
        }
        for(PaymentModel pm : payments){
            payment.add(pm.toDbModel(commande));
        }
        for(DetailCommandeIngredientConverter ingredient : ingredients){
            ingredientss.add(ingredient.toDbModel(commande));
        }
        commande.setPayments(payment);
        commande.setProducts(details);
        commande.setIngredients(ingredientss);
        if(cashier!= null)
        commande.setUsername(cashier.getUsername());
        commande.setStatus(status);
        if(client!=null)
        commande.setCustomerCode(client.getCode());
        return commande;
    }

    public static  CommandeConverterModel fromDbModel(Commande c){

        CommandeConverterModel ccm = new CommandeConverterModel();
        ccm.date = c.getDate();
        ccm.id.commandeNumber = c.getCommandeNumber();
        if(c.getCustomer()!=null)
        ccm.client = CustomerGroupConverter.CustomerConverter.toDbModel(c.getCustomer());
        ccm.detailsCommandes = new ArrayList<>();
        ccm.ingredients = new ArrayList<>();
        for(DetailCommande dc : c.getProducts()){
            ccm.detailsCommandes.add(CommandeDetailModel.fromDbModel(dc));
        }
        for(DetailCommandeIngredient dci : c.getIngredients()){
            ccm.ingredients.add(DetailCommandeIngredientConverter.fromDbModel(dci));
        }
        return ccm;
    }

}
