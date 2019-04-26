
package com.expedia.homeaway.maps.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.expedia.homeaway.common.PlacesSearchConstants;
import com.expedia.homeaway.R;
import com.expedia.homeaway.data.model.MapPin;
import com.expedia.homeaway.venue.detail.ui.VenueDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;


public class VenuesMapActivity extends AppCompatActivity implements
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    @Inject
    Picasso picasso;

    List<MapPin> mapPins;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_map);

        mapPins = getIntent().getParcelableArrayListExtra(PlacesSearchConstants.MAP_PINS_EXTRA);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        LatLngBounds latLngBounds = addMarkersToMap();

        googleMap.setInfoWindowAdapter(new InfoWindowAdapter());

        googleMap.setContentDescription("Search Results");

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
    }

    private LatLngBounds addMarkersToMap() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (int i = 0; i < mapPins.size(); i++) {
            MapPin mapPin = mapPins.get(i);
            LatLng latLng = new LatLng(mapPin.getLat(), mapPin.getLng());
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(mapPin.getPinName())
                    .snippet(mapPin.getVenueId())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .infoWindowAnchor(0.5f, 0.5f));
            boundsBuilder.include(latLng);
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                onMarkerInfoWindowClicked(marker);
            }
        });

        return boundsBuilder.build();
    }

    private void onMarkerInfoWindowClicked(Marker marker) {
        Intent intent = new Intent(this, VenueDetailsActivity.class);
        intent.putExtra(PlacesSearchConstants.VENUE_ID_EXTRA, marker.getSnippet());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
    }

    private class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;
        private final View mContents;

        InfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.map_marker_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.map_marker_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(final Marker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = view.findViewById(R.id.title);
            titleUi.setText(title);
        }
    }

}