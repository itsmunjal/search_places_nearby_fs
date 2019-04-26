package com.expedia.homeaway.service;

import com.expedia.homeaway.data.model.SuggestedVenuesResponse;
import com.expedia.homeaway.data.model.VenueResponse;
import com.expedia.homeaway.data.model.VenuesResponse;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;


public interface PlacesApi {
    int READ_TIMEOUT = 20000;

    Observable<Result<VenuesResponse>> searchForVenues(String searchTerm);

    Observable<Result<SuggestedVenuesResponse>> searchForSuggestedVenues(String searchTerm);

    Observable<Result<VenueResponse>> getVenue(String venueId);
}
