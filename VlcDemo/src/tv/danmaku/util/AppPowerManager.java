package tv.danmaku.util;

import android.content.Context;
import android.os.PowerManager;

public class AppPowerManager {

    public static PowerManager.WakeLock newWakeLock(Context context, int flags,
            String tag) {
        Object service = context.getSystemService(Context.POWER_SERVICE);
        if (service == null)
            return null;

        PowerManager pw = (PowerManager) service;
        return pw.newWakeLock(flags, tag);
    }

}
