package com.grepsound.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lisional on 2014-06-25.
 */
public class Users extends ArrayList<User> {

    public Users() {

    }

    public Users(JSONArray result) {
        for (int i = 0; i < result.length(); ++i) {
            try {
                add(new User((JSONObject) result.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
