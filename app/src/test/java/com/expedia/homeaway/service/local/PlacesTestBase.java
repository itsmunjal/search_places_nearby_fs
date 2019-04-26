package com.expedia.homeaway.service.local;


import com.expedia.homeaway.service.PlacesApi;

import java.io.File;

public abstract class PlacesTestBase {

    private static final String RESOURCES_PATH = "/resources";
    private static final PlacesApi PLACES_API = new PlacesApiLocal(getResourcesPath());

    public static final String getResourcesPath() {
        return new File(".").getAbsolutePath() + RESOURCES_PATH;
    }

    protected final PlacesApi getPlaces() {
        return PLACES_API;
    }
}
