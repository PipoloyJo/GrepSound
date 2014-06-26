package com.grepsound.model;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lisional on 2014-06-25.
 */
public class Users extends ArrayList<Users.User> {

    private HashMap<String, String> info;

    public Users() {

    }

    public Users(JSONArray result) throws JSONException {
        for (int i = 0; i < result.length(); ++i) {
            Log.i("Users", " : " + result.get(i).toString());
            add(new User((JSONObject) result.get(i)));
        }
    }


    private interface fields {
        String TITLE = "title";
        String ARTWORK_URL = "artwork_url";
    }

/**
 * {
 * "track_count":0,
 * "website_title":null,
 * "followings_count":916,
 * "avatar_url":"https:\/\/i1.sndcdn.com\/avatars-000088829578-hhu0xz-large.jpg?2aaad5e",
 * "subscriptions":[],
 * "website":null,
 * "myspace_name":null,
 * "uri":"https:\/\/api.soundcloud.com\/users\/99248244",
 * "kind":"user",
 * "online":false,
 * "discogs_name":null,
 * "city":null,
 * "country":null,
 * "id":99248244,
 * "public_favorites_count":552,
 * "first_name":"",
 * "username":"Music-Republic",
 * "plan":"Free",
 * "permalink":"music_republic",
 * "description":"♜ SUPPORT US-- FOLLOW US ♜\nand get chance to be promoted or hosted your remix contest :\nsoundcloud.com\/groups\/you-are-here-11-november-remix-contest-by-music-republic",
 * "last_name":"",
 * "permalink_url":"http:\/\/soundcloud.com\/music_republic",
 * "followers_count":152,
 * "playlist_count":2,
 * "full_name":""
 * }
**/
    public class User extends HashMap<String, String>{

        public User(JSONObject obj) throws JSONException {
            Iterator ite = obj.keys();
            for (int i = 0; i < obj.length(); ++i) {
                String key = ite.next().toString();
                put(key, obj.get(key).toString());
            }
        }

        public String getImageUrl() {
            return get(fields.ARTWORK_URL);
        }
    }
}
