package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Objects;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by lilk on 15/04/2018.
 */

@Table(database = EfficaisseDatabase.class)
public class Discount_Group extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private int discount_id;
    @Column
    private String group_name;


    private Discount discount;
    private CustomerGroup customerGroup;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(int discount_id) {
        this.discount_id = discount_id;
    }


    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Discount getDiscount() {
        if(discount == null)
            discount =  SQLite.select().from(Discount.class).where(Discount_Table.id.eq(discount_id)).querySingle();
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public CustomerGroup getCustomerGroup() {
        if(customerGroup == null)
            customerGroup =  SQLite.select().from(CustomerGroup.class).where(CustomerGroup_Table.name.eq(group_name)).querySingle();
        return customerGroup;
    }

    public void setCustomerGroup(CustomerGroup customerGroup) {
        this.customerGroup = customerGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount_Group that = (Discount_Group) o;
        return discount_id == that.discount_id &&
                Objects.equals(group_name, that.group_name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(discount_id, group_name);
    }

    @Override
    public String toString() {
        return "Discount_Group{" +
                "id=" + id +
                ", discount_id=" + discount_id +
                ", group_name='" + group_name + '\'' +
                '}';
    }
}
