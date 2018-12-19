package tn.meteor.efficaisse.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.Set;

import tn.meteor.efficaisse.model.Role;

/**
 * Created by SKIIN on 07/02/2018.
 */


@com.raizlabs.android.dbflow.annotation.TypeConverter
public class RoleConverter extends TypeConverter<String, Set> {


    @Override
    public String getDBValue(Set authorities) {

        if(authorities.contains("MANAGER")){
            return "MANAGER";
        }
        else{
            return "CASHIER";
        }
    }


    @Override
    public Set getModelValue(String data) {
        return null;
    }
}
