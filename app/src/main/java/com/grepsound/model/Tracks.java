package com.grepsound.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
