package com.grepsound.services;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import com.grepsound.model.Track;
import com.grepsound.utils.Utilities;

import java.io.IOException;
import java.util.LinkedList;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 26/07/14.
 */

public class GrepMediaPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private final String TAG = GrepMediaPlayer.class.getSimpleName();
    private final Context mContext;
    private LinkedList<MediaPlayer> mPlayers;
    private int index;
    private static int PLAYERS_COUNT = 2;
    public static final String INFO_TRACK = AudioService.INTENT_BASE_NAME + ".INFO_TRACK";
    public static final String NOT_PLAYING = AudioService.INTENT_BASE_NAME + ".NOT_PLAYING";

    private Track mTrackPlaying;

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "onError:"+what);
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "onInfo:"+what);
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if(percent < 100)
            Log.i(TAG, "Buffer percent is:"+percent);
    }

    public void seekTo(int progress) {
        Log.i(TAG, "seeking to:"+progress);
        getCurrentMediaPlayer().seekTo(Utilities.progressToTimer(progress, getCurrentMediaPlayer().getDuration()));
        broadcastStatus();
    }

    public interface info {
        String DURATION = "duration";
        String CURRENT_PROGRESS = "current";
        String RUNNING = "running";
        String TRACK = "track";
    }

    public void release() {
        for(MediaPlayer mp : mPlayers) {
            if(mp.isPlaying()) {
                mp.pause();
            }
            mp.release();
        }
    }

    public GrepMediaPlayer(Context c) {
        mContext = c;
        mPlayers= new LinkedList<>();
        for(int i = 0 ; i < PLAYERS_COUNT; ++i) {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(this);
            player.setOnErrorListener(this);
            player.setOnInfoListener(this);
            player.setOnBufferingUpdateListener(this);
            mPlayers.add(player);
        }
        index = 1;
    }

    /**
     * This methods gives back the next MediaPlayer in the list
     * Warning it does not update the index! It gives you the opportunity to prepare the next player
     * and keep the index on the current player for further commands (like stopping it you know)
     * @return The next media player in queue
     */
    private MediaPlayer getNextMediaPlayer() {
        MediaPlayer toReturn;
        if((index + 1) == mPlayers.size()){
            Log.i(TAG, "Next Player is first one");
            toReturn = mPlayers.getFirst();
        } else {
            Log.i(TAG, "Next player is :" + index + 1);
            toReturn = mPlayers.get(index + 1);
        }
        return toReturn;
    }

    private MediaPlayer getCurrentMediaPlayer() {
        Log.i(TAG, "Current Player is :" + index);
        return mPlayers.get(index);
    }

    public void play(Track tr) throws IOException {
        MediaPlayer p = getNextMediaPlayer();
        p.setDataSource(tr.getStreamUrl()+"?client_id="+ SpiceUpService.CLIENT_ID);
        p.prepareAsync();
        mTrackPlaying = tr;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "Player prepared index is:" + index);
        if(getCurrentMediaPlayer().isPlaying()) {
            Log.i(TAG, "Current MP is playing, should stop!");
            getCurrentMediaPlayer().stop();
            getCurrentMediaPlayer().reset();
        }
        increaseIndex();
        getCurrentMediaPlayer().start();
        broadcastStatus();
    }

    private void increaseIndex() {
        if((index + 1) == mPlayers.size()){
            index = 0;
        } else {
            ++index;
        }
    }

    public boolean isPlaying() {
        return getCurrentMediaPlayer().isPlaying();
    }

    public void setVolume(float v, float v1) {
        getCurrentMediaPlayer().setVolume(v, v1);
    }

    public void pause() {
        getCurrentMediaPlayer().pause();
        broadcastStatus();
    }

    public void resume() {
        if(getCurrentMediaPlayer().isPlaying())
            getCurrentMediaPlayer().pause();
        else
            getCurrentMediaPlayer().start();

        broadcastStatus();
    }

    public void broadcastStatus() {
        if(mTrackPlaying == null) {
            Intent infoTrack = new Intent(NOT_PLAYING);
            mContext.sendBroadcast(infoTrack);
        } else {
            Intent infoTrack = new Intent(INFO_TRACK);
            infoTrack.putExtra(info.DURATION, getCurrentMediaPlayer().getDuration());
            infoTrack.putExtra(info.CURRENT_PROGRESS, getCurrentMediaPlayer().getCurrentPosition());
            infoTrack.putExtra(info.RUNNING, isPlaying());
            infoTrack.putExtra(info.TRACK, mTrackPlaying);
            mContext.sendBroadcast(infoTrack);
        }
    }
}
