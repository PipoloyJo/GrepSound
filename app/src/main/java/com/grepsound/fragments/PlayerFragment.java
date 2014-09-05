package com.grepsound.fragments;

import android.app.Fragment;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.model.Track;
import com.grepsound.services.AudioService;
import com.grepsound.services.GrepMediaPlayer;
import com.grepsound.utils.Utilities;
import com.grepsound.views.CircularSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerFragment extends Fragment implements CircularSeekBar.OnCircularSeekBarChangeListener {

	private static final String TAG = PlayerFragment.class.getSimpleName();

	private BroadcastReceiver audioPlayerBroadcastReceiver = new AudioPlayerBroadCastReceiver();
	private AudioService audioPlayer;
	private Intent audioPlayerIntent;
	SharedPreferences prefs;
	private boolean isTracking;

	private Handler mHandler = new Handler();
	private Timer timer;
	private UpdaterTask updater;
	RelativeLayout mSliderButton;

    private Track trackPlaying;
    TextView songDuration, currentTime;
    ImageButton mPlayPauseButton;
    private CircularSeekBar mCircularSeekBar;

    public interface info {
		String POS_PLAYLIST = "pos_playlist";
		String PROGRESS = "new_progress";
	}

	private class UpdaterTask extends TimerTask {

		int duration;
		int current;
		Handler callback;

		public UpdaterTask(int dur, int cur, Handler c) {
			duration = dur;
			current = cur;
			callback = c;
		}

		@Override
		public void run() {

			if (current >= duration) {
				timer.cancel();
				callback.post(new Runnable() {

					@Override
					public void run() {
                        mCircularSeekBar.setProgress(0);

					}
				});
				return;
			}

            final int progress = Utilities.getProgressPercentage(current, duration);
            final String current_time = Utilities.milliSecondsToTimer(current);
            final String remaining = "-" + Utilities.milliSecondsToTimer(duration - current);
            current += 1000;

			callback.post(new Runnable() {

				@Override
				public void run() {
                    if (!isTracking)
                        mCircularSeekBar.setProgress(progress);
                    songDuration.setText(remaining);
                    currentTime.setText(current_time);
				}
			});

		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		audioPlayerBroadcastReceiver = new AudioPlayerBroadCastReceiver();
		IntentFilter filter = new IntentFilter(GrepMediaPlayer.INFO_TRACK);
        filter.addAction(GrepMediaPlayer.NOT_PLAYING);
		getActivity().registerReceiver(audioPlayerBroadcastReceiver, filter);
	}

    @Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(audioPlayerBroadcastReceiver);
		audioPlayerBroadcastReceiver = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.frag_player, null);

        mPlayPauseButton = (ImageButton) rootView.findViewById(R.id.play_pause);
        mCircularSeekBar = (CircularSeekBar) rootView.findViewById(R.id.progress);
        songDuration = (TextView) rootView.findViewById(R.id.total_duration);
        currentTime = (TextView) rootView.findViewById(R.id.current_time);

        mCircularSeekBar.setOnSeekBarChangeListener(this);
        mPlayPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent request = new Intent(AudioService.commands.RESUME);
                getActivity().sendBroadcast(request);
            }
        });

		return rootView;
	}

	private class AudioPlayerBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.contentEquals(GrepMediaPlayer.NOT_PLAYING)) {
                return;
            }

			if (timer != null) {
				timer.cancel();
				updater.cancel();
			}
            updater = new UpdaterTask(intent.getIntExtra(GrepMediaPlayer.info.DURATION, 0), intent.getIntExtra(GrepMediaPlayer.info.CURRENT_PROGRESS, 0), mHandler);
            trackPlaying = intent.getParcelableExtra(GrepMediaPlayer.info.TRACK);
            if (intent.getBooleanExtra(GrepMediaPlayer.info.RUNNING, false)) {
                mPlayPauseButton.setImageResource(R.drawable.ic_action_pause);
                timer = new Timer();
                timer.scheduleAtFixedRate(updater, 0, 1000);
            } else {
                mPlayPauseButton.setImageResource(R.drawable.ic_action_play);
                // Just to update current progress in case of
                // seek while in pause
                updater.run();
            }
        }
	}

	@Override
	public void onProgressChanged(CircularSeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(CircularSeekBar seekBar) {
		Log.i(TAG, "onStartTrackingTouch");
		isTracking = true;
	}

	@Override
	public void onStopTrackingTouch(CircularSeekBar seekBar) {
		Log.i(TAG, "onStopTrackingTouch");
		isTracking = false;
		Intent intent = new Intent(AudioService.commands.SEEK_MOVED);
		intent.putExtra(info.PROGRESS, seekBar.getProgress());
		getActivity().sendBroadcast(intent);
	}

}
