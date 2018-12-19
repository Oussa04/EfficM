package tn.meteor.efficaisse.utils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import tn.meteor.efficaisse.model.Category;

/**
 * Created by SKIIN on 27/01/2018.
 */

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class CategoryConverter extends TypeConverter<String, Category> {


    @Override
    public String getDBValue(Category category) {
        return category.getName();
    }

    @Override
    public Category getModelValue(String data) {
        return new Category(data);
    }
}
