package tv.danmaku.util;

import android.content.Context;
import android.widget.Toast;

public class AppToast {
    public static void showToast(Context context, int textResId, int duration) {
        showToast(context, context.getString(textResId), duration);
    }

    public static void showToast(Context context, String text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
