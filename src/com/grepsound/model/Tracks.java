package com.grepsound.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Tracks extends ArrayList<Tracks.Track> {

    public Tracks(){

    }


    public Tracks(JSONArray result) throws JSONException {
        for (int i = 0; i < result.length(); ++i) {
            add(new Track((JSONObject) result.get(i)));
        }
    }

    public String getImageUrlOf(int position) {
        return get(position).getImageUrl();
    }

    private interface fields {
        String TITLE = "title";
        String DURATION = "duration";
        String PERMALINK_URL = "permalink_url";
        String ARTWORK_URL = "artwork_url";
    }


    public class Track {

        /**
         * {"genre":"Deep House",
         * "track_type":null,
         * "isrc":null,
         * "release_day":null,
         * "release_year":null,
         * "state":"finished",
         * "favoritings_count":2963,
         * "download_count":0,
         * "artwork_url":"https:\/\/i1.sndcdn.com\/artworks-000072740553-5g54tb-large.jpg?164b459",
         * "downloadable":false,
         * "kind":"track",
         * "id":138185759,
         * "title":"Joris Delacroix - We Are The Future 14\/02\/2014",
         * "sharing":"public",
         * "label_name":null,
         * "video_url":null,
         * "description":"",
         * "streamable":true,
         * "created_at":"2014\/03\/06 09:42:25 +0000",
         * "permalink_url":"http:\/\/soundcloud.com\/fuse-club-brussels\/joris-delacroix-we-are-the",
         * "user_id":4501682,"original_format":"wav","original_content_size":1087571220,
         * "license":"all-rights-reserved",
         * "embeddable_by":"all",
         * "commentable":true,
         * "attachments_uri":"https:\/\/api.soundcloud.com\/tracks\/138185759\/attachments",
         * "comment_count":203,
         * "purchase_url":null,
         * "playback_count":89358,
         * "stream_url":"https:\/\/api.soundcloud.com\/tracks\/138185759\/stream",
         * "label_id":null,"user_playback_count":1,
         * "uri":"https:\/\/api.soundcloud.com\/tracks\/138185759",
         * "key_signature":null,"bpm":null,
         * "duration":6166023,
         * "permalink":"joris-delacroix-we-are-the",
         * "tag_list":"",
         * "release_month":null,
         * "purchase_title":null,
         * "waveform_url":"https:\/\/w1.sndcdn.com\/i5ABm4r13mk5_m.png",
         * "user":{"id":4501682,"avatar_url":"https:\/\/i1.sndcdn.com\/avatars-000023103976-db3a2i-large.jpg?164b459","permalink_url":"http:\/\/soundcloud.com\/fuse-club-brussels","username":"Fuse Club Brussels","permalink":"fuse-club-brussels","uri":"https:\/\/api.soundcloud.com\/users\/4501682","kind":"user"},
         * "release":null,
         * "user_favorite":true}
         */

        private HashMap<String, String> info;

        public Object getUrl() {
            return info.get(fields.PERMALINK_URL);
        }


        public Track(JSONObject obj) throws JSONException {
            info = new HashMap<String, String>();

            Iterator ite = obj.keys();

            for (int i = 0; i < obj.length(); ++i) {
                String key = ite.next().toString();
                info.put(key, obj.get(key).toString());
            }
        }

        public String getTitle() {
            return info.get(fields.TITLE);
        }

        public String getDuration() {
            return info.get(fields.DURATION);
        }


        public String getImageUrl() {
            return info.get(fields.ARTWORK_URL);
        }
    }
}
