package com.grepsound;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by lisional on 2/19/2014.
 */
public class GrepSoundService extends Service {

    private static String TAG = GrepSoundService.class.getSimpleName();
    private static String CLIENT_ID = "398a7f28d61b10d5ee14fcb8bff95d68";
    private static String CLIENT_SECRET = "398a7f28d61b10d5ee14fcb8bff95d68";
    private AccountManager mAccountManager;
    boolean shouldStop;
    OAuthThread mThread;

    Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        mThread = new OAuthThread();
        mThread.start();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        mThread.setRunning(false);
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mMessenger.getBinder();
    }

    private class OAuthThread extends Thread {

        private boolean isRunning;

        public OAuthThread() {
            super();
        }

        public void setRunning(boolean b) {
            isRunning = b;
        }

        @Override
        public void run() {
            isRunning = true;
            registerMe();
        }

        public void registerMe() {
            ApiWrapper wrapper = new ApiWrapper(CLIENT_ID, CLIENT_SECRET, null, null);

            try {
                Token tok = wrapper.login("", "", Token.SCOPE_NON_EXPIRING);
                HttpResponse resp = wrapper.get(Request.to("/me"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
