package tv.danmaku.android.util;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;

public class WeakActivity<T_Activity extends Activity> {
    private WeakReference<T_Activity> mWeakReference;
    
    public WeakActivity(T_Activity activity) {
        if (activity != null) {
            mWeakReference = new WeakReference<T_Activity>(activity);
        }
    }
    
    final public void setActivity(T_Activity activity) {
        if (activity == null) {
            mWeakReference = null;
        } else {
            mWeakReference = new WeakReference<T_Activity>(activity);
        }
    }
    
    final public T_Activity getActivity() {
        if (mWeakReference == null)
            return null;
        
        return mWeakReference.get();
    }
    
    // methods for Activity
    final public void startActivity(Intent intent) {
        T_Activity activity = getActivity();
        if (activity == null)
            return;
        
        activity.startActivity(intent);
    }
    
    final public void finish() {
        T_Activity activity = getActivity();
        if (activity == null)
            return;
        
        activity.finish();
    }
    
}
