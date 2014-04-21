package com.grepsound.requests;

import android.os.Bundle;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by lisional on 2014-04-18.
 */
public class LoginRequest extends SpiceRequest<Token> {

    private static String CLIENT_ID = "398a7f28d61b10d5ee14fcb8bff95d68";
    private static String CLIENT_SECRET = "a072bc979fa0a87ef880a529337e7f66";
    private String username;
    private String password;

    public LoginRequest(Bundle msg) {
        super(Token.class);
        if(msg == null)
            return;

        username = msg.getString("username");
        password = msg.getString("password");
        if(username ==null || password == null)
            return;
    }

    @Override
    public Token loadDataFromNetwork() throws Exception {
        ApiWrapper wrapper = new ApiWrapper(CLIENT_ID, CLIENT_SECRET, null, null);
            return wrapper.login(username, password, Token.SCOPE_NON_EXPIRING);
            //HttpResponse resp = wrapper.get(Request.to("/me"));
    }

}
