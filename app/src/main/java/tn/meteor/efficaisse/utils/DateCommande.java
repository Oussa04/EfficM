package tn.meteor.efficaisse.utils;

import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Session;

/**
 * Created by lilk on 11/02/2018.
 */

public class DateCommande {


    private Session date;

    private List<Commande> commandeList;
    private double sum;


    public Session getDate() {
        return date;
    }

    public void setDate(Session date) {
        this.date = date;
    }

    public List<Commande> getCommandeList() {
        return commandeList;
    }

    public void setCommandeList(List<Commande> commandeList) {
        this.commandeList = commandeList;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public DateCommande(Session date, List<Commande> commandeList, float sum) {
        this.date = date;
        this.commandeList = commandeList;
        this.sum = sum;
    }

    public DateCommande() {

    }
}
