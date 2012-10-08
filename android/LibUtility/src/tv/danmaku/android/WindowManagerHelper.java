package tv.danmaku.android;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class WindowManagerHelper {
    public static final String TAG = WindowManagerHelper.class.getName();

    public static WindowManager getWindowManager(Context context) {
        Object service = context.getSystemService(Context.WINDOW_SERVICE);
        if (service == null)
            return null;

        return (WindowManager) service;
    }

    public static Display getDefaultDisplay(Context context) {
        WindowManager wm = getWindowManager(context);
        if (wm == null)
            return null;

        return wm.getDefaultDisplay();
    }
}
