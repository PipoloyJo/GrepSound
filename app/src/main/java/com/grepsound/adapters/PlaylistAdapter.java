package com.grepsound.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.image.ImageLoader;
import com.grepsound.model.PlayLists;

import java.util.ArrayList;

/**
 * Created by lisional on 2014-04-22.
 */
public class PlaylistAdapter extends BaseAdapter {

    Context mContext;
    PlayLists mPlaylists;
    private LayoutInflater mInflater;
    private final ImageLoader mImageLoader;
    private final static String TAG = TrackAdapter.class.getSimpleName();

    public PlaylistAdapter(Context c, boolean headers) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
        mPlaylists = new PlayLists();
        mImageLoader = new ImageLoader(c);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_playlist, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.playlist_name);
        TextView duration = (TextView) convertView.findViewById(R.id.playlist_duration);
        ImageView cover = (ImageView) convertView.findViewById(R.id.playlist_cover);
        name.setText(mPlaylists.get(position).getTitle());
        duration.setText(mPlaylists.get(position).getDuration());
        mImageLoader.DisplayImage(mPlaylists.get(position).getCover(), cover);

        return convertView;
    }

    @Override
    public int getCount() {
        return mPlaylists.size();
    }

    @Override
    public PlayLists.Playlist getItem(int position) {
        return mPlaylists.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void removeAll() {
        mPlaylists.clear();
    }

    public void addAll(PlayLists tr) {
        mPlaylists.clear();
        mPlaylists.addAll(tr);
    }



}