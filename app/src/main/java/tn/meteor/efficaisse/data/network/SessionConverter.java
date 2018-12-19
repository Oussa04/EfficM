package tn.meteor.efficaisse.data.network;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;

import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.Cashier_Table;
import tn.meteor.efficaisse.model.Session;

/**
 * Created by ahmed on 03/14/18.
 */

public class SessionConverter {
    private Date date;
    private Date closed;
    private Cashier user;
    public Session toDbModel(){
        Session session = new Session();
        session.setDate(date);
        session.setClosed(closed);
        session.setUser(SQLite.select().from(Cashier.class).where(Cashier_Table.username.eq(user.getUsername())).querySingle());
        return session;
    }
}
