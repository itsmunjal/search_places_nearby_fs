package com.expedia.homeaway.service.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.expedia.homeaway.data.model.SuggestedVenuesResponse;
import com.expedia.homeaway.data.model.VenueResponse;
import com.expedia.homeaway.data.model.VenuesResponse;
import com.expedia.homeaway.service.PlacesApi;
import com.expedia.homeaway.service.FourSquareService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.Result;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;


public class PlacesApiRetrofit implements PlacesApi {

    private final Retrofit retrofit;
    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;
    private final String currentLatLong;
    private final String apiVersion;

    private CallAdapter.Factory callAdapterFactory;


    public PlacesApiRetrofit(String baseUrl, String apiVersion, String clientId, String clientSecret, String currentLatLong) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.currentLatLong = currentLatLong;
        this.apiVersion = apiVersion;

        retrofit = newInstance(baseUrl);
    }

    private Retrofit newInstance(String baseUrl) {
        Gson gson = getGsonInstance();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();

                        HttpUrl.Builder urlBuilder = request.url().newBuilder();

                        urlBuilder.addQueryParameter("v", apiVersion).build();
                        urlBuilder.addQueryParameter("client_id", clientId).build();
                        urlBuilder.addQueryParameter("client_secret", clientSecret).build();
                        urlBuilder.addQueryParameter("ll", currentLatLong).build();

                        request = request.newBuilder().url(urlBuilder.build()).build();

                        return chain.proceed(request);
                    }
                })
                .readTimeout(PlacesApi.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        callAdapterFactory = PlacesRxJavaCallAdapterFactory.create();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(callAdapterFactory)
                .build();

    }

    private Gson getGsonInstance() {
        return new GsonBuilder().create();
    }


    @Override
    public Observable<Result<VenuesResponse>> searchForVenues(String searchTerm) {
        return retrofit.create(FourSquareService.class).search(searchTerm);
    }

    @Override
    public Observable<Result<SuggestedVenuesResponse>> searchForSuggestedVenues(String searchTerm) {
        return retrofit.create(FourSquareService.class).suggestCompletion(searchTerm);
    }

    @Override
    public Observable<Result<VenueResponse>> getVenue(String venueId) {
        return retrofit.create(FourSquareService.class).venue(venueId);
    }
}
