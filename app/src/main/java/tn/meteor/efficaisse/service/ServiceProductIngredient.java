package tn.meteor.efficaisse.service;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import tn.meteor.efficaisse.data.network.IngredientProduct;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by ahmed on 02/08/18.
 */

public interface ServiceProductIngredient {


    @POST(Constants.HTTP.ADD_PRODUCT_INGREDIENT)
    Observable<JsonObject> addIngredientProducts(@Body List<IngredientProduct> ingredientProductList, @Query("store") String registreDC);

    @PUT(Constants.HTTP.UPDATE_PRODUCT_INGREDIENT)
    Observable<JsonObject> updateIngredientProducts(@Body IngredientProduct ingredientProduct, @Query("store") String registreDC);

    @POST( Constants.HTTP.DELETE_PRODUCT_INGREDIENT)
    Observable<JsonObject> deleteIngredientProducts(@Body IngredientProduct ingredientProduct, @Query("store") String registreDC);
}
