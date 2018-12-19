package tn.meteor.efficaisse.data.network;

import tn.meteor.efficaisse.model.Product_Ingredient;

/**
 * Created by ahmed on 02/08/18.
 */

public class IngredientProduct {

    private Id id;
    private double quantity;

    private class Id{
        ProductId productId;
        ProductId ingredientId;

        private Id() {
            productId = new ProductId();
            ingredientId = new ProductId();
        }
    }

    private IngredientProduct() {
        id = new Id();
    }

    private class ProductId{
        int id;
    }


    public Product_Ingredient toDbModel(){
        Product_Ingredient product_ingredient = new Product_Ingredient();
        product_ingredient.setIngredient_id(id.ingredientId.id);
        product_ingredient.setProduct_id(id.productId.id);
        product_ingredient.setQuantity(quantity);
        return product_ingredient;
    }

    public static  IngredientProduct fromDbModel(Product_Ingredient pi){
        IngredientProduct ip = new IngredientProduct();
        ip.quantity = pi.getQuantity();
        ip.id.ingredientId.id=pi.getIngredient_id();
        ip.id.productId.id=pi.getProduct_id();
        return ip;
    }
}
