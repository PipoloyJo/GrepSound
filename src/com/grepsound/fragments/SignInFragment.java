package com.grepsound.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.grepsound.R;
import com.grepsound.requests.LoginRequest;
import com.grepsound.requests.MeProfileRequest;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.soundcloud.api.Http;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.IOException;

public class SignInFragment extends Fragment {

    private static final String TAG = SignInFragment.class.getSimpleName();


//    https://accounts.google.com/o/oauth2/auth?client_id=984739005367.apps.googleusercontent.com&redirect_uri=postmessage&response_type=code%20token%20id_token%20gsession&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fplus.login%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&state=1607858991%7C0.883842412&access_type=offline&request_visible_actions=http%3A%2F%2Fschemas.google.com%2FAddActivity%20http%3A%2F%2Fschemas.google.com%2FListenActivity%20http%3A%2F%2Fschemas.google.com%2FCreateActivity&after_redirect=keep_open&cookie_policy=single_host_origin&include_granted_scopes=true&proxy=oauth2relay631423002&origin=https%3A%2F%2Fsoundcloud.com&authuser=0
//   https://soundcloud.com/connect?client_id=b45b1aa10f1ac2941910a7f0d10f8e28&response_type=token&scope=non-expiring%20fast-connect%20purchase%20upload&display=next&redirect_uri=https%3A//soundcloud.com/soundcloud-callback.html

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.signin_frag, null);



        return rootView;
    }


}