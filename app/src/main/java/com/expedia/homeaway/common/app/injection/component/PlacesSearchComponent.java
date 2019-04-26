package com.expedia.homeaway.common.app.injection.component;

import com.expedia.homeaway.common.app.AppPreferenceManager;
import com.expedia.homeaway.common.app.HomeAwayApplication;
import com.expedia.homeaway.common.app.injection.module.PlacesSearchModule;
import com.expedia.homeaway.common.data.UserDataRepository;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {PlacesSearchModule.class})
public interface PlacesSearchComponent {

    AppPreferenceManager providePlacesSearchPreferenceManager();

    UserDataRepository provideSearchDataManager();

    Picasso providePicasso();

    void inject(HomeAwayApplication app);


}
