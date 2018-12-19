package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.model.LoungeTable_Table;

/**
 * Created by lilk on 10/03/2018.
 */

public class LoungTablesRepository {


    public LoungeTable find(LoungeTable loungeTable) {
        return SQLite.select().from(LoungeTable.class).where(LoungeTable_Table.id.eq(loungeTable.getId())).querySingle();
    }
    public List<LoungeTable> findAll() {
        return SQLite.select().from(LoungeTable.class).queryList();
    }


    public void save(LoungeTable loungeTable) {

        loungeTable.save();
        LogSystem.persist(loungeTable, "save", "LoungeTable", false);
    }


    public void update(LoungeTable loungeTable) {
        loungeTable.update();


    }



}
