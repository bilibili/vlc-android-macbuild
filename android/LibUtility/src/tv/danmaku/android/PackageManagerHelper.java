package tv.danmaku.android;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

public class PackageManagerHelper {

    public static PackageInfo getPackageInfo(Context context,
            String packageName, int flag) {
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

    public static ApplicationInfo getApplicationInfo(Context context,
            String packageName) {
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

    public static ActivityInfo getActivityInfo(PackageManager pkgManager,
            ComponentName name) {
        if (pkgManager == null)
            return null;

        try {
            ActivityInfo activityInfo = pkgManager.getActivityInfo(name, 0);
            if (activityInfo == null)
                return null;

            return activityInfo;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getActivityLabel(Context context, ComponentName name) {
        PackageManager pkgManager = context.getPackageManager();
        if (pkgManager == null)
            return null;

        ActivityInfo activityInfo = getActivityInfo(pkgManager, name);
        if (activityInfo == null)
            return null;

        CharSequence cs = activityInfo.loadLabel(pkgManager);
        if (cs == null)
            return null;

        return cs.toString();
    }

    public static int getActivitOrientation(Context context, ComponentName name) {
        PackageManager pkgManager = context.getPackageManager();
        if (pkgManager == null)
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

        ActivityInfo activityInfo = getActivityInfo(pkgManager, name);
        if (activityInfo == null)
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

        return activityInfo.screenOrientation;
    }

    public static int getActivitOrientation(Activity activity) {
        return getActivitOrientation(activity, activity.getComponentName());
    }

    public static List<ResolveInfo> queryIntentActivities(Context context,
            Intent intent, int flags) {

        PackageManager manager = context.getPackageManager();
        if (manager == null)
            return null;

        return manager.queryIntentActivities(intent, flags);
    }

    public static String getVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return null;
    }
}
