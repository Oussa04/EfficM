package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.User;

/**
 * Created by SKIIN on 07/02/2018.
 */

public class UserRepository {


    public User find() {
        return SQLite.select().from(User.class).querySingle();
    }


    public void save(User user) {
        user.save();
    }


    public void update(User user) {
        LogSystem.persist(user, "update", "User", false);
        user.update();
    }

    public List<User> findAll() {
        return SQLite.select().from(User.class).queryList();
    }



}
