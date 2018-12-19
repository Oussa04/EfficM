package tn.meteor.efficaisse.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.model.CustomerGroup_Table;

/**
 * Created by lilk on 10/03/2018.
 */

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class CustomerGroupConverter extends TypeConverter<String, CustomerGroup> {


    @Override
    public String getDBValue(CustomerGroup customerGroup) {
        return customerGroup.getName();
    }

    @Override
    public CustomerGroup getModelValue(String data) {
        return SQLite.select().from(CustomerGroup.class).where(CustomerGroup_Table.name.eq(data)).querySingle();
    }
}
