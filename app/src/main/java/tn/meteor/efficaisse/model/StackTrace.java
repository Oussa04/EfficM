package tn.meteor.efficaisse.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

@Table(database = EfficaisseDatabase.class)
public class StackTrace extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private String stackTrace;

    @Column
    private String store;

    @Column
    private String description;

    @Column(typeConverter = DateConverter.class)
    private Date date;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
