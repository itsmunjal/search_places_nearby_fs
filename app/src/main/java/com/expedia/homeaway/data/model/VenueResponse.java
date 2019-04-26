package com.expedia.homeaway.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VenueResponse {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("response")
    @Expose
    private SingleVenueResponse singleVenueResponse;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public SingleVenueResponse getSingleVenueResponse() {
        return singleVenueResponse;
    }

    public void setSingleVenueResponse(SingleVenueResponse singleVenueResponse) {
        this.singleVenueResponse = singleVenueResponse;
    }
}