package tv.danmaku.android.support;

import tv.danmaku.android.BuildHelper;
import android.app.Activity;

public class SystemUINavHider {

    public static SystemUINavHider create(Activity activity) {
        if (BuildHelper.isApi11_HoneyCombOrLater()) {
            return new SystemUINavHider_HoneyComb(activity);
        } else {
            return new SystemUINavHider(activity);
        }
    }

    public static interface OnSystemUiVisibilityChangeListener {
        public abstract void onSystemUiVisibilityChange(int visibility);
    };

    public SystemUINavHider(Activity activity) {
        // do nothing
    }

    public void setEnableHide(boolean enableHide) {
        // do nothing
    }

    public boolean shouldHide() {
        return false;
    }

    public void show() {
        // do nothing
    }

    public void hide() {
        // do nothing
    }

    public void forceHideImmediately() {
        // do nothing
    }

    public void setOnSystemUiVisibilityChangeListener(
            OnSystemUiVisibilityChangeListener listener) {
        // do nothing
    }
}
