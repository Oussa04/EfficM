package tn.meteor.efficaisse.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tn.meteor.efficaisse.data.network.SessionConverter;
import tn.meteor.efficaisse.model.Session;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by ahmed on 03/13/18.
 */

public interface ServiceSession {
    @POST(Constants.HTTP.ADD_SESSION)
    Observable<JsonObject> addSessions(@Body Session session,
                                        @Query("store") String storeRDC);

    @GET(Constants.HTTP.GET_SESSION)
    Observable<JsonArray> getSessions();
}
