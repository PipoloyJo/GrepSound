package com.grepsound.signin;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.activities.Api;
import com.grepsound.activities.MainActivity;
import com.grepsound.requests.LoginRequest;
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
public class ClassicSignInFrag extends Fragment implements RequestListener<Token>{

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
                login();
            }
        });

        pass.getEdit_text().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i(TAG, "actionID "+actionId);
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    return true;
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    private void login() {
        Bundle b = new Bundle();
        b.putString("username", user.getText().toString());
        b.putString("password", pass.getText().toString());

        request = new LoginRequest(b);
        spiceManager.execute(request, ClassicSignInFrag.this);
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
    public void onRequestSuccess(Token o) {
        Log.e(TAG, "Success");
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("sc-token",
                                                                Context.MODE_PRIVATE).edit();

        Log.i(TAG, "token_access:"+ o.access);
        Log.i(TAG, "token_scopes:"+ o.scope);
        Log.i(TAG, "token_refresh:"+ o.refresh);
        editor.putString("token_access", o.access);
        editor.putString("token_scopes", o.scope);
        editor.putString("token_refresh", o.refresh);
        editor.commit();

        // Restore token
        Token token = new Token(o.access, o.scope, o.refresh);

        // create the API wrapper with the token
        Api.wrapper = new ApiWrapper(null, null, null, token);

        Intent intent = new Intent();
        intent.setClass(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
