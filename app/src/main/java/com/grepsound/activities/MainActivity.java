package com.grepsound.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.*;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import com.grepsound.R;
import com.grepsound.fragments.*;
import com.grepsound.model.*;
import com.grepsound.requests.*;
import com.grepsound.services.AudioService;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-04-22.
 */

public class MainActivity extends Activity implements   MenuFragment.Callbacks,
                                                        LikesFragment.Callbacks,
                                                        PlaylistsFragment.Callbacks,
                                                        MyProfileFragment.Callbacks,
                                                        FollowFragment.Callbacks,
                                                        SlidingFragment.OnSlidingFragmentAnimationEndListener,
                                                        FragmentManager.OnBackStackChangedListener {
    private static String TAG = MainActivity.class.getSimpleName();

    private PlayerFragment fMenu;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ServiceConnection serviceConnection = new AudioPlayerServiceConnection();

    private SpiceManager spiceManager = new SpiceManager(SpiceUpService.class);

    boolean mDidSlideOut = false;
    boolean mIsAnimating = false;
    View mDarkHoverView;
    SlidingFragment mDetailsFragment;
    Fragment mMainFrag;

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.grepsound.sync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.grepsound";
    // The account name
    public static final String ACCOUNT = "Grepsound";
    // Instance fields
    Account mAccount;
    // Sync interval constants
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE *
                    MILLISECONDS_PER_SECOND;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences prefs = getSharedPreferences("sc-token", Context.MODE_PRIVATE);

        mDarkHoverView = findViewById(R.id.dark_hover_view);
        mDarkHoverView.setAlpha(0);

        getFragmentManager().addOnBackStackChangedListener(this);

        fMenu = new PlayerFragment();
        mMainFrag = new MyProfileFragment();
        getFragmentManager().beginTransaction().replace(R.id.move_to_back_container, mMainFrag)
                                                .replace(R.id.left_drawer, fMenu)
                                                .commit();

        setUpNavigationDrawer();
        mAccount = CreateSyncAccount(this);
        getContentResolver().addPeriodicSync(
                mAccount,
                AUTHORITY,
                new Bundle(),
                SYNC_INTERVAL);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        Log.i(TAG, "wifi only is " + prefs.getBoolean("wifi_only", false));

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
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, AudioService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConnection);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.ac_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, AudioService.class));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
            getActionBar().setBackgroundDrawable(null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
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
                    transaction.setCustomAnimations(R.animator.slide_fragment_left, 0, 0,
                            R.animator.slide_fragment_right);
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
        boolean canback = getFragmentManager().getBackStackEntryCount()>0;
        //getActionBar().setHomeButtonEnabled(!canback);
        //getActionBar().setDisplayShowHomeEnabled(!canback);
        mDrawerToggle.setDrawerIndicatorEnabled(!canback);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if(!canback)
            mMainFrag.onResume();
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

    private final class AudioPlayerServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder baBinder) {
            Log.d(TAG, "AudioPlayerServiceConnection: Service connected");
            startService(new Intent(MainActivity.this, AudioService.class));
            Intent intent = new Intent(AudioService.commands.UPDATE);
            sendBroadcast(intent);
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "AudioPlayerServiceConnection: Service disconnected");
        }
    }

    ////////////////  Callbacks    //////////////////////

    @Override
    public void getLikes(RequestListener<Tracks> cb) {
        LikesRequest req = new LikesRequest("me");
        spiceManager.execute(req, req.createCacheKey(), DurationInMillis.ONE_DAY, cb);
    }

    @Override
    public void getSong(RequestListener cb, String url) {
        spiceManager.execute(new DownloadTrackRequest(this, url), cb);
    }

    @Override
    public void getPlaylists(RequestListener<Playlists> cb) {
        PlaylistsRequest req = new PlaylistsRequest("me");
        spiceManager.execute(req, req.createCacheKey(), DurationInMillis.ONE_DAY, cb);
    }

    @Override
    public void displayPlaylistDetails(Playlist selected) {
        mDetailsFragment = new DetailsPlaylistFragment();
        Bundle b = new Bundle();
        b.putParcelable("DetailsPlaylistFragment", selected);
        mDetailsFragment.setArguments(b);
        mDetailsFragment.setOnSlidingFragmentAnimationEnd(this);
        mDetailsFragment.setClickListener(mClickListener);
        switchFragments();
    }

    @Override
    public void getProfile(RequestListener<User> cb) {
        ProfileRequest req = new ProfileRequest("me");
        spiceManager.execute(req, req.createCacheKey(), DurationInMillis.ONE_DAY, cb);
    }

    @Override
    public void displayFollowers() {
        mDetailsFragment = new FollowFragment();
        Bundle b = new Bundle();
        b.putSerializable("type", FollowFragment.TYPE.FOLLOWER);
        mDetailsFragment.setArguments(b);
        mDetailsFragment.setOnSlidingFragmentAnimationEnd(this);
        mDetailsFragment.setClickListener(mClickListener);
        switchFragments();
    }

    @Override
    public void displayFollowing() {
        mDetailsFragment = new FollowFragment();
        Bundle b = new Bundle();
        b.putSerializable("type", FollowFragment.TYPE.FOLLOWING);
        mDetailsFragment.setArguments(b);
        mDetailsFragment.setOnSlidingFragmentAnimationEnd(this);
        mDetailsFragment.setClickListener(mClickListener);
        switchFragments();
    }

    @Override
    public void getFollowByType(FollowFragment.TYPE t, RequestListener<Users> cb) {
        spiceManager.execute(new FollowRequest(Users.class, t), cb);
    }

    // ----------- Menu callbacks ----------------------
    @Override
    public void logOut() {
        SharedPreferences.Editor editor = getSharedPreferences("sc-token", Context.MODE_PRIVATE).edit();
        editor.remove("token_access");
        editor.remove("token_scopes");
        editor.remove("token_refresh");
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        sendBroadcast(new Intent(AudioService.commands.SHUTDOWN));
        startActivity(intent);
        finish();
    }

    @Override
    public void showSettings() {
        mDetailsFragment = new SettingsSlidingFragment();mDrawerLayout.closeDrawer(Gravity.LEFT);
        switchFragments();
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

}
