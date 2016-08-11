package com.example.lukekim.popularmovie_2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.lukekim.popularmovie_2.data.Movie;
import com.example.lukekim.popularmovie_2.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivityFragment extends Fragment implements FetchMovieDbTask.Listener, LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayList<Movie> moviesList;
    private ArrayList<String> posters;
    private GridView gridview;
    private PosterAdapter posterAdapter;
    private static final int FAVORITE_MOVIES_LOADER = 0;
    private MainActivity mainActivity;
    final static String IMAGE_URL = "http://image.tmdb.org/t/p/";
    final static String IMAGE_SIZE_185 = "w185";
    final static String IMAGE_NOT_FOUND = "http://i.imgur.com/N9FgF7M.png";
    private final String LOG_TAG =MainActivityFragment.class.getSimpleName();
    DataPassListener mCallback;
    public interface DataPassListener{
        public void itemSelected(Movie data);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Make sure that container activity implement the callback interface
        try {
            mCallback = (DataPassListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataPassListener");
        }
    }
    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        posterAdapter = new PosterAdapter(getActivity());
        Log.v("Luke", "MainActivtyFragmenet.onCreateView savedInstanceState "+savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey("moviesList")) {
            moviesList = savedInstanceState.getParcelableArrayList("moviesList");
            posters = savedInstanceState.getStringArrayList("posters");
            posterAdapter.addAll(posters);
        }
        else {
            moviesList = new ArrayList<Movie>();
            posters = new ArrayList<String>();
            updateMovies();
        }
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("moviesList", moviesList);
        outState.putStringArrayList("posters", posters);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        gridview = (GridView) rootView.findViewById(R.id.grid_view);
        gridview.setNumColumns(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3: 2);
        gridview.setAdapter(posterAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                mCallback.itemSelected(moviesList.get(position));
            }
        });

        return rootView;
    }

    private void updateMovies() {
        Log.v("Luke", "@@@@@@@@@@@@@@@MainActivtyFragmenet.onCreateView updateMovies isfavorite? !!!!!!!!!!!! ");
        SharedPreferences shared_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortingCriteria = shared_prefs.getString(getString(R.string.pref_sort_option_key), getString(R.string.pref_sort_option_default_value));
        Log.v("Luke", "@@@@@@@@@@@@@@@MainActivtyFragmenet.onCreateView updateMovies isfavorite? !!!!!!!!!!!! (sortingCriteria.equals(\"favorites\")) "+(sortingCriteria.equals("favorites")));
        if (sortingCriteria.equals("favorites")) {
            getActivity().getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
        }
        else {
            String prarms[] = {sortingCriteria};
            new FetchMovieDbTask(this).execute(sortingCriteria, FetchMovieDbTask.MOVIE_POSTER);
        }

    }


    public void onFetchFinished(String response, String type) {
        if (type == FetchMovieDbTask.MOVIE_POSTER) {
            retrieveData(response);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        posters.clear();
        moviesList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movie movieItem = new Movie(cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW), cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE), cursor.getString(MovieContract.MovieEntry.COL_MOVIE_VOTE_AVERAGE),
                        cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH), cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE), cursor.getInt(MovieContract.MovieEntry.COL_MOVIE_ID));
                posters.add(IMAGE_URL + IMAGE_SIZE_185 + cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH));
                moviesList.add(movieItem);
            } while (cursor.moveToNext());
        }

        posterAdapter.addAll(posters);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    public void retrieveData (String jsonString) {
        posters.clear();
        moviesList.clear();

        try {
            if (jsonString != null) {
                JSONObject moviesObject = new JSONObject(jsonString);
                JSONArray moviesArray = moviesObject.getJSONArray("results");

                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movie = moviesArray.getJSONObject(i);

                    Movie movieItem = new Movie(movie.getString("overview"), movie.getString("release_date"), movie.getString("vote_average"),
                            movie.getString("poster_path"), movie.getString("original_title"), movie.getInt("id"));

                    if (movie.getString("poster_path") == "null") {
                        posters.add(IMAGE_NOT_FOUND);
                        movieItem.setPosterPath(IMAGE_NOT_FOUND);
                    } else {
                        posters.add(IMAGE_URL + IMAGE_SIZE_185 + movie.getString("poster_path"));
                    }
                    moviesList.add(movieItem);
                }

                posterAdapter.addAll(posters);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
