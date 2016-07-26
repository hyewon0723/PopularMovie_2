package com.example.lukekim.popularmovie_2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private DetailActivity detailActivity;
    public DetailActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailActivity = (DetailActivity) getActivity();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Movie movie = detailActivity.getMovie();

        TextView movie_title = (TextView)rootView.findViewById(R.id.movie_title);
        TextView movie_year = (TextView)rootView.findViewById(R.id.movie_year);
        TextView movie_rate = (TextView)rootView.findViewById(R.id.movie_rating);
        ImageView movie_poster = (ImageView)rootView.findViewById(R.id.movie_poster);
        TextView movie_overview = (TextView)rootView.findViewById(R.id.movie_overview);
        movie_title.setText(movie.getOriginalTitle());
        movie_year.setText( movie.getReleaseDate());
        movie_title.setVisibility(View.VISIBLE);
        movie_rate.setText(movie.getVoteAverage() + "/10");
        movie_overview.setText(movie.getOverview());
        String movie_poster_url;
        if (movie.getPosterPath() == MainActivityFragment.IMAGE_NOT_FOUND) {
            movie_poster_url = MainActivityFragment.IMAGE_NOT_FOUND;
        }else {
            movie_poster_url = MainActivityFragment.IMAGE_URL + MainActivityFragment.IMAGE_SIZE_185 + "/" + movie.getPosterPath();
        }
        Picasso.with(rootView.getContext()).load(movie_poster_url).into(movie_poster);
        movie_poster.setVisibility(View.VISIBLE);


        return rootView;
    }
}
