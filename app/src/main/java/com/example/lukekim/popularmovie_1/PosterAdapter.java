package com.example.lukekim.popularmovie_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class PosterAdapter extends BaseAdapter {


    private Context mContext;


    public PosterAdapter(Context c) {
        mContext = c;
    }


    public int getCount() {
        //return MainActivity.images.size();
        return 4;
    }


    public Object getItem(int position) {
        return null;
    }


    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView poster;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            poster = (ImageView) inflater.inflate(R.layout.movie_poster, parent, false);
        } else {
            poster = (ImageView) convertView;
        }
        //poster.setImageResource(R.mipmap.poster);

        //Picasso.with(mContext).load(MainActivity.images.get(position)).into(poster);
        Picasso.with(mContext).load(R.mipmap.poster).into(poster);
        return poster;
    }

}

