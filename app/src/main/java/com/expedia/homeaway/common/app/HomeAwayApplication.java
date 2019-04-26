package com.expedia.homeaway.common.app;

import android.app.Application;
import android.content.Context;

import com.expedia.homeaway.common.app.injection.component.DaggerPlacesSearchComponent;
import com.expedia.homeaway.common.app.injection.component.PlacesSearchComponent;
import com.expedia.homeaway.common.app.injection.module.PlacesSearchModule;


public class HomeAwayApplication extends Application {

    private PlacesSearchComponent component;

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        component().inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public PlacesSearchComponent component() {
        if (component == null) {
            component = DaggerPlacesSearchComponent.builder().placesSearchModule(new PlacesSearchModule(this)).build();
        }
        return component;
    }



}
