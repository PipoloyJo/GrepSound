package com.grepsound.requests;

import com.grepsound.model.Profile;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;


/**
 * Created by lisional on 2014-04-18.
 */
public class MeProfileRequest extends SpiceRequest<Profile> {

    private static String CLIENT_ID = "398a7f28d61b10d5ee14fcb8bff95d68";
    private static String CLIENT_SECRET = "a072bc979fa0a87ef880a529337e7f66";
    Token token;



    public MeProfileRequest(Token tok) {
        super(Profile.class);
        token = tok;
    }

    @Override
    public Profile loadDataFromNetwork() throws Exception {
        ApiWrapper wrapper = new ApiWrapper(CLIENT_ID, CLIENT_SECRET, null, token) ;

        HttpResponse resp = wrapper.get(Request.to("/me"));
        //if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            //System.out.println("\n" + Http.formatJSON(Http.getString(resp)));
        //}
        Profile profile = new Profile(resp);

        return profile;
    }
}