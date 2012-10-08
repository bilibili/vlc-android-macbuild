package org.videolan.vlc.events;

import android.os.Bundle;

public class MediaPlayerBuffering {
    private static final String BUNDLE_NEW_CACHE = "new_cache";

    public float mNewCache;

    public MediaPlayerBuffering(Bundle args) {
        mNewCache = getNewCache(args);
    }

    public static float getNewCache(Bundle args) {
        return args.getFloat(BUNDLE_NEW_CACHE, 0.0f);
    }
}
