package com.grepsound.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.grepsound.R;
import com.grepsound.adapters.SectionsPagerAdapter;
import com.grepsound.views.PagerSlidingTabStrip;

/**
 * Created by lisional on 2014-04-21.
 */
public class MyProfileFragment extends Fragment {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), getFragmentManager());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_myprofile, container, false);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        final PagerSlidingTabStrip strip = PagerSlidingTabStrip.class.cast(rootView.findViewById(R.id.pts_main));

        strip.setViewPager(mViewPager);

        return rootView;
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_ALPHA = .6f;

        public ZoomOutPageTransformer() {
            super();
        }

        @Override
        public void transformPage(View page, float position) {
            final float normalizedposition = Math.abs(Math.abs(position) - 1);
            page.setAlpha(MIN_ALPHA + (1.f - MIN_ALPHA) * normalizedposition);
        }
    }


}