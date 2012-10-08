package tv.danmaku.android;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InputMethodManagerHelper {

    public static void showSoftInput(Context context, View view, int flags) {
        InputMethodManager manager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null)
            return;

        manager.showSoftInput(view, flags);
    }

    public static void hideSoftInput(Context context, View view, int flags) {
        InputMethodManager manager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null)
            return;

        manager.hideSoftInputFromWindow(view.getWindowToken(), flags);
    }
}
