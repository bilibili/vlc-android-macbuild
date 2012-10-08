package tv.danmaku.android;

import android.content.Context;
import android.os.PowerManager;

public class PowerManagerHelper {

    public static PowerManager.WakeLock newWakeLock(Context context, int flags,
            String tag) {
        Object service = context.getSystemService(Context.POWER_SERVICE);
        if (service == null)
            return null;

        PowerManager pw = (PowerManager) service;
        return pw.newWakeLock(flags, tag);
    }

}
