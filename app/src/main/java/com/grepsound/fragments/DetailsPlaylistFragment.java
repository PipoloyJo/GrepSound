package com.grepsound.fragments;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import com.grepsound.R;
import com.grepsound.adapters.TrackAdapter;
import com.grepsound.model.Playlist;
import com.grepsound.services.AudioService;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-06-23.
 */
 public class DetailsPlaylistFragment extends SlidingFragment {

    Playlist mDisplayed;
    TrackAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActivity().getActionBar();

        try {
            mDisplayed = getArguments().getParcelable("DetailsPlaylistFragment");
        } catch (NullPointerException e) {
            throw new NullPointerException("DetailsPlaylistFragment must receive a playlist in arguments");
        }

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        actionBar.setTitle(mDisplayed.getTitle());
        getActionBarIconView().setAlpha(1f);
    }

    private ImageView getActionBarIconView() {
        return (ImageView) getActivity().getWindow().getDecorView().findViewById(android.R.id.home);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.frag_details_playlist, container, false);



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
