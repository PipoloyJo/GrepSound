package com.grepsound.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.grepsound.R;
import com.grepsound.image.ImageLoader;
import com.grepsound.model.Playlists;
import com.grepsound.model.Playlist;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-04-22.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Playlists mPlaylists;
    private final ImageLoader mImageLoader;
    private final static String TAG = PlaylistAdapter.class.getSimpleName();
    PlaylistViewHolder.IPlaylistClick mListener;

    private interface TYPES {
        int HEADER = 0;
        int PLAYLIST = 1;
    }

    public PlaylistAdapter(Context c, PlaylistViewHolder.IPlaylistClick list) {
        mListener = list;
        mPlaylists = new Playlists();
        mImageLoader = new ImageLoader(c);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder result = null;
        switch (viewType) {
            case TYPES.PLAYLIST:
                // create a new view
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
                // set the view's size, margins, paddings and layout parameters
                result = new PlaylistViewHolder(v);
                break;
            case TYPES.HEADER :
                LinearLayout viewHeader = new LinearLayout(parent.getContext());
                viewHeader.setOrientation(LinearLayout.HORIZONTAL);
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        parent.getContext().getResources().getDimensionPixelSize(R.dimen.header_height));
                viewHeader.setLayoutParams(lp);
                result = new HeaderViewHolder(viewHeader);
                break;
        }

        return result;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (position != TYPES.HEADER) {
            ((PlaylistViewHolder)holder).name.setText(mPlaylists.get(position).getTitle());
            ((PlaylistViewHolder)holder).duration.setText(mPlaylists.get(position).getDuration());
            mImageLoader.DisplayImage(mPlaylists.get(position).getCover(), ((PlaylistViewHolder) holder).cover);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPlaylistClicked(mPlaylists.get((Integer) v.getTag()));
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == TYPES.HEADER)
            return TYPES.HEADER;
        else
            return TYPES.PLAYLIST;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    public void addAll(Playlists tr) {
        mPlaylists.clear();
        mPlaylists.addAll(tr);
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {

        public ImageView cover;
        public TextView name;
        public TextView duration;

        public PlaylistViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.playlist_name);
            duration = (TextView) v.findViewById(R.id.playlist_duration);
            cover = (ImageView) v.findViewById(R.id.playlist_cover);
        }

        public static interface IPlaylistClick {
            public void onPlaylistClicked(Playlist pl);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View v) {
            super(v);
        }
    }
}