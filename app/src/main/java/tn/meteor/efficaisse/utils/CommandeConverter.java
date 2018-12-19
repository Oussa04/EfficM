package tn.meteor.efficaisse.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import tn.meteor.efficaisse.model.Commande;

/**
 * Created by lilk on 10/02/2018.
 */

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class CommandeConverter extends TypeConverter<Integer, Commande> {


    @Override
    public Integer getDBValue(Commande model) {
        return  model.getCommandeNumber();
    }

    @Override
    public Commande getModelValue(Integer data) {
      return new Commande(data);
    }
}
