package com.example.lukekim.popularmovie_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PosterAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> posters;

    public PosterAdapter(Context context) {
        this.context = context;
        posters = new ArrayList<String>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView poster;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            poster = (ImageView) inflater.inflate(R.layout.movie_poster, parent, false);
        } else {
            poster = (ImageView) convertView;
        }

        Picasso.with(context).load(posters.get(position)).into(poster);

        return poster;
    }

    public int getCount() {
        return posters.size();
    }


    public Object getItem(int position) {
        return null;
    }


    public void addAll(ArrayList<String> images) {
        posters.addAll(images);
        notifyDataSetChanged();
    }

    public long getItemId(int position) {
        return 0;
    }

}

