package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import tn.meteor.efficaisse.data.network.Credential;
import tn.meteor.efficaisse.data.network.Credential_Table;

public  class CredentialsRepository {


    public static Credential find(String username) {
        return SQLite.select().from(Credential.class).where(Credential_Table.username.eq(username)).querySingle();
    }

}
