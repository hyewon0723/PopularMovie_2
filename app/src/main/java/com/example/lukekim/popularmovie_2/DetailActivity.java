package com.example.lukekim.popularmovie_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity {
    private final String LOG_TAG =DetailActivity.class.getSimpleName();
    Movie movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        movie =intent.getExtras().getParcelable("movie");

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
