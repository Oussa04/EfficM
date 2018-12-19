package tn.meteor.efficaisse.utils;


import com.raizlabs.android.dbflow.converter.TypeConverter;

import tn.meteor.efficaisse.model.Store;


/**
 * Created by SKIIN on 07/02/2018.
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class StoreConverter  extends TypeConverter<Integer, Store> {


    @Override
    public Integer getDBValue(Store store) {
        return store.getId();
    }

    @Override
    public Store getModelValue(Integer id) {
        return new Store(id);
    }
}
