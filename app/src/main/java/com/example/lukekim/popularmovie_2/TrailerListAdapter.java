package com.example.lukekim.popularmovie_2;

/**
 * Created by lukekim on 8/4/16.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lukekim.popularmovie_2.data.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TrailerListAdapter extends ArrayAdapter<Trailer> {

    private final static String LOG_TAG = TrailerListAdapter.class.getSimpleName();
    private final Context context;
    private final ArrayList<Trailer> mTrailers;
    ImageView mThumbnailView;
    TextView mTrailerTitle;
    public Trailer mTrailer;


    public TrailerListAdapter(Context context, ArrayList<Trailer> trailers) {
        super(context, -1, trailers);
        this.context = context;
        this.mTrailers = trailers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.trailer_list_content, parent, false);
        final Trailer trailer = mTrailers.get(position);
        final Context context = rowView.getContext();

        mThumbnailView = (ImageView)rowView.findViewById(R.id.trailer_thumbnail);
        mTrailerTitle = (TextView)rowView.findViewById(R.id.textview_trailer_title);
        mTrailerTitle.setText(trailer.getName());

        mTrailer = trailer;

        String thumbnailUrl = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";

        Picasso.with(context)
                .load(thumbnailUrl)
                .config(Bitmap.Config.RGB_565)
                .into(mThumbnailView);


        return rowView;
    }

    public void addAll(ArrayList<Trailer> trailers) {
        mTrailers.clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }

    public ArrayList<Trailer> getTrailers() {
        return mTrailers;
    }
}
