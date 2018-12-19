package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.structure.BaseModel;


import java.util.Date;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;
import tn.meteor.efficaisse.utils.LicenceConverter;


@Table(database = EfficaisseDatabase.class)
public class Store extends BaseModel {

    @PrimaryKey
    private Integer id;

    @Column
    private String name;

    @Column
    private String adress;

    @Column
    private String phone;

    @Column
    private int type;


    @Column(typeConverter = LicenceConverter.class)
    private Licence licence;

    @Column(typeConverter = DateConverter.class)
    private Date payDate;


    @Column
    private String registerDC;

    public Store() {
    }

    public Store(Integer id) {
        this.id = id;
    }


    public Store(Integer id, String name, String adress, String phone, Licence licence, Long payDate, String registerDC) {
        this.id = id;
        this.name = name;
        this.adress = adress;
        this.phone = phone;
        this.licence = licence;
        this.payDate = new Date( payDate);
        this.registerDC = registerDC;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Licence getLicence() {
        return licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getRegisterDC() {
        return registerDC;
    }

    public void setRegisterDC(String registerDC) {
        this.registerDC = registerDC;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
