package tn.meteor.efficaisse.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tn.meteor.efficaisse.model.ContreBon;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by ahmed on 03/11/18.
 */

public interface ServiceCustomer {
    @POST(Constants.HTTP.ADD_CUSTOMER_GROUP)
    Observable<JsonObject> addCustomerGroup(@Body CustomerGroup group,
                                            @Query("store") String storeRDC);

    @POST(Constants.HTTP.MODIFY_CUSTOMER_GROUP)
    Observable<JsonObject> updateCustomerGroup(@Body CustomerGroup group,
                                               @Query("store") String storeRDC);

    @DELETE(Constants.HTTP.DELETE_CUSTOMER_GROUP)
    Observable<JsonObject> deleteCustomerGroup(@Query("group") String group,
                                               @Query("store") String storeRDC);

    @GET(Constants.HTTP.GET_CUSTOMER_GROUPS)
    Observable<JsonArray> getGroups();

    @POST(Constants.HTTP.ADD_CUSTOMER)
    Observable<JsonObject> addCustomer(@Body Customer customer, @Query("group") String group,
                                       @Query("store") String storeRDC);

    @POST(Constants.HTTP.UPDATE_CUSTOMER)
    Observable<JsonObject> updateCustomer(@Body Customer customer, @Query("group") String group,
                                          @Query("store") String storeRDC);
    @GET(Constants.HTTP.GET_CUSTOMERS)
    Observable<JsonArray> getCustomers();
}
