package com.expedia.homeaway.search.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.expedia.homeaway.R;
import com.expedia.homeaway.common.app.AppPreferenceManager;
import com.expedia.homeaway.common.util.PlacesSearchUtil;
import com.expedia.homeaway.data.model.Venue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.VenueViewHolder> {

    private List<Venue> data = new ArrayList<>();
    private VenueListener listener;
    private AppPreferenceManager placesPreferenceManager;
    private Context context;
    private Picasso picasso;

    public SearchResultsAdapter(Context context, VenueListener listener, AppPreferenceManager placesPreferenceManager, Picasso picasso) {
        this.context = context;
        this.placesPreferenceManager = placesPreferenceManager;
        this.listener = listener;
        this.picasso = picasso;
    }

    @Override
    public VenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new VenueViewHolder(inflater.inflate(R.layout.list_item_venue, parent, false));
    }

    @Override
    public void onBindViewHolder(VenueViewHolder holder, int position) {
        holder.bind(context, data.get(position), listener, picasso, placesPreferenceManager);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<Venue> batches) {
        this.data = new ArrayList<>(batches);
        notifyDataSetChanged();
    }

    public interface VenueListener {
        void onVenueItemClicked(Venue venue);
    }

    public static class VenueViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.venue_container)
        public RelativeLayout venueContainer;

        @Bind(R.id.venue_category_image)
        public ImageView venueCategoryImage;

        @Bind(R.id.venue_name)
        public TextView venueName;

        @Bind(R.id.category_name)
        public TextView categoryName;

        @Bind(R.id.distance_to_user_location)
        public TextView distanceToUserLocation;

        @Bind(R.id.favorite_status)
        public ImageView favoriteStatusImage;

        @Bind(R.id.non_favorite_status)
        public ImageView nonFavoriteStatusImage;

        private View itemView;
        private boolean isFavorite;
        private boolean favoriteStatusNeedsUpdating;
        private String venueId;

        public VenueViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            isFavorite = false;
            this.itemView = itemView;
        }

        private static void loadImage(Context context, ImageView imageView, Venue venue, Picasso picasso) {

            if (venue.getCategories().isEmpty() || venue.getCategories().get(0).getIcon() == null) {
                return;
            }

            final String imageUrl = venue.getListImgUrl();

            if (imageUrl == null) {
                imageView.setImageDrawable(context.getDrawable(android.R.drawable.stat_notify_error));
                return;
            }

            picasso.load(imageUrl).into(imageView);
        }

        public void bind(final Context context, final Venue data, final VenueListener listener, Picasso picasso, final AppPreferenceManager placesPreferenceManager) {

            isFavorite = false;

            venueName.setText(data.getName());

            if (data.getCategories().size() >= 1) {
                categoryName.setText(data.getCategories().get(0).getName());
            } else {
                categoryName.setText(context.getString(R.string.unavailable));
            }

            String distanceToUserStr = PlacesSearchUtil.getDistanceInMilesToUserLocation(data.getLocation());
            if (distanceToUserStr == null) {
                distanceToUserLocation.setText(context.getString(R.string.unavailable));
            } else {
                distanceToUserLocation.setText(distanceToUserStr + " miles");
            }

            loadImage(context, venueCategoryImage, data, picasso);

            venueContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onVenueItemClicked(data);
                    setFavoriteStatusNeedsUpdating(true);
                    venueId = data.getId();
                }
            });
            setInitialFavoriteStatus(placesPreferenceManager, data);

            itemView.getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
                @Override
                public void onWindowFocusChanged(boolean hasFocus) {
                    if (hasFocus && getFavoriteStatusNeedsUpdating() && venueId != null && venueId.equals(data.getId())) {
                        refreshFavoriteStatus(data.getId(), placesPreferenceManager);
                        setFavoriteStatusNeedsUpdating(false);
                        venueId = null;
                    }
                }
            });
        }

        private boolean getFavoriteStatusNeedsUpdating() {
            return this.favoriteStatusNeedsUpdating;
        }

        public void setFavoriteStatusNeedsUpdating(boolean needsUpdating) {
            this.favoriteStatusNeedsUpdating = needsUpdating;
        }

        private void refreshFavoriteStatus(String venueId, AppPreferenceManager placesPreferenceManager) {
            boolean latestFavoriteStatus = PlacesSearchUtil.isFavorite(placesPreferenceManager, venueId);
            if (!isFavorite && latestFavoriteStatus) {
                isFavorite = latestFavoriteStatus;
                favoriteStatusImage.setVisibility(View.VISIBLE);
                nonFavoriteStatusImage.setVisibility(View.GONE);
            } else if (isFavorite && !latestFavoriteStatus) {
                isFavorite = latestFavoriteStatus;
                favoriteStatusImage.setVisibility(View.GONE);
                nonFavoriteStatusImage.setVisibility(View.VISIBLE);
            }
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


    }

}
