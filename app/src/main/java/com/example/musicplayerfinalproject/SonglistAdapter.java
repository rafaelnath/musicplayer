package com.example.musicplayerfinalproject;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

class SonglistAdapter extends ArrayAdapter<String> {
    private static final String TAG = "SonglistAdapter";
    private Context mContext;
    MainActivity mainActivity = new MainActivity();
    int mResource;

    public SonglistAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String song = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);


        TextView songName = (TextView) convertView.findViewById(R.id.textView);
        songName.setText(song);
        /*if(position == mainActivity.songPos) {songName.setTextColor(Color.parseColor("#4CAF50"));*/
        return convertView;
    }
}
