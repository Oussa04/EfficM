package tn.meteor.efficaisse.model;

import android.util.Base64;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.CustomerGroupConverter;
import tn.meteor.efficaisse.utils.EfficaisseApplication;

/**
 * Created by lilk on 10/03/2018.
 */
@Table(database = EfficaisseDatabase.class)
public class Customer extends BaseModel {


    @PrimaryKey
    private String code;

    @Column
    private String name;

    @Column
    private String email;
    @Column
    private String phone;
    @Column(typeConverter = DateConverter.class)
    private Date joinDate;

    @Column(typeConverter = CustomerGroupConverter.class)
    private CustomerGroup customerGroup;


    private String crypted;
    private static Key key;

    private static Cipher cipher;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public void setCrypted(String crypted) {
        this.crypted = crypted;
    }

    public String getCrypted() {

        if(crypted==null){
            crypted = generateCipher();
        }

        return crypted;
    }
    public String getCode() {
        if(code==null)  {
            code = decryptCipher();
        }
        return code;
    }
    private Cipher getCipher() {
        if (cipher == null) {
            try {
                cipher = Cipher.getInstance("DESede");
            } catch (Exception e) {
                Log.e("hi",e.getMessage());
            }
        }
        return cipher;
    }

    private Key getKey() {
        PreferencesHelper prefs = new AppPreferencesHelper(EfficaisseApplication.getInstance().getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
        if (key == null) {
            if (prefs.getCipherKey() != null) {
                byte[] encodedKey = prefs.getCipherKey().getBytes();

                key = new SecretKeySpec(encodedKey, 0, 16, "DESede");

            }
        }
        return key;
    }
    private String decryptCipher(){
        if (getKey() != null && getCipher()!=null && crypted !=null) {
            try {
                getCipher().init(Cipher.DECRYPT_MODE, getKey());
                byte[] inputByte = Base64.decode(getCrypted(),Base64.DEFAULT);
                return new String(cipher.doFinal(inputByte));
            } catch (Exception e) {
                Log.e("hi",e.getMessage());
                return null;
            }
        }
        return null;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CustomerGroup getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(CustomerGroup customerGroup) {
        this.customerGroup = customerGroup;
    }

    private String generateCipher() {
        if (getKey() != null && getCipher()!=null && code !=null) {
            try {
                getCipher().init(Cipher.ENCRYPT_MODE, getKey());
                byte[] inputByte = code.getBytes();
                return Base64.encodeToString(cipher.doFinal(inputByte),Base64.DEFAULT);
            }catch(Exception e){
                Log.e("hi",e.getMessage());
                return null;
            }
        }

        return null;
    }
    public Customer() {
        joinDate = new Date();

        do {
            code = UUID.randomUUID().toString();
            code = "customer-"+code;
        }
        while (SQLite.select().from(ContreBon.class).where(Customer_Table.code.eq(code)).querySingle() != null);

    }
    public Customer(String cipher) {
        this.crypted = cipher;

    }
}
