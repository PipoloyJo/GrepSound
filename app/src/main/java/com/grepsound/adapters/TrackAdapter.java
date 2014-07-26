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
import com.grepsound.model.Track;
import com.grepsound.model.Tracks;

import java.util.ArrayList;

public class TrackAdapter extends BaseAdapter {

	Context mContext;
	Tracks tracks;

	private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
	private final static String TAG = TrackAdapter.class.getSimpleName();

	public TrackAdapter(Context c) {
		mContext = c;
		mInflater = LayoutInflater.from(c);
		tracks = new Tracks();
        mImageLoader = new ImageLoader(c);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_track, parent, false);
		}

		TextView name = (TextView) convertView.findViewById(R.id.track_name);
		TextView duration = (TextView) convertView.findViewById(R.id.track_duration);
        ImageView cover = (ImageView) convertView.findViewById(R.id.track_cover);
		name.setText(tracks.get(position).getTitle());
		duration.setText(tracks.get(position).getDuration());
        mImageLoader.DisplayImage(tracks.getImageUrlOf(position), cover);
		return convertView;

	}

    /**
     *
     * ViewHolder
     * @return
     */

    private class TrackViewHolder {
        ImageView photo;
        TextView name;
        TextView duration;
    }

	@Override
	public int getCount() {
		return tracks.size();
	}
	@Override
	public Track getItem(int position) {
		return tracks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void addAll(Tracks tr) {
        if(tr == null)
            return;
        tracks.clear();
        tracks = tr;
	}
}
