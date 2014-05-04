package com.grepsound.requests;

import android.util.Log;
import com.grepsound.model.PlayLists;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lisional on 2014-04-22.
 */
public class PlaylistsRequest extends SpiceRequest<PlayLists> {


    Token token;

    public PlaylistsRequest(Token tok) {
        super(PlayLists.class);
        token = tok;
    }

    @Override
    public PlayLists loadDataFromNetwork() throws Exception {

        ApiWrapper wrapper = new ApiWrapper(SpiceUpService.CLIENT_ID, SpiceUpService.CLIENT_SECRET, null, token) ;

        HttpResponse resp = wrapper.get(Request.to("/me/playlists"));

        JSONArray result = new JSONArray(Http.getString(resp));

        PlayLists playlists = new PlayLists(result);

        return playlists;
    }
}