package com.expedia.homeaway.service.local;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.expedia.homeaway.data.model.SuggestedVenuesResponse;
import com.expedia.homeaway.data.model.VenueResponse;
import com.expedia.homeaway.data.model.VenuesResponse;
import com.expedia.homeaway.service.PlacesApi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;

public class PlacesApiLocal implements PlacesApi {

    private final String resourcesDirectory;

    public PlacesApiLocal(String resourcesDirectory) {
        System.out.println(resourcesDirectory);
        this.resourcesDirectory = resourcesDirectory;
    }

    public static String fetchJsonFromFile(String filePath) {
        System.out.println(filePath);
        Writer writer = new StringWriter();
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            char[] buffer = new char[1024];
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return writer.toString();
    }

    @Override
    public Observable<Result<VenuesResponse>> searchForVenues(String searchTerm) {
        return null;
    }

    @Override
    public Observable<Result<SuggestedVenuesResponse>> searchForSuggestedVenues(String searchTerm) {
        String jsonString = fetchJsonFromFile(resourcesDirectory + "/suggested_venues.json");
        return Observable.just(Result.response(Response.success(getGsonInstance().fromJson(jsonString, SuggestedVenuesResponse.class))));
    }

    @Override
    public Observable<Result<VenueResponse>> getVenue(String venueId) {
        String jsonString = fetchJsonFromFile(resourcesDirectory + "/single_venue.json");
        return Observable.just(Result.response(Response.success(getGsonInstance().fromJson(jsonString, VenueResponse.class))));
    }

    private Gson getGsonInstance() {
        return new GsonBuilder().create();
    }
}
