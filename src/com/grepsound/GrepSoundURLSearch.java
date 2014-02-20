package com.grepsound;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by lisional on 2/18/2014.
 */
public class GrepSoundURLSearch extends Fragment implements LoaderManager.LoaderCallbacks<Bundle>{

    private static final String TAG = GrepSoundURLSearch.class.getSimpleName();
    private Button goGetIt;
    private EditText urlTextfield;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.main, null);

        goGetIt = (Button) rootView.findViewById(R.id.button);
        urlTextfield = (EditText) rootView.findViewById(R.id.editText);


        goGetIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urlTextfield.getText().length() > 0) {

                    Bundle args = new Bundle();
                    args.putString("url", urlTextfield.getText().toString());
                    getLoaderManager().restartLoader(1, args, GrepSoundURLSearch.this);
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<Bundle> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader");
        String songToRetrieve = args.getString("url");
        GrepSoundLoader l = new GrepSoundLoader(getActivity(), songToRetrieve);
        return l;
    }

    @Override
    public void onLoadFinished(Loader<Bundle> loader, Bundle data) {
        Log.i(TAG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Bundle> loader) {

    }
}