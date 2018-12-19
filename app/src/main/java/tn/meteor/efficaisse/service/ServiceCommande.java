package tn.meteor.efficaisse.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tn.meteor.efficaisse.data.network.CommandeConverterModel;
import tn.meteor.efficaisse.data.network.CommandeDetailModel;
import tn.meteor.efficaisse.data.network.PaymentModel;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by SKIIN on 08/02/2018.
 */

public interface ServiceCommande {


    @POST(Constants.HTTP.ADD_COMMANDE)
    Observable<JsonObject> addCommande(@Query("username")String username,
                                       @Body CommandeConverterModel ccm,
                                       @Query("commande") int commandeNumber,@Query("store") String storeRDC);

    @POST(Constants.HTTP.ADD_PAYMENT)
    Observable<JsonObject> addPayment(@Body List<PaymentModel> payments,
                                       @Query("store") String storeRDC,@Query("commande")int commandeNumber);

    @GET(Constants.HTTP.GET_COMMANDE)
    Observable<JsonArray> getCommande();


}
