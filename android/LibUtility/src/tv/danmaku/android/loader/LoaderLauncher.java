package tv.danmaku.android.loader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import tv.danmaku.android.util.Assure;
import tv.danmaku.android.util.DebugLog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public class LoaderLauncher<D> implements LoaderCallbacks<D> {
    private static final String TAG = LoaderLauncher.class.getSimpleName();
    private boolean ENABLE_VERBOSE;

    /* 使用loader pool的id的任务也会统一以 mLoaderId回调给外部 */
    private WeakReference<LoaderCallbacks<D>> mWeakCallbacks;
    private WeakReference<Fragment> mWeakFragment;

    /* 如果没有足够的loader, 请求会在这里暂存 */
    private ArrayList<Integer> mLoaderPool = new ArrayList<Integer>();
    private HashSet<Integer> mBusyLoaders = new HashSet<Integer>();
    private LinkedList<LoaderRequest<D>> mRequestQueue = new LinkedList<LoaderRequest<D>>();

    private Handler mHandler = new Handler();
    private int mQueueSize; // <=0 for no limit

    public LoaderLauncher(int id, Fragment fragment,
            LoaderCallbacks<D> callbacks) {
        mWeakFragment = new WeakReference<Fragment>(fragment);
        mWeakCallbacks = new WeakReference<LoaderCallbacks<D>>(callbacks);

        addExtraLoader(id);
    }

    public LoaderLauncher(int idBegin, int idEnd, Fragment fragment,
            LoaderCallbacks<D> callbacks) {
        mWeakFragment = new WeakReference<Fragment>(fragment);
        mWeakCallbacks = new WeakReference<LoaderCallbacks<D>>(callbacks);

        addExtraLoaderRange(idBegin, idEnd);
    }

    public void setQueueSize(int queueSize) {
        mQueueSize = queueSize;
    }

    private void addExtraLoader(int id) {
        mLoaderPool.add(id);
    }

    private void addExtraLoaderRange(int begin, int end) {
        for (int i = begin; i < end; ++i)
            addExtraLoader(i);

    }

    public void pushHead(Bundle args) {
        LoaderRequest<D> request = new LoaderRequest<D>(0, args, this);
        mRequestQueue.addFirst(request);

        if (mQueueSize > 0) {
            while (mRequestQueue.size() > mQueueSize) {
                mRequestQueue.removeLast();
            }
        }

        launchHeadLoader();
    }

    public void pushTail(Bundle args) {
        LoaderRequest<D> request = new LoaderRequest<D>(0, args, this);
        mRequestQueue.addLast(request);

        if (mQueueSize > 0) {
            while (mRequestQueue.size() > mQueueSize) {
                mRequestQueue.removeFirst();
            }
        }

        launchHeadLoader();
    }

    private void launchHeadLoader() {
        if (mRequestQueue.isEmpty())
            return;

        LoaderRequest<D> request = mRequestQueue.removeFirst();
        if (request == null)
            return;

        if (launchLoader(request))
            return;

        // not launched, push back
        if (ENABLE_VERBOSE)
            DebugLog.wfmt(
                    TAG,
                    "queueLoader x%x, %s-%d",
                    request.hashCode(),
                    new String(request.mArgs
                            .getString(LoaderBundle._BUNDLE_NAME)),
                    request.mArgs.getInt(LoaderBundle._BUNDLE_ID));

        mRequestQueue.addFirst(request);
    }

    private boolean launchLoader(LoaderRequest<D> request) {
        return launchLoader(request.mArgs);
    }

    private boolean launchLoader(Bundle args) {
        Assure.checkNotEmptyCollection(mLoaderPool);

        LoaderManager loaderManager = getLoaderManager();
        if (loaderManager == null)
            return false;

        /* find a loader */
        for (Integer id : mLoaderPool) {
            Loader<D> loader = loaderManager.getLoader(id);
            if (loader == null) {

                if (ENABLE_VERBOSE)
                    DebugLog.vfmt(TAG, "initLoader %d", id);

                mBusyLoaders.add(id);
                loaderManager.initLoader(id, args, this);
                return true;

            } else if (!mBusyLoaders.contains(id)) {

                if (ENABLE_VERBOSE)
                    DebugLog.vfmt(TAG, "restartLoader %d", id);

                mBusyLoaders.add(id);
                loaderManager.restartLoader(id, args, this);
                return true;

            }
        }

        return false;
    }

    /*--------------------------------------
     * LoaderManager.Callbacks
     */
    @Override
    public Loader<D> onCreateLoader(int id, Bundle args) {
        LoaderCallbacks<D> callbacks = getLoaderCallbacks();
        if (callbacks == null)
            return null;

        return callbacks.onCreateLoader(id, args);
    }

    @Override
    public void onLoadFinished(Loader<D> loader, D data) {
        int id = loader.getId();
        mBusyLoaders.remove(id);

        if (ENABLE_VERBOSE)
            DebugLog.vfmt(TAG, "finish %d", id);

        LoaderCallbacks<D> callbacks = getLoaderCallbacks();
        if (callbacks == null)
            return;

        callbacks.onLoadFinished(loader, data);

        /* avoid unlimited recursive */
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                /* launch next loader */
                launchHeadLoader();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<D> loader) {
        int id = loader.getId();
        mBusyLoaders.remove(id);

        if (ENABLE_VERBOSE)
            DebugLog.vfmt(TAG, "reset %d", id);

        LoaderCallbacks<D> callbacks = getLoaderCallbacks();
        mBusyLoaders.add(loader.getId());
        if (callbacks == null)
            return;

        callbacks.onLoaderReset(loader);
    }

    /*--------------------------------------
     * getter
     */
    private LoaderManager getLoaderManager() {
        Fragment fragment = mWeakFragment.get();
        if (fragment == null)
            return null;

        if (!fragment.isAdded() || fragment.isDetached())
            return null;

        try {
            LoaderManager loaderManager = fragment.getLoaderManager();
            return loaderManager;
        } catch (IllegalStateException e) {
        }

        return null;
    }

    private LoaderCallbacks<D> getLoaderCallbacks() {
        return mWeakCallbacks.get();
    }

    public void setEnableVerbose(boolean enableVerbose) {
        ENABLE_VERBOSE = enableVerbose;
    }
}
