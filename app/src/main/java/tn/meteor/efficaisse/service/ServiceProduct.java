package tn.meteor.efficaisse.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by SKIIN on 22/01/2018.
 */

public interface ServiceProduct {



    @GET(Constants.HTTP.GET_PRODUCTS)
    Observable<JsonArray> getProducts();



    @Multipart
    @POST(Constants.HTTP.ADD_PRODUCT)
    Observable<JsonObject> addProduct(@Part MultipartBody.Part  file,
                                @Part("name") RequestBody name,
                                @Part("categoryName") RequestBody category,
                                @Part("favoris")  RequestBody favoris,
                                @Part("cost") RequestBody cost,
                                @Part("quantity") RequestBody quantity,
                                @Part("price") RequestBody price,
                                @Part("id") RequestBody id,
                                @Part("store") RequestBody registerDC);


    @Multipart
    @PUT(Constants.HTTP.UPDATE_PRODUCT)
    Observable<JsonObject> updateProduct(@Part MultipartBody.Part  file,
                                         @Part("name") RequestBody name,
                                         @Part("favoris")  RequestBody favoris,
                                         @Part("cost") RequestBody cost,
                                         @Part("quantity") RequestBody quantity,
                                         @Part("price") RequestBody price,
                                         @Part("id") RequestBody id,
                                         @Part("store") RequestBody registerDC);



    @DELETE(Constants.HTTP.DELETE_PRODUCT)
    Observable<JsonObject> deleteProduct(@Path("id") Integer id,@Query("store") String store);


}
