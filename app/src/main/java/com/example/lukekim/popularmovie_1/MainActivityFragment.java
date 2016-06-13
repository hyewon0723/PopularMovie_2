package com.example.lukekim.popularmovie_1;

import android.content.res.Configuration;
import android.graphics.Movie;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayList<Movie> moviesList;
    ArrayList<String> posters;
    GridView gridview;
    PosterAdapter posterAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        if(savedInstanceState != null && savedInstanceState.containsKey("movies")) {
//           // moviesList = savedInstanceState.getParcelableArrayList("movies");
//            posters = savedInstanceState.getStringArrayList("images");
//        }
//        else {
//            moviesList = new ArrayList<Movie>();
//            posters = new ArrayList<String>();
//            posterAdapter = new PosterAdapter(getActivity());
//        }
        posterAdapter = new PosterAdapter(getActivity());
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        gridview = (GridView) rootView.findViewById(R.id.grid_view);
        int orientation = getResources().getConfiguration().orientation;
        gridview.setNumColumns(orientation == Configuration.ORIENTATION_LANDSCAPE ? 3: 2);
        gridview.setAdapter(posterAdapter);
        return rootView;
    }
}
