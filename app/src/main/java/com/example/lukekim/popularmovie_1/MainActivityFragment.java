package com.example.lukekim.popularmovie_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivityFragment extends Fragment {

    private ArrayList<Movie> moviesList;
    private ArrayList<String> posters;
    private GridView gridview;
    private PosterAdapter posterAdapter;
    private MainActivity mainActivity;
    final static String IMAGE_URL = "http://image.tmdb.org/t/p/";
    final static String IMAGE_SIZE_185 = "w185";
    final static String IMAGE_NOT_FOUND = "http://i.imgur.com/N9FgF7M.png";
    private final String LOG_TAG =MainActivityFragment.class.getSimpleName();
    DataPassListener mCallback;
    public interface DataPassListener{
        public void passData(Movie data);
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
        if(savedInstanceState != null && savedInstanceState.containsKey("moviesList")) {
            moviesList = savedInstanceState.getParcelableArrayList("moviesList");
            posters = savedInstanceState.getStringArrayList("posters");
        }
        else {
            moviesList = new ArrayList<Movie>();
            posters = new ArrayList<String>();
            posterAdapter = new PosterAdapter(getActivity());
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
                mCallback.passData(moviesList.get(position));
            }
        });

        return rootView;
    }

    private void updateMovies() {
        SharedPreferences shared_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortingCriteria = shared_prefs.getString(getString(R.string.pref_sort_option_key), getString(R.string.pref_sort_option_default_value));
        new FetchMoviesTask().execute(sortingCriteria, null);
    }



    public class FetchMoviesTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG =FetchMoviesTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEY_PARAM = "api_key";
            final String API_KEY = "ac4a7ebe43d27f86492ec2014ca66131";


            try {
                Uri builtUri = Uri.parse(BASE_URL+params[0]).buildUpon()
                        .appendQueryParameter(KEY_PARAM, API_KEY)
                        .build();
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String response = null;
                InputStream inputStream;
                StringBuffer buffer;
                //Log.v(LOG_TAG, "URL Information: "+builtUri.toString());
                try {

                    URL url = new URL(builtUri.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();


                    inputStream = urlConnection.getInputStream();
                    buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    response = buffer.toString();
                } catch (IOException e) {
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {

                        }
                    }
                }
                return response;
            }catch (Exception e){
                Log.v(LOG_TAG, "Connection Error: "+e);
                return null;
            }


        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                retrieveData(response);
                mainActivity.getProgressBar().setVisibility(View.INVISIBLE);
            } else {
                Log.v(LOG_TAG, "No Internet Conection! ");
            }
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

}
