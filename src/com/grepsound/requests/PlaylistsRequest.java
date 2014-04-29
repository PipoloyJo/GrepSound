package com.grepsound.requests;

import android.util.Log;
import com.grepsound.model.Playlist;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by lisional on 2014-04-22.
 */
public class PlaylistsRequest extends SpiceRequest<ArrayList<Playlist>> {


    Token token;

    public PlaylistsRequest(Token tok) {
        super(null);
        token = tok;
    }

    @Override
    public ArrayList<Playlist> loadDataFromNetwork() throws Exception {

        ApiWrapper wrapper = new ApiWrapper(SpiceUpService.CLIENT_ID, SpiceUpService.CLIENT_SECRET, null, token) ;

        HttpResponse resp = wrapper.get(Request.to("/me/playlists"));

        JSONArray result = new JSONArray(Http.getString(resp));

        ArrayList<Playlist> playlists = new ArrayList<Playlist>();

        for (int i = 0; i < result.length(); ++i) {
            Log.i("Playlists", " : " + result.get(i).toString());
            playlists.add(new Playlist((JSONObject) result.get(i)));
        }
        //if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        //  System.out.println("\n" + Http.formatJSON(Http.getString(resp)));
        //}
        //ArrayList<Track> tracks = new Profile(resp);

        return playlists;
    }
}