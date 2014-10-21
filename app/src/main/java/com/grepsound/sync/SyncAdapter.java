package com.grepsound.sync;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 02/08/14.
 */

import android.accounts.Account;
import android.content.*;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import com.google.gson.Gson;
import com.grepsound.activities.Api;
import com.grepsound.model.Playlists;
import com.grepsound.model.Tracks;
import com.grepsound.requests.LikesRequest;
import com.grepsound.requests.PlaylistsRequest;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Token;

import java.io.*;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    private SpiceManager mSpiceManager;

    private static String LIKES_CACHE_FILE = "likes";
    private static String PLAYLISTS_CACHE_FILE = "playlists";


    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize, SpiceManager spiceManager) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mSpiceManager = spiceManager;
        Api.wrapper = new ApiWrapper(null, null, null, getStoredToken());
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    public Token getStoredToken() {
        SharedPreferences prefs = getContext().getSharedPreferences("sc-token", Context.MODE_PRIVATE);
        return new Token(prefs.getString("token_access", null), prefs.getString("token_scopes", null), prefs.getString("token_refresh", null));
    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        mSpiceManager.removeDataFromCache(Tracks.class, "Likes.me");
        mSpiceManager.removeDataFromCache(Playlists.class, "Playlists.me");

        final LikesRequest tracksRequest = new LikesRequest("me");
        Tracks resultTracks = (Tracks) getSynchronously(tracksRequest, Tracks.class);

        final PlaylistsRequest playlistsRequest = new PlaylistsRequest("me");
        Playlists resultPlaylists = (Playlists) getSynchronously(playlistsRequest, Playlists.class);

        try {
            Tracks cachedTracks = getCachedTracks();
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            Playlists cachedPlaylist = getCachedPlaylists();
        } catch (IOException e) {
            e.printStackTrace();
        }
    /*
     * Put the data transfer code here.
     *
     * 1. getLikes && PlayLists - DONE
     * 2. getCache for Likes and Playlist
     * 3. Comparisons
     * 4. Download new
     * 5. Remove old
     * 6. Optionnal (Notification if new sounds in cache)
     *
     */

    }

    private Playlists getCachedPlaylists() throws IOException {
        FileInputStream fis = getContext().openFileInput(getContext().getExternalCacheDir().getPath()+PLAYLISTS_CACHE_FILE);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        String json = sb.toString();
        Gson gson = new Gson();
        return gson.fromJson(json, Playlists.class);
    }

    private Tracks getCachedTracks() throws IOException {
        FileInputStream fis = getContext().openFileInput(getContext().getExternalCacheDir().getPath()+LIKES_CACHE_FILE);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }

        String json = sb.toString();
        Gson gson = new Gson();
        return gson.fromJson(json, Tracks.class);
    }

    private boolean saveToCache(String filename, Object o) {
        Gson gson = new Gson();
        String s = gson.toJson(o);

        FileOutputStream outputStream;

        try {
            outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(s.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Use RS synchronously
     * @param req
     * @param rep
     * @return
     */
    private Object getSynchronously(SpringAndroidSpiceRequest req, final Object rep) {
        final CountDownLatch latch = new CountDownLatch(1);
        final Object[] result = new Object[1];

        mSpiceManager.execute(req, new RequestListener() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                latch.countDown();
            }

            @Override
            public void onRequestSuccess(Object response) {
                latch.countDown();
                result[0] = response;
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}
