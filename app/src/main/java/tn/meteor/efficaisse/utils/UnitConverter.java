package tn.meteor.efficaisse.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;
import tn.meteor.efficaisse.model.Unit;

/**
 * Created by SKIIN on 28/01/2018.
 */


@com.raizlabs.android.dbflow.annotation.TypeConverter
public class UnitConverter  extends TypeConverter<String, Unit> {


    @Override
    public String getDBValue(Unit unit) {
        return String.valueOf(unit);
    }

    @Override
    public Unit getModelValue(String data) {

        Unit u1 = null;
        if (data == null) {
            return null;

        }
        for (Unit u : Unit.values()) {
            if (u.getId().equals(data)) {
                u1 = u;
            }
        }
        return u1;
    }

}
