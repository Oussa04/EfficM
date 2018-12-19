package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import tn.meteor.efficaisse.model.Store;


/**
 * Created by SKIIN on 07/02/2018.
 */

public class StoreRepository {


    public String getRDCStore(){

        Store store = SQLite.select().from(Store.class).querySingle();
        if(store != null) {
            return store.getRegisterDC();
        }
        else {
            return null;
        }
    }


    public Store getActualStore(){

        return SQLite.select().from(Store.class).querySingle();

    }

}
