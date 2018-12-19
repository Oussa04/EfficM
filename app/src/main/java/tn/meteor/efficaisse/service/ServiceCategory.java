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
 * Created by ahmed on 02/08/18.
 */

public interface ServiceCategory {

    @GET(Constants.HTTP.SHOW_CATEGORY)
    Observable<JsonArray> getCategories();

    @Multipart
    @POST(Constants.HTTP.ADD_CATEGORY)
    Observable<JsonObject> addCategory(@Part MultipartBody.Part  file, @Part("name") RequestBody name,@Part("store") RequestBody registerDC);

    @DELETE(Constants.HTTP.DELETE_CATEGORY)
    Observable<JsonObject> deleteCategory(@Path("name")String name, @Query("store")String registerDC);
    @Multipart
    @PUT(Constants.HTTP.UPDATE_CATEGORY)
    Observable<JsonObject> updateCategory(@Path("name")String name,@Part MultipartBody.Part  file,@Part("store") RequestBody registerDC);
}
