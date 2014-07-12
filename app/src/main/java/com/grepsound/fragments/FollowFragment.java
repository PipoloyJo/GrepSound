package com.grepsound.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.grepsound.R;
import com.grepsound.adapters.GridUserAdapter;
import com.grepsound.model.Users;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by lisional on 2014-06-24.
 */
public class FollowFragment extends SlidingFragment implements RequestListener<Users> {

    public static enum TYPE {
        FOLLOWING,
        FOLLOWER
    }

    private Callbacks mCallbacks = sDummyCallbacks;

    private static final String TAG = LikesFragment.class.getSimpleName();
    private GridUserAdapter mAdapter;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //mCallbacks.displayPlaylistDetails(mAdapter.getItem(position));
        }
    };

    public interface Callbacks {

        public void getFollowByType(TYPE t, RequestListener<Users> cb);

    }

    private static Callbacks sDummyCallbacks = new Callbacks() {

        @Override
        public void getFollowByType(TYPE t, RequestListener<Users> cb) {

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new GridUserAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_follow, container, false);

        mCallbacks.getFollowByType((TYPE) getArguments().getSerializable("type"), this);

        GridView mGrid = (GridView) rootView.findViewById(R.id.follow_grid);
        mGrid.setOnItemClickListener(mItemClickListener);

        mGrid.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Log.e(TAG, "Failure");
    }

    @Override
    public void onRequestSuccess(Users users) {
        Log.e(TAG, "Success");
        mAdapter.addAll(users);
        mAdapter.notifyDataSetChanged();
    }

/*    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim)
    {
        int id = enter ? R.animator.slide_fragment_left : R.animator.slide_fragment_right;
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
    }*/

}
