package com.example.lukekim.popularmovie_2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private final String LOG_TAG =DetailActivityFragment.class.getSimpleName();
    public static final String ARG_MOVIE = "movie";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";
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
        Log.v("Luke", "DetailActivityFragment.onCreate mMovie "+mMovie);

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

}
