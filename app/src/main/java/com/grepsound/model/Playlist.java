package com.grepsound.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 * <p/>
 * Alexandre Lision on 04/08/14.
 */
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Playlist implements Parcelable {

    private HashMap<String, String> info;
    private Tracks set;

    private interface fields {
        String TITLE = "title";
        String DURATION = "duration";
        String PERMALINK_URL = "permalink_url";
        String ARTWORK_URL = "artwork_url";
    }


    public Playlist() {
        // Necessary for jackson caching
    }

    public HashMap<String, String> getInfo() {
        return info;
    }

    public void setInfo(HashMap<String, String> info) {
        this.info = info;
    }

    public void setSet(Tracks set) {
        this.set = set;
    }

    public CharSequence getTitle() {
        return info.get(fields.TITLE);
    }

    public String getDuration() {
        String dur = info.get(fields.DURATION);
        int milliSeconds = Integer.parseInt(dur);

        int seconds = milliSeconds / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        int hours = minutes / 60;
        minutes %= 60;

        return hours + "." + minutes + "." + seconds;
    }

    public Playlist(JSONObject obj) throws JSONException {
        info = new HashMap<>();

        Iterator ite = obj.keys();
        for (int i = 0; i < obj.length(); ++i) {
            String key = ite.next().toString();
            if (key.contentEquals("tracks")) {
                JSONArray tracksJSON = new JSONArray(obj.get(key).toString());
                set = new Tracks(tracksJSON);
            } else
                info.put(key, obj.get(key).toString());
        }
    }

    public Tracks getSet() {
        return set;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(info);
        dest.writeParcelableArray(set.toArray(new Track[set.size()]), 0);
    }


    public final static Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    private Playlist(Parcel in) {
        info = (HashMap<String, String>) in.readSerializable();
        set = new Tracks();
        set.addAll(Arrays.asList((Track[]) in.readParcelableArray(Track.class.getClassLoader())));
    }

    public String getCover() {
        if (!info.get(fields.ARTWORK_URL).contentEquals("null"))
            return info.get(fields.ARTWORK_URL);

        for (Track t : set) {
            if (t.getImageUrl() != null) {
                return t.getImageUrl();
            }
        }

        // return an empty string, causing GET request to fail miserably
        return "";
    }
}
