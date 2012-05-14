package tv.danmaku.util;

import java.io.File;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

public class AppContext {
    private static final String THUMB_SUB_DIR_NAME = "thumb";

    public static boolean isDebugMode() {
        return true;
    }

    public static File getThumbCacheDir(Context context, int subDirId)
            throws SecurityException {
        String subDir = String.format("%s/%03d", THUMB_SUB_DIR_NAME, subDirId);
        if (TextUtils.isEmpty(subDir))
            return null;

        // 优先使用SD卡
        File externalCacheDir = getExternalCacheDir(context);
        if (externalCacheDir != null) {

            File externalThumbCacheDir = new File(externalCacheDir, subDir);
            if (externalThumbCacheDir.isDirectory())
                return externalThumbCacheDir;

            externalThumbCacheDir.mkdirs();
            if (externalThumbCacheDir.isDirectory())
                return externalThumbCacheDir;
        }

        // 如果没有SD卡,则使用默认存储
        File systemCacheDir = context.getCacheDir();
        if (systemCacheDir != null) {

            File systemThumbCacheDir = new File(systemCacheDir, subDir);
            if (systemThumbCacheDir.isDirectory())
                return systemThumbCacheDir;

            systemThumbCacheDir.mkdirs();
            if (systemThumbCacheDir.isDirectory())
                return systemThumbCacheDir;
        }

        return null;
    }

    // for Android 2.3
    // public static getExternalCacheDir() {
    // Context context = getContext();
    // if (context == null)
    // return null;
    //
    // return context.getExternalCacheDir();
    // }

    // for Android 2.1
    public static File getExternalCacheDir(Context context) {
        StringBuilder relPath = new StringBuilder("Android/data");
        relPath.append("/");
        relPath.append(context.getPackageName());
        relPath.append("/cache");

        File extStorage = Environment.getExternalStorageDirectory();
        return new File(extStorage, relPath.toString());
    }

    public static void clearThumbCacheDir(Context context) {
        String subDir = String.format("%s", THUMB_SUB_DIR_NAME);
        if (TextUtils.isEmpty(subDir))
            return;

        // 清除SD卡的缓存
        File externalCacheDir = getExternalCacheDir(context);
        if (externalCacheDir != null) {

            File externalThumbCacheDir = new File(externalCacheDir, subDir);
            if (externalThumbCacheDir.isDirectory())
                doClearThumbCacheDir(externalThumbCacheDir);
        }

        // 清除默认存储的缓存
        File systemCacheDir = context.getCacheDir();
        if (systemCacheDir != null) {

            File systemThumbCacheDir = new File(systemCacheDir, subDir);
            if (systemThumbCacheDir.isDirectory())
                doClearThumbCacheDir(systemThumbCacheDir);
        }
    }

    private static void doClearThumbCacheDir(File thumbRootDir) {
        if (thumbRootDir.isDirectory()) {
            File[] subDirList = thumbRootDir.listFiles();
            if (subDirList != null) {

                for (File subDir : subDirList) {
                    if (subDir.isDirectory()) {
                        File[] thumbList = subDir.listFiles();
                        if (thumbList != null) {

                            for (File thumb : thumbList) {
                                if (!thumb.isDirectory()) {
                                    thumb.delete();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
