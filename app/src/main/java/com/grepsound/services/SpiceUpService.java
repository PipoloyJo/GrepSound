package com.grepsound.services;

import android.app.Application;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileBitmapObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.memory.LruCacheBitmapObjectPersister;
import com.octo.android.robospice.persistence.springandroid.json.jackson.JacksonObjectPersisterFactory;

/**
 * Created by lisional on 2014-04-18.
 */
public class SpiceUpService extends SpiceService {

    public static String CLIENT_ID = "398a7f28d61b10d5ee14fcb8bff95d68";
    public static String CLIENT_SECRET = "a072bc979fa0a87ef880a529337e7f66";

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        try {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            JacksonObjectPersisterFactory jacksonObjectPersisterFactory = new JacksonObjectPersisterFactory(application);
            cacheManager.addPersister( jacksonObjectPersisterFactory );

            // Try to add support for bitmap caching here
            InFileBitmapObjectPersister filePersister = new InFileBitmapObjectPersister(application);
            LruCacheBitmapObjectPersister memoryPersister = new LruCacheBitmapObjectPersister(filePersister, cacheSize);
            cacheManager.addPersister(memoryPersister);

        } catch (CacheCreationException e) {
            e.printStackTrace();
        }
        return cacheManager;
    }

    @Override
    public int getThreadCount() {
        return 3;
    }
}
