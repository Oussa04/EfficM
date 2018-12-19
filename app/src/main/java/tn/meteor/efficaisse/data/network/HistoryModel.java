package tn.meteor.efficaisse.data.network;

import com.raizlabs.android.dbflow.annotation.Column;

import java.util.Date;

import tn.meteor.efficaisse.model.History;

/**
 * Created by ahmed on 02/17/18.
 */

public class HistoryModel {
    private String name;

    private Date date;
    private float quantity;

    private String type;
    private String username;

    public History toDbModel() {
        History dc = new History();

        dc.setDate(date);
        dc.setQuantity(quantity);
        dc.setName(name);
        dc.setType(type);
        dc.setUsername(username);
        return dc;
    }


}
