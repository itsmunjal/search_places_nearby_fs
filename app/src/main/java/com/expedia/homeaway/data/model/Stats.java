package com.expedia.homeaway.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("checkinsCount")
    @Expose
    private long checkinsCount;
    @SerializedName("usersCount")
    @Expose
    private long usersCount;

    public long getCheckinsCount() {
        return checkinsCount;
    }

    public void setCheckinsCount(long checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    public long getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(long usersCount) {
        this.usersCount = usersCount;
    }

}