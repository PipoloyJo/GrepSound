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
import com.grepsound.model.Tracks;

import java.util.ArrayList;

public class TrackAdapter extends BaseAdapter implements SectionIndexer {

	Context mContext;
	Tracks tracks;

	private boolean isHeadersActivated;
	private int[] mSectionIndices;
	private Character[] mSectionLetters;
	private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
	private final static String TAG = TrackAdapter.class.getSimpleName();

	public TrackAdapter(Context c, boolean headers) {
		mContext = c;
		mInflater = LayoutInflater.from(c);
		tracks = new Tracks();
		isHeadersActivated = headers;
        mImageLoader = new ImageLoader(c);
		mSectionIndices = getSectionIndices();
		mSectionLetters = getSectionLetters();
	}

	private int[] getSectionIndices() {
		if (tracks.isEmpty() || !isHeadersActivated)
			return new int[0];
		ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
		char lastFirstChar = tracks.get(0).getTitle().charAt(0);
		sectionIndices.add(0);
		for (int i = 1; i < tracks.size(); i++) {
			if (tracks.get(i).getTitle().charAt(0) != lastFirstChar) {
				lastFirstChar = tracks.get(i).getTitle().charAt(0);
				sectionIndices.add(i);
			}
		}
		int[] sections = new int[sectionIndices.size()];
		for (int i = 0; i < sectionIndices.size(); i++) {
			sections[i] = sectionIndices.get(i);
		}
		return sections;
	}

	private Character[] getSectionLetters() {
		if (tracks.isEmpty() || !isHeadersActivated)
			return new Character[0];
		Character[] letters = new Character[mSectionIndices.length];
		for (int i = 0; i < mSectionIndices.length; i++) {
			letters[i] = tracks.get(mSectionIndices[i]).getTitle().charAt(0);
		}
		return letters;
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
	public int getViewTypeCount() {
		return isHeadersActivated ? 2 : 1;
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= mSectionIndices.length) {
			section = mSectionIndices.length - 1;
		} else if (section < 0) {
			section = 0;
		}
		return mSectionIndices[section];
	}

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < mSectionIndices.length; i++) {
			if (position < mSectionIndices[i]) {
				return i - 1;
			}
		}
		return mSectionIndices.length - 1;
	}

	@Override
	public Object[] getSections() {
		return mSectionLetters;
	}

	@Override
	public Tracks.Track getItem(int position) {
		return tracks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void removeAll() {
		tracks.clear();
	}

	public void addAll(Tracks tr) {
        tracks.clear();
		tracks = tr;

		mSectionIndices = getSectionIndices();
		mSectionLetters = getSectionLetters();
	}
}