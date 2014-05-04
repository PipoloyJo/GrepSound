package com.grepsound.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.grepsound.R;
import com.grepsound.adapters.SectionsPagerAdapter;
import com.grepsound.image.ImageLoader;
import com.grepsound.model.Profile;
import com.grepsound.views.PagerSlidingTabStrip;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by lisional on 2014-04-21.
 */
public class MyProfileFragment extends Fragment implements RequestListener {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ImageView mUserCover;
    private ImageLoader mImgLoader;
    private Callbacks mCallbacks;

    public interface Callbacks {
        public void getProfile(RequestListener cb);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void getProfile(RequestListener cb) {
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

        mCallbacks.getProfile(this);
    }

    @Override
    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), getFragmentManager());
        mImgLoader = new ImageLoader(getActivity());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_myprofile, container, false);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        final PagerSlidingTabStrip strip = PagerSlidingTabStrip.class.cast(rootView.findViewById(R.id.pts_main));

        strip.setViewPager(mViewPager);

        mUserCover = (ImageView) rootView.findViewById(R.id.user_cover);


        return rootView;
    }

    @Override
    public void onRequestFailure(SpiceException e) {

    }

    @Override
    public void onRequestSuccess(Object o) {

        mImgLoader.DisplayImage(((Profile)o).getAvatarUrl(), mUserCover);
    }

}