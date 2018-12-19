package tn.meteor.efficaisse.model;

import com.facebook.stetho.json.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import tn.meteor.efficaisse.data.db.EfficaisseDatabase;
import tn.meteor.efficaisse.utils.StoreConverter;

@Table(database = EfficaisseDatabase.class)
public class Cashier extends BaseModel{
    @PrimaryKey(autoincrement = true)
    private long id;
    @Column
    private String name;

    @Column
    private String username;

    @Column
    private String pattern;

    private Long schema;
    public long getId() {
        return id;
    }

    public void setId(long id) {
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


    public String getPattern() {

        if(pattern == null)

            pattern = generateCipher();
        return pattern;
    }

    public void setSchema(Long schema) {
        this.schema = schema;
    }
    public boolean isValid() {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(schema.toString().getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString().equals(pattern);

        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }


    private String generateCipher() {
        if (schema == null)
            return null;
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(schema.toString().getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = String.format("0%s", h);
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            return null;
        }

    }
}
