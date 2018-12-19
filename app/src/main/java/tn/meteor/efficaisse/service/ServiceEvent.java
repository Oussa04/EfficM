package tn.meteor.efficaisse.service;

import com.google.gson.JsonObject;

import io.reactivex.Observable;

import retrofit2.http.GET;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by Oussa on 22-Apr-18.
 */

public interface ServiceEvent {


    @GET(Constants.HTTP.GET_WORLDCUP_FiXTURES)
    Observable<JsonObject> getWorldcupFixtures();

    @GET(Constants.HTTP.GET_CHAMPIONSLEAGUE_FiXTURES)
    Observable<JsonObject> getChampionsLeagueFixtures();

}
