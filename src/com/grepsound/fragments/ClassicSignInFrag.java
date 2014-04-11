package com.grepsound.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import com.grepsound.R;

/**
 * Created by lisional on 2014-04-11.
 */
public class ClassicSignInFrag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_classic_sign_in, container, false);
    }
}