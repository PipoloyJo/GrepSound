package com.grepsound.services;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import com.grepsound.model.Track;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by alision on 26/07/14.
 */
public class GrepMediaPlayer implements MediaPlayer.OnPreparedListener {

    private final String TAG = GrepMediaPlayer.class.getSimpleName();
    private LinkedList<MediaPlayer> mPlayers;
    private int index;
    private static int PLAYERS_COUNT = 2;

    public GrepMediaPlayer() {
        mPlayers= new LinkedList<MediaPlayer>();
        for(int i = 0 ; i < PLAYERS_COUNT; ++i) {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(this);
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
    }
}
