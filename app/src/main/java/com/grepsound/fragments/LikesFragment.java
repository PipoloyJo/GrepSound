package com.grepsound.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.grepsound.R;
import com.grepsound.adapters.TrackAdapter;
import com.grepsound.model.Tracks;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

/**
 * Created by lisional on 2014-04-21.
 */
public class LikesFragment extends Fragment implements RequestListener<Tracks> {

    private Callbacks mCallbacks = sDummyCallbacks;

    private static final String TAG = LikesFragment.class.getSimpleName();
    private TrackAdapter mAdapter;
    private AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mCallbacks.getSong(LikesFragment.this, mAdapter.getItem(position).getUrl().toString());
        }
    };

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
        mAdapter = new TrackAdapter(getActivity(), true);
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

        GridView mGrid = (GridView) rootView.findViewById(R.id.likes_grid);
        mGrid.setOnItemClickListener(mClickListener);

        mGrid.setAdapter(mAdapter);

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