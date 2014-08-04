package com.grepsound.requests;

import com.grepsound.activities.Api;
import com.grepsound.model.Tracks;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.json.JSONArray;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-04-21.
 */

public class LikesRequest extends SpringAndroidSpiceRequest<Tracks> {

    private String userId;

    public LikesRequest(String user) {
        super(Tracks.class);
        userId = user;
    }

    @Override
    public Tracks loadDataFromNetwork() throws Exception {
        HttpResponse resp = Api.wrapper.get(Request.to("/me/favorites?limit=100"));
        JSONArray result = new JSONArray(Http.getString(resp));
        return new Tracks(result);
    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return unique string cache key
     */
    public String createCacheKey() {
        return "Likes." + userId;
    }
}