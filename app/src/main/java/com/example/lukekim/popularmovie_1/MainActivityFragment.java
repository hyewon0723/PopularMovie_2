package com.example.lukekim.popularmovie_1;

import android.app.Activity;
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
    private String prev_SortOrder;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Make sure that container activity implement the callback interface
        try {
            mCallback = (DataPassListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DataPassListener");
        }
    }
    public MainActivityFragment() {
    }

    public ArrayList<String> getPosters() {
        return posters;
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

    public void onResume() {

        SharedPreferences shared_Prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortingOption= shared_Prefs.getString(getString(R.string.pref_sort_option_key), getString(R.string.pref_sort_option_default_value));

        if(!sortingOption.equals(prev_SortOrder)){
            moviesList = new ArrayList<Movie>();
            posters = new ArrayList<String>();
            updateMovies();
        }
        prev_SortOrder = sortingOption;


        super.onResume();
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
//                Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
                Log.v(LOG_TAG, "URL OnCreateView moviesList.get(position).getOverView: "+moviesList.get(position) + " getTitle() "+moviesList.get(position).getTitle());
//                intent.putExtra("movie_obj", moviesList.get(position));
//                getActivity().startActivity(intent);
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
            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
            final String KEY_PARAM = "api_key";
            final String SORT_PARAM = "sort_by";

            try {
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0] + ".desc")
                        .appendQueryParameter(KEY_PARAM, "ac4a7ebe43d27f86492ec2014ca66131")
                        .build();
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String response = null;
                InputStream inputStream;
                StringBuffer buffer;
                Log.v(LOG_TAG, "URL Information: "+builtUri.toString());
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

                        Movie movieItem = new Movie();
                        movieItem.setOverview(movie.getString("overview"));
                        movieItem.setReleaseDate(movie.getString("release_date"));
                        movieItem.setVoteAverage(movie.getString("vote_average"));
                        movieItem.setPosterPath(movie.getString("poster_path"));
                        movieItem.setTitle(movie.getString("title"));
                        movieItem.setId(movie.getInt("id"));

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
