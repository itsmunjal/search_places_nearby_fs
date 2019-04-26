package com.expedia.homeaway.common.app.injection.module;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.expedia.homeaway.common.PlacesSearchConstants;
import com.expedia.homeaway.common.util.PlacesSearchUtil;
import com.expedia.homeaway.service.PlacesApi;
import com.expedia.homeaway.service.network.PlacesApiRetrofit;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

import static com.expedia.homeaway.common.PlacesSearchConstants.FOUR_SQUARE_API_CLIENT_ID;
import static com.expedia.homeaway.common.PlacesSearchConstants.FOUR_SQUARE_API_CLIENT_SECRET;
import static com.expedia.homeaway.common.PlacesSearchConstants.FOUR_SQUARE_API_VERSION;
import static java.util.concurrent.TimeUnit.SECONDS;

@Module
public class PlacesSearchModule {

    private final Context context;
    private PlacesApi mPlacesApi;

    public PlacesSearchModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }


    @Provides
    @Singleton
    public PlacesApi providePlaces() {
        return getPlacesInstance();
    }

    @Provides
    @Singleton
    public Picasso providePicasso(OkHttpClient client) {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return createOkHttpClient(context).build();
    }

    @NonNull
    private PlacesApi getPlacesInstance() {
        if (mPlacesApi == null) {
            mPlacesApi = new PlacesApiRetrofit(PlacesSearchConstants.FOURSQUARE_BASE_URL, FOUR_SQUARE_API_VERSION,
                    FOUR_SQUARE_API_CLIENT_ID, FOUR_SQUARE_API_CLIENT_SECRET, PlacesSearchUtil.getLatLngOfUserLocation());
        }
        return mPlacesApi;
    }

    private static OkHttpClient.Builder createOkHttpClient(Context context) {
        File cacheDir = new File(context.getCacheDir(), PlacesSearchConstants.HTTP);

        return new OkHttpClient.Builder()
                .connectTimeout(PlacesSearchConstants.HTTP_TIMEOUT_VALUE, SECONDS)
                .readTimeout(PlacesSearchConstants.HTTP_TIMEOUT_VALUE, SECONDS)
                .writeTimeout(PlacesSearchConstants.HTTP_TIMEOUT_VALUE, SECONDS);
    }

}
