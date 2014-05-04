package com.grepsound.model;

import android.util.Log;
import com.soundcloud.api.Http;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lisional on 2014-04-21.
 */
public class Profile {

    private HashMap<String, String> info;

    private interface fields {
        String TRACK_COUNT = "track_count";
        String FISRT_NAME = "first_name";
        String AVATAR_URL = "avatar_url";
        String USERNAME = "username";
        String PLAYLIST_COUNT = "playlist_count";
        String CITY = "city";
    }

    public Profile(JSONObject result) throws JSONException, IOException {

        info = new HashMap<String, String>();

        Iterator ite = result.keys();

        for (int i = 0; i < result.length(); ++i) {
            String key = ite.next().toString();
            Log.i("Profile", key + " : " + result.get(key).toString());
            info.put(key, result.get(key).toString());
        }
    }

    public String getAvatarUrl(){
        return info.get(fields.AVATAR_URL);
    }
}
