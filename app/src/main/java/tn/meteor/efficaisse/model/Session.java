package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;
import tn.meteor.efficaisse.utils.CashierConverter;

/**
 * Created by ahmed on 03/13/18.
 */
@Table(database = EfficaisseDatabase.class)
public class Session extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column(typeConverter = DateConverter.class)
    private Date date;
    @Column(typeConverter = DateConverter.class)
    private Date closed;

    @Column(typeConverter = CashierConverter.class)
    private Cashier  user;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getClosed() {
        return closed;
    }

    public void setClosed(Date closed) {
        this.closed = closed;
    }

    public Cashier getUser() {
        return user;
    }

    public void setUser(Cashier user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
