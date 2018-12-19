package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import tn.meteor.efficaisse.model.Sync;
import tn.meteor.efficaisse.model.Sync_Table;


/**
 * Created by SKIIN on 05/02/2018.
 */

public class SyncRepository {


    public Sync find(Sync sync) {
       return SQLite.select().from(Sync.class).where(Sync_Table.id.eq(sync.getId())).querySingle();

    }


    public List<Sync> find(String id,String storeId,String className ){
        return SQLite.select().from(Sync.class).where(Sync_Table.idReference.eq(id)).and(Sync_Table.registerDC.eq(storeId)).and(Sync_Table.entity.eq(className)).queryList();
    }
    public Sync find(String id,String storeId ,String type,String className){
        return SQLite.select().from(Sync.class).where(Sync_Table.idReference.eq(id)).and(Sync_Table.registerDC.eq(storeId)).and(Sync_Table.type.eq(type)).and(Sync_Table.entity.eq(className)).querySingle();
    }
    public List<Sync> findIngredientProductByProduct(String id,String storeId ){
        return SQLite.select().from(Sync.class).where(Sync_Table.idReference.like("%"+id)).and(Sync_Table.registerDC.eq(storeId)).and(Sync_Table.entity.eq("Product_Ingredient")).queryList();
    }

    public void save(Sync sync){

        if(sync != null) {

            sync.save();
        }else{
            android.util.Log.i("fgfg","sync instance is null");
        }
    }

    public void delete(Sync sync){

        if(sync != null) {
            sync.delete();
        }
    }

    public List<Sync> findAll(){
        return SQLite.select().from(Sync.class).queryList();
    }

}
