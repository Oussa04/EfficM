package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.ContreBon;
import tn.meteor.efficaisse.model.ContreBon_Table;

/**
 * Created by ahmed on 02/18/18.
 */

public class ContreBonRepository {

    public static ContreBon findByCode(String code) {
        return SQLite.select().from(ContreBon.class).where(ContreBon_Table.code.eq(code)).querySingle();
    }

    public static void save(ContreBon contreBon) {
       contreBon.save();
       LogSystem.persist(contreBon,"save","ContreBon",false);
    }

    public static void update(ContreBon contreBon){
        contreBon.update();
        LogSystem.persist(contreBon,"update","ContreBon",false);
    }

}
