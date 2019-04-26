package com.expedia.homeaway.venue.detail.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.expedia.homeaway.R;
import com.expedia.homeaway.common.PlacesSearchConstants;
import com.expedia.homeaway.common.app.AppPreferenceManager;
import com.expedia.homeaway.common.ui.BaseActivity;
import com.expedia.homeaway.common.util.PlacesSearchUtil;
import com.expedia.homeaway.data.model.Category;
import com.expedia.homeaway.data.model.Venue;
import com.expedia.homeaway.search.ui.VenuesContract;
import com.expedia.homeaway.search.ui.VenuesPresenter;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VenueDetailsActivity extends BaseActivity implements VenuesContract.View {

    @Bind(R.id.favorite_status)
    public ImageView favoriteStatusImage;
    @Bind(R.id.non_favorite_status)
    public ImageView nonFavoriteStatusImage;
    @Bind(R.id.directions_image)
    public ImageView directionsImage;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.address_card_view)
    CardView addressCardView;
    @Bind(R.id.venue_categories)
    TextView venueCategories;
    @Bind(R.id.venue_url)
    TextView venueUrl;
    @Bind(R.id.addr_line_1)
    TextView addrLine1;
    @Bind(R.id.addr_line_2)
    TextView addrLine2;
    @Bind(R.id.addr_line_3)
    TextView addrLine3;
    @Bind(R.id.primary_details_card_view)
    CardView primaryDetailsCardView;

    @Inject
    Picasso picasso;

    @Inject
    VenuesPresenter venuesPresenter;

    @Inject
    AppPreferenceManager preferenceManager;

    private boolean isFavorite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);
        ButterKnife.bind(this);

        component().inject(this);

        initToolbar(toolbar);

        venuesPresenter.bindView(this);

        String venueId = getIntent().getStringExtra(PlacesSearchConstants.VENUE_ID_EXTRA);

        venuesPresenter.doGetVenue(venueId);
    }

    @Override
    protected void onPause() {
        venuesPresenter.unBindView();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }


    @Override
    public void onVenue(final Venue venue) {
        if (venue != null) {

            toolbarTitle.setText(venue.getName());

            setupVenueUrl(venue);

            if (!venue.getCategories().isEmpty()) {
                venueCategories.setText(getCategoriesString(venue.getCategories()));
            } else {
                venueCategories.setVisibility(View.GONE);
            }

            setupAddressInfo(venue);
            setInitialFavoriteStatus(preferenceManager, venue);
        }
    }

    @Override
    public void onError() {
        primaryDetailsCardView.setVisibility(View.INVISIBLE);
        addressCardView.setVisibility(View.INVISIBLE);
        displayCustomToast(getString(R.string.err_msg));
    }

    @Override
    public void onSearch(List<Venue> venues) {
    }

    @Override
    public void onSuggestedSearches(List<String> suggestedSearches) {
    }

    private void setupAddressInfo(final Venue venue) {
        if (venue.getLocation() != null && !venue.getLocation().getFormattedAddress().isEmpty()) {
            for (int i = 0; i < venue.getLocation().getFormattedAddress().size(); i++) {
                String formattedAddress = venue.getLocation().getFormattedAddress().get(i);
                if (i == 0) {
                    addrLine1.setText(formattedAddress);
                } else if (i == 1) {
                    addrLine2.setText(formattedAddress);
                } else if (i == 2) {
                    addrLine3.setText(formattedAddress);
                }
            }
            directionsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchMapsForDirections(venue);
                }
            });

        } else {
            addressCardView.setVisibility(View.GONE);
        }
    }

    private void setupVenueUrl(final Venue venue) {
        if (venue.getUrl() != null) {
            venueUrl.setText(venue.getUrl());
            venueUrl.setPaintFlags(venueUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            venueUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchUrl(venue.getUrl());
                }
            });
        } else {
            venueUrl.setVisibility(View.GONE);
        }
    }

    private void launchMapsForDirections(Venue venue) {
        StringBuilder staticMapImageUrlBuilder = new StringBuilder();
        staticMapImageUrlBuilder.append("https://www.google.com/maps/dir/?api=1&travelmode=driving&origin=");
        staticMapImageUrlBuilder.append(PlacesSearchConstants.USER_LOCATION_LAT + "," + PlacesSearchConstants.USER_LOCATION_LNG);
        staticMapImageUrlBuilder.append("&destination=" + String.format("%.4f", venue.getLocation().getLat()));
        staticMapImageUrlBuilder.append("," + String.format("%.4f", venue.getLocation().getLng()));
        staticMapImageUrlBuilder.append("&key=AIzaSyBJ8FpHurNJQ0oyEhxY4U1HMAZ2xF_pv9w");

        final String mapUrl = staticMapImageUrlBuilder.toString();

        launchUrl(mapUrl);
    }

    private String getCategoriesString(List<Category> categories) {
        if (categories.isEmpty()) {
            return "";
        }

        StringBuilder categoriesString = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            categoriesString.append(categories.get(i).getName());
            if (i != categories.size() - 1) {
                categoriesString.append(", ");
            }
        }

        return categoriesString.toString();
    }


    private void setInitialFavoriteStatus(final AppPreferenceManager placesPreferenceManager, final Venue venue) {
        isFavorite = PlacesSearchUtil.isFavorite(placesPreferenceManager, venue.getId());
        if (isFavorite) {
            favoriteStatusImage.setVisibility(View.VISIBLE);
            nonFavoriteStatusImage.setVisibility(View.GONE);
        } else {
            favoriteStatusImage.setVisibility(View.GONE);
            nonFavoriteStatusImage.setVisibility(View.VISIBLE);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                if (isFavorite) {
                    PlacesSearchUtil.addFavorite(placesPreferenceManager, venue.getId());
                    favoriteStatusImage.setVisibility(View.VISIBLE);
                    nonFavoriteStatusImage.setVisibility(View.GONE);

                } else {
                    PlacesSearchUtil.removeFavorite(placesPreferenceManager, venue.getId());
                    favoriteStatusImage.setVisibility(View.GONE);
                    nonFavoriteStatusImage.setVisibility(View.VISIBLE);
                }
            }
        };

        nonFavoriteStatusImage.setOnClickListener(onClickListener);
        favoriteStatusImage.setOnClickListener(onClickListener);
    }

       protected void launchUrl(String urlToLoad) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToLoad));
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(browserIntent);
    }

}
