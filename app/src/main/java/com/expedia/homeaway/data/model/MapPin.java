package com.expedia.homeaway.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MapPin implements Parcelable {

    public static final Creator<MapPin> CREATOR = new Creator<MapPin>() {
        @Override
        public MapPin createFromParcel(Parcel in) {
            return new MapPin(in);
        }

        @Override
        public MapPin[] newArray(int size) {
            return new MapPin[size];
        }
    };
    private String venueId;
    private String pinName;
    private double lat;
    private double lng;
    private String imgUrl;

    public MapPin() {
    }

    protected MapPin(Parcel in) {
        venueId = in.readString();
        pinName = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        imgUrl = in.readString();
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getPinName() {
        return pinName;
    }

    public void setPinName(String pinName) {
        this.pinName = pinName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(venueId);
        parcel.writeString(pinName);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeString(imgUrl);
    }
}
