package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Session;
import tn.meteor.efficaisse.model.Session_Table;

/**
 * Created by ahmed on 03/13/18.
 */

public class SessionRepository {
    public static List<Session> findAll(){
        return SQLite.select().from(Session.class).orderBy(Session_Table.date,false).queryList();
    }
    public static void open(Session s){
        s.setDate(new Date());
        s.save();
    }
    public static void close(){
        Session s = SQLite.select().from(Session.class).orderBy(Session_Table.date,false).querySingle();
        s.setClosed(new Date());
        s.update();
        LogSystem.persist(s, "save", "Session", false);
    }

}
