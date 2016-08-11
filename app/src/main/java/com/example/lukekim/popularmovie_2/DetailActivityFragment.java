package com.example.lukekim.popularmovie_2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lukekim.popularmovie_2.data.Movie;
import com.example.lukekim.popularmovie_2.data.MovieContract;
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
public class DetailActivityFragment extends Fragment implements FetchMovieDbTask.Listener, ReviewListAdapter.Callbacks {
    private ListView mListViewForTrailers;
    private ImageButton mFavoriteButton;
    private TrailerListAdapter mTrailerListAdapter;
    private RecyclerView mReviewListViewForTrailers;
    private ReviewListAdapter mReviewListAdapter;
    public static final String ARG_MOVIE = "movie";
    public static final String ARG_TRAILERS = "trailers";
    public static final String ARG_REVIEWS = "reviews";
    private ArrayList<Review> reviewList;
    private ArrayList<Trailer> trailerList;
    Movie mMovie;

    public DetailActivityFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();

        if (arguments != null && arguments.containsKey(ARG_MOVIE)) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }

        setHasOptionsMenu(true);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mTrailerListAdapter != null) {
            ArrayList<Trailer> trailers = mTrailerListAdapter.getTrailers();
            if (trailers != null) {
                outState.putParcelableArrayList(ARG_TRAILERS, trailers);
            }
        }

        if(mReviewListAdapter != null) {
            ArrayList<Review> reviews = mReviewListAdapter.getReviews();
            if (reviews != null) {
                outState.putParcelableArrayList(ARG_REVIEWS, reviews);
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

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

                //Trailer Adapter in ListView
                mListViewForTrailers = (ListView) rootView.findViewById(R.id.trailer_list);
                mTrailerListAdapter = new TrailerListAdapter(getActivity(), new ArrayList<Trailer>());
                mListViewForTrailers.setAdapter(mTrailerListAdapter);

                mListViewForTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Trailer trailer = trailerList.get(position);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));
                    }
                });

                //Review Adapter in Recycler View
                mReviewListViewForTrailers = (RecyclerView) rootView.findViewById(R.id.review_list);
                mReviewListAdapter = new ReviewListAdapter(new ArrayList<Review>(), this);
                mReviewListViewForTrailers.setAdapter(mReviewListAdapter);

                //Handing Favoite image button
                mFavoriteButton = (ImageButton) rootView.findViewById(R.id.button_favorite);
                if (isFavoriteMovie()) {
                    mFavoriteButton.setImageResource(R.drawable.favorites);
                }
                mFavoriteButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setFavorite();
                            }
                        });

                //Handling saveInstance for trailer and review
                if (savedInstanceState != null && savedInstanceState.containsKey(ARG_TRAILERS)) {
                    trailerList = savedInstanceState.getParcelableArrayList(ARG_TRAILERS);
                    mTrailerListAdapter.addAll(trailerList);
                    ViewGroup.LayoutParams params = mListViewForTrailers.getLayoutParams();
                    if (getActivity().findViewById(R.id.grid_view) == null) {
                        params.height = 350 * mTrailerListAdapter.getCount();
                    }
                    else {
                        params.height = 150 * mTrailerListAdapter.getCount();
                    }
                    mListViewForTrailers.setLayoutParams(params);
                    mListViewForTrailers.requestLayout();
                } else {
                    fetchTrailer();
                }

                if (savedInstanceState != null && savedInstanceState.containsKey(ARG_REVIEWS)) {
                    reviewList = savedInstanceState.getParcelableArrayList(ARG_REVIEWS);
                    mReviewListAdapter.addAll(reviewList);
                } else {
                    fetchReview();
                }

            }

        return rootView;
    }


    private boolean isFavoriteMovie() {
        Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }

    public void setFavorite() {

        new AsyncTask<Void, Void, Void>() {

            protected Void doInBackground(Void... params) {
                if (!isFavoriteMovie()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            mMovie.getId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovie.getOriginalTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                            mMovie.getPosterPath());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovie.getOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                            mMovie.getVoteAverage());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                            mMovie.getReleaseDate());
                    getContext().getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                mFavoriteButton.setImageResource(R.drawable.favorites);
                mFavoriteButton.setTag(R.drawable.favorites);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void readReview(Review review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getUrl())));
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
                retrieveTrailersData(list);
                break;
            case FetchMovieDbTask.MOVIE_REVIEW:
                retrieveReviewData(list);
                break;
        }

    }


    private void retrieveTrailersData(String str){

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

                trailerList.add(trailer);
            }

            // add to traileradapter
            mTrailerListAdapter.addAll(trailerList);
            ViewGroup.LayoutParams params = mListViewForTrailers.getLayoutParams();
            if (getActivity().findViewById(R.id.grid_view) == null) {
                params.height = 350 * mTrailerListAdapter.getCount();
            }
            else {
                params.height = 150 * mTrailerListAdapter.getCount();
            }

            mListViewForTrailers.setLayoutParams(params);
            mListViewForTrailers.requestLayout();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void retrieveReviewData(String str) {

        final String REVIEW_ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";
        final String RESULTS_ARRAY = "results";

        try {
            JSONObject moviesJson = new JSONObject(str);

            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS_ARRAY);
            reviewList = new ArrayList<Review>();

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject reviewObject = moviesArray.getJSONObject(i);

                Review review = new Review(reviewObject.getString(REVIEW_ID), reviewObject.getString(AUTHOR), reviewObject.getString(CONTENT),
                        reviewObject.getString(URL));

                reviewList.add(review);
            }

            // add to traileradapter
            mReviewListAdapter.addAll(reviewList);;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
