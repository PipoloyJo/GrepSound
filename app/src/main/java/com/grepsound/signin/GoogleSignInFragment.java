package com.grepsound.signin;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Toast;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.grepsound.R;

import java.io.IOException;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-04-11.
 */

public class GoogleSignInFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
                                                                View.OnClickListener {

    private static final String TAG = GoogleSignInFragment.class.getSimpleName();

    // The core Google+ client.
    private GoogleApiClient mPlusClient;

    // A flag to stop multiple dialogues appearing for the user.
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    // A magic number we will use to know that our sign-in error
    // resolution activity has completed.
    private static final int REQUEST_RESOLVE_ERROR = 49404;

    // A progress dialog to display when the user is connecting in
    // case there is a delay in any of the dialogs being ready.
    private ProgressDialog mConnectionProgressDialog;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Getting status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        // Showing status
        if(status==ConnectionResult.SUCCESS)
            Toast.makeText(getActivity(), "Google Play Services are available", Toast.LENGTH_SHORT);
        else{

            Toast.makeText(getActivity(), "Google Play Services are not available", Toast.LENGTH_SHORT);
            //tvStatus.setText("");
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();
        }

        View rootView = inflater.inflate(R.layout.frag_google_sign_in, null);
        mPlusClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        // Connect our sign in, sign out and disconnect buttons.
        rootView.findViewById(R.id.sign_in_button).setOnClickListener(this);
        rootView.findViewById(R.id.sign_out_button).setOnClickListener(this);
        rootView.findViewById(R.id.revoke_access_button).setOnClickListener(this);
        rootView.findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.revoke_access_button).setVisibility(View.INVISIBLE);

        // Configure the ProgressDialog that will be shown if there is a
        // delay in presenting the user with the next sign in step.
        mConnectionProgressDialog = new ProgressDialog(getActivity());
        mConnectionProgressDialog.setMessage("Signing in...");

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "Start");
        mPlusClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "Stop");
        mPlusClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mResolvingError) {
            if (result.hasResolution()) {
                try {
                    mResolvingError = true;
                    result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    // There was an error with the resolution intent. Try again.
                    mPlusClient.connect();
                }
            } else {
                // Show dialog using GooglePlayServicesUtil.getErrorDialog()
                mConnectionProgressDialog.dismiss();
                showErrorDialog(result.getErrorCode());
                mResolvingError = true;
            }
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Yay! We can get the oAuth 2.0 access token we are using.
        Log.v(TAG, "Connected. Yay!");

        // Turn off the flag, so if the user signs out they'll have to
        // tap to sign in again.
        mResolvingError = false;

        // Hide the progress dialog if its showing.
        mConnectionProgressDialog.dismiss();

        // Hide the sign in button, show the sign out buttons.
        getView().findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.revoke_access_button).setVisibility(View.VISIBLE);

        // Retrieve the oAuth 2.0 access token.
        final Context context = getActivity().getApplicationContext();
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                String scope = "oauth2:" + Scopes.PLUS_LOGIN;

                    // We can retrieve the token to check via
                    // tokeninfo or to pass to a service-side
                    // application.
                try {
                    String token = GoogleAuthUtil.getToken(context,
                            Plus.AccountApi.getAccountName(mPlusClient), scope);
                    Log.i(TAG, token);
                    Log.i(TAG, "Hello " + Plus.AccountApi.getAccountName(mPlusClient));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        task.execute((Void) null);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlusClient.connect();

        // Hide the sign out buttons, show the sign in button.
        getView().findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.revoke_access_button).setVisibility(View.INVISIBLE);

    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        Log.v(TAG, "ActivityResult: " + requestCode);
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mPlusClient.isConnecting() &&
                        !mPlusClient.isConnected()) {
                    mPlusClient.connect();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                Log.v(TAG, "Tapped sign in");
                if (!mPlusClient.isConnected() && !mResolvingError) {
                    // Show the dialog as we are now signing in.
                    mConnectionProgressDialog.show();
                    // Make sure that we will start the resolution (e.g. fire the
                    // intent and pop up a dialog for the user) for any errors
                    // that come in.
                    mResolvingError = true;
                    // We should always have a connection result ready to resolve,
                    // so we can start that process.
                    mPlusClient.connect();
                }
                break;
            case R.id.sign_out_button:
                Log.v(TAG, "Tapped sign out");
                // We only want to sign out if we're connected.
                if (mPlusClient.isConnected()) {
                    // Clear the default account in order to allow the user
                    // to potentially choose a different account from the
                    // account chooser.
                    //mPlusClient.clearDefaultAccount();

                    // Disconnect from Google Play Services, then reconnect in
                    // order to restart the process from scratch.
                    mPlusClient.disconnect();
                    mPlusClient.connect();

                    // Hide the sign out buttons, show the sign in button.
                    getView().findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.sign_out_button)
                            .setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.revoke_access_button).setVisibility(
                            View.INVISIBLE);
                }
                break;
            case R.id.revoke_access_button:
                Log.v(TAG, "Tapped disconnect");
                if (mPlusClient.isConnected()) {
                    // Clear the default account as in the Sign Out.
                    //mPlusClient.clearDefaultAccount();

                    // Go away and revoke access to this entire application.
                    // This will call back to onAccessRevoked when it is
                    // complete as it needs to go away to the Google
                    // authentication servers to revoke all token.
                    //mPlusClient.revokeAccessAndDisconnect(this);
                }
                break;
            default:
                // Unknown id.
        }
    }
}

