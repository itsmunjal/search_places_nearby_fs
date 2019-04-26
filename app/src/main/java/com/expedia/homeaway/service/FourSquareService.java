package com.expedia.homeaway.service;

import com.expedia.homeaway.data.model.SuggestedVenuesResponse;
import com.expedia.homeaway.data.model.VenueResponse;
import com.expedia.homeaway.data.model.VenuesResponse;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


public interface FourSquareService {

    @GET("venues/search?intent=checkin&limit=25")
    Observable<Result<VenuesResponse>> search(@Query("query") String queryTerm);

    @GET("venues/suggestcompletion?intent=checkin&limit=50")
    Observable<Result<SuggestedVenuesResponse>> suggestCompletion(@Query("query") String queryTerm);

    @GET("venues/{venue_id}")
    Observable<Result<VenueResponse>> venue(@Path("venue_id") String venueId);

}
