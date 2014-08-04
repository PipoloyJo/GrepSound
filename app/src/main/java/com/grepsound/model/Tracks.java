package com.grepsound.model;

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
 * Alexandre Lision on 26/07/14.
 */

public class Tracks extends ArrayList<Track> {

    public Tracks() {

    }

    public Tracks(JSONArray result) {
        for (int i = 0; i < result.length(); ++i) {
            try {
                add(new Track((JSONObject) result.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageUrlOf(int position) {
        return get(position).getImageUrl();
    }


}
