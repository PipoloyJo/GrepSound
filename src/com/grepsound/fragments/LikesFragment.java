package com.grepsound.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.grepsound.R;
import com.grepsound.adapters.TrackAdapter;
import com.grepsound.model.Track;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

/**
 * Created by lisional on 2014-04-21.
 */
public class LikesFragment extends Fragment implements RequestListener {

    private Callbacks mCallbacks = sDummyCallbacks;

    private static final String TAG = LikesFragment.class.getSimpleName();
    private TrackAdapter mAdapter;

    public interface Callbacks {
        public void getLikes(RequestListener cb);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void getLikes(RequestListener cb) {
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

        mGrid.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Log.e(TAG, "Failure");
    }

    @Override
    public void onRequestSuccess(Object tr) {
        Log.e(TAG, "Success");
        mAdapter.addAll((ArrayList<Track>) tr);
        mAdapter.notifyDataSetChanged();
    }
}
