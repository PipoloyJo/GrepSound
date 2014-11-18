package com.grepsound.fragments;


import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.grepsound.R;
import com.grepsound.views.FractionalLinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsSlidingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsSlidingFragment extends SlidingFragment {

    private int mActionBarHeight;
    private TypedValue mTypedValue = new TypedValue();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsSlidingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsSlidingFragment newInstance() {
        return new SettingsSlidingFragment();
    }

    public SettingsSlidingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = new FractionalLinearLayout(getActivity());

        rootView.setId(R.id.frame_settings);
        rootView.setBackgroundColor(getResources().getColor(R.color.white));

        rootView.setPadding(0, getActionBarHeight(), 0, 0);
        getFragmentManager().beginTransaction().add(R.id.frame_settings, new SettingsFragment()).commit();

        return rootView;
    }

    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }

        getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
        return mActionBarHeight;
    }


}
