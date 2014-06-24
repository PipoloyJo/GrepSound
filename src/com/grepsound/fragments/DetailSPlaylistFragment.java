package com.grepsound.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.grepsound.R;
import com.grepsound.adapters.TrackAdapter;
import com.grepsound.model.PlayLists;

/**
 * Created by lisional on 2014-06-23.
 */
public class DetailsPlaylistFragment extends Fragment {

    View.OnClickListener clickListener;
    OnDetailsPlaylistFragmentAnimationEndListener mListener;
    PlayLists.Playlist mDisplayed;
    TrackAdapter mAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.frag_details_playlist, container, false);

        try {
            mDisplayed = getArguments().getParcelable("DetailsPlaylistFragment");
        } catch (NullPointerException e) {
            throw new NullPointerException("DetailsPlaylistFragment must receive a playlist in arguments");
        }

        mAdapter = new TrackAdapter(getActivity(), true);
        mAdapter.addAll(mDisplayed.getSet());

        GridView mGrid = (GridView) rootView.findViewById(R.id.tracks_grid);
        mGrid.setAdapter(mAdapter);

        rootView.setOnClickListener(clickListener);
        return rootView;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim)
    {
        int id = enter ? R.animator.slide_fragment_in : R.animator.slide_fragment_out;
        final Animator anim = AnimatorInflater.loadAnimator(getActivity(), id);
        if (enter) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mListener.onAnimationEnd();
                }
            });
        }
        return anim;
    }

    public void setOnDetailsPlaylistFragmentAnimationEnd(OnDetailsPlaylistFragmentAnimationEndListener listener)
    {
        mListener = listener;
    }

    /**
     * This interface is used to inform the main activity when the entry
     * animation of the text fragment has completed in order to avoid the
     * start of a new animation before the current one has completed.
     */
    public interface OnDetailsPlaylistFragmentAnimationEndListener {
        public void onAnimationEnd();
    }
}