package com.expedia.homeaway.common.data;

import com.expedia.homeaway.data.model.SuggestedVenuesResponse;
import com.expedia.homeaway.data.model.VenueResponse;
import com.expedia.homeaway.data.model.VenuesResponse;
import com.expedia.homeaway.service.PlacesApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;

@Singleton
public class UserDataRepository {

    private final PlacesApi placesApi;

    @Inject
    public UserDataRepository(PlacesApi placesApi) {
        this.placesApi = placesApi;
    }

    public Observable<Result<VenuesResponse>> searchForVenues(String term) {
        return placesApi.searchForVenues(term);
    }

    public Observable<Result<SuggestedVenuesResponse>> suggestedSearchForVenues(String term) {
        return placesApi.searchForSuggestedVenues(term);
    }

    public Observable<Result<VenueResponse>> getVenue(String venueId) {
        return placesApi.getVenue(venueId);
    }

}
