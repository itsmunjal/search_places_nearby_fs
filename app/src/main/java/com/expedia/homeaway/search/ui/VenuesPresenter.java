package com.expedia.homeaway.search.ui;

import com.expedia.homeaway.common.data.UserDataRepository;
import com.expedia.homeaway.common.ui.injection.scope.ActivityScope;
import com.expedia.homeaway.data.model.Category;
import com.expedia.homeaway.data.model.SuggestedVenuesResponse;
import com.expedia.homeaway.data.model.Venue;
import com.expedia.homeaway.data.model.VenueResponse;
import com.expedia.homeaway.data.model.VenuesResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.Result;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@ActivityScope
public class VenuesPresenter implements VenuesContract.Presenter {

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final UserDataRepository venuesDataManager;
    private VenuesContract.View viewListener;

    @Inject
    public VenuesPresenter(UserDataRepository venuesDataManager) {
        this.venuesDataManager = venuesDataManager;
    }

    @Override
    public void bindView(VenuesContract.View viewListener) {
        this.viewListener = viewListener;
    }

    @Override
    public void unBindView() {
        unsubscribe();
        this.viewListener = null;
    }

    @Override
    public void doSearch(String searchTerm) {
        subscriptions.add(getSearchSubscription(searchTerm));
    }

    @Override
    public void doSuggestedSearch(String searchTerm) {
        subscriptions.add(getSearchSuggestionsSubscription(searchTerm));
    }

    @Override
    public void doGetVenue(String venueId) {
        subscriptions.add(getVenueSubscription(venueId));
    }

    private void unsubscribe() {
        subscriptions.clear();
    }

    private Subscription getVenueSubscription(String venueId) {
        return venuesDataManager.getVenue(venueId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new VenueSubscriber(viewListener));
    }

    private Subscription getSearchSubscription(String term) {
        return venuesDataManager.searchForVenues(term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SearchSubscriber(viewListener));
    }

    private Subscription getSearchSuggestionsSubscription(String term) {

        return venuesDataManager.suggestedSearchForVenues(term)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SearchSuggestionsSubscriber(viewListener));
    }

    private static class SearchSuggestionsSubscriber extends Subscriber<Result<SuggestedVenuesResponse>> {

        private VenuesContract.View listener;

        public SearchSuggestionsSubscriber(VenuesContract.View listener) {
            this.listener = listener;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            listener.onError();
        }

        @Override
        public void onNext(Result<SuggestedVenuesResponse> result) {
            List<String> suggestedSearchStrings = new ArrayList<>();
            SuggestedVenuesResponse venuesResponse = result.response().body();
            if (venuesResponse != null && venuesResponse.getResponse() != null) {
                List<Venue> miniVenues = venuesResponse.getResponse().getVenues();

                for (int i = 0; i < miniVenues.size(); i++) {

                    Venue venue = miniVenues.get(i);
                    if (venue.getName() != null && !venue.getName().isEmpty()) {
                        suggestedSearchStrings.add(venue.getName());
                    }

                    List<Category> venueCategories = new ArrayList<>();
                    for (int j = 0; j < venueCategories.size(); j++) {
                        Category category = venueCategories.get(j);
                        if (category.getName() != null && !category.getName().isEmpty()) {
                            suggestedSearchStrings.add(category.getName());
                        }
                    }
                }

                if (listener != null) {
                    listener.onSuggestedSearches(suggestedSearchStrings);
                }

            } else {
                if (listener != null) {
                    listener.onSuggestedSearches(new ArrayList<String>());
                }
            }
        }
    }

    private static class SearchSubscriber extends Subscriber<Result<VenuesResponse>> {

        private VenuesContract.View listener;

        public SearchSubscriber(VenuesContract.View listener) {
            this.listener = listener;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            listener.onError();
        }

        @Override
        public void onNext(Result<VenuesResponse> result) {
            VenuesResponse venuesResponse = result.response().body();
            if (venuesResponse != null && venuesResponse.getVenueListResponse() != null) {
                List<Venue> venues = venuesResponse.getVenueListResponse().getVenues();
                if (listener != null) {
                    listener.onSearch(venues);
                }
            }
        }
    }

    private static class VenueSubscriber extends Subscriber<Result<VenueResponse>> {

        private VenuesContract.View listener;

        public VenueSubscriber(VenuesContract.View listener) {
            this.listener = listener;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            listener.onError();
        }

        @Override
        public void onNext(Result<VenueResponse> result) {
            VenueResponse venueResponse = result.response().body();
            if (venueResponse != null && venueResponse.getSingleVenueResponse() != null) {
                if (listener != null) {
                    listener.onVenue(venueResponse.getSingleVenueResponse().getVenue());
                }
            }
        }
    }

}
