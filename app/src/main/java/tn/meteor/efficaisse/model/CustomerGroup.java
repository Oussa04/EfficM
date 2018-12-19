package tn.meteor.efficaisse.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by lilk on 10/03/2018.
 */


@Table(database = EfficaisseDatabase.class)
public class CustomerGroup extends BaseModel {


    @PrimaryKey
    private String name;


    @Column
    private double discount;


    private List<Customer> customers;


    public CustomerGroup() {
    }

    public CustomerGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Customer> getCustomers() {
        if (customers == null) {
            customers = SQLite.select().from(Customer.class).where(Customer_Table.customerGroup.eq(this)).queryList();
        }
        return customers;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    @Override
    public String toString() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerGroup)) return false;

        CustomerGroup that = (CustomerGroup) o;

        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}