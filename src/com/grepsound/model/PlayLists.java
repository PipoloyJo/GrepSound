package com.grepsound.model;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lisional on 2014-04-22.
 */

public class PlayLists extends ArrayList<PlayLists.Playlist> {

    public PlayLists(){

    }

    public PlayLists(JSONArray result) throws JSONException {
        for (int i = 0; i < result.length(); ++i) {
            Log.i("Playlists", " : " + result.get(i).toString());
            add(new Playlist((JSONObject) result.get(i)));
        }
    }

    private interface fields {
        String TITLE = "title";
        String DURATION = "duration";
        String PERMALINK_URL = "permalink_url";
    }


    public class Playlist {

        private HashMap<String, String> info;

        public CharSequence getTitle() {
            return info.get(fields.TITLE);
        }

        public String getDuration() {
            return info.get(fields.DURATION);
        }



        public Playlist(JSONObject obj) throws JSONException {
            info = new HashMap<String, String>();

            Iterator ite = obj.keys();
            for (int i = 0; i < obj.length(); ++i) {
                String key = ite.next().toString();
                info.put(key, obj.get(key).toString());
            }
        }
    }

}

