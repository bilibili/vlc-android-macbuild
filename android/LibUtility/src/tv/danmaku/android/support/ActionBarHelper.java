package tv.danmaku.android.support;

import tv.danmaku.android.BuildHelper;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;

@TargetApi(11)
public class ActionBarHelper {
    public static void setDisplayHomeAsUpEnabled(Activity activity,
            boolean showHomeAsUp) {
        if (BuildHelper.isApi11_HoneyCombOrLater()) {
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public static void setDisplayShowTitleEnabled(Activity activity,
            boolean showTitle) {
        if (BuildHelper.isApi11_HoneyCombOrLater()) {
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(showTitle);
            }
        }
    }

    public static void setDisplayUseLogoEnabled(Activity activity,
            boolean useLogo) {
        if (BuildHelper.isApi11_HoneyCombOrLater()) {
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayUseLogoEnabled(useLogo);
            }
        }
    }

    public static void setDisplayOptions(Activity activity, int options,
            int mask) {
        if (BuildHelper.isApi11_HoneyCombOrLater()) {
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayOptions(options, mask);
            }
        }
    }
}
