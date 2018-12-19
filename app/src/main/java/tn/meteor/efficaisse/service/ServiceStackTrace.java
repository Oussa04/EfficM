package tn.meteor.efficaisse.service;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.model.StackTrace;
import tn.meteor.efficaisse.utils.Constants;

public interface ServiceStackTrace {

    @POST(Constants.HTTP.ADD_STACKTRACE)
    Observable<JsonObject> addStackTrace(@Body List<StackTrace> stack);
    @POST(Constants.HTTP.ADD_TABLE)
    Observable<JsonObject> addTable(@Body List<LoungeTable> loungeTables, @Query("store") String store);
    @GET(Constants.HTTP.GET_TABLE)
    Observable<List<LoungeTable>> getTables();
}
