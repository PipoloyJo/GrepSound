package com.grepsound.requests;

import com.grepsound.activities.Api;
import com.grepsound.fragments.FollowFragment;
import com.grepsound.model.Users;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import org.apache.http.HttpResponse;
import org.json.JSONArray;


/**
 * Created by lisional on 2014-06-25.
 */
public class FollowRequest extends SpiceRequest<Users> {

    FollowFragment.TYPE state;


    public FollowRequest(Class<Users> clazz, FollowFragment.TYPE s) {
        super(clazz);
        state = s;
    }

    @Override
    public Users loadDataFromNetwork() throws Exception {
        String url;
        switch (state){
            case FOLLOWER:
                url = "/me/followers";
                break;
            case FOLLOWING:
                url = "/me/following";
                break;
            default:
                throw new Exception("state must be FOLLOWERS or FOLLOWING");
        }
        HttpResponse resp = Api.wrapper.get(Request.to(url));
        JSONArray result = new JSONArray(Http.getString(resp));
        return new Users(result);
    }
}
