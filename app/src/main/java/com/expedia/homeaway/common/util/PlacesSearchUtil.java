package com.expedia.homeaway.common.util;

import com.expedia.homeaway.common.app.AppPreferenceManager;
import com.google.gson.Gson;
import com.expedia.homeaway.common.PlacesSearchConstants;
import com.expedia.homeaway.common.app.FavoritePlacesPreferences;
import com.expedia.homeaway.data.model.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class PlacesSearchUtil {

    public static boolean isFavorite(AppPreferenceManager preferenceManager, String venueId) {
        FavoritePlacesPreferences preferences = preferenceManager.getFavoriteVenuePreferences();
        String[] text = new Gson().fromJson(preferences.favoriteVenues, String[].class);

        if (text == null || text.length == 0) {
            return false;
        }

        List<String> favorites = Arrays.asList(text);
        boolean isFavorite = favorites != null && favorites.contains(venueId);
        return isFavorite;
    }

    public static void addFavorite(AppPreferenceManager preferenceManager, String venueId) {
        FavoritePlacesPreferences preferences = preferenceManager.getFavoriteVenuePreferences();
        String[] text = new Gson().fromJson(preferences.favoriteVenues, String[].class);

        List<String> favorites;
        if (text != null) {
            favorites = new ArrayList<>();
            favorites.addAll(Arrays.asList(text));
        } else {
            favorites = new ArrayList<>();
        }

        favorites.add(venueId);

        preferences.favoriteVenues = new Gson().toJson(favorites);
        preferenceManager.setFavoriteVenuePreferences(preferences);
    }

    public static void removeFavorite(AppPreferenceManager preferenceManager, String venueId) {
        FavoritePlacesPreferences preferences = preferenceManager.getFavoriteVenuePreferences();
        String[] text = new Gson().fromJson(preferences.favoriteVenues, String[].class);
        if (text == null || text.length == 0) {
            return;
        }

        List<String> favorites = new ArrayList<>();
        favorites.addAll(Arrays.asList(text));

        if (favorites == null || favorites.isEmpty()) {
            return;
        }
        favorites.remove(venueId);

        preferences.favoriteVenues = new Gson().toJson(favorites);
        preferenceManager.setFavoriteVenuePreferences(preferences);
    }

    public static String getDistanceInMilesToUserLocation(Location distanceTo) {

        if (distanceTo == null || distanceTo.getLat() == 0 || distanceTo.getLng() == 0) {
            return null;
        }

        android.location.Location startPoint = new android.location.Location("a");
        startPoint.setLatitude(PlacesSearchConstants.USER_LOCATION_LAT);
        startPoint.setLongitude(PlacesSearchConstants.USER_LOCATION_LNG);

        android.location.Location endPoint = new android.location.Location("b");
        endPoint.setLatitude(distanceTo.getLat());
        endPoint.setLongitude(distanceTo.getLng());

        float distance = startPoint.distanceTo(endPoint);

        float mile = distance / 1609.34f;
        return String.format("%.2f", mile);
    }

    public static String getLatLngOfUserLocation() {
        return PlacesSearchConstants.USER_LOCATION_LAT + "," + PlacesSearchConstants.USER_LOCATION_LNG;
    }
}
