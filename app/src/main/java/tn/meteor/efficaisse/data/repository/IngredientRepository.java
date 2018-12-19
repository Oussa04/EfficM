package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.History;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Ingredient_Table;


/**
 * Created by lilk on 28/01/2018.
 */

public class IngredientRepository {

    private SyncRepository syncRepository = new SyncRepository();

    public Ingredient find(Ingredient ingredient) {
        return SQLite.select().from(Ingredient.class).where(Ingredient_Table.id.eq(ingredient.getId())).querySingle();
    }


    public void delete(Ingredient ingredient) {
        Ingredient ing = find(ingredient);
        if (ing != null) {
            ing.delete();

            LogSystem.persist(ingredient, "delete", "Ingredient", false);
        }

    }

    public static List<Ingredient> findAll() {
        return SQLite.select().from(Ingredient.class).queryList();
    }

    public List<Ingredient> findIngredientsByName(String name) {
        if (name.isEmpty()) {
            return findAll();
        } else {
            return SQLite.select().from(Ingredient.class).where(Ingredient_Table.name.like("%" + name + "%")).queryList();
        }
    }

    public void save(Ingredient ingredient) {
        if (ingredient != null) {
            ingredient.save();
            LogSystem.persist(ingredient, "save", "Ingredient", true);


            //MAX THABBET BRASS OMMEK
            History history = new History();
            history.setDate(new Date());
            history.setName(ingredient.getName());
            history.setQuantity(ingredient.getStockQuantity());
            history.setType("Ingredient");
            HistoryRepository historyRepository = new HistoryRepository();
            historyRepository.save(history);

        }
    }

    public void update(Ingredient ingredient, boolean imageUpdated) {


        LogSystem.persist(ingredient, "update", "Ingredient", imageUpdated);

        ingredient.update();
    }

}
