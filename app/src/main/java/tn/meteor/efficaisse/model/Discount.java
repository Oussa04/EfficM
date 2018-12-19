package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by lilk on 15/04/2018.
 */

@Table(database = EfficaisseDatabase.class)
public class Discount extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    private double discount;

    @Column
    private String name;

    @Column(typeConverter = DateConverter.class)
    private Date datebegin;

    @Column(typeConverter = DateConverter.class)
    private Date dateend;

    private List<CustomerGroup> customerGroups;

    private List<Product> productList;
    private Set<Discount_Product> discount_products;
    private Set<Discount_Group> discount_groups;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDatebegin() {
        return datebegin;
    }

    public void setDatebegin(Date datebegin) {
        this.datebegin = datebegin;
    }

    public Date getDateend() {
        return dateend;
    }

    public void setDateend(Date dateend) {
        this.dateend = dateend;
    }

    public List<CustomerGroup> getCustomerGroups() {

            customerGroups =  SQLite.select().from(CustomerGroup.class).where(CustomerGroup_Table.name.in(SQLite.select(Discount_Group_Table.group_name).from(Discount_Group.class).where(Discount_Group_Table.discount_id.eq(id)))).queryList();

        return customerGroups;
    }

    public void setCustomerGroups(List<CustomerGroup> customerGroups) {
        this.customerGroups = customerGroups;
    }

    public List<Product> getProductList() {
       productList =  SQLite.select().from(Product.class).where(Product_Table.id.in(SQLite.select(Discount_Product_Table.product_id).from(Discount_Product.class).where(Discount_Product_Table.discount_id.eq(id)))).queryList();
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }


    public Discount() {
    }

    public Set<Discount_Product> getDiscount_products() {
        if(discount_products == null)
            discount_products =new HashSet<>( SQLite.select().from(Discount_Product.class).where(Discount_Product_Table.discount_id.eq(id)).queryList());
        return discount_products;
    }

    public void setDiscount_products(Set<Discount_Product> discount_products) {
        this.discount_products = discount_products;
    }

    public Set<Discount_Group> getDiscount_groups() {
        if (discount_groups == null){
            discount_groups =new HashSet<>( SQLite.select().from(Discount_Group.class).where(Discount_Group_Table.discount_id.eq(id)).queryList());
        }
        return discount_groups;
    }

    public void setDiscount_groups(Set<Discount_Group> discount_groups) {
        this.discount_groups = discount_groups;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", discount=" + discount +
                ", name='" + name + '\'' +
                ", datebegin=" + datebegin +
                ", dateend=" + dateend +
                ", customerGroups=" + customerGroups +
                ", productList=" + productList +
                '}';
    }
}
