package com.grepsound.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.image.ImageLoader;
import com.grepsound.model.User;
import com.grepsound.model.Users;

/**
 * Created by lisional on 2014-06-26.
 */
public class GridUserAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    Users mUsers;
    Context mContext;
    private ImageLoader mImageLoader;
    private final static String TAG = TrackAdapter.class.getSimpleName();

    public GridUserAdapter(Context c) {
        mContext = c;
        mUsers = new Users();
        mInflater = LayoutInflater.from(c);
        mImageLoader = new ImageLoader(c);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        UserView viewholder = new UserView();

        if (v == null) {
            v = mInflater.inflate(R.layout.item_user, null);
            viewholder.cover = (ImageView) v.findViewById(R.id.user_cover);
            viewholder.num_album = (TextView) v.findViewById(R.id.user_num_albums);
            viewholder.num_tracks = (TextView) v.findViewById(R.id.user_num_tracks);
            viewholder.title = (TextView) v.findViewById(R.id.user_name);
            v.setTag(viewholder);
        } else {
            viewholder = (UserView) v.getTag();
        }

        User toDisplay = mUsers.get(position);

        mImageLoader.DisplayImage(toDisplay.getLargeAvatarUrl(), viewholder.cover);

        viewholder.num_album.setText("Alb ");
        viewholder.num_tracks.setText("Trk ");
        viewholder.title.setText("COUCOU");
        return v;
    }

        @Override
        public int getCount() {
        return mUsers.size();
    }

        @Override
        public User getItem(int position) {
        return mUsers.get(position);
    }

        @Override
        public long getItemId(int position) {
        return position;
    }

    public void removeAll() {
        mUsers.clear();
    }

    public void add(User artist) {
        mUsers.add(artist);
    }

    public void addAll(Users list) {
        mUsers.clear();
        mUsers.addAll(list);
    }

    public Users getAll() {
        return mUsers;
    }

    public class UserView {
        ImageView cover;
        TextView title;
        TextView num_album;
        TextView num_tracks;
    }
}
