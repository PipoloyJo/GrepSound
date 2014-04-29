package com.grepsound.requests;

import android.util.Log;
import com.grepsound.model.Profile;
import com.grepsound.model.Track;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lisional on 2014-04-21.
 */
public class LikesRequest extends CachedSpiceRequest<ArrayList<Track>> {

    Token token;

    public LikesRequest(Token tok) {
        super(null, true, DurationInMillis.ONE_MINUTE);
        token = tok;
    }

    @Override
    public ArrayList<Track> loadDataFromNetwork() throws Exception {
        ApiWrapper wrapper = new ApiWrapper(SpiceUpService.CLIENT_ID, SpiceUpService.CLIENT_SECRET, null, token) ;

        HttpResponse resp = wrapper.get(Request.to("/me/favorites"));

        JSONArray result = new JSONArray(Http.getString(resp));

        ArrayList<Track> tracks = new ArrayList<Track>();

        for (int i = 0; i < result.length(); ++i) {
            Log.i("Track", " : " + result.get(i).toString());
            tracks.add(new Track((JSONObject) result.get(i)));
        }

        return tracks;
    }
}