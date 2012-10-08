package tv.danmaku.android;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
    public static void showToast(Context context, int textResId, int duration) {
        showToast(context, context.getString(textResId), duration);
    }

    public static void showToast(Context context, String text, int duration) {
        Toast.makeText(context, text, duration).show();
    }

    public static void showToastLong(Context context, int textResId) {
        showToast(context, textResId, Toast.LENGTH_LONG);
    }

    public static void showToastLong(Context context, String text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    public static void showToastShort(Context context, int textResId) {
        showToast(context, textResId, Toast.LENGTH_SHORT);
    }

    public static void showToastShort(Context context, String text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }
}
