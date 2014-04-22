package com.grepsound.services;

import android.app.Application;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;

/**
 * Created by lisional on 2014-04-18.
 */
public class SpiceUpService extends SpiceService {

    public static String CLIENT_ID = "398a7f28d61b10d5ee14fcb8bff95d68";
    public static String CLIENT_SECRET = "a072bc979fa0a87ef880a529337e7f66";

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        return new CacheManager();
    }
}
