package com.grepsound.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import com.grepsound.R;
import com.grepsound.fragments.LikesFragment;
import com.grepsound.fragments.MenuFragment;
import com.grepsound.fragments.MyProfileFragment;
import com.grepsound.fragments.PlaylistsFragment;
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
                                                        MyProfileFragment.Callbacks {
    private MenuFragment fMenu;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private SpiceManager spiceManager = new SpiceManager(SpiceUpService.class);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("sc-token", Context.MODE_PRIVATE);

        fMenu = new MenuFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_frame, new MyProfileFragment())
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
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
    public void getProfile(RequestListener<Profile> cb) {
        MeProfileRequest req = new MeProfileRequest(Profile.class);
        spiceManager.execute(req, cb);
    }
}
