package com.grepsound.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.grepsound.R;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.CloudAPI;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Token;

import java.io.IOException;

public class SignInFragment extends Fragment {

    private static final String TAG = "SignInTestActivity";

    private static String CLIENT_ID = "398a7f28d61b10d5ee14fcb8bff95d68";
    private static String CLIENT_SECRET = "a072bc979fa0a87ef880a529337e7f66";




//    https://accounts.google.com/o/oauth2/auth?client_id=984739005367.apps.googleusercontent.com&redirect_uri=postmessage&response_type=code%20token%20id_token%20gsession&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fplus.login%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&state=1607858991%7C0.883842412&access_type=offline&request_visible_actions=http%3A%2F%2Fschemas.google.com%2FAddActivity%20http%3A%2F%2Fschemas.google.com%2FListenActivity%20http%3A%2F%2Fschemas.google.com%2FCreateActivity&after_redirect=keep_open&cookie_policy=single_host_origin&include_granted_scopes=true&proxy=oauth2relay631423002&origin=https%3A%2F%2Fsoundcloud.com&authuser=0
//   https://soundcloud.com/connect?client_id=b45b1aa10f1ac2941910a7f0d10f8e28&response_type=token&scope=non-expiring%20fast-connect%20purchase%20upload&display=next&redirect_uri=https%3A//soundcloud.com/soundcloud-callback.html

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.signin_frag, null);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "Start");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "Stop");
    }
}