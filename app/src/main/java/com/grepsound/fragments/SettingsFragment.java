package com.grepsound.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.ImageView;
import com.grepsound.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

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
    }

    private ImageView getActionBarIconView() {
        return (ImageView) getActivity().getWindow().getDecorView().findViewById(android.R.id.home);
    }
}
