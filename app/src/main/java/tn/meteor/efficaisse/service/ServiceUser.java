package tn.meteor.efficaisse.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tn.meteor.efficaisse.model.Cashier;
import tn.meteor.efficaisse.model.User;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by SKIIN on 22/01/2018.
 */

public interface ServiceUser {

    @POST(Constants.HTTP.ACCESS_TOKEN)
    @FormUrlEncoded
    Observable<JsonObject> credentials(@Field("username") String username,@Field("password")
            String pass,@Field("grant_type") String password, @Header(("Authorization")) String cre);

    @POST(Constants.HTTP.ACCESS_TOKEN)
    @FormUrlEncoded
    Observable<JsonObject> refreshToken(@Field("refresh_token") String token,@Field("grant_type") String password, @Header(("Authorization")) String cre);


    @GET(Constants.HTTP.CURRENT_USER)
    Observable<JsonObject> getCurrentUser();
    @GET(Constants.HTTP.GET_USERS)
    Observable<JsonArray> getUsers();

    @GET(Constants.HTTP.CURRENT_KEY)
    Observable<JsonObject> getKey();

    @POST(Constants.HTTP.ADD_CASHIER)
    Observable<JsonObject> addCashier(@Body Cashier cashier);

    @GET(Constants.HTTP.CHANGE_PATTERN)
    Observable<JsonObject> changePatten(@Query("pattern")String pattern,@Query("username") String username,@Query("store") String storeRDC);
}
