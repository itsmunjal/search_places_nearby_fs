package com.expedia.homeaway.search.ui;

import com.expedia.homeaway.data.model.Venue;

import java.util.List;

public interface VenuesContract {

    interface View {
        void onSearch(List<Venue> venues);
        void onSuggestedSearches(List<String> suggestedSearches);
        void onVenue(Venue venue);
        void onError();
    }

    interface Presenter {
        void bindView(VenuesContract.View view);
        void unBindView();
        void doSearch(String term);
        void doSuggestedSearch(String searchTerm);
        void doGetVenue(String venueId);
    }
}

