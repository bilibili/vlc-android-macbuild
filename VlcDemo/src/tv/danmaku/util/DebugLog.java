package tv.danmaku.util;

import android.util.Log;

public class DebugLog {
    
    // 调试配置,发布时,将 DEBUG_ON 置为 false
    public static final boolean DEBUG_ON = AppBuildConfig.DEBUG;
    public static final boolean DEBUG_OFF = false;

    public static boolean ENABLE_ERROR = DEBUG_ON;
    public static boolean ENABLE_WARN = DEBUG_ON;
    public static boolean ENABLE_DEBUG = DEBUG_ON;
    public static boolean ENABLE_VERBOSE = DEBUG_ON;

    public static int e(String tag, String msg) {
        if (ENABLE_ERROR) {
            return Log.e(tag, msg);
        }

        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (ENABLE_ERROR) {
            return Log.e(tag, msg, tr);
        }

        return 0;
    }
    
    public static int w(String tag, String msg) {
        if (ENABLE_WARN) {
            return Log.w(tag, msg);
        }

        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (ENABLE_WARN) {
            return Log.w(tag, msg, tr);
        }

        return 0;
    }

    public static int d(String tag, String msg) {
        if (ENABLE_DEBUG) {
            return Log.d(tag, msg);
        }

        return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (ENABLE_DEBUG) {
            return Log.d(tag, msg, tr);
        }

        return 0;
    }

    public static int v(String tag, String msg) {
        if (ENABLE_VERBOSE) {
            return Log.v(tag, msg);
        }

        return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (ENABLE_VERBOSE) {
            return Log.v(tag, msg, tr);
        }

        return 0;
    }
}
