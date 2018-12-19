package tn.meteor.efficaisse.data.network;

import tn.meteor.efficaisse.model.DetailCommande;

/**
 * Created by ahmed on 02/08/18.
 */

public class CommandeDetailModel {

    private P product;

    private int quantity;
    private String productName;
    private double price;

    private class P {
        private IdProduct id;

        private  P() {
            id = new IdProduct();
        }
    }

    private class IdProduct {
        private int id;
    }


    private  CommandeDetailModel() {
        product = new P();

    }

    public DetailCommande toDbModel(){
        DetailCommande dc = new DetailCommande();

        if(product!=null)
        dc.setProductId(product.id.id);
        dc.setQuantity(quantity);
        dc.setPrice(price);
        dc.setProductName(productName);
        
        return dc;
    }

    public static  CommandeDetailModel fromDbModel(DetailCommande dc){
        CommandeDetailModel cdm = new CommandeDetailModel();

        cdm.product.id.id =  dc.getProductId();
        cdm.quantity = dc.getQuantity();
        cdm.productName = dc.getProductName();
        cdm.price = dc.getPrice();
        return cdm;
    }
}
