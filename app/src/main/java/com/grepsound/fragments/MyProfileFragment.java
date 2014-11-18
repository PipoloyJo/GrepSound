package com.grepsound.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.adapters.SectionsPagerAdapter;
import com.grepsound.image.ImageLoader;
import com.grepsound.model.User;
import com.grepsound.views.AlphaForegroundColorSpan;
import com.grepsound.views.PagerSlidingTabStrip;
import com.grepsound.views.TwoTextCounter;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-04-21.
 */

public class MyProfileFragment extends Fragment implements ScrollTabHolder, ViewPager.OnPageChangeListener, RequestListener<User> {

    private SectionsPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private ImageView mUserCover;
    private TextView mName, mCity;
    private TwoTextCounter mFollowers, mFollowing;
    private ImageLoader mImgLoader;
    private Callbacks mCallbacks;

    private static final String TAG = MyProfileFragment.class.getSimpleName();
    private static AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();
    private View mHeader;
    private int mActionBarHeight;
    private int mMinHeaderHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private int lastScrollY, deltaY;

    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();

    private SpannableString mSpannableString;
    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private User mProfile;

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }

    @Override
    public void onPageSelected(int position) {
        SparseArray<ScrollTabHolder> scrollTabHolders = mPagerAdapter.getScrollTabHolders();
        ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);

        if(mHeader != null)
            currentHolder.adjustScroll((int) (mHeader.getHeight() + mHeader.getTranslationY()));
    }

    @Override
    public void onScroll(RecyclerView view, int firstVisiblePos, int pagePosition) {
        if (mViewPager.getCurrentItem() == pagePosition) {
            int scrollY = getScrollY(view, firstVisiblePos);

            deltaY += lastScrollY - scrollY;

            if(deltaY > mCallbacks.getToolbar().getHeight()) {
                deltaY = mCallbacks.getToolbar().getHeight();
            }

            if(deltaY < -mCallbacks.getToolbar().getHeight()) {
                deltaY = -mCallbacks.getToolbar().getHeight();
            }


            if(scrollY > lastScrollY) {
                // We are scrolling down
                mCallbacks.getToolbar().setTranslationY(Math.min(deltaY, 0));
            } else {
                // We are scrolling up
                mCallbacks.getToolbar().setTranslationY(Math.min(deltaY, 0));
            }
            mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));

            //Log.i(TAG, "scrollY:"+scrollY);


            //TODO: hide/show toolbar
            //ToolBar should appear when scrolling up no matter how far in the list

            lastScrollY = scrollY;
        }
    }

    public int getScrollY(RecyclerView view, int firstVisiblePosition) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - mHeader.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }

        TypedValue mTypedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());

        return mActionBarHeight;
    }

    public interface Callbacks {
        public void getProfile(RequestListener<User> cb);
        public void displayFollowers();
        public void displayFollowing();
        Toolbar getToolbar();
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void getProfile(RequestListener<User> cb) {
        }

        @Override
        public void displayFollowers() {

        }

        @Override
        public void displayFollowing() {

        }

        @Override
        public Toolbar getToolbar() {
            return null;
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
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        mPagerAdapter = new SectionsPagerAdapter(getActivity(), getFragmentManager());
        mImgLoader = new ImageLoader(getActivity());

        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + getActionBarHeight();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_myprofile, container, false);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mPagerAdapter);

        mHeader = rootView.findViewById(R.id.header);
        mPagerAdapter.setTabHolderScrollingContent(this);

        final PagerSlidingTabStrip strip = PagerSlidingTabStrip.class.cast(rootView.findViewById(R.id.pts_main));
        strip.setViewPager(mViewPager);
        strip.setOnPageChangeListener(this);

        mUserCover = (ImageView) rootView.findViewById(R.id.user_cover);

        mSpannableString = new SpannableString(getString(R.string.app_name));

        mName = (TextView) rootView.findViewById(R.id.user_name);
        mFollowers = (TwoTextCounter) rootView.findViewById(R.id.followers_button);
        mFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.displayFollowers();
            }
        });
        mFollowing = (TwoTextCounter) rootView.findViewById(R.id.following_button);
        mFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.displayFollowing();
            }
        });
        mCity = (TextView) rootView.findViewById(R.id.user_city);

        mFollowers.setLabel("followers");
        mFollowing.setLabel("following");


        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(0xffffffff);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("PROFILE", "onResume");
        ((ActionBarActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.holo_orange_light)));
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Lisional");
        if(mProfile == null)
            mCallbacks.getProfile(this);
        else
            onRequestSuccess(mProfile);
    }

    @Override
    public void onRequestFailure(SpiceException e) {

    }

    @Override
    public void onRequestSuccess(User profile) {
        mProfile = profile;
        mImgLoader.DisplayImage(profile.getLargeAvatarUrl(), mUserCover);
        mName.setText(profile.getUsername());
        mSpannableString = new SpannableString(profile.getUsername());
        mFollowers.setCounter(Integer.parseInt(profile.getFollowersCount()));
        mFollowing.setCounter(Integer.parseInt(profile.getFollowingCount()));
        mCity.setText(profile.getCity());
    }

}
