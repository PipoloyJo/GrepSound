package com.grepsound.services;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import com.grepsound.fragments.PlayerFragment;
import com.grepsound.model.Track;
import com.octo.android.robospice.SpiceManager;

import java.io.IOException;

public class AudioService extends Service implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener {
    public static final String INTENT_BASE_NAME = "com.grepsound.services.AudioService";

    public interface commands {
        public static final String PLAY_NEXT = INTENT_BASE_NAME + ".PLAY_NEXT";
        public static final String PLAY_PREVIOUS = INTENT_BASE_NAME + ".PLAY_PREVIOUS";
        public static final String PLAY = INTENT_BASE_NAME + ".PLAY";
        public static final String PAUSE = INTENT_BASE_NAME + ".PAUSE";
        public static final String RESUME = INTENT_BASE_NAME + ".RESUME";
        public static final String UPDATE = INTENT_BASE_NAME + ".UPDATE";
        public static final String SHUFFLE = INTENT_BASE_NAME + ".SHUFFLE";
        public static final String REPEAT = INTENT_BASE_NAME + ".REPEAT";
        public static final String SEEK_MOVED = INTENT_BASE_NAME + ".SEEK_MOVED";
        public static final String SHUTDOWN = INTENT_BASE_NAME + ".SHUTDOWN";
    }

    public interface fields {
        public static final String SONG = INTENT_BASE_NAME + ".SONG";
    }

    private SpiceManager spiceManager = new SpiceManager(SpiceUpService.class);
    private AudioManager mAudioManager;
    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;
    private final String TAG = AudioService.class.getSimpleName();
    private GrepMediaPlayer mMediaPlayer;

    private boolean headsetConnected = false;
    private AudioPlayerBroadcastReceiver broadcastReceiver = new AudioPlayerBroadcastReceiver();
    private HeadsetStateReceiver checkHeadsetReceiver;

    private WakeLock wl;

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "AudioPlayer: onBind() called");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(getApplicationContext(), "onStartCommand",
        // Toast.LENGTH_LONG).show();
        Log.v(TAG, "AudioService: onStartCommand");

        PowerManager wm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = wm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.grepsound.lock");
        if (!wl.isHeld())
            wl.acquire();

        //mMediaPlayer.setOnPreparedListener(mediaPreparedListener);
        //mMediaPlayer.prepareAsync();
        //mMediaPlayer.setOnCompletionListener(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(commands.PLAY_NEXT);
        intentFilter.addAction(commands.PLAY_PREVIOUS);
        intentFilter.addAction(commands.PLAY);
        intentFilter.addAction(commands.PAUSE);
        intentFilter.addAction(commands.RESUME);
        intentFilter.addAction(commands.UPDATE);
        intentFilter.addAction(commands.SHUFFLE);
        intentFilter.addAction(commands.REPEAT);
        intentFilter.addAction(commands.SEEK_MOVED);
        intentFilter.addAction(commands.SHUTDOWN);
        registerReceiver(broadcastReceiver, intentFilter);

        // Don't like passing a context here but it enables the player to broadcast its status on its own
        mMediaPlayer = new GrepMediaPlayer(this);

        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        receiverFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        checkHeadsetReceiver = new HeadsetStateReceiver();
        registerReceiver(checkHeadsetReceiver, receiverFilter);

    }

    private class HeadsetStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.contentEquals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                headsetConnected = false;
                sendBroadcast(new Intent(AudioService.commands.PAUSE));
            }

            if (action.contentEquals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                if (state == 0 && headsetConnected) {
                    headsetConnected = false;
                    sendBroadcast(new Intent(AudioService.commands.PAUSE));
                } else if (state == 0 && !headsetConnected) {
                    headsetConnected = true;
                } else if (headsetConnected && state == 1) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);
                    sendBroadcast(new Intent(AudioService.commands.RESUME));
                } else if (!headsetConnected && state == 1) {
                    headsetConnected = true;
                }
            }
        }
    }

    @Override
    public void onLowMemory() {
        unregisterReceiver(checkHeadsetReceiver);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "AudioPlayer: onDestroy() called");
        if (wl != null)
            wl.release();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(checkHeadsetReceiver);
    }

    private class AudioPlayerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();

            if (action.contentEquals(commands.PLAY)) {
                Track tr = intent.getParcelableExtra(fields.SONG);
                Log.i(TAG, "PLAYING :" + tr.getTitle());
                try {
                    mMediaPlayer.play(tr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (action.contentEquals(commands.PAUSE)) {
                mMediaPlayer.pause();
            } else if (action.contentEquals(commands.RESUME)) {
                mMediaPlayer.resume();
            } else if (action.contentEquals(commands.UPDATE)) {
                mMediaPlayer.broadcastStatus();
            } else if (action.contentEquals(commands.SHUTDOWN)) {
                mMediaPlayer.release();
                stopSelf();
            } else if (action.contentEquals(commands.SEEK_MOVED)) {
                int progress = intent.getIntExtra(PlayerFragment.info.PROGRESS, 0);
                mMediaPlayer.seekTo(progress);
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "Player finished!");
    }

    // do we have audio focus?
    enum AudioFocus {
        NoFocusNoDuck, // we don't have audio focus, and can't duck
        NoFocusCanDuck, // we don't have focus, but can play at a low volume
        // ("ducking")
        Focused // we have full audio focus
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                onGainedAudioFocus();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                onLostAudioFocus(false);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                onLostAudioFocus(true);
                break;
            default:
        }
    }

    public void onGainedAudioFocus() {
        mAudioFocus = AudioFocus.Focused;
        // restart media player with new focus settings
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
            mMediaPlayer.setVolume(1.0f, 1.0f);
    }

    public void onLostAudioFocus(boolean canDuck) {
        mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;

        // start/restart/pause media player with new focus settings
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            if (canDuck) {
                mMediaPlayer.setVolume(0.1f, 0.1f);
            } else {
                Intent intent = new Intent(AudioService.commands.PAUSE);
                sendBroadcast(intent);
            }
        }
    }
}
