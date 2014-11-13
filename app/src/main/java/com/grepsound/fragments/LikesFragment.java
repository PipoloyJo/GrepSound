package com.grepsound.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.grepsound.R;
import com.grepsound.adapters.TrackAdapter;
import com.grepsound.model.Track;
import com.grepsound.model.Tracks;
import com.grepsound.services.AudioService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class LikesFragment extends ScrollTabHolderFragment implements
        OnRefreshListener,
        RequestListener<Tracks>,
        TrackAdapter.TrackViewHolder.ITrackClick {

    private Callbacks mCallbacks = sDummyCallbacks;

    private static final String TAG = LikesFragment.class.getSimpleName();
    private TrackAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private PullToRefreshLayout mPullToRefreshLayout;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void adjustScroll(int scrollHeight) {
        Log.i(TAG, "adjust: " +scrollHeight);
        if (scrollHeight == 0 && mLayoutManager.findFirstVisibleItemPosition() >= 1) {
            return;
        }
        mLayoutManager.scrollToPositionWithOffset(1, scrollHeight);
    }


    @Override
    public void onRefreshStarted(View view) {
        Log.i(TAG, "onRefreshStarted");
        mCallbacks.getLikes(this, true);
    }

    @Override
    public void onTrackClicked(Track tr) {
        Intent request = new Intent(AudioService.commands.PLAY);
        request.putExtra(AudioService.fields.SONG, tr);
        getActivity().sendBroadcast(request);
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
        mAdapter = new TrackAdapter(getActivity(), this);
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



        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.likes_grid);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mScrollTabHolder != null)
                    mScrollTabHolder.onScroll(recyclerView, mLayoutManager.findFirstVisibleItemPosition(), 0);

                if(mPullToRefreshLayout != null)
                    mPullToRefreshLayout.setRefreshing(false);
                }
        });

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
