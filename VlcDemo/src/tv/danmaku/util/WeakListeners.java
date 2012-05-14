package tv.danmaku.util;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.view.View;

public class WeakListeners {
    
    public static class CallFinishOnClicked implements View.OnClickListener {
        private WeakReference<Activity> mWeakActivity;
        
        public CallFinishOnClicked(Activity activity) {
            mWeakActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void onClick(View v) {
            if (mWeakActivity == null) {
                return;
            }
            
            Activity activity = mWeakActivity.get();
            if (activity == null) {
                return;
            }
            
            activity.finish();
        }
    }
}
