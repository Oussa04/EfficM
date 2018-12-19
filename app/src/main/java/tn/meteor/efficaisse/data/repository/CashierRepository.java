package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.property.PropertyFactory;

import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.Cashier_Table;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Commande_Table;
import tn.meteor.efficaisse.model.Session;
import tn.meteor.efficaisse.model.Session_Table;
import tn.meteor.efficaisse.model.User;

public  class CashierRepository {


    public static Cashier find(String username) {
        return SQLite.select().from(Cashier.class).where(Cashier_Table.username.eq(username)).querySingle();
    }
    public static boolean save(Cashier user) {
        if (find(user.getUsername())== null){
            user.getPattern();
            user.save();
            LogSystem.persist(user, "save", "Cashier", false);
            return true;
        }
        return false;

    }


    public static void update(Cashier user) {
        LogSystem.persist(user, "update", "Cashier", false);
        user.getPattern();
        user.update();
    }

    public static List<Cashier> findAll() {
        return SQLite.select().from(Cashier.class).queryList();
    }
    public static boolean canLogout(){
        Session session = SQLite.select().from(Session.class).where(Session_Table.id.greaterThan(-20)).orderBy(Session_Table.date,false).querySingle();
        List<Commande> list = SQLite.select().from(Commande.class).where(Commande_Table.date.greaterThanOrEq(session.getDate())).queryList();
        for (Commande c :
           list  ) {
            if (!c.isStatus())
                return false;
        }
        return true;

    }
}
