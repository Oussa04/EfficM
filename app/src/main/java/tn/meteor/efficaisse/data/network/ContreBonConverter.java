package tn.meteor.efficaisse.data.network;


import java.util.Date;

import tn.meteor.efficaisse.model.ContreBon;

/**
 * Created by ahmed on 02/18/18.
 */

public class ContreBonConverter {


    private Date dateSortie;
    private Date datePay;


    private boolean payed;

    private double montant;

    private String code;

    public ContreBon toDbModel() {
        ContreBon dc = new ContreBon();

        dc.setDateSortie(dateSortie);
        dc.setPayed(payed);
        dc.setCode(code);
        dc.setMontant(montant);
        if (datePay != null) {
            dc.setDatePay(datePay);
        }

        return dc;
    }
}
