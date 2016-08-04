package com.example.lukekim.popularmovie_2.data;

/**
 * Created by lukekim on 8/4/16.
 */
import android.os.Parcel;
import android.os.Parcelable;


public class Review implements Parcelable {

    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public static final Parcelable.Creator<Review> CREATOR = new Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getContent() {
        return mContent;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    public Review(Parcel in) {
        this.mId = in.readString();
        this.mAuthor = in.readString();
        this.mContent = in.readString();
        this.mUrl = in.readString();
    }

    public Review(String mId, String mAuthor, String mContent, String mUrl) {
        this.mId = mId;
        this.mAuthor = mAuthor;
        this.mContent = mContent;
        this.mUrl = mUrl;
    }




    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mId);
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
        parcel.writeString(mUrl);
    }
}

