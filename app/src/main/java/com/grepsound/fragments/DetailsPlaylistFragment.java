package com.grepsound.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.grepsound.R;
import com.grepsound.adapters.TrackAdapter;
import com.grepsound.model.PlayLists;
import com.grepsound.services.AudioService;

/**
 * Created by lisional on 2014-06-23.
 */
public class DetailsPlaylistFragment extends SlidingFragment {


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

        mAdapter = new TrackAdapter(getActivity());
        mAdapter.addAll(mDisplayed.getSet());

        GridView mGrid = (GridView) rootView.findViewById(R.id.tracks_grid);
        mGrid.setAdapter(mAdapter);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent request = new Intent(AudioService.commands.PLAY);
                request.putExtra(AudioService.fields.SONG, mAdapter.getItem(position));
                getActivity().sendBroadcast(request);
            }
        });

        rootView.setOnClickListener(clickListener);
        return rootView;
    }

 /*   @Override
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
    }
*/

}
