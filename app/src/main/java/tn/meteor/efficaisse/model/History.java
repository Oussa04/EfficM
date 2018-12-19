package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by ahmed on 02/17/18.
 */
@Table(database = EfficaisseDatabase.class)
public class History extends BaseModel{
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private String name;
    @Column(typeConverter = DateConverter.class)
    private Date date;
    @Column
    private double quantity;

    @Column
    private String type;

    @Column
    private String username;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
