package com.grepsound.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

 public abstract class SlidingFragment extends Fragment {

    View.OnClickListener clickListener;
    OnSlidingFragmentAnimationEndListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnSlidingFragmentAnimationEnd(OnSlidingFragmentAnimationEndListener listener)
    {
        mListener = listener;
    }

    /**
     * This interface is used to inform the main activity when the entry
     * animation of the text fragment has completed in order to avoid the
     * start of a new animation before the current one has completed.
     */
    public interface OnSlidingFragmentAnimationEndListener {
        public void onAnimationEnd();
    }
}
