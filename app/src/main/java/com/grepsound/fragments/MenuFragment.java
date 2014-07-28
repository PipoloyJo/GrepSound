package com.grepsound.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.grepsound.R;
import com.grepsound.adapters.MenuAdapter;

public class MenuFragment extends Fragment implements OnItemClickListener {

    private MenuAdapter mAdapter;
    View rootView;
    private static final String TAG = MenuFragment.class.getSimpleName();

    private Callbacks mCallbacks = sDummyCallbacks;
    private ListView mSections;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        public void logOut();
        public void showSettings();
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {

        @Override
        public void logOut() {

        }

        @Override
        public void showSettings() {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[2];
        drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_action_settings, "Settings");
        drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_action_logout, "Logout");

        mAdapter = new MenuAdapter(getActivity(), drawerItem);
    }

    public class ObjectDrawerItem {

        public int icon;
        public String name;

        // Constructor.
        public ObjectDrawerItem(int icon, String name) {
            this.icon = icon;
            this.name = name;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.frag_menu, null);

        mSections = (ListView) rootView.findViewById(R.id.menu_listView);
        mSections.setAdapter(mAdapter);
        mSections.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        switch(pos) {
            case 0:
                mCallbacks.showSettings();
                break;
            case 1:
                // LogOut
                mCallbacks.logOut();
                break;
        }
    }
}