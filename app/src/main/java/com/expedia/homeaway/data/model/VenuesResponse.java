package com.expedia.homeaway.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VenuesResponse {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("response")
    @Expose
    private VenueListResponse mVenueListResponse;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public VenueListResponse getVenueListResponse() {
        return mVenueListResponse;
    }

    public void setVenueListResponse(VenueListResponse venueListResponse) {
        this.mVenueListResponse = venueListResponse;
    }

}