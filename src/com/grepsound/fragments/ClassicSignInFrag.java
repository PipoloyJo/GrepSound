package com.grepsound.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.grepsound.R;
import com.grepsound.activities.Api;
import com.grepsound.activities.MainActivity;
import com.grepsound.model.Profile;
import com.grepsound.requests.LoginRequest;
import com.grepsound.requests.MeProfileRequest;
import com.grepsound.services.SpiceUpService;
import com.grepsound.views.PasswordEditText;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Token;

/**
 * Created by lisional on 2014-04-11.
 */
public class ClassicSignInFrag extends Fragment implements RequestListener{

    private static final String TAG = ClassicSignInFrag.class.getSimpleName();
    private LoginRequest request;
    private SpiceManager spiceManager = new SpiceManager(SpiceUpService.class);
    EditText user;
    PasswordEditText pass;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_classic_sign_in, container, false);

        user = (EditText) rootView.findViewById(R.id.username_input);
        pass = (PasswordEditText) rootView.findViewById(R.id.password_input);

        rootView.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("username", user.getText().toString());
                b.putString("password", pass.getText().toString());

                request = new LoginRequest(b);
                spiceManager.execute(request, ClassicSignInFrag.this);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());

    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Log.e(TAG, "Failure");
    }

    @Override
    public void onRequestSuccess(Object o) {
        Log.e(TAG, "Success");
        if(o instanceof Token) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("sc-token",
                                                                Context.MODE_PRIVATE).edit();

            Log.i(TAG, "token_access:"+((Token) o).access);
            Log.i(TAG, "token_scopes:"+((Token) o).scope);
            Log.i(TAG, "token_refresh:"+((Token) o).refresh);
            editor.putString("token_access", ((Token) o).access);
            editor.putString("token_scopes", ((Token) o).scope);
            editor.putString("token_refresh", ((Token) o).refresh);
            editor.commit();

            // Restore token
            Token token = new Token(((Token) o).access, ((Token) o).scope, ((Token) o).refresh);

            // create the API wrapper with the token
            Api.wrapper = new ApiWrapper(null, null, null, token);

            Intent intent = new Intent();
            intent.setClass(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }
}