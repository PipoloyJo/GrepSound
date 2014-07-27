package com.grepsound.fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.grepsound.R;
import com.grepsound.adapters.TrackAdapter;
import com.grepsound.model.Tracks;
import com.grepsound.services.AudioService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by lisional on 2014-04-21.
 */
public class LikesFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener, RequestListener<Tracks> {

    private Callbacks mCallbacks = sDummyCallbacks;

    private static final String TAG = LikesFragment.class.getSimpleName();
    private TrackAdapter mAdapter;

    private AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent request = new Intent(AudioService.commands.PLAY);
            request.putExtra(AudioService.fields.SONG, mAdapter.getItem(position));
            getActivity().sendBroadcast(request);
        }
    };
    private ListView mListView;

    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        mListView.setSelectionFromTop(1, scrollHeight);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollTabHolder != null)
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, 0);
    }

    public interface Callbacks {
        public void getLikes(RequestListener<Tracks> cb);

        void getSong(RequestListener cb, String url);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void getLikes(RequestListener<Tracks> cb) {
        }

        @Override
        public void getSong(RequestListener cb, String url) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;

        mCallbacks.getLikes(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TrackAdapter(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.frag_likes, null);

        LinearLayout viewHeader = new LinearLayout(getActivity());
        viewHeader.setOrientation(LinearLayout.HORIZONTAL);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.header_height));
        viewHeader.setLayoutParams(lp);

        mListView = (ListView) rootView.findViewById(R.id.likes_grid);
        mListView.setOnItemClickListener(mClickListener);
        mListView.setOnScrollListener(this);
        mListView.addHeaderView(viewHeader);

        mListView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Log.e(TAG, "Failure");
    }

    @Override
    public void onRequestSuccess(Tracks tr) {
        Log.e(TAG, "Success");
        mAdapter.addAll(tr);
        mAdapter.notifyDataSetChanged();
    }
}
