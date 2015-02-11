package com.grepsound.requests;

import android.os.Bundle;
import android.util.Log;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

/**
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return
 * <p/>
 * Alexandre Lision on 18/12/14.
 */

public class GoogleLoginRequest extends SpiceRequest<Token> {

    private String mGToken;

    public GoogleLoginRequest(String googleToken) {
        super(Token.class);
        mGToken = googleToken;
    }

    @Override
    public Token loadDataFromNetwork() throws Exception {



//        Request req = new Request("https://api.soundcloud.com/oauth2/token");

        HttpPost req = new HttpPost("https://api.soundcloud.com/oauth2/token");

        JSONObject body = new JSONObject();
        body.put("access_token", mGToken);
        body.put("scope", Token.SCOPE_NON_EXPIRING);

        ApiWrapper wrapper = new ApiWrapper(SpiceUpService.CLIENT_ID, SpiceUpService.CLIENT_SECRET, null, null);
        HttpClient client = wrapper.getHttpClient();

        HttpResponse response = client.execute(req);

        Log.i("G+REQUEST", response.getEntity().toString());

        return null;
    }

}
