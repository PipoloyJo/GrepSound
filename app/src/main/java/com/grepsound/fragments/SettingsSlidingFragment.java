package com.grepsound.fragments;



import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.views.FractionalLinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsSlidingFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SettingsSlidingFragment extends SlidingFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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

        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        rootView.setPadding(0,mActionBarSize,0,0);
        getFragmentManager().beginTransaction().add(R.id.frame_settings, new SettingsFragment()).commit();

        return rootView;
    }


}
