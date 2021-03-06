package com.mifos.utils.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.mifos.objects.Center;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Page;
import com.mifos.objects.client.PageItem;
import com.mifos.utils.Constants;
import retrofit.*;
import retrofit.client.*;
import retrofit.http.*;

import java.util.Iterator;
import java.util.List;

public class API {
    public static String url = "https://demo.openmf.org/mifosng-provider/api/v1";

    static RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(url)
            .setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Accept", "application/json");
                    request.addHeader("Content-Type", "application/json");
                    request.addHeader("X-Mifos-Platform-TenantId", "default");

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Constants.applicationContext);
                    String authToken = pref.getString(User.AUTHENTICATION_KEY, "NA");

                    if (authToken != null && !"NA".equals(authToken)) {
                        request.addHeader("Authorization", authToken);
                    }
                }
            })
            .setErrorHandler(new MifosRestErrorHandler())
            .build();

    static {
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

    }

     static class MifosRestErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError retrofitError) {

            Response r = retrofitError.getResponse();
            if (r != null && r.getStatus() == 401) {
                Log.e("Status", "Authentication Error.");


            }else if(r.getStatus() == 400){
                Log.d("Status","Bad Request - Invalid Parameter or Data Integrity Issue.");
                Log.d("URL", r.getUrl());
                List<retrofit.client.Header> headersList = r.getHeaders();
                Iterator<retrofit.client.Header> iterator = headersList.iterator();
                while(iterator.hasNext())
                {    retrofit.client.Header header = iterator.next();
                    Log.d("Header ",header.toString());
                }
            }


            return retrofitError;
        }


    }

    public interface CenterService {

        @GET("/centers")
        public void getAllCenters(Callback<List<Center>> callback);


    }

    public static CenterService centerService = restAdapter.create(CenterService.class);

    public interface ClientAccountsService {

        @GET("/clients/{clientId}/accounts")
        public void getAllAccountsOfClient(@Path("clientId") int clientId, Callback<ClientAccounts> callback);

    }

    public static ClientAccountsService clientAccountsService = restAdapter.create(ClientAccountsService.class);

    public interface ClientService {


        /**
         * @param callback - Callback to handle the response and/or error
         */
        @GET("/clients")
        public void listAllClients(Callback<Page> callback);


        /**
         * @param clientId - ID of the client
         * @param callback - Callback to handle the response and/or error
         */
        @GET("/clients/{clientId}")
        public void getClient(@Path("clientId") int clientId, Callback<PageItem> callback);


    }

    public static ClientService clientService = restAdapter.create(ClientService.class);

    public interface SearchService {

        @GET("/search?resource=clients")
        public void searchClientsByName(@Query("query") String clientName, Callback<List<SearchedEntity>> callback);


    }

    public static SearchService searchService = restAdapter.create(SearchService.class);

    public interface UserAuthService {

        @POST("/authentication")
        public void authenticate(@Query("username") String username, @Query("password") String password, Callback<User> userCallback);
    }

    public static UserAuthService userAuthService = restAdapter.create(UserAuthService.class);


    public static <T> Callback<T> getCallback(T t) {
        Callback<T> cb = new Callback<T>() {
            @Override
            public void success(T o, Response response) {
                System.out.println("Object " + o);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                System.out.println("Error: " + retrofitError);
            }
        };

        return cb;
    }


    public static <T> Callback<List<T>> getCallbackList(List<T> t) {
        Callback<List<T>> cb = new Callback<List<T>>() {
            @Override
            public void success(List<T> o, Response response) {
                System.out.println("Object " + o);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                System.out.println("Error: " + retrofitError);
            }
        };

        return cb;
    }
}

