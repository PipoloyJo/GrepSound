package com.grepsound.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.grepsound.R;

/**
 * Created by lisional on 2014-06-23.
 */
public class DetailsPlaylistFragment extends Fragment {

    View.OnClickListener clickListener;
    OnDetailsPlaylistFragmentAnimationEndListener mListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_details_playlist, container, false);
        view.setOnClickListener(clickListener);
        return view;
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
