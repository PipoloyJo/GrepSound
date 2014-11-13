package com.grepsound.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.image.ImageLoader;
import com.grepsound.model.Track;
import com.grepsound.model.Tracks;


public class TrackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Tracks mTracks;
    private ImageLoader mImageLoader;
    private final static String TAG = TrackAdapter.class.getSimpleName();
    TrackViewHolder.ITrackClick mListener;

    private interface TYPES {
        int HEADER = 0;
        int TRACK = 1;
    }

    public TrackAdapter(Context c, TrackViewHolder.ITrackClick list) {
        mListener = list;
        mTracks = new Tracks();
        mImageLoader = new ImageLoader(c);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder result = null;
        switch (viewType) {
            case TYPES.TRACK:
                // create a new view
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
                // set the view's size, margins, paddings and layout parameters
                result = new TrackViewHolder(v);
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
            ((TrackViewHolder)holder).name.setText(mTracks.get(position).getTitle());
            ((TrackViewHolder)holder).duration.setText(mTracks.get(position).getDuration());
            mImageLoader.DisplayImage(mTracks.getImageUrlOf(position), ((TrackViewHolder) holder).cover);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTrackClicked(mTracks.get((Integer) v.getTag()));
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == TYPES.HEADER)
            return TYPES.HEADER;
        else
           return TYPES.TRACK;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public void addAll(Tracks tr) {
        if (tr == null)
            return;
        mTracks.clear();
        mTracks = tr;
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {

        public ImageView cover;
        public TextView name;
        public TextView duration;

        public TrackViewHolder(View v) {
            super(v);

            name = (TextView) v.findViewById(R.id.track_name);
            duration = (TextView) v.findViewById(R.id.track_duration);
            cover = (ImageView) v.findViewById(R.id.track_cover);
        }

        public static interface ITrackClick {
            public void onTrackClicked(Track tr);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View v) {
            super(v);
        }
    }
}