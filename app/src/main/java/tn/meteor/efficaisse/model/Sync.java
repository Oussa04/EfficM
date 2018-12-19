package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by SKIIN on 04/02/2018.
 */
@Table(database = EfficaisseDatabase.class)
public class Sync extends BaseModel {


    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private String idReference;

    @Column
    private String entity;

    @Column
    private String type;

    @Column
    private boolean upload;

    @Column(typeConverter = DateConverter.class)
    private Date date;


    @Column
    private String registerDC;


    public Sync(){}


    public Sync(String idReference, String entity, String type, boolean upload, String registerDC) {
        this.idReference = idReference;
        this.entity = entity;
        this.type = type;
        this.upload = upload;
        date = new Date();
        this.registerDC = registerDC;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdReference() {
        return idReference;
    }

    public void setIdReference(String idReference) {
        this.idReference = idReference;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRegisterDC() {
        return registerDC;
    }

    public void setRegisterDC(String registerDC) {
        this.registerDC = registerDC;
    }
}
