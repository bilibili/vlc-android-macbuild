package tv.danmaku.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AppInputMethodManager {

    public static void showSoftInput(Context context, View view, int flags) {
        InputMethodManager manager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager == null)
            return;

        manager.showSoftInput(view, flags);
    }
}
