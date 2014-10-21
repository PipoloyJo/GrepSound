package com.grepsound.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.grepsound.R;
import com.grepsound.adapters.PlaylistAdapter;
import com.grepsound.model.Playlists;
import com.grepsound.model.Playlist;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-04-21.
 */

public class PlaylistsFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener, OnRefreshListener, RequestListener<Playlists> {

    private Callbacks mCallbacks = sDummyCallbacks;

    private static final String TAG = PlaylistsFragment.class.getSimpleName();
    private PlaylistAdapter mAdapter;
    private ListView mListView;

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCallbacks.displayPlaylistDetails(mAdapter.getItem(position));
        }
    };
    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollTabHolder != null)
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, 1);

        if(mPullToRefreshLayout != null) {
            mPullToRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        mListView.setSelectionFromTop(1, scrollHeight);

    }

    @Override
    public void onRefreshStarted(View view) {
        Log.i(TAG, "onRefreshStarted");
        mCallbacks.getPlaylists(this, true);
    }

    public interface Callbacks {
        public void getPlaylists(RequestListener<Playlists> cb, boolean force);

        public void displayPlaylistDetails(Playlist selected);

    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void getPlaylists(RequestListener<Playlists> cb, boolean force) {
        }

        @Override
        public void displayPlaylistDetails(Playlist selected) {

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
        mCallbacks.getPlaylists(this, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PlaylistAdapter(getActivity(), true);
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

        View rootView = inflater.inflate(R.layout.frag_playlists, null);

        LinearLayout viewHeader = new LinearLayout(getActivity());
        viewHeader.setOrientation(LinearLayout.HORIZONTAL);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.header_height));
        viewHeader.setLayoutParams(lp);


        mListView = (ListView) rootView.findViewById(R.id.playlists_grid);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(mItemClickListener);
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
    public void onRequestSuccess(Playlists tr) {
        Log.e(TAG, "Success");
        mAdapter.addAll(tr);
        mAdapter.notifyDataSetChanged();
        mPullToRefreshLayout.setRefreshing(false);
    }
}
