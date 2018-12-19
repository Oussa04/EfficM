package tn.meteor.efficaisse.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by SKIIN on 27/01/2018.
 */


@Table(database = EfficaisseDatabase.class)
public class Category extends BaseModel {


    @PrimaryKey
    private String name;

    @Column
    private String photo;


    private List<Product> products ;


    public Category(){}

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }





    public List<Product> getProducts(){
        if(products == null){
            products = SQLite.select().from(Product.class).where(Product_Table.category.eq(this)).queryList();
        }
        return products;
    }


    @Override
    public String toString() {
        return name;
    }
}
