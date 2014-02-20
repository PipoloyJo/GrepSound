package com.grepsound;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by lisional on 2/19/2014.
 */
public class SoundService extends Service {

    private static String CLIENT_ID = "398a7f28d61b10d5ee14fcb8bff95d68";
    private static String CLIENT_SECRET = "398a7f28d61b10d5ee14fcb8bff95d68";

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void registerMe(){
        ApiWrapper wrapper = new ApiWrapper(CLIENT_ID, CLIENT_SECRET, null, null);

        private String[] getAccountNames() {
            mAccountManager = AccountManager.get(this);
            Account[] accounts = mAccountManager.getAccountsByType(
                    GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String[] names = new String[accounts.length];
            for (int i = 0; i < names.length; i++) {
                names[i] = accounts[i].name;
            }
            return names;
        }

        try {
            Token tok = wrapper.login("Alexandre Lision", "password", Token.SCOPE_NON_EXPIRING);

            HttpResponse resp = wrapper.get(Request.to("/me"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
