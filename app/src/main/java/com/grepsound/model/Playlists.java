package com.grepsound.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-04-22.
 */

/**
 *
 * {"genre":"Electronic",
 * "track_count":10,
 * "release_day":null,
 * "release_year":null,
 * "type":null,
 * "artwork_url":null,
 * "downloadable":null,
 * "kind":"playlist",
 * "id":13879269,
 * "title":"1113",
 * "sharing":"public",
 * "label_name":null,
 * "description":null,
 * "streamable":true,
 * "created_at":"2013\/11\/05 18:38:30 +0000",
 * "permalink_url":"http:\/\/soundcloud.com\/alexandre-lision\/sets\/1113-1",
 * "user_id":50770010,
 * "created_with":{
 *      "id":46941,
 *      "permalink_url":"http:\/\/soundcloud.com\/apps\/v2-on-https-soundcloud-com",
 *      "external_url":"","uri":"https:\/\/api.soundcloud.com\/apps\/46941",
 *      "kind":"app",
 *      "creator":"spadgos",
 *      "name":"SoundCloud.com"},
 * "license":"all-rights-reserved",
 * "embeddable_by":"all",
 * "secret_token":"s-dzQfk",
 * "purchase_url":null,
 * "label_id":null,
 * "tracks":[
 *      {"genre":"Deep",
 *      "track_type":"",
 *      "isrc":"",
 *      "release_day":null,
 *      "release_year":null,
 *      "state":"finished",
 *      "favoritings_count":769,
 *      "download_count":883,
 *      "artwork_url":"https:\/\/i1.sndcdn.com\/artworks-000061709997-pzhd1q-large.jpg?30a2558",
 *      "downloadable":true,
 *      "kind":"track",
 *      "id":118225577,
 *      "title":"Tronicsound - On The Road (original Mix) - FREE DL",
 *      "sharing":"public",
 *      "label_name":"",
 *      "video_url":null,
 *      "download_url":"https:\/\/api.soundcloud.com\/tracks\/118225577\/download",
 *      "description":"Follow TronicSound here:\r\n\r\n► https:\/\/soundcloud.com\/tronicsound-1\r\n► https:\/\/www.facebook.com\/TronicSound\r\n\r\nAnd Dealer De Musique here:\r\n\r\n► www.dealerdemusique.fr\r\n► facebook.com\/dealerdemusique.fr \r\n► youtube.com\/dealerdemusique\r\n\r\nNo copyright intended\r\nAny Claim: contact@dealerdemusique.fr",
 *      "streamable":true,
 *      "created_at":"2013\/11\/02 12:44:55 +0000",
 *      "permalink_url":"http:\/\/soundcloud.com\/dealerdemusique\/thetronicsound-on-the-road",
 *      "user_id":10552440,
 *      "original_format":"mp3",
 *      "original_content_size":17272233,
 *      "license":"all-rights-reserved",
 *      "embeddable_by":"all",
 *      "commentable":true,
 *      "attachments_uri":"https:\/\/api.soundcloud.com\/tracks\/118225577\/attachments",
 *      "comment_count":17,
 *      "purchase_url":null,
 *      "playback_count":38158,
 *      "stream_url":"https:\/\/api.soundcloud.com\/tracks\/118225577\/stream",
 *      "label_id":null,
 *      "user_playback_count":1,
 *      "uri":"https:\/\/api.soundcloud.com\/tracks\/118225577",
 *      "key_signature":"",
 *      "bpm":null,
 *      "duration":431870,
 *      "permalink":"thetronicsound-on-the-road",
 *      "tag_list":"\"The Tronicsound\" \"On The Road\"",
 *      "release_month":null,
 *      "purchase_title":null,
 *      "waveform_url":"https:\/\/w1.sndcdn.com\/aqtRuIe7se9M_m.png",
 *      "user":{
 *          "id":10552440,
 *          "avatar_url":"https:\/\/i1.sndcdn.com\/avatars-000075915194-wq2obx-large.jpg?30a2558",
 *          "permalink_url":"http:\/\/soundcloud.com\/dealerdemusique",
 *          "username":"Dealer de Musique",
 *          "permalink":"dealerdemusique",
 *          "uri":"https:\/\/api.soundcloud.com\/users\/10552440",
 *          "kind":"user"},
 *      "release":"",
 *      "user_favorite":false},
 *      ]
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlists extends ArrayList<Playlist> {

    public Playlists(int capacity) {
        super(capacity);
    }

    public Playlists(){
        // Necessary for jackson caching
    }

    public Playlists(JSONArray result) {
        for (int i = 0; i < result.length(); ++i) {
            try {
                add(new Playlist((JSONObject) result.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

