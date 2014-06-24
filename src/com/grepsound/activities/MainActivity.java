package com.grepsound.activities;

import android.animation.*;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.grepsound.R;
import com.grepsound.fragments.*;
import com.grepsound.model.PlayLists;
import com.grepsound.model.Profile;
import com.grepsound.model.Tracks;
import com.grepsound.requests.DownloadTrackRequest;
import com.grepsound.requests.LikesRequest;
import com.grepsound.requests.MeProfileRequest;
import com.grepsound.requests.PlaylistsRequest;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by lisional on 2014-04-21.
 */
public class MainActivity extends Activity implements   MenuFragment.Callbacks,
                                                        LikesFragment.Callbacks,
                                                        PlaylistsFragment.Callbacks,
                                                        MyProfileFragment.Callbacks,
                                                        DetailsPlaylistFragment.OnDetailsPlaylistFragmentAnimationEndListener,
                                                        FragmentManager.OnBackStackChangedListener {
    private static String TAG = MainActivity.class.getSimpleName();

    private MenuFragment fMenu;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private SpiceManager spiceManager = new SpiceManager(SpiceUpService.class);

    boolean mDidSlideOut = false;
    boolean mIsAnimating = false;
    View mDarkHoverView;
    DetailsPlaylistFragment mDetailsFragment;
    Fragment mMainFrag;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences prefs = getSharedPreferences("sc-token", Context.MODE_PRIVATE);

        mDarkHoverView = findViewById(R.id.dark_hover_view);
        mDarkHoverView.setAlpha(0);




        mDetailsFragment = new DetailsPlaylistFragment();

        getFragmentManager().addOnBackStackChangedListener(this);

        mDarkHoverView.setOnClickListener(mClickListener);
        mDetailsFragment.setOnDetailsPlaylistFragmentAnimationEnd(this);


        fMenu = new MenuFragment();
        mMainFrag = new MyProfileFragment();
        getFragmentManager().beginTransaction().replace(R.id.move_to_back_container, mMainFrag)
                                                .replace(R.id.left_drawer, fMenu)
                                                .commit();

        setUpNavigationDrawer();
    }

    private void setUpNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

        };


        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this);

    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed");
        if(mDidSlideOut){
            slideForward(null);
            mDidSlideOut = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

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
    private void switchFragments() {
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
            slideBack(listener);
        }
    }

    @Override
    public void onBackStackChanged() {
        Log.i(TAG, "onBackStackChanged");
        if (!mDidSlideOut) {
            Log.i(TAG, "NOT mDidSlideOut");
            slideForward(null);
        }
    }

    View.OnClickListener mClickListener = new View.OnClickListener () {
        @Override
        public void onClick(View view) {
            switchFragments();
        }
    };

    /**
     * This method animates the image fragment into the background by both
     * scaling and rotating the fragment's view, as well as adding a
     * translucent dark hover view to inform the user that it is inactive.
     */
    public void slideBack(Animator.AnimatorListener listener)
    {
        View movingFragmentView = mMainFrag.getView();

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
        View movingFragmentView = mMainFrag.getView();

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
    public void onSectionSelected(int id) {

    }

    @Override
    public void getLikes(RequestListener<Tracks> cb) {
        LikesRequest request = new LikesRequest(Tracks.class);
        spiceManager.execute(request, cb);
    }

    @Override
    public void getSong(RequestListener cb, String url) {
        DownloadTrackRequest request = new DownloadTrackRequest(this, url);
        spiceManager.execute(request, cb);
    }

    @Override
    public void getPlaylists(RequestListener<PlayLists> cb) {
        PlaylistsRequest request = new PlaylistsRequest(PlayLists.class);
        spiceManager.execute(request, cb);
    }

    @Override
    public void displayPlaylistDetails(PlayLists.Playlist selected) {
        mDetailsFragment = new DetailsPlaylistFragment();
        Bundle b = new Bundle();
        b.putParcelable("DetailsPlaylistFragment", selected);
        mDetailsFragment.setArguments(b);
        mDetailsFragment.setOnDetailsPlaylistFragmentAnimationEnd(this);
        mDetailsFragment.setClickListener(mClickListener);
        switchFragments();
    }

    @Override
    public void getProfile(RequestListener<Profile> cb) {
        MeProfileRequest req = new MeProfileRequest(Profile.class);
        spiceManager.execute(req, cb);
    }
}
