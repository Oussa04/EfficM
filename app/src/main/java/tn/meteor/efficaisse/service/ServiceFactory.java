package tn.meteor.efficaisse.service;



import android.util.Base64;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tn.meteor.efficaisse.data.preferences.AppPreferencesHelper;
import tn.meteor.efficaisse.data.preferences.PreferencesHelper;
import tn.meteor.efficaisse.utils.Constants;
import tn.meteor.efficaisse.utils.EfficaisseApplication;


public class ServiceFactory {

    private static OkHttpClient httpClient;
    private static OkHttpClient getInstance(){
        if(httpClient == null){
            httpClient =new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();
        }
        return httpClient;
    }
    private static Retrofit retrofit = null;
    private static PreferencesHelper preferencesHelper= new AppPreferencesHelper(EfficaisseApplication.getInstance().getApplicationContext(),Constants.SHARED_PREFERENCES_NAME.PREFERENCES_USER);
    public static Retrofit getApiClient(){

        if(retrofit == null){
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();
            retrofit = new Retrofit
                    .Builder().client(new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .build())
                    .baseUrl(Constants.HTTP.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
     final  static class AuthInterceptor implements Interceptor {
        private String username="efficaiseAuthorization",pass="eff20186978";

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            if(request.headers().get("Authorization") == null){

                request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer "+preferencesHelper.getAccessToken())
                        .build();

            }


            Response response = chain.proceed(request);
            if(response.code()==401 ) {
                if(response.body().string().contains("Access token expired:")){

                synchronized (ServiceFactory.getInstance()) { //perform all 401 in sync blocks, to avoid multiply token updates
                    String refresh = preferencesHelper.getRefreshToken() ; //get currently stored token
                    String credential = username + ":" + pass;
                    final String basic =
                            "Basic " + Base64.encodeToString(credential.getBytes(), Base64.NO_WRAP);
                    RequestBody refreshBody = new FormBody.Builder().add("refresh_token",refresh).add("grant_type","refresh_token").build();
                    Request refreshRequest = new Request.Builder()
                            .addHeader("Authorization",basic).url(Constants.HTTP.BASE_URL+Constants.HTTP.ACCESS_TOKEN).post(refreshBody).build();
                    Response refreshResponse = ServiceFactory.getInstance().newCall(refreshRequest).execute();
                    ResponseBody responseBodyCopy = refreshResponse.peekBody(Long.MAX_VALUE);

                    String responseref =responseBodyCopy.string();

                    if(refreshResponse.code()!=200){
                        EfficaisseApplication.getInstance().logout(preferencesHelper.getAccessToken());
                    }else{
                        Gson gson = new Gson();
                        tn.meteor.efficaisse.data.network.Response token = gson.fromJson(responseref, tn.meteor.efficaisse.data.network.Response.class);
                        preferencesHelper.setAccesToken(token.getAccess_token());
                        preferencesHelper.setRefreshToken(token.getRefresh_token());
                }
                    request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer "+preferencesHelper.getAccessToken())
                            .build();
                    response =  chain.proceed(request);
                    if(response.code() == 401){
                        EfficaisseApplication.getInstance().logout(preferencesHelper.getAccessToken());
                    }else{
                        return response;
                    }

                }
                }else{
                    Toast.makeText(EfficaisseApplication.getInstance().getContext(), "Veuillez reconnecter pour se benificer des service enligne", Toast.LENGTH_LONG).show();
                }
            }


            return response;
        }
    }
}