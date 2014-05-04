package com.grepsound.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.model.PlayLists;

import java.util.ArrayList;

/**
 * Created by lisional on 2014-04-22.
 */
public class PlaylistAdapter extends BaseAdapter implements SectionIndexer {

    Context mContext;
    PlayLists mPlaylists;

    private boolean isHeadersActivated;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;
    private LayoutInflater mInflater;
    private final static String TAG = TrackAdapter.class.getSimpleName();

    public PlaylistAdapter(Context c, boolean headers) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
        mPlaylists = new PlayLists();
        isHeadersActivated = headers;
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        if (mPlaylists.isEmpty() || !isHeadersActivated)
            return new int[0];
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        char lastFirstChar = mPlaylists.get(0).getTitle().charAt(0);
        sectionIndices.add(0);
        for (int i = 1; i < mPlaylists.size(); i++) {
            if (mPlaylists.get(i).getTitle().charAt(0) != lastFirstChar) {
                lastFirstChar = mPlaylists.get(i).getTitle().charAt(0);
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
        if (mPlaylists.isEmpty() || !isHeadersActivated)
            return new Character[0];
        Character[] letters = new Character[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = mPlaylists.get(mSectionIndices[i]).getTitle().charAt(0);
        }
        return letters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_playlist, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.playlist_name);
        TextView duration = (TextView) convertView.findViewById(R.id.playlist_duration);
        name.setText(mPlaylists.get(position).getTitle());
        duration.setText(mPlaylists.get(position).getDuration());
        return convertView;

    }

    @Override
    public int getCount() {
        return mPlaylists.size();
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
    public PlayLists.Playlist getItem(int position) {
        return mPlaylists.get(position);
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

        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }



}