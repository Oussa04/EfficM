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
import tn.meteor.efficaisse.utils.EfficaisseApplication;

/**
 * Created by ahmed on 02/17/18.
 */
@Table(database = EfficaisseDatabase.class)
public class ContreBon extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column(typeConverter = DateConverter.class)
    private Date dateSortie;

    @Column(typeConverter = DateConverter.class)
    private Date datePay;

    @Column
    private boolean payed;
    @Column
    private double montant;

    @Column
    private String code;

    private String crypted;
    private static Key key;

    private static Cipher cipher;

    public ContreBon(double montant) {
        this();
        this.montant = montant;

    }

    public ContreBon(String crypted) {
        this.crypted = crypted;
        this.code = decryptCipher();
    }

    public ContreBon() {
        dateSortie = new Date();
        this.payed = false;
        do {
            code = UUID.randomUUID().toString();
        }
        while (SQLite.select().from(ContreBon.class).where(ContreBon_Table.code.eq(code)).querySingle() != null);

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(Date dateSortie) {
        this.dateSortie = dateSortie;
    }

    public Date getDatePay() {
        return datePay;
    }

    public void setDatePay(Date datePay) {
        this.datePay = datePay;
    }

    public boolean isPayed() {
        return payed;

    }


    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    public String getCode() {
        if(code==null)  {
            code = decryptCipher();
        }
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCrypted() {

        if(crypted==null){
            crypted = generateCipher();
        }

        return crypted;
    }

    public void setCrypted(String crypted) {
        this.crypted = crypted;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContreBon)) return false;

        ContreBon contreBon = (ContreBon) o;

        return getCode() != null ? getCode().equals(contreBon.getCode()) : contreBon.getCode() == null;
    }

    @Override
    public int hashCode() {
        return getCode() != null ? getCode().hashCode() : 0;
    }
}
