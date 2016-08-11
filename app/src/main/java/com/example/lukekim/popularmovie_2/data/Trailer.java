package com.example.lukekim.popularmovie_2.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lukekim on 8/4/16.
 */
public class Trailer implements Parcelable {

    private String mId;
    private String mKey;
    private String mName;
    private String mSite;
    private String mSize;

    public static final Parcelable.Creator<Trailer> CREATOR = new Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    private Trailer(Parcel in) {
        this.mId = in.readString();
        this.mKey = in.readString();
        this.mName = in.readString();
        this.mSite = in.readString();
        this.mSize = in.readString();
    }

    public Trailer(String mId, String mKey, String mName, String mSite,  String mSize) {
        this.mId = mId;
        this.mKey = mKey;
        this.mName = mName;
        this.mSite = mSite;
        this.mSize = mSize;
    }

    public String getName() {
        return mName;
    }


    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mId);
        parcel.writeString(mKey);
        parcel.writeString(mName);
        parcel.writeString(mSite);
        parcel.writeString(mSize);
    }

    public int describeContents() {
        return 0;
    }

    public String getKey() {
        return mKey;
    }

    public String getTrailerUrl() {
        return "http://www.youtube.com/watch?v=" + mKey;
    }
}
