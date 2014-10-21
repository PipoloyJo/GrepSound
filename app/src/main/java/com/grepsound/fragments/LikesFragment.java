package com.grepsound.fragments;

import android.app.Activity;
import android.content.Intent;
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
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class LikesFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener,
        OnRefreshListener,
        RequestListener<Tracks> {

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
    private PullToRefreshLayout mPullToRefreshLayout;

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

        if(mPullToRefreshLayout != null) {
            mPullToRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        Log.i(TAG, "onRefreshStarted");
        mCallbacks.getLikes(this, true);
    }

    public interface Callbacks {
        public void getLikes(RequestListener<Tracks> cb, boolean force);

        void getSong(RequestListener cb, String url);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void getLikes(RequestListener<Tracks> cb, boolean force) {
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

        mCallbacks.getLikes(this, false);
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
        mListView.addHeaderView(viewHeader, null, false);

        mListView.setAdapter(mAdapter);

        // Now find the PullToRefreshLayout to setup
        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.ptr_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                .options(Options.create()
                        // Here we make the refresh scroll distance to 75% of the refreshable view's height
                        .scrollDistance(.25f)
                                // Here we define a custom header transformer which will alter the header
                                // based on the current pull-to-refresh state
                        .build())
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set a OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        return rootView;
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Log.e(TAG, "Failure");
        mPullToRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRequestSuccess(Tracks tr) {
        Log.e(TAG, "Success");
        mAdapter.addAll(tr);
        mAdapter.notifyDataSetChanged();
        mPullToRefreshLayout.setRefreshing(false);
    }
}
