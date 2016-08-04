package com.example.lukekim.popularmovie_2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lukekim.popularmovie_2.data.Movie;
import com.example.lukekim.popularmovie_2.data.Review;
import com.example.lukekim.popularmovie_2.data.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements FetchMovieDbTask.Listener {
    private final String LOG_TAG =DetailActivityFragment.class.getSimpleName();
    public static final String ARG_MOVIE = "movie";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";
    private ArrayList<Review> reviewList;
    private ArrayList<Trailer> trailerList;
    Movie mMovie;
    public DetailActivityFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        Log.v("Luke", "DetailActivityFragment.onCreate getArguments()" +getArguments());

        if (arguments != null && arguments.containsKey(ARG_MOVIE)) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
        if (mMovie != null) {
            Log.v("Luke", "DetailActivityFragment.onCreate mMovie "+mMovie);
            fetchTrailer();
            fetchReview();
        }

        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Log.v("Luke", "DetailActivityFragment.onCreateView mMovie "+mMovie);
            if (mMovie != null) {

                TextView movie_title = (TextView)rootView.findViewById(R.id.movie_title);
                TextView movie_year = (TextView)rootView.findViewById(R.id.movie_year);
                TextView movie_rate = (TextView)rootView.findViewById(R.id.movie_rating);
                ImageView movie_poster = (ImageView)rootView.findViewById(R.id.movie_poster);
                TextView movie_overview = (TextView)rootView.findViewById(R.id.movie_overview);
                movie_title.setText(mMovie.getOriginalTitle());
                movie_year.setText( mMovie.getReleaseDate());
                movie_title.setVisibility(View.VISIBLE);
                movie_rate.setText(mMovie.getVoteAverage() + "/10");
                movie_overview.setText(mMovie.getOverview());
                String movie_poster_url;
                if (mMovie.getPosterPath() == MainActivityFragment.IMAGE_NOT_FOUND) {
                    movie_poster_url = MainActivityFragment.IMAGE_NOT_FOUND;
                }else {
                    movie_poster_url = MainActivityFragment.IMAGE_URL + MainActivityFragment.IMAGE_SIZE_185 + "/" + mMovie.getPosterPath();
                }
                Picasso.with(rootView.getContext()).load(movie_poster_url).into(movie_poster);
                movie_poster.setVisibility(View.VISIBLE);
            }


        return rootView;
    }

    private void fetchTrailer() {
        new FetchMovieDbTask(this).execute(Integer.toString(mMovie.getId()), FetchMovieDbTask.MOVIE_TRAILER);
    }
    private void fetchReview() {
        new FetchMovieDbTask(this).execute(Integer.toString(mMovie.getId()), FetchMovieDbTask.MOVIE_REVIEW);
    }

    @Override
    public void onFetchFinished(String list, String type) {

        switch (type) {
            case FetchMovieDbTask.MOVIE_TRAILER:
                Log.v("Luke", "DetailActivityFragment.onFetchFinished trailer "+list);
                retrieveTrailersData(list);
                break;
            case FetchMovieDbTask.MOVIE_REVIEW:
                Log.v("Luke", "DetailActivityFragment.onFetchFinished review "+list);
                break;
        }

    }


    private void retrieveTrailersData(String str){

        final String MOVIE_ID = "id";
        final String RESULTS_ARRAY = "results";
        final String TRAILER_ID = "id";
        final String KEY = "key";
        final String NAME = "name";
        final String SITE = "site";
        final String SIZE = "size";

        try {
            JSONObject moviesJson = new JSONObject(str);

            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS_ARRAY);
            trailerList = new ArrayList<Trailer>();

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject trailerObject = moviesArray.getJSONObject(i);

                Trailer trailer = new Trailer(trailerObject.getString(TRAILER_ID), trailerObject.getString(KEY), trailerObject.getString(NAME),
                        trailerObject.getString(SITE), trailerObject.getString(SIZE));
                Log.v("Luke", "DetailActivityFragment.retrieveTrailersData trailer KEY: "+trailerObject.getString(KEY));

                trailerList.add(trailer);
            }

            // add to traileradapter

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
