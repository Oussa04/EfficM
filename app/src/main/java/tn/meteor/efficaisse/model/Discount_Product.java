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

public class Discount_Product extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private int discount_id;
    @Column
    private int product_id;


    private Discount discount;
    private Product product;


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


    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public Product getProduct() {
        if (product == null)
            product = SQLite.select().from(Product.class).where(Product_Table.id.eq(product_id)).querySingle();
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Discount getDiscount() {
        if (discount == null)
            discount = SQLite.select().from(Discount.class).where(Discount_Table.id.eq(discount_id)).querySingle();
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount_Product that = (Discount_Product) o;
        return discount_id == that.discount_id &&
                product_id == that.product_id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(discount_id, product_id);
    }

    @Override
    public String toString() {
        return "Discount_Product{" +
                "id=" + id +
                ", discount_id=" + discount_id +
                ", product_id=" + product_id +
                '}';
    }
}
