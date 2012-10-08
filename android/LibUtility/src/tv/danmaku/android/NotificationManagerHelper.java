package tv.danmaku.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class NotificationManagerHelper {
    public static NotificationManager getNotificationManager(Context context) {
        Object service = context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (service == null)
            return null;

        return (NotificationManager) service;
    }

    public static void notify(Context context, int id, Notification notification) {
        NotificationManager nm = getNotificationManager(context);
        if (nm == null)
            return;

        nm.notify(id, notification);
    }
}
