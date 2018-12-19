package tn.meteor.efficaisse.model;


import android.util.Base64;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;
import tn.meteor.efficaisse.utils.RoleConverter;
import tn.meteor.efficaisse.utils.StoreConverter;

@Table(database = EfficaisseDatabase.class)
public class User extends BaseModel {


    @PrimaryKey
    private Integer id;

    @Column
    private String name;

    @Column
    private String username;

    @Column
    private String email;

    @Column(typeConverter = StoreConverter.class)
    private Store cashierStore;

    @Column(typeConverter = RoleConverter.class)
    private Set authorities;


    @Column
    private String password;



    public User() {
    }

    public User(Integer id, String name, String username, String email, Store cashierStore, Set<Role> authorities) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.cashierStore = cashierStore;
        this.authorities = authorities;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Store getCashierStore() {
        return cashierStore;
    }

    public void setCashierStore(Store cashierStore) {
        this.cashierStore = cashierStore;
    }

    public Set<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
