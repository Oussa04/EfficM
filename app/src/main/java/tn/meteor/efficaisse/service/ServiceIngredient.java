package tn.meteor.efficaisse.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by SKIIN on 07/02/2018.
 */

public interface ServiceIngredient {

    @GET(Constants.HTTP.GET_INGREDIENTS)
    Observable<JsonArray> getIngredients();

    @Multipart
    @POST(Constants.HTTP.ADD_INGREDIENT)
    Observable<JsonObject> addIngredient(@Part MultipartBody.Part  file, @Part("name") RequestBody name, @Part("cost") RequestBody cost,
                                         @Part("quantity") RequestBody quantity,@Part("unit") RequestBody unit,
                                        @Part("id") RequestBody id, @Part("store") RequestBody registerDC);
    @DELETE(Constants.HTTP.DELETE_INGREDIENT)
    Observable<JsonObject> deleteIngredient(@Path("id")int id, @Query("store")String registerDC);

    @Multipart
    @PUT(Constants.HTTP.UPDATE_INGREDIENT)
    Observable<JsonObject> updateIngredient(@Part MultipartBody.Part  file, @Part("name") RequestBody name, @Part("cost") RequestBody cost,
                                         @Part("quantity") RequestBody quantity,@Part("unit") RequestBody unit,
                                          @Part("id") RequestBody id, @Part("store") RequestBody registerDC);
}
