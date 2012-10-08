package tv.danmaku.android.support;

import tv.danmaku.android.BuildHelper;
import android.annotation.TargetApi;
import android.app.Activity;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;

public class SystemUIHelper {

    @TargetApi(11)
    public static void setSystemUiVisibility(Activity activity, int visibility) {
        if (activity == null)
            return;

        if (BuildHelper.isApi11_HoneyCombOrLater()) {
            activity.getWindow().getDecorView()
                    .setSystemUiVisibility(visibility);
        }
    }

    public static void hideNavigation(Activity activity) {
        if (activity == null)
            return;

        if (!BuildHelper.isApi11_HoneyCombOrLater())
            return;

        int visibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        // visibility |= View.SYSTEM_UI_FLAG_LOW_PROFILE;

        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

        setSystemUiVisibility(activity, visibility);
    }

    public static void showNavigation(Activity activity) {
        if (activity == null)
            return;
        if (!BuildHelper.isApi11_HoneyCombOrLater())
            return;

        int visibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        setSystemUiVisibility(activity, visibility);
    }

    @TargetApi(11)
    public static void setOnSystemUiVisibilityChangeListener(Activity activity,
            OnSystemUiVisibilityChangeListener listener) {
        if (activity == null)
            return;

        if (BuildHelper.isApi11_HoneyCombOrLater()) {
            activity.getWindow().getDecorView()
                    .findViewById(android.R.id.content)
                    .setOnSystemUiVisibilityChangeListener(listener);
        }
    }

    @TargetApi(16)
    public static void requestFitSysWindow(View view) {
        if (view == null)
            return;

        if (BuildHelper.isApi16_JellyBeanOrLater()) {
            view.requestFitSystemWindows();
        }
    }

    public static String getVisibilityVerbose(int visibility) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(visibility));
        sb.append(':');

        if (0 != (visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE))
            sb.append("low|");
        if (0 != (visibility & View.SYSTEM_UI_FLAG_FULLSCREEN))
            sb.append("full|");
        if (0 != (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION))
            sb.append("hide|");
        if (0 != (visibility & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN))
            sb.append("layout_full|");
        if (0 != (visibility & View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION))
            sb.append("layout_hide|");
        if (0 != (visibility & View.SYSTEM_UI_FLAG_LAYOUT_STABLE))
            sb.append("layout_stable|");

        return sb.toString();
    }
}
