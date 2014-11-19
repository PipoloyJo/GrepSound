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
import com.dd.processbutton.iml.ActionProcessButton;
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
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-04-11.
 */

public class ClassicSignInFrag extends Fragment implements RequestListener<Token>{

    private static final String TAG = ClassicSignInFrag.class.getSimpleName();
    private SpiceManager spiceManager = new SpiceManager(SpiceUpService.class);
    EditText mUserField;
    PasswordEditText mPasswordField;
    private ActionProcessButton mBtnSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_classic_sign_in, container, false);

        mUserField = (EditText) rootView.findViewById(R.id.username_input);
        mPasswordField = (PasswordEditText) rootView.findViewById(R.id.password_input);

        rootView.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mBtnSignIn = (ActionProcessButton) rootView.findViewById(R.id.login_button);

        mBtnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSignIn.setEnabled(false);
                mUserField.setEnabled(false);
                mPasswordField.setEnabled(false);
                mBtnSignIn.setProgress(10);
                login();
            }
        });

        mPasswordField.getEdit_text().setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        b.putString("username", mUserField.getText().toString());
        b.putString("password", mPasswordField.getText().toString());

        LoginRequest request = new LoginRequest(b);
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
        mUserField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mBtnSignIn.setEnabled(true);
        mBtnSignIn.setProgress(0);
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
        editor.apply();

        // Restore token
        Token token = new Token(o.access, o.scope, o.refresh);

        // create the API wrapper with the token
        Api.wrapper = new ApiWrapper(null, null, null, token);

        Intent intent = new Intent();
        intent.setClass(getActivity(), MainActivity.class);

        mBtnSignIn.setProgress(0);

        startActivity(intent);
        getActivity().finish();
    }
}
