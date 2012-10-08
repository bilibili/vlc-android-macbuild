package tv.danmaku.android;

import java.io.File;
import android.content.Context;
import android.os.Environment;

public class EnvironmentHelper {
    // for Android 2.1
    public static File getExternalCacheDir(Context context) {
        StringBuilder relPath = new StringBuilder("Android/data");
        relPath.append("/");
        relPath.append(context.getPackageName());
        relPath.append("/cache");

        File extStorage = Environment.getExternalStorageDirectory();
        return new File(extStorage, relPath.toString());
    }
}
