package tn.meteor.efficaisse.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.Cashier_Table;
import tn.meteor.efficaisse.model.CustomerGroup;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class CashierConverter extends TypeConverter<String , Cashier> {
    @Override
    public String getDBValue(Cashier model) {
        return model.getUsername();
    }

    @Override
    public Cashier getModelValue(String data) {
        return SQLite.select().from(Cashier.class).where(Cashier_Table.username.eq(data)).querySingle();
       
    }
}
