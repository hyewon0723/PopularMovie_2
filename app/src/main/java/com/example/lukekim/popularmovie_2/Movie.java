package com.example.lukekim.popularmovie_2;

/**
 * Created by lukekim on 6/13/16.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {

            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private String overview;
    private String originalTitle;
    private String posterPath;
    private String voteAverage;
    private String releaseDate="";
    private int id;

    public Movie() {

    }

    // parcel construtor
    protected Movie(Parcel p) {
        id = p.readInt();
        overview = p.readString();
        posterPath = p.readString();
        originalTitle = p.readString();
        voteAverage = p.readString();
        releaseDate=p.readString();
    }

    public Movie(String overview, String releaseDate, String voteAverage, String posterPath, String originalTitle, int id) {
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.id = id;
    }


    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public void setOriginalTitleTitle(String title) {
        this.originalTitle = title;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAvarage) {
        this.voteAverage = voteAvarage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(voteAverage);
        dest.writeString(releaseDate);
    }


}