package tv.danmaku.android.util;

import tv.danmaku.pragma.Pragma;
import android.util.Log;

public class DebugLog {

    // 调试配置,发布时,将 DEBUG_ON 置为 false
    public static final boolean DEBUG_ON = Pragma.DEBUG;
    public static final boolean DEBUG_OFF = false;

    public static boolean ENABLE_ERROR = DEBUG_ON;
    public static boolean ENABLE_INFO = DEBUG_ON;
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

    public static int efmt(String tag, String fmt, Object... args) {
        if (ENABLE_ERROR) {
            String msg = String.format(fmt, args);
            return Log.e(tag, msg);
        }

        return 0;
    }

    public static int i(String tag, String msg) {
        if (ENABLE_INFO) {
            return Log.i(tag, msg);
        }

        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (ENABLE_INFO) {
            return Log.i(tag, msg, tr);
        }

        return 0;
    }

    public static int ifmt(String tag, String fmt, Object... args) {
        if (ENABLE_INFO) {
            String msg = String.format(fmt, args);
            return Log.i(tag, msg);
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

    public static int wfmt(String tag, String fmt, Object... args) {
        if (ENABLE_WARN) {
            String msg = String.format(fmt, args);
            return Log.w(tag, msg);
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

    public static int dfmt(String tag, String fmt, Object... args) {
        if (ENABLE_DEBUG) {
            String msg = String.format(fmt, args);
            return Log.d(tag, msg);
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

    public static int vfmt(String tag, String fmt, Object... args) {
        if (ENABLE_VERBOSE) {
            String msg = String.format(fmt, args);
            return Log.v(tag, msg);
        }

        return 0;
    }
}
