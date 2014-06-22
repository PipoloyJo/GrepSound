package com.grepsound.requests;

import com.grepsound.activities.Api;
import com.grepsound.model.Tracks;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.json.JSONArray;

/**
 * Created by lisional on 2014-04-21.
 */
public class LikesRequest extends SpiceRequest<Tracks> {


    public LikesRequest(Class<Tracks> clazz) {
        super(clazz);
    }

    @Override
    public Tracks loadDataFromNetwork() throws Exception {
        HttpResponse resp = Api.wrapper.get(Request.to("/me/favorites"));
        JSONArray result = new JSONArray(Http.getString(resp));
        return new Tracks(result);
    }
}