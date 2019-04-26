package com.expedia.homeaway.service.local;

import com.expedia.homeaway.data.model.SuggestedVenuesResponse;
import com.expedia.homeaway.data.model.Venue;
import com.expedia.homeaway.data.model.VenueResponse;

import org.junit.Test;

import java.util.List;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.observables.BlockingObservable;

import static org.assertj.core.api.Assertions.assertThat;

public class VenuesTest extends PlacesTestBase {

    @Test
    public void testSuggestedVenues() {
        Observable<Result<SuggestedVenuesResponse>> observable = getPlaces().searchForSuggestedVenues("cof");
        BlockingObservable<Result<SuggestedVenuesResponse>> bo = BlockingObservable.from(observable);
        SuggestedVenuesResponse venuesResponse = bo.first().response().body();
        assertThat(venuesResponse).isNotNull();
        assertThat(venuesResponse.getResponse().getVenues().size()).isEqualTo(20);

        List<Venue> suggestedVenues = venuesResponse.getResponse().getVenues();
        Venue venue = suggestedVenues.get(0);
        assertThat(venue).isNotNull();

        assertThat(venue.getId()).isEqualTo("52d456c811d24128cdd7bc8b");
        assertThat(venue.getName()).isEqualTo("Storyville Coffee Company");
        assertThat(venue.getLocation()).isNotNull();
        assertThat(venue.getCategories().size()).isEqualTo(1);
        assertThat(venue.getCategories().get(0).getId()).isEqualTo("4bf58dd8d48988d1e0931735");
        assertThat(venue.getCategories().get(0).getName()).isEqualTo("Coffee Shop");
    }

    @Test
    public void testGetVenue() {
        Observable<Result<VenueResponse>> observable = getPlaces().getVenue("cof");
        BlockingObservable<Result<VenueResponse>> bo = BlockingObservable.from(observable);
        VenueResponse venuesResponse = bo.first().response().body();
        assertThat(venuesResponse).isNotNull();

        Venue venue = venuesResponse.getSingleVenueResponse().getVenue();
        assertThat(venue).isNotNull();

        assertThat(venue.getId()).isEqualTo("52d456c811d24128cdd7bc8b");
        assertThat(venue.getName()).isEqualTo("Storyville Coffee Company");
        assertThat(venue.getLocation()).isNotNull();
        assertThat(venue.getCategories().size()).isEqualTo(1);
        assertThat(venue.getCategories().get(0).getId()).isEqualTo("4bf58dd8d48988d1e0931735");
        assertThat(venue.getCategories().get(0).getName()).isEqualTo("Coffee Shop");
    }
}
