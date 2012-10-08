package tv.danmaku.android.util;

import tv.danmaku.android.BuildHelper;

public class StringHelper {
    // prevent memory leak by reference huge string
    public static String dup(String original) {
        if (BuildHelper.isApi9_GingerBreadOrLater()) {
            return new String(original);
        } else {
            // there is a constructor memory-leak bug in android before api-10
            // http://code.google.com/p/android/issues/detail?id=26228
            return new String(original.getBytes());
        }
    }

    public static String notNullString(String original) {
        if (original == null)
            return "";

        return original;
    }
}
