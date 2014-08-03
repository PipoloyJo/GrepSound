package com.grepsound.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.ImageView;
import com.grepsound.R;
import com.grepsound.sync.GrepSoundPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private  static String TAG = SettingsFragment.class.getSimpleName();

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);


        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        actionBar.setTitle("Settings");
        getActionBarIconView().setAlpha(1f);

        injectSummaries();

    }

    private void injectSummaries() {
        findPreference("sync_interval").setSummary(""+GrepSoundPreferences.getSyncInterval(getActivity()));
        findPreference("cache_likes_count").setSummary(""+GrepSoundPreferences.getCacheLikesCount(getActivity()));
    }

    private ImageView getActionBarIconView() {
        return (ImageView) getActivity().getWindow().getDecorView().findViewById(android.R.id.home);
    }

    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        injectSummaries();
    }
}
