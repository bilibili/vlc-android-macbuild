package tv.danmaku.android.util;

import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;

public class ImageLruCache {
    private static final boolean ENABLE_VERBOSE = false;

    private final static int GLOBAL_IMAGE_MEM_CACHE_MAX_SIZE = 40;
    private final static int PRIVATE_IMAGE_MEM_CACHE_MAX_SIZE = 10;

    private static LruCache<Integer, Drawable> sGlobalMemCache = new LruCache<Integer, Drawable>(
            GLOBAL_IMAGE_MEM_CACHE_MAX_SIZE);
    private LruCache<Integer, Drawable> mImageMemCache;

    public ImageLruCache() {
        this(PRIVATE_IMAGE_MEM_CACHE_MAX_SIZE);
    }

    public ImageLruCache(int capacity) {
        mImageMemCache = new LruCache<Integer, Drawable>(capacity);
    }

    public Drawable get(int key) {
        Drawable thumb = mImageMemCache.get(key);
        if (thumb != null) {
            if (ENABLE_VERBOSE)
                DebugLog.vfmt("ImageLruCache", "get %d", key);
            return thumb;
        }

        thumb = sGlobalMemCache.get(key);
        if (thumb != null) {
            if (ENABLE_VERBOSE)
                DebugLog.vfmt("ImageLruCache", "get %d global", key);
            Drawable mutateThumb = thumb.mutate();
            mImageMemCache.put(key, mutateThumb);
            return mutateThumb;
        }

        if (ENABLE_VERBOSE)
            DebugLog.vfmt("ImageLruCache", "get %d missing", key);

        return null;
    }

    public void put(int key, Drawable thumb) {
        if (thumb == null)
            return;

        if (ENABLE_VERBOSE) {
            DebugLog.vfmt("ImageLruCache", "put %d", key);
        }

        mImageMemCache.put(key, thumb);
        sGlobalMemCache.put(key, thumb.mutate());
    }
}