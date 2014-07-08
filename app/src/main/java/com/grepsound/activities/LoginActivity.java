package com.grepsound.activities;

import android.accounts.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.OperationCanceledException;
import android.util.Log;
import android.widget.Toast;
import com.grepsound.R;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Token;

import java.io.IOException;

public class LoginActivity extends Activity {

    private static String TAG = "GrepSound";
    public static final String SC_ACCOUNT_TYPE = "com.soundcloud.android.account";
    public static final String ACCESS_TOKEN = "access_token";

    private static final Uri MARKET_URI = Uri.parse("market://details?id=com.soundcloud.android");
    private static final int DIALOG_NOT_INSTALLED = 0;

    private AccountManager mAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Token tr = getStoredToken();
        if (tr.valid()) {
            // create the API wrapper with the token
            Api.wrapper = new ApiWrapper(null, null, null, tr);

            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.signin_frag);

            mAccountManager = AccountManager.get(this);

            final Account account = getAccount();
            if (account != null) {
                new Thread(mGetToken).start();
            } else {
                addAccount();
            }
        }
    }

    private final Runnable mGetToken = new Runnable() {
        @Override
        public void run() {
            final Token token = getToken(getAccount());
            Log.i(TAG, "access: "+token.access);
            Log.i(TAG, "scope: "+ token.scope);
            Log.i(TAG, "LOL: "+ token.refresh);
            if (token != null && token.valid()) {
                success(token);
            } else {
                notifyUser(R.string.could_not_get_token);
            }
        }
    };

    // request a new SoundCloud account to be added
    private void addAccount() {
        notifyUser(R.string.no_active_sc_account);
        mAccountManager.addAccount(SC_ACCOUNT_TYPE, ACCESS_TOKEN, null, null, this,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle result = future.getResult();
                            String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                            Log.d(TAG, "created account for " + name);

                            // should have an account by now
                            Account account = getAccount();
                            if (account != null) {
                                new Thread(mGetToken).start();
                            } else {
                                notifyUser(R.string.could_not_create_account);
                            }
                        } catch (OperationCanceledException e) {
                            notifyUser(R.string.operation_canceled);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (AuthenticatorException e) {
                            // SoundCloud app is not installed
                            appNotInstalled();
                        } catch (android.accounts.OperationCanceledException e) {
                            notifyUser(R.string.operation_canceled);
                        }
                    }
                }, null);
    }


    private void appNotInstalled() {
        showDialog(DIALOG_NOT_INSTALLED);
    }


    private void success(Token token) {
        // create the API wrapper with the token
        Api.wrapper = new ApiWrapper(null, null, null, token);

        // and launch our main activity
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    private Account getAccount() {
        Account[] accounts = mAccountManager.getAccountsByType(SC_ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return accounts[0];
        } else {
            return null;
        }
    }

    private Token getToken(Account account) {
        try {
            AccountManagerFuture<Bundle> accountManagerFuture = mAccountManager.getAuthToken(account, ACCESS_TOKEN, null, this, null, null);
            //String access = mAccountManager.getAuthToken(account, ACCESS_TOKEN, false);
            Bundle authTokenBundle = accountManagerFuture.getResult();
            String access = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();

            return new Token(access, null, Token.SCOPE_NON_EXPIRING);
        } catch (OperationCanceledException e) {
            notifyUser(R.string.operation_canceled);
            return null;
        } catch (IOException e) {
            Log.w(TAG, "error", e);
            return null;
        } catch (AuthenticatorException e) {
            Log.w(TAG, "error", e);
            return null;
        } catch (android.accounts.OperationCanceledException e) {
            notifyUser(R.string.operation_canceled);
            e.printStackTrace();
            return null;
        }
    }

    private void notifyUser(final int id) {
        notifyUser(getResources().getString(id));
    }

    private void notifyUser(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle data) {
        if (DIALOG_NOT_INSTALLED == id) {
            return new AlertDialog.Builder(this)
                    .setTitle(R.string.sc_app_not_found)
                    .setMessage(R.string.sc_app_not_found_message)
                    .setPositiveButton(android.R.string.yes, new Dialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, MARKET_URI));
                            } catch(android.content.ActivityNotFoundException e){
                                // Just in case google play store is not installed
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create();
        } else {
            return null;
        }
    }

    public Token getStoredToken() {
        SharedPreferences prefs = getSharedPreferences("sc-token", Context.MODE_PRIVATE);
        return new Token(prefs.getString("token_access", null), prefs.getString("token_scopes", null), prefs.getString("token_refresh", null));
    }


//
//    THIS IS THE SOUND OF FREEDOM
//
//      clientID="b45b1aa10f1ac2941910a7f0d10f8e28"
//
//    STEP 1
//     -s : silent, no output
//     -L : follow redirects
//    page=$(curl -s -L --user-agent 'Mozilla/5.0' "$url")
//      DO: get all http page content following redirections
//      STATUS: DONE
//
//    STEP 2
//    -v : Selected lines are those not matching any of the specified patterns
//      discard lines containing "small" as they contain data-sc-track with a different id.
//    -o : Prints only the matching part of the lines.
//    -E : Interpret pattern as an extended regular expression (i.e. force grep to behave as egrep).
//    sort : sort lines
//    uniq : filters identical lines
//
//    id=$(echo "$page" | grep -v "small" | grep -oE "data-sc-track=.[0-9]*" | grep -oE "[0-9]*" | sort | uniq)
//      STATUS: DONE
//
//    STEP 3
//    title=$(echo -e "$page" | grep -A1 "<em itemprop=\"name\">" | tail -n1 | sed 's/\\u0026/\&/g' | recode html..u8)
//
//    STEP 4
//    filename=$(echo "$title".mp3 | tr '*/\?"<>|' '+       ' )
//
//    STEP 5
//    songurl=$(curl -s -L --user-agent 'Mozilla/5.0' "https://api.sndcdn.com/i1/tracks/$id/streams?client_id=$clientID" | cut -d '"' -f 4 | sed 's/\\u0026/\&/g')
//
//    STEP 6
//    artist=$(echo "$page" | grep byArtist | sed 's/.*itemprop="name">\([^<]*\)<.*/\1/g' | recode html..u8)
//
//    STEP 7
//    imageurl=$(echo "$page" | tr ">" "\n" | grep -A1 '<div class="artwork-download-link"' | cut -d '"' -f 2 | tr " " "\n" | grep 'http' | sed 's/original/t500x500/g' | sed 's/png/jpg/g' )
//
//    STEP 8
//    genre=$(echo "$page" | tr ">" "\n" | grep -A1 '<span class="genre search-deprecation-notification" data="/tags/' | tr ' ' "\n" | grep '</span' | cut -d "<" -f 1 | recode html..u8)
//
//    DL
//    -#: display progress bar
//    -o: write output in a file instead of stdout
//
//    curl -# -L --user-agent 'Mozilla/5.0' -o "`echo -e "$filename"`" "$songurl";
//
//    function downsong() { #Done!
//        # Grab Info
//        url="$1"
//        echo "[i] Grabbing song page"
//        if $curlinstalled; then
//                page=$(curl -s -L --user-agent 'Mozilla/5.0' "$url")
//        else
//        page=$(wget --max-redirect=1000 --trust-server-names --progress=bar -U -O- 'Mozilla/5.0' "$url")
//        fi
//                id=$(echo "$page" | grep -v "small" | grep -oE "data-sc-track=.[0-9]*" | grep -oE "[0-9]*" | sort | uniq)
//        title=$(echo -e "$page" | grep -A1 "<em itemprop=\"name\">" | tail -n1 | sed 's/\\u0026/\&/g' | recode html..u8)
//        filename=$(echo "$title".mp3 | tr '*//*\?"<>|' '+       ' )
//        songurl=$(curl -s -L --user-agent 'Mozilla/5.0' "https://api.sndcdn.com/i1/tracks/$id/streams?client_id=$clientID" | cut -d '"' -f 4 | sed 's/\\u0026/\&/g')
//        artist=$(echo "$page" | grep byArtist | sed 's/.*itemprop="name">\([^<]*\)<.*//*\1/g' | recode html..u8)
//        imageurl=$(echo "$page" | tr ">" "\n" | grep -A1 '<div class="artwork-download-link"' | cut -d '"' -f 2 | tr " " "\n" | grep 'http' | sed 's/original/t500x500/g' | sed 's/png/jpg/g' )
//        genre=$(echo "$page" | tr ">" "\n" | grep -A1 '<span class="genre search-deprecation-notification" data="/tags/' | tr ' ' "\n" | grep '</span' | cut -d "<" -f 1 | recode html..u8)
//        # DL
//        echo ""
//        if [ -e "$filename" ]; then
//        echo "[!] The song $filename has already been downloaded..."  && exit
//        else
//        echo "[-] Downloading $title..."
//        fi
//        if $curlinstalled; then
//        curl -# -L --user-agent 'Mozilla/5.0' -o "`echo -e "$filename"`" "$songurl";
//        else
//        wget --max-redirect=1000 --trust-server-names -U 'Mozilla/5.0' -O "`echo -e "$filename"`" "$songurl";
//        fi
//        settags "$artist" "$title" "$filename" "$genre" "$imageurl"
//        echo "[i] Downloading of $filename finished"
//        echo ''
//    }


}
