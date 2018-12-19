package tn.meteor.efficaisse.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by lilk on 20/01/2018.
 */


@Table(database = EfficaisseDatabase.class)
public class Licence extends BaseModel {

    @PrimaryKey
    private Integer id;

    @Column
    private String name;

    @Column
    private int length;


    public Licence() {
    }

    public Licence(Integer id) {
        this.id = id;
    }

    public Licence(Integer id, String name, int length) {
        this.id = id;
        this.name = name;
        this.length = length;

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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
