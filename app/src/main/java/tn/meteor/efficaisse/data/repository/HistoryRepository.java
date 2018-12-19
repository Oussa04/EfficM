package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.model.History;
import tn.meteor.efficaisse.model.History_Table;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;

/**
 * Created by ahmed on 02/17/18.
 */

public class HistoryRepository {
    public List<History> find(Ingredient ingredient) {
        return SQLite.select().from(History.class).where(History_Table.type.eq("Ingredient")).and(History_Table.name.eq(ingredient.getName())).orderBy(History_Table.date, true).queryList();

    }

    public List<History> find(Product product) {
        return SQLite.select().from(History.class).where(History_Table.type.eq("Product")).and(History_Table.name.eq(product.getName())).orderBy(History_Table.date, true).queryList();
    }

    public void save(History history) {
        PreferencesHelper prefs = new AppPreferencesHelper(EfficaisseApplication.getInstance().getApplicationContext(), Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
        if (history != null) {
            history.setDate(new Date());
            history.setUsername(prefs.getUsername());

            history.save();

            LogSystem.persist(history, "save", "History", false);

        }
    }


}
