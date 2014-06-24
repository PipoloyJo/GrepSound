package com.grepsound.model;

import android.util.Log;
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

    public String getCity() {
        return info.get(fields.CITY);
    }

    public String getFollowersCount() {
        return info.get(fields.FOLLOWERS_COUNT);
    }

    public String getFollowingCount() {
        return info.get(fields.FOLLOWINGS_COUNT);
    }

    /*
    {
  "id": 3207,
  "permalink": "jwagener",
  "username": "Johannes Wagener",
  "uri": "https://api.soundcloud.com/users/3207",
  "permalink_url": "http://soundcloud.com/jwagener",
  "avatar_url": "http://i1.sndcdn.com/avatars-000001552142-pbw8yd-large.jpg?142a848",
  "country": "Germany",
  "full_name": "Johannes Wagener",
  "city": "Berlin",
  "description": "<b>Hacker at SoundCloud</b>\r\n\r\nSome of my recent Hacks:\r\n\r\nsoundiverse.com \r\nbrowse recordings with the FiRe app by artwork\r\n\r\ntopbillin.com \r\nfind people to follow on SoundCloud\r\n\r\nchatter.fm \r\nget your account hooked up with a voicebox\r\n\r\nrecbutton.com \r\nrecord straight to your soundcloud account",
  "discogs_name": null,
  "myspace_name": null,
  "website": "http://johannes.wagener.cc",
  "website_title": "johannes.wagener.cc",
  "online": true,
  "track_count": 12,
  "playlist_count": 1,
  "followers_count": 416,
  "followings_count": 174,
  "public_favorites_count": 26,
  "plan": "Pro Plus",
  "private_tracks_count": 63,
  "private_playlists_count": 3,
  "primary_email_confirmed": true
}
     */



    private interface fields {
        String TRACK_COUNT = "track_count";
        String FISRT_NAME = "first_name";
        String AVATAR_URL = "avatar_url";
        String USERNAME = "username";
        String PLAYLIST_COUNT = "playlist_count";
        String FOLLOWERS_COUNT = "followers_count";
        String FOLLOWINGS_COUNT = "followings_count";
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
        // what we got from API: https://i1.sndcdn.com/avatars-000047135865-5cgfjx-large.jpg?61e8f21
        // what we want : https://i2.sndcdn.com/avatars-000047135865-5cgfjx-t500x500.jpg?61e8f21
        String smallAvatarUrl = info.get(fields.AVATAR_URL);
        String bigAvatar = smallAvatarUrl.replaceAll("i1", "i2");
        bigAvatar = bigAvatar.replaceAll("large", "t500x500");
        return bigAvatar;
    }

    public String getUsername() {
        return info.get(fields.USERNAME);
    }
}
