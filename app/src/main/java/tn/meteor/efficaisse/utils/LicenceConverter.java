package tn.meteor.efficaisse.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import tn.meteor.efficaisse.model.Licence;


/**
 * Created by SKIIN on 07/02/2018.
 */

public class LicenceConverter extends TypeConverter<Integer, Licence> {


    @Override
    public Integer getDBValue(Licence licence) {
        if(licence != null){
        return licence.getId();}
        else{
            return 0;
        }
    }

    @Override
    public Licence getModelValue(Integer id) {
        return new Licence(id);
    }
}
