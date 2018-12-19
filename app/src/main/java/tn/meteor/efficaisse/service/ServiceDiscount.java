package tn.meteor.efficaisse.service;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import tn.meteor.efficaisse.data.network.DiscountConverter;
import tn.meteor.efficaisse.utils.Constants;

public interface ServiceDiscount {
    @POST(Constants.HTTP.ADD_DISCOUNT)
    Observable<JsonObject> addDiscount(@Body DiscountConverter discountConverter);
    @POST(Constants.HTTP.UPDATE_DISCOUNT)
    Observable<JsonObject> updateDiscount(@Body DiscountConverter discountConverter);
    @GET(Constants.HTTP.GET_DISCOUNT)
    Observable<List<DiscountConverter>> getDiscount();
}
