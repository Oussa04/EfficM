package tn.meteor.efficaisse.data.network;

import tn.meteor.efficaisse.model.Category;

/**
 * Created by SKIIN on 11/02/2018.
 */

public class CategoryConverterModel {


    private String photo;
    private Id id;


    private class Id{
       String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Id(String name) {
            this.name = name;
        }


    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public CategoryConverterModel() {
    }

    public CategoryConverterModel(String photo, Id id) {
        this.photo = photo;
        this.id = id;
    }

    public Category toDBModel(CategoryConverterModel converter){

        Category category = new Category();
        category.setName(converter.id.name);
        category.setPhoto(converter.getPhoto());
        return category;

    }


}
