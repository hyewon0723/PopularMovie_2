package com.example.lukekim.popularmovie_2;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lukekim on 8/4/16.
 */
public class FetchMovieDbTask extends AsyncTask<String, Void, String> {

    public static final String MOVIE_POSTER = "poster";
    public static final String MOVIE_REVIEW = "review";
    public static final String MOVIE_TRAILER = "trailer";
    private String mFetchType = "";

    private final String LOG_TAG = FetchMovieDbTask.class.getSimpleName();
    private final Listener mListener;

    interface Listener {
        void onFetchFinished(String list, String type);
    }

    public FetchMovieDbTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        final String BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String KEY_PARAM = "api_key";
        final String API_KEY = "ac4a7ebe43d27f86492ec2014ca66131";
        final String VIDEOS = "videos";
        final String REVIEWS = "reviews";

        Log.v("Luke", "FetchMovieTask params length "+params.length + "params[1]" + params[1]);
        mFetchType = params[1];

        try {
            Uri builtUri = null;
            switch (mFetchType) {
                case MOVIE_POSTER:
                    builtUri = Uri.parse(BASE_URL + params[0]).buildUpon()
                            .appendQueryParameter(KEY_PARAM, API_KEY).build();
                    break;
                case MOVIE_TRAILER:
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(params[0])
                            .appendPath(VIDEOS)
                            .appendQueryParameter(KEY_PARAM, API_KEY).build();
                    break;
                case MOVIE_REVIEW:
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(params[0])
                            .appendPath(REVIEWS)
                            .appendQueryParameter(KEY_PARAM, API_KEY).build();
                    break;
            }


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
        } catch (Exception e) {
            Log.v(LOG_TAG, "Connection Error: " + e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            mListener.onFetchFinished(response, mFetchType);
        } else {
            Log.v(LOG_TAG, "No Internet Conection! ");
        }
    }
}
