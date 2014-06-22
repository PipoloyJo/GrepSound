package com.grepsound.requests;

import com.grepsound.activities.Api;
import com.grepsound.model.Profile;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.json.JSONObject;


/**
 * Created by lisional on 2014-04-18.
 */
public class MeProfileRequest extends SpiceRequest<Profile> {


    public MeProfileRequest(Class<Profile> clazz) {
        super(clazz);
    }

    @Override
    public Profile loadDataFromNetwork() throws Exception {
        HttpResponse resp = Api.wrapper.get(Request.to("/me"));
        JSONObject result = Http.getJSON(resp);
        return new Profile(result);
    }
}