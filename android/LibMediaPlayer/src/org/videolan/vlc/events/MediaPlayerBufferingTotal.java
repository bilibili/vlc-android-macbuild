package org.videolan.vlc.events;

import android.os.Bundle;

public class MediaPlayerBufferingTotal {
    private static final String BUNDLE_NEW_CACHE_TOTAL = "new_cache_total";

    public float mNewCacheTotal;

    public MediaPlayerBufferingTotal(Bundle args) {
        mNewCacheTotal = getNewCacheTotal(args);
    }

    public static float getNewCacheTotal(Bundle args) {
        return args.getFloat(BUNDLE_NEW_CACHE_TOTAL, 0.0f);
    }
}
