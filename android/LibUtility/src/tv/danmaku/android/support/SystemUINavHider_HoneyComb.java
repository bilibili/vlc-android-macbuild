package tv.danmaku.android.support;

import java.lang.ref.WeakReference;

import tv.danmaku.android.BuildHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Handler;
import android.view.View;

@TargetApi(11)
public class SystemUINavHider_HoneyComb extends SystemUINavHider {
    private WeakReference<Activity> mWeakActivity;
    private boolean mEnableHide;
    private OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;

    private Handler mHandler = new Handler();

    private boolean mShouldHide;
    private boolean mIsReadyToHide;

    public SystemUINavHider_HoneyComb(Activity activity) {
        super(activity);
        mWeakActivity = new WeakReference<Activity>(activity);
    }

    @Override
    public void setEnableHide(boolean enableHide) {
        mEnableHide = enableHide;
    }

    @Override
    public boolean shouldHide() {
        return mShouldHide;
    }

    @Override
    public void show() {
        mShouldHide = false;

        if (!mEnableHide)
            return;

        Activity activity = mWeakActivity.get();
        if (activity == null)
            return;

        mHandler.removeCallbacks(mDelayedHide);

        mHandler.removeCallbacks(mReadyToHide);
        mHandler.postDelayed(mReadyToHide, 5000);

        SystemUIHelper.showNavigation(activity);
    }

    @Override
    public void hide() {
        mShouldHide = true;

        if (!mEnableHide)
            return;

        // do not need activity here, but check if activity is alive
        Activity activity = mWeakActivity.get();
        if (activity == null)
            return;

        mHandler.removeCallbacks(mDelayedHide);
        mHandler.postDelayed(mDelayedHide, 500);
    }

    @Override
    public void forceHideImmediately() {
        mShouldHide = true;
        mIsReadyToHide = true;

        doHide();
    }

    private void doHide() {
        if (!mShouldHide)
            return;

        if (!mEnableHide)
            return;

        Activity activity = mWeakActivity.get();
        if (activity == null)
            return;

        if (mIsReadyToHide) {
            mHandler.removeCallbacks(mDelayedHide);
            SystemUIHelper.hideNavigation(activity);
        } else {
            mHandler.removeCallbacks(mDelayedHide);
            mHandler.postDelayed(mDelayedHide, 1000);
        }
    }

    private Runnable mDelayedHide = new Runnable() {
        @Override
        public void run() {
            doHide();
        }
    };

    private Runnable mReadyToHide = new Runnable() {
        @Override
        public void run() {
            if (!mShouldHide)
                return;

            mIsReadyToHide = true;
        }
    };

    @Override
    public void setOnSystemUiVisibilityChangeListener(
            OnSystemUiVisibilityChangeListener listener) {
        if (BuildHelper.isApi14_IceCreamSandwichOrLater()) {
            Activity activity = mWeakActivity.get();
            if (activity == null)
                return;

            mOnSystemUiVisibilityChangeListener = listener;
            SystemUIHelper.setOnSystemUiVisibilityChangeListener(activity,
                    mPrivateListener);
        }
    }

    private View.OnSystemUiVisibilityChangeListener mPrivateListener = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            if (BuildHelper.isApi11_HoneyCombOrLater()) {
                if (mEnableHide && mOnSystemUiVisibilityChangeListener != null) {
                    mOnSystemUiVisibilityChangeListener
                            .onSystemUiVisibilityChange(visibility);
                }
            }
        }
    };
}
