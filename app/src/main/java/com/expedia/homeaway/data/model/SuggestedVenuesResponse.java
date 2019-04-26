package com.expedia.homeaway.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuggestedVenuesResponse {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("response")
    @Expose
    private SuggestedResponse suggestedResponse;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public SuggestedResponse getResponse() {
        return suggestedResponse;
    }

    public void setResponse(SuggestedResponse suggestedResponse) {
        this.suggestedResponse = suggestedResponse;
    }

}