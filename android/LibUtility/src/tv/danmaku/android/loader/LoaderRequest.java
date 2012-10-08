package tv.danmaku.android.loader;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

public class LoaderRequest<D> {
    public int mId;
    public Bundle mArgs;
    public LoaderManager.LoaderCallbacks<D> mCallback;

    public boolean mRestart;

    public LoaderRequest(int id, Bundle args, LoaderCallbacks<D> callback) {
        mId = id;
        mArgs = args;
        mCallback = callback;
    }

    public LoaderRequest(int id, Bundle args, LoaderCallbacks<D> callback,
            boolean restart) {
        mId = id;
        mArgs = args;
        mCallback = callback;

        mRestart = restart;
    }
}
