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
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 *
 * Alexandre Lision on 2014-06-25.
 */

public class FollowRequest extends SpiceRequest<Users> {

    private final String userId;
    FollowFragment.TYPE state;


    public FollowRequest(String user, FollowFragment.TYPE s) {
        super(Users.class);
        state = s;
        userId = user;
    }

    @Override
    public Users loadDataFromNetwork() throws Exception {
        String url;
        switch (state){
            case FOLLOWER:
                url = "/me/followers";
                break;
            case FOLLOWING:
                url = "/me/followings";
                break;
            default:
                throw new Exception("state must be FOLLOWERS or FOLLOWING");
        }
        HttpResponse resp = Api.wrapper.get(Request.to(url));
        JSONArray result = new JSONArray(Http.getString(resp));
        return new Users(result);
    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return unique string cache key
     */
    public String createCacheKey() {
        return "Follow." + userId + "." + state;
    }
}
