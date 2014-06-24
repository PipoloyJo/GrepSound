package com.grepsound.requests;

import com.grepsound.activities.Api;
import com.grepsound.model.PlayLists;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import org.apache.http.HttpResponse;
import org.json.JSONArray;

/**
 * Created by lisional on 2014-04-22.
 */
public class PlaylistsRequest extends SpiceRequest<PlayLists> {

    public PlaylistsRequest(Class<PlayLists> clazz) {
        super(clazz);
    }

    @Override
    public PlayLists loadDataFromNetwork() throws Exception {
        HttpResponse resp = Api.wrapper.get(Request.to("/me/playlists"));
        JSONArray result = new JSONArray(Http.getString(resp));
        return new PlayLists(result);
    }
}
