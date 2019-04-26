package com.expedia.homeaway.search.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.expedia.homeaway.R;
import com.expedia.homeaway.common.PlacesSearchConstants;
import com.expedia.homeaway.common.app.AppPreferenceManager;
import com.expedia.homeaway.common.ui.BaseActivity;
import com.expedia.homeaway.data.model.MapPin;
import com.expedia.homeaway.data.model.Venue;
import com.expedia.homeaway.maps.ui.VenuesMapActivity;
import com.expedia.homeaway.venue.detail.ui.VenueDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity implements VenuesContract.View, SearchResultsAdapter.VenueListener {

    protected RecyclerView.Adapter searchResultsAdapter;
    protected SuggestedSearchRecyclerAdapter suggestedSearchRecyclerAdapter;

    @Bind(R.id.map_fab)
    FloatingActionButton mapFab;
    @Bind(R.id.search_edittext)
    EditText searchEditText;
    @Bind(R.id.search_results_recycler_view)
    RecyclerView searchResultsRecyclerView;
    @Bind(R.id.suggested_search_list_recycler_view)
    RecyclerView suggestedSearchesRecyclerView;
    @Bind(R.id.clear_icon)
    ImageView clearIcon;
    @Bind(R.id.loader)
    ProgressBar loader;
    @Bind(R.id.no_results_message)
    TextView noResultsMessageText;
    @Bind(R.id.suggested_searches)
    ViewGroup suggestedSearchesContainer;
    @Bind(R.id.suggested_search_header)
    ViewGroup suggestedSearchHeader;
    @Bind(R.id.suggested_searches_message)
    TextView suggestedSearchesMessage;

    @Inject
    Picasso picasso;
    @Inject
    VenuesPresenter venuesPresenter;
    @Inject
    AppPreferenceManager preferenceManager;

    private LinearLayoutManager searchResultsLayoutManager;
    private String searchInput;

    private final TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (clearIcon != null) {
                clearIcon.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }

            searchInput = s.toString();

            if (s.length() != 0) {
                displayNoResultsState(false);
                venuesPresenter.doSuggestedSearch(s.toString());
            } else {
                displaySuggestedSearchViews(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private LinearLayoutManager suggestedSearchTermsLayoutManager;
    private List<Venue> searchResults = new ArrayList<>();
    private View.OnTouchListener backgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hideKeyboard();
            return false;
        }
    };
    private SuggestedSearchRecyclerAdapter.SuggestedSearchOnClickListener suggestedSearchItemClickListener =
            new SuggestedSearchRecyclerAdapter.SuggestedSearchOnClickListener() {
                @Override
                public void onSuggestedSearchItemClick(String searchTerm) {
                    displaySuggestedSearchViews(false);
                    doSearch(searchTerm);
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        component().inject(this);
        initToolbar(toolbar);

        initSearchTextView();

        setupSearchResultsRecyclerView();
        setupSuggestedSearchResultsRecyclerView();

        displayNoResultsState(false);

        displaySuggestedSearchViews(false);

    }

    @Override
    protected void onPause() {
        venuesPresenter.unBindView();
        hideKeyboard();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        venuesPresenter.bindView(this);
    }

    @OnClick(R.id.clear_icon)
    public void onClearClicked() {
        searchEditText.setText("");
        searchEditText.setSelection(0);
        searchEditText.setCursorVisible(true);
        searchEditText.isEnabled();
        showKeyboard();
        displayMapFab(false);
        displayNoResultsState(false);
        displaySuggestedSearchViews(false);
    }

    @OnClick(R.id.search_edittext)
    public void onSearchEditTextClicked() {
        searchEditText.setCursorVisible(true);
        searchEditText.isEnabled();
    }

    @NonNull
    private TextView.OnEditorActionListener getOnEditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchString = searchEditText.getText().toString().trim();
                    doSearch(searchString);
                    return true;
                }
                return false;
            }
        };
    }

    private void initSearchTextView() {
        searchEditText.clearComposingText();
        searchEditText.setText("");
        searchEditText.setSelection(0);

        searchEditText.setOnEditorActionListener(getOnEditorActionListener());
        searchEditText.addTextChangedListener(searchTextWatcher);
    }

    private void displayNoResultsState(boolean show) {
        if (show) {
            noResultsMessageText.setVisibility(View.VISIBLE);
            noResultsMessageText.setText(String.format(getString(R.string.search_no_results_response),
                    searchInput));
        } else {
            noResultsMessageText.setVisibility(View.INVISIBLE);
        }
    }

    private void displayMapFab(boolean show) {
        if (show) {
            mapFab.setVisibility(View.VISIBLE);
        } else {
            mapFab.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.map_fab)
    public void mapFabClicked() {
        Intent intent = new Intent(this, VenuesMapActivity.class);
        intent.putParcelableArrayListExtra(PlacesSearchConstants.MAP_PINS_EXTRA, getMapPinsOfSearchResults());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
    }

    private ArrayList<MapPin> getMapPinsOfSearchResults() {
        ArrayList<MapPin> mapPins = new ArrayList<>();
        for (int i = 0; i < searchResults.size(); i++) {
            Venue venue = searchResults.get(i);
            if (venue.getLocation() != null) {
                MapPin mapPin = new MapPin();
                mapPin.setVenueId(venue.getId());
                mapPin.setPinName(venue.getName());
                mapPin.setLat(venue.getLocation().getLat());
                mapPin.setLng(venue.getLocation().getLng());
                mapPin.setImgUrl(venue.getMapPinUrl());
                mapPins.add(mapPin);
            }
        }
        return mapPins;
    }

    private void setupSearchResultsRecyclerView() {
        searchResultsAdapter = new SearchResultsAdapter(this, this, preferenceManager, picasso);
        searchResultsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchResultsRecyclerView.setLayoutManager(searchResultsLayoutManager);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
    }

    private void setupSuggestedSearchResultsRecyclerView() {
        suggestedSearchTermsLayoutManager = new LinearLayoutManager(this);
        suggestedSearchesRecyclerView.setLayoutManager(suggestedSearchTermsLayoutManager);
        suggestedSearchRecyclerAdapter = new SuggestedSearchRecyclerAdapter(suggestedSearchesRecyclerView, new ArrayList<String>(), suggestedSearchHeader);
        suggestedSearchRecyclerAdapter.setClickListeners(suggestedSearchItemClickListener, backgroundTouchListener);
        suggestedSearchesRecyclerView.setAdapter(suggestedSearchRecyclerAdapter);
    }

    private void doSearch(String searchString) {
        if (searchString.length() > 0) {
            this.searchInput = searchString;

            displaySuggestedSearchViews(false);

            hideKeyboard();
            searchEditText.setCursorVisible(false);

            displayProgressBar(true);
            displayMapFab(false);
            venuesPresenter.doSearch(searchString);
        }
    }

    @Override
    public void onSearch(List<Venue> venues) {
        displayProgressBar(false);
        if (venues != null && !venues.isEmpty()) {
            displayNoResultsState(false);
            this.searchResults = venues;
            displayMapFab(true);
            searchResultsLayoutManager.scrollToPosition(0);
            ((SearchResultsAdapter) searchResultsAdapter).updateData(venues);
        } else {
            searchResultsLayoutManager.scrollToPosition(0);
            ((SearchResultsAdapter) searchResultsAdapter).updateData(new ArrayList<Venue>());
            displayNoResultsState(true);
            displayMapFab(false);
        }
    }

    @Override
    public void onSuggestedSearches(List<String> suggestedSearches) {
        if (searchInput.isEmpty()) {
            return;
        }
        updateSuggestedSearches(suggestedSearches);
    }

    @Override
    public void onVenue(Venue venue) {
    }

    @Override
    public void onError() {
        displayProgressBar(false);
        Snackbar.make(mapFab, getResources().getString(R.string.err_msg), Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public void onVenueItemClicked(Venue venue) {
        Intent intent = new Intent(this, VenueDetailsActivity.class);
        intent.putExtra(PlacesSearchConstants.VENUE_NAME_EXTRA, venue.getName());
        intent.putExtra(PlacesSearchConstants.VENUE_ID_EXTRA, venue.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
    }

    public void displayProgressBar(boolean show) {
        if (show) {
            loader.setVisibility(View.VISIBLE);
        } else {
            loader.setVisibility(View.GONE);
        }
    }

    private void updateSuggestedSearches(List<String> suggestedSearches) {
        displaySuggestedSearchViews(true);
        suggestedSearchRecyclerAdapter.setSearchResults(suggestedSearches, searchInput);

        if (suggestedSearchRecyclerAdapter.isSearchResultsEmpty()) {
            suggestedSearchesMessage.setText(R.string.no_suggested_searches);
            suggestedSearchesMessage.setVisibility(View.VISIBLE);
        } else {
            suggestedSearchesMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void displaySuggestedSearchViews(boolean show) {
        if (show) {
            suggestedSearchesContainer.setVisibility(View.VISIBLE);
        } else {
            suggestedSearchesContainer.setVisibility(View.INVISIBLE);
            if (suggestedSearchRecyclerAdapter != null) {
                suggestedSearchRecyclerAdapter.clearSearchResults();

            }
            if(searchResultsAdapter != null){
                ((SearchResultsAdapter) searchResultsAdapter).updateData(new ArrayList<Venue>());
            }
        }
    }

}
