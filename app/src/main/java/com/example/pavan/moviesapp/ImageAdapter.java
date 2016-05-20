package com.example.pavan.moviesapp;


import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import Utils.AndroidUtil;


/**
 * Created by pavan on 3/11/2016.
 */
public class ImageAdapter extends BaseAdapter {

    private final String LOG_TAG = getClass().getSimpleName();
    public ImageView movie_posters_imageView;
    ArrayList<String> moviePosters = new ArrayList<>();
    Context context;
    String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";

    Picasso picasso;

    private AndroidUtil androidUtil;



    public ImageAdapter(Context context, ArrayList<String> Posters) {
        this.context = context;
        this.moviePosters = Posters;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    @Override
    public int getCount() {
        return moviePosters.size();
    }

    @Override
    public Object getItem(int position) {

        return moviePosters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return moviePosters.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.movie_image, parent, false);
            movie_posters_imageView = (ImageView) convertView.findViewById(R.id.movie_poster_image_view);
            androidUtil = new AndroidUtil(context);
            picasso = Picasso.with(context);
        }

        else
            movie_posters_imageView = (ImageView) convertView;

        if (androidUtil.isOnline())
            picasso
                 .load(BASE_POSTER_URL + moviePosters.get(position))
                .noFade()
                .resize(185 * 2, 278 * 2)
                .into(movie_posters_imageView);
        else
            picasso
                    .load(Uri.fromFile(new File(moviePosters.get(position))))
                    .noFade()
                    .resize(185 * 2, 278 * 2)
                    .into(movie_posters_imageView);



        return movie_posters_imageView;
    }

}

