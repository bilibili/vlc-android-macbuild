package tv.danmaku.android.util;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public class WeakHandler extends Handler {
    private WeakReference<Handler.Callback> mWeakCallback;

    public WeakHandler(Handler.Callback callback) {
        mWeakCallback = new WeakReference<Handler.Callback>(callback);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        Handler.Callback callback = mWeakCallback.get();
        if (callback == null)
            return;

        callback.handleMessage(msg);
    }
}
