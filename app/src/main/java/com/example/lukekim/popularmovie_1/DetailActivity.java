package com.example.lukekim.popularmovie_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {
    private final String LOG_TAG =DetailActivity.class.getSimpleName();
    Movie movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "URL Real DetailActivity 0 getTitle() ");
        Intent intent = getIntent();
        movie =intent.getExtras().getParcelable("movie");
        Log.v(LOG_TAG, "URL Real DetailActivity 1  "+movie  +" getTitle() "+movie.getTitle());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public Movie getMovie() {
        return movie;
    }

}
