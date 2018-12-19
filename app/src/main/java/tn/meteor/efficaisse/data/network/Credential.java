package tn.meteor.efficaisse.data.network;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;

/**
 * Created by SKIIN on 27/01/2018.
 */



@Table(database = EfficaisseDatabase.class)
public class Credential extends BaseModel {

    @Column
    @PrimaryKey
    private String username;
    @Column
    private String access_token;

    @Column
    private String refresh_token;


    public Credential(){}


    public Credential(String username, String access_token, String refresh_token) {
        this.username = username;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
