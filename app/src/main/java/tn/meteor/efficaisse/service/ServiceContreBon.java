package tn.meteor.efficaisse.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tn.meteor.efficaisse.model.ContreBon;
import tn.meteor.efficaisse.model.History;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by ahmed on 02/18/18.
 */

public interface ServiceContreBon {

    @POST(Constants.HTTP.ADD_CONTREBON)
    Observable<JsonObject> addContreBon(@Body List<ContreBon> contreBons,
                                        @Query("store") String storeRDC);

    @GET(Constants.HTTP.GET_CONTREBON)
    Observable<JsonArray> getContreBon();
}
