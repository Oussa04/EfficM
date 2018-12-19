package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import tn.meteor.efficaisse.model.DetailCommandeIngredient;
import tn.meteor.efficaisse.model.DetailCommandeIngredient_Table;
import tn.meteor.efficaisse.model.Ingredient;

public class DetailCommandeIngredientRepository {
    public static List<DetailCommandeIngredient> findByIngredient(Ingredient i) {
       return SQLite.select().from(DetailCommandeIngredient.class).where(DetailCommandeIngredient_Table.ingredientName.eq(i.getName())).queryList();
    }
}
