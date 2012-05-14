package tv.danmaku.util;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

public class AppPackageManager {

    public static PackageInfo getPackageInfo(Context context, String packageName, int flag) {
        PackageManager manager = context.getPackageManager();
        if (manager == null)
            return null;

        try {
            return manager.getPackageInfo(packageName, flag);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ApplicationInfo getApplicationInfo(Context context, String packageName) {
        PackageInfo pkgInfo = getPackageInfo(context, packageName, 0);
        if (pkgInfo == null)
            return null;

        return pkgInfo.applicationInfo;
    }

    public static String getAppLabel(Context context, String packageName) {
        PackageManager pkgManager = context.getPackageManager();
        if (pkgManager == null)
            return null;

        PackageInfo pkgInfo;
        try {
            pkgInfo = pkgManager.getPackageInfo(packageName, 0);
            if (pkgInfo == null)
                return null;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (appInfo == null)
            return null;

        CharSequence cs = appInfo.loadLabel(pkgManager);
        if (cs == null)
            return null;

        return cs.toString();
    }

    public static String getActivityLabel(Context context, ComponentName name) {
        PackageManager pkgManager = context.getPackageManager();
        if (pkgManager == null)
            return null;

        ActivityInfo activityInfo;
        try {
            activityInfo = pkgManager.getActivityInfo(name, 0);
            if (activityInfo == null)
                return null;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        CharSequence cs = activityInfo.loadLabel(pkgManager);
        if (cs == null)
            return null;

        return cs.toString();
    }

    public static List<ResolveInfo> queryIntentActivities(Context context, Intent intent,
            int flags) {

        PackageManager manager = context.getPackageManager();
        if (manager == null)
            return null;

        return manager.queryIntentActivities(intent, flags);
    }
    
    public static String getVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);

            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        
        return null;
    }
}
