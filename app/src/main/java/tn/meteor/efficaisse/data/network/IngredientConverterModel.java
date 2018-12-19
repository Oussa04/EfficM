package tn.meteor.efficaisse.data.network;

import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.model.Unit;

/**
 * Created by SKIIN on 11/02/2018.
 */

public class IngredientConverterModel {


    private Id id;
    private String name;
    private float price;
    private String photo;
    private Unit unit;
    private float stockQuantity;


    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public float getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(float stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    private class Id{
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Id(int id) {
            this.id = id;
        }
    }

    public Ingredient toDBModel(IngredientConverterModel ingredientConverterModel){

        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientConverterModel.getId().id);
        ingredient.setName(ingredientConverterModel.getName());
        ingredient.setPhoto(ingredientConverterModel.getPhoto());
        ingredient.setPrice(ingredientConverterModel.getPrice());
        ingredient.setStockQuantity(ingredientConverterModel.getStockQuantity());
        ingredient.setUnit(ingredientConverterModel.getUnit());

        return ingredient;
    }



}
