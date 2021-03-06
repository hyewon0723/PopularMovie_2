package com.example.lukekim.popularmovie_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.example.lukekim.popularmovie_2.data.Movie;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.DataPassListener {
    private ProgressBar bar;
    private boolean mTwoPane;
    private MainActivityFragment mFragment;
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MDFTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPane = findViewById(R.id.container_detail) != null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void itemSelected(Movie data) {
        if (mTwoPane) {
            DetailActivityFragment fragment = new DetailActivityFragment();
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.ARG_MOVIE, data);
            fragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.container_detail, fragment, MOVIE_DETAIL_FRAGMENT_TAG);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivityFragment.ARG_MOVIE, data);
            startActivity(intent);
        }
    }
}
