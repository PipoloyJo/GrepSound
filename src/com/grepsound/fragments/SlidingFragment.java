package com.grepsound.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lisional on 2014-06-25.
 */
public abstract class SlidingFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    public abstract void setClickListener(View.OnClickListener clickListener);

    public abstract void setOnSlidingFragmentAnimationEnd(OnSlidingFragmentAnimationEndListener listener);

    /**
     * This interface is used to inform the main activity when the entry
     * animation of the text fragment has completed in order to avoid the
     * start of a new animation before the current one has completed.
     */
    public interface OnSlidingFragmentAnimationEndListener {
        public void onAnimationEnd();
    }
}
