package com.expedia.homeaway.common.ui.injection.component;

import com.expedia.homeaway.common.app.injection.component.PlacesSearchComponent;
import com.expedia.homeaway.maps.ui.VenuesMapActivity;
import com.expedia.homeaway.search.ui.SearchActivity;
import com.expedia.homeaway.common.ui.injection.scope.ActivityScope;
import com.expedia.homeaway.venue.detail.ui.VenueDetailsActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = PlacesSearchComponent.class)
public interface ActivityComponent {

    void inject(SearchActivity activity);

    void inject(VenuesMapActivity activity);

    void inject(VenueDetailsActivity activity);
}
