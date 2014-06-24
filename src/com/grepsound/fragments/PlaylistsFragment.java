package com.grepsound.fragments;

import android.animation.*;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.grepsound.R;
import com.grepsound.adapters.PlaylistAdapter;
import com.grepsound.model.PlayLists;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by lisional on 2014-04-21.
 */
public class PlaylistsFragment extends Fragment implements RequestListener<PlayLists>,
                                                           DetailsPlaylistFragment.OnDetailsPlaylistFragmentAnimationEndListener,
                                                           FragmentManager.OnBackStackChangedListener{

    private Callbacks mCallbacks = sDummyCallbacks;
    DetailsPlaylistFragment mDetailsFragment;
    View mDarkHoverView;

    boolean mDidSlideOut = false;
    boolean mIsAnimating = false;

    private static final String TAG = LikesFragment.class.getSimpleName();
    private PlaylistAdapter mAdapter;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switchFragments();
        }
    };

    public interface Callbacks {
        public void getPlaylists(RequestListener<PlayLists> cb);

    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void getPlaylists(RequestListener<PlayLists> cb) {
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

        mCallbacks.getPlaylists(this);
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

        GridView mGrid = (GridView) rootView.findViewById(R.id.playlists_grid);
        mGrid.setOnItemClickListener(mItemClickListener);

        mGrid.setAdapter(mAdapter);

        mDarkHoverView = rootView.findViewById(R.id.dark_hover_view);
        mDarkHoverView.setAlpha(0);

        mDetailsFragment = new DetailsPlaylistFragment();

        getFragmentManager().addOnBackStackChangedListener(this);

        mDetailsFragment.setClickListener(mClickListener);
        mDetailsFragment.setOnDetailsPlaylistFragmentAnimationEnd(this);
        mDarkHoverView.setOnClickListener(mClickListener);

        return rootView;
    }

    View.OnClickListener mClickListener = new View.OnClickListener () {
        @Override
        public void onClick(View view) {
            switchFragments();
        }
    };

    /**
     * This method is used to toggle between the two fragment states by
     * calling the appropriate animations between them. The entry and exit
     * animations of the text fragment are specified in R.animator resource
     * files. The entry and exit animations of the image fragment are
     * specified in the slideBack and slideForward methods below. The reason
     * for separating the animation logic in this way is because the translucent
     * dark hover view must fade in at the same time as the image fragment
     * animates into the background, which would be difficult to time
     * properly given that the setCustomAnimations method can only modify the
     * two fragments in the transaction.
     */
    private void switchFragments () {
        if (mIsAnimating) {
            return;
        }
        mIsAnimating = true;
        if (mDidSlideOut) {
            mDidSlideOut = false;
            getFragmentManager().popBackStack();
        } else {
            mDidSlideOut = true;

            Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator arg0) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.animator.slide_fragment_in, 0, 0,
                            R.animator.slide_fragment_out);
                    transaction.add(R.id.move_to_back_container, mDetailsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            };
            slideBack (listener);
        }
    }

    @Override
    public void onBackStackChanged() {
        if (!mDidSlideOut) {
            slideForward(null);
        }

    }

    /**
     * This method animates the image fragment into the background by both
     * scaling and rotating the fragment's view, as well as adding a
     * translucent dark hover view to inform the user that it is inactive.
     */
    public void slideBack(Animator.AnimatorListener listener)
    {
        View movingFragmentView = getView();

        PropertyValuesHolder rotateX =  PropertyValuesHolder.ofFloat("rotationX", 40f);
        PropertyValuesHolder scaleX =  PropertyValuesHolder.ofFloat("scaleX", 0.8f);
        PropertyValuesHolder scaleY =  PropertyValuesHolder.ofFloat("scaleY", 0.8f);
        ObjectAnimator movingFragmentAnimator = ObjectAnimator.
                ofPropertyValuesHolder(movingFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator = ObjectAnimator.
                ofFloat(mDarkHoverView, "alpha", 0.0f, 0.5f);

        ObjectAnimator movingFragmentRotator = ObjectAnimator.
                ofFloat(movingFragmentView, "rotationX", 0);
        movingFragmentRotator.setStartDelay(getResources().
                getInteger(R.integer.half_slide_up_down_duration));

        AnimatorSet s = new AnimatorSet();
        s.playTogether(movingFragmentAnimator, darkHoverViewAnimator, movingFragmentRotator);
        s.addListener(listener);
        s.start();
    }

    /**
     * This method animates the image fragment into the foreground by both
     * scaling and rotating the fragment's view, while also removing the
     * previously added translucent dark hover view. Upon the completion of
     * this animation, the image fragment regains focus since this method is
     * called from the onBackStackChanged method.
     */
    public void slideForward(Animator.AnimatorListener listener)
    {
        View movingFragmentView = getView();

        PropertyValuesHolder rotateX =  PropertyValuesHolder.ofFloat("rotationX", 40f);
        PropertyValuesHolder scaleX =  PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder scaleY =  PropertyValuesHolder.ofFloat("scaleY", 1.0f);
        ObjectAnimator movingFragmentAnimator = ObjectAnimator.
                ofPropertyValuesHolder(movingFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator = ObjectAnimator.
                ofFloat(mDarkHoverView, "alpha", 0.5f, 0.0f);

        ObjectAnimator movingFragmentRotator = ObjectAnimator.
                ofFloat(movingFragmentView, "rotationX", 0);
        movingFragmentRotator.setStartDelay(
                getResources().getInteger(R.integer.half_slide_up_down_duration));

        AnimatorSet s = new AnimatorSet();
        s.playTogether(movingFragmentAnimator, movingFragmentRotator, darkHoverViewAnimator);
        s.setStartDelay(getResources().getInteger(R.integer.slide_up_down_duration));
        s.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
            }
        });
        s.start();
    }

    public void onAnimationEnd() {
        mIsAnimating = false;
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Log.e(TAG, "Failure");
    }

    @Override
    public void onRequestSuccess(PlayLists tr) {
        Log.e(TAG, "Success");
        mAdapter.addAll(tr);
        mAdapter.notifyDataSetChanged();
    }
}
