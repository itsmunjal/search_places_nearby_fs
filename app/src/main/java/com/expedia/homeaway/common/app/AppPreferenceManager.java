package com.expedia.homeaway.common.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppPreferenceManager {

    public static final String PLACES_SEARCH_PREFERENCES_KEY = "pref";
    private static final String FAVORITE_VENUE_PREFERENCES_CONTENT = AppPreferenceManager.class.getSimpleName() + ".favoriteVenuePreferences";

    private final SharedPreferences sharedPreferences;
    private FavoritePlacesPreferences favoriteVenuePreferences;

    @Inject
    public AppPreferenceManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PLACES_SEARCH_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public FavoritePlacesPreferences getFavoriteVenuePreferences() {
        if (favoriteVenuePreferences != null) {
            return favoriteVenuePreferences;
        } else {
            String jsonString = sharedPreferences.getString(FAVORITE_VENUE_PREFERENCES_CONTENT, null);
            if (jsonString == null) {
                return new FavoritePlacesPreferences();
            } else {
                return new Gson().fromJson(jsonString, FavoritePlacesPreferences.class);
            }
        }
    }

    public void setFavoriteVenuePreferences(FavoritePlacesPreferences favoriteVenuePreferences) {
        this.favoriteVenuePreferences = favoriteVenuePreferences;
        sharedPreferences.edit().putString(FAVORITE_VENUE_PREFERENCES_CONTENT, new Gson().toJson(favoriteVenuePreferences)).commit();
    }

}
