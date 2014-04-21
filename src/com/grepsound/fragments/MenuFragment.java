package com.grepsound.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
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
    // private static final String TAG = MenuFragment.class.getSimpleName();

    private Callbacks mCallbacks = sDummyCallbacks;
    private ListView sections;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        public void onSectionSelected(int id);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onSectionSelected(int id) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String cat[] = getResources().getStringArray(R.array.categories);
        mAdapter = new MenuAdapter(getActivity(), cat);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.frag_menu, null);

        sections = (ListView) rootView.findViewById(R.id.menu_listView);
        sections.setAdapter(mAdapter);
        sections.setOnItemClickListener(this);
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
        mCallbacks.onSectionSelected(pos);

        /*view.findViewById(R.id.menu_item).setBackgroundResource(0);
        ((TextView) view.findViewById(R.id.menu_title_categorie)).setTextColor(getResources().getColor(R.color.transparent_black));
        ((TextView) view.findViewById(R.id.menu_num_categorie)).setTextColor(getResources().getColor(R.color.transparent_black));
*/
        /*if (lastCheckedItem != null && lastCheckedItem != view) {

            if (Compatibility.isCompatible(Build.VERSION_CODES.JELLY_BEAN))
                lastCheckedItem.findViewById(R.id.menu_item).setBackground(getResources().getDrawable(R.drawable.menu_round_bg));
            else
                lastCheckedItem.findViewById(R.id.menu_item).setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_round_bg));
            ((TextView) lastCheckedItem.findViewById(R.id.menu_title_categorie)).setTextColor(getResources().getColor(R.color.holo_orange_light));
            ((TextView) lastCheckedItem.findViewById(R.id.menu_num_categorie)).setTextColor(getResources().getColor(R.color.holo_orange_light));
        }

        lastCheckedItem = view;*/
    }
}