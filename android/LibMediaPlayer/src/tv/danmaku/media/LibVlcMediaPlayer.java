package tv.danmaku.media;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.videolan.vlc.EventManager;
import org.videolan.vlc.LibVLC;
import org.videolan.vlc.LibVlcLibraryLoader;
import org.videolan.vlc.LibVlcMessages;
import org.videolan.vlc.LibVlcException;
import org.videolan.vlc.events.MediaPlayerBuffering;
import org.videolan.vlc.events.MediaPlayerBufferingTotal;
import org.videolan.vlc.events.MediaPlayerSeekableChanged;

import tv.danmaku.android.util.Assure;
import tv.danmaku.android.util.CollectionHelper;
import tv.danmaku.android.util.DebugLog;
import tv.danmaku.android.util.WeakHandler;
import tv.danmaku.media.resource.PlayIndex;
import tv.danmaku.media.vsl.LibVlcVideoSegmentListLoader;
import tv.danmaku.pragma.Pragma;
import android.content.Context;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

// 用于原始的libvlc封装
public class LibVlcMediaPlayer extends AbsMediaPlayer implements
        Handler.Callback {

    public static final String TAG = LibVlcMediaPlayer.class.getSimpleName();

    // audiotrack/opensles
    public static final String[] sAout_AudioTrack_Native = new String[] { ":aout=android_audiotrack" };
    public static final String[] sAout_AudioTrack_Java = new String[] { ":aout=audiotrack_java" };
    public static final String[] sAout_OpenSLES = new String[] { ":aout=opensles" };

    // HW/SW
    public static final String[] sCodec_MediaCodec_Iomx = new String[] {
            ":codec=mediacodec,iomx,all", ":file-caching=1500",
            ":network-caching=1500" };
    public static final String[] sCodec_Iomx = new String[] {
            ":codec=iomx,all", ":file-caching=1500", ":network-caching=1500" };
    public static final String[] sCodec_All = new String[] { ":codec=all",
            ":avcodec-fast=1" };

    // avcodec optimize
    public static final String[] sAvcodecOptimize_SkipMost = new String[] {
            ":avcodec-skiploopfilter=4", ":avcodec-skip-frame=1",
            ":avcodec-skip-idct=1", };
    public static final String[] sAvcodecOptimize_SkipLoopfilter_None = new String[] { ":avcodec-skiploopfilter=0" };
    public static final String[] sAvcodecOptimize_SkipLoopfilter_NonRef = new String[] { ":avcodec-skiploopfilter=1" };
    public static final String[] sAvcodecOptimize_SkipLoopfilter_BiDir = new String[] { ":avcodec-skiploopfilter=2" };
    public static final String[] sAvcodecOptimize_SkipLoopfilter_NonKey = new String[] { ":avcodec-skiploopfilter=3" };
    public static final String[] sAvcodecOptimize_SkipLoopfilter_All = new String[] { ":avcodec-skiploopfilter=4" };

    public static final String[] sAvcodecOptimize_SkipNone = new String[] {};

    // membuf
    public static final String[] sMembuf_Enable = new String[] { ":membuf-enable=1" };

    // audio stretch
    public static final String[] sAudioStretch_Enable = new String[] { ":audio-time-stretch" };

    public static final String[] sHttpUserAgent_BiliDroid = new String[] { ":http-user-agent="
            + Pragma.BILI_HTTP_UA_BILIDROID };

    private LibVLC mLibVLC;
    private String[] mVlcPlayerOptions;

    private SurfaceHolder mSurfaceHolder;

    private LibVlcVideoSegmentListLoader mIndexResolver;
    private String mRawVideoMrl;
    private String mVideoMrl;
    private int mVideoHeight;
    private int mVideoWidth;

    private boolean mHasReadMedia;

    private boolean mPrepared;
    private float mLastBufferingPercent;

    private boolean mCompleted;

    private int mSeekWhenDurationChanged;

    private WeakReference<Context> mWeakContext;
    private WeakHandler mVlcHandler;

    private ModuleInfo mModuleInfo;

    public static LibVlcMediaPlayer createWithOptions(Context context,
            LibVlcLibraryLoader libLoader, PlayIndex.Resolver resolver,
            String[]... extraParams) throws LibVlcException {
        // options
        ArrayList<String> options = new ArrayList<String>();
        CollectionHelper.Append(options, extraParams);
        return create(context, libLoader, resolver,
                CollectionHelper.toArray(options));
    }

    private static LibVlcMediaPlayer create(Context context,
            LibVlcLibraryLoader libLoader, PlayIndex.Resolver resolver,
            String... extraParams) throws LibVlcException {
        DebugLog.v(TAG, "create");
        LibVlcMediaPlayer player = new LibVlcMediaPlayer(context, resolver);

        try {
            player.mLibVLC = LibVLC.getInstance(context, libLoader);
            EventManager.getIntance().addHandler(player.mVlcHandler);

            // options
            ArrayList<String> options = new ArrayList<String>();
            CollectionHelper.Append(options, extraParams);

            // TODO: add options here

            player.mVlcPlayerOptions = options.toArray(new String[options
                    .size()]);

            return player;
        } catch (LibVlcException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected LibVlcMediaPlayer(Context context, PlayIndex.Resolver resolver) {
        mVlcHandler = new WeakHandler(this);
        mWeakContext = new WeakReference<Context>(context);

        mIndexResolver = new LibVlcVideoSegmentListLoader(context, resolver);
    }

    @Override
    public int getCurrentPosition() {
        return (int) mLibVLC.getTime();
    }

    @Override
    public int getDuration() {
        int duration = (int) mLibVLC.getLength();
        return duration;
    }

    @Override
    public int getVideoHeight() {
        return mVideoHeight;
    }

    @Override
    public int getVideoWidth() {
        return mVideoWidth;
    }

    @Override
    public boolean isPlaying() {
        return mLibVLC.isPlaying();
    }

    @Override
    public void start() throws IllegalStateException {
        if (mHasReadMedia) {
            DebugLog.v(TAG, "play");
            if (mCompleted) {
                restart();
            } else {
                if (!mLibVLC.isPlaying()) {
                    DebugLog.v(TAG, "start:play");
                    mLibVLC.play();
                }
            }
        } else {
            DebugLog.v(TAG, "start:readMediaEX");
            mLibVLC.readMediaEx(mVideoMrl, mVlcPlayerOptions);
            mHasReadMedia = true;
        }
    }

    private void restart() {
        DebugLog.v(TAG, "restart");
        mCompleted = false;
        mLibVLC.stop();
        mLibVLC.setPosition(0);
        mLibVLC.play();
    }

    @Override
    public void stop() throws IllegalStateException {
        DebugLog.v(TAG, "stop");
        mLibVLC.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        DebugLog.v(TAG, "pause");
        mLibVLC.pause();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        Assure.checkNotNull(mVideoMrl);

        DebugLog.v(TAG, "prepareAsync:readMediaEx");
        mLibVLC.readMediaEx(mVideoMrl, mVlcPlayerOptions);
        mHasReadMedia = true;
        // Message msg = mSurfaceHandler.obtainMessage(MSG_FAKE_PREPARED);
        // mSurfaceHandler.sendMessage(msg);
    }

    @Override
    public void release() {
        DebugLog.v(TAG, "release");
        mLibVLC.stop();

        EventManager.getIntance().removeHandler(mVlcHandler);
    }

    @Override
    public void reset() {
        DebugLog.v(TAG, "reset");
        mLibVLC.stop();
        mPrepared = false;
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        int duration = getDuration();
        if (duration <= 0) {
            DebugLog.e(TAG, "no duration for seek, try later");
            mSeekWhenDurationChanged = msec;
            return;
        }

        DebugLog.v(TAG, "vlc seek to " + msec);
        mLibVLC.setTime(msec);
        mSeekWhenDurationChanged = 0;
    }

    @Override
    public void setDataSource(String uri) throws IOException,
            IllegalArgumentException, IllegalStateException {
        DebugLog.v(TAG, "vlc play " + uri);

        mRawVideoMrl = uri;
        // mRawVideoMrl = "http:/sina-hlv/v.iask.com/v_play.php?vid=47424256";
        // mRawVideoMrl =
        // "http:/youku-750000/v.youku.com/player/getPlayList/VideoIDS/XNDQ3MDc2MjYw";
        // mRawVideoMrl =
        // "http:/youku-2000000/v.youku.com/player/getPlayList/VideoIDS/XNDQ2MDA0MDYw";
        // mRawVideoMrl = "http:/sina-hlv/v.iask.com/v_play.php?vid=78817175";
        /*-
         * http://v.youku.com/player/getPlayList/VideoIDS/XNDQ2MDA0MDYw
         * http://v.iask.com/v_play.php?vid=78817175
         * http://hot.vrs.sohu.com/vrs_flash.action?vid=717069
         * http://cache.video.qiyi.com/v/6fb817df2a5c4e9fac8fa9e6af5c5935
         * http://vdn.apps.cntv.cn/api/getHttpVideoInfo.do?pid=9f2f1704cae745cb99be4ceb207d0f97
         */
        mIndexResolver.parseIndexMrl(mRawVideoMrl);
        mVideoMrl = mIndexResolver.getIndexMrlForVlcPlayer();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setDisplay(SurfaceHolder holder) {
        DebugLog.v(TAG, "setDisplay");
        mSurfaceHolder = holder;
        if (holder != null) {
            holder.addCallback(mSurfaceCallback);
            holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
            holder.setFormat(PixelFormat.RGBX_8888);
        } else {
            mLibVLC.detachSurface();
        }
    }

    private SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            mLibVLC.attachSurface(holder.getSurface(), LibVlcMediaPlayer.this,
                    width, height);
        }

        public void surfaceCreated(SurfaceHolder holder) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            mLibVLC.detachSurface();
        }
    };

    @Override
    public void setAudioStreamType(int streamMusic) {
        // TODO:implement
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setKeepScreenOn(screenOn);
        }
    }

    @Override
    public void enableLog(boolean enable) {
        if (mLibVLC != null)
            mLibVLC.changeVerbosity(enable);
    }

    @Override
    public boolean isBufferingEnd() {
        return true;
    }

    @Override
    public ModuleInfo getModuleInfo() {
        return mModuleInfo;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case LibVlcMessages.VLC_VIDEO_SIZE_CHANGED:
            if (mOnVideoSizeChangedListener != null) {
                mOnVideoSizeChangedListener.onVideoSizeChanged(
                        LibVlcMediaPlayer.this, mVideoWidth, mVideoHeight);
            }
            return true;
        case LibVlcMessages.VLC_FAKE_PREPARED:
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(LibVlcMediaPlayer.this);
            }
            return true;
        case LibVlcMessages.VLC_EVENT:
            // process later
            break;
        default:
            return false;
        }

        switch (msg.arg1) {
        case EventManager.MediaPlayerLengthChanged:
        case EventManager.MediaDurationChanged: {
            DebugLog.v(TAG,
                    String.format("vlc duration changed %d", getDuration()));
            if (mSeekWhenDurationChanged != 0) {
                seekTo(mSeekWhenDurationChanged);
            }
            break;
        }
        case EventManager.MediaStateChanged: {
            break;
        }
        case EventManager.MediaPlayerOpening: {
            break;
        }
        case EventManager.MediaPlayerBuffering: {
            if (mOnInfoListener != null) {
                if (msg.obj != null) {
                    float percent = MediaPlayerBuffering
                            .getNewCache((Bundle) msg.obj);
                    // 这里返回的是缓冲百分比，而不是缓冲位置
                    if (percent < mLastBufferingPercent) {
                        if (mOnInfoListener != null) {
                            mOnInfoListener.onInfo(this,
                                    MediaPlayer.MEDIA_INFO_BUFFERING_START, 0);
                        }
                    }

                    if (percent >= 100.0f) {
                        /*
                         * if (mOnInfoListener != null) {
                         * mOnInfoListener.onInfo(LibVlcMediaPlayer.this,
                         * PlayerAdapter.MEDIA_INFO_BUFFERING_END, 0); }
                         */

                        if (!mPrepared) {
                            mPrepared = true;

                            if (mOnPreparedListener != null) {
                                mOnPreparedListener.onPrepared(this);
                            }
                        }
                    }

                    mLastBufferingPercent = percent;
                }
            }
            break;
        }
        case EventManager.MediaPlayerBufferingTotal: {
            if (mOnBufferingUpdateListener != null) {
                if (msg.obj != null) {
                    float percent = MediaPlayerBufferingTotal
                            .getNewCacheTotal((Bundle) msg.obj);
                    // 这里返回的是缓冲比例，而不是缓冲位置
                    DebugLog.v(TAG, "buffering " + percent);
                    mOnBufferingUpdateListener.onBufferingUpdate(this,
                            (int) percent);
                }
            }
            break;
        }
        case EventManager.MediaPlayerPlaying: {
            break;
        }
        case EventManager.MediaPlayerPaused: {
            break;
        }
        case EventManager.MediaPlayerStopped: {
            break;
        }
        case EventManager.MediaPlayerEndReached: {
            mCompleted = true;
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(this);
            }
            break;
        }
        case EventManager.MediaPlayerEncounteredError: {
            if (mOnErrorListener != null) {
                mOnErrorListener.onError(this, MediaPlayer.MEDIA_ERROR_UNKNOWN,
                        0);
            }
            break;
        }
        case EventManager.MediaPlayerSeekableChanged: {
            if (mOnInfoListener != null) {
                if (msg.obj != null) {
                    boolean seekable = MediaPlayerSeekableChanged
                            .getNewSeekable((Bundle) msg.obj);
                    if (!seekable && mOnInfoListener != null) {
                        mOnInfoListener.onInfo(this,
                                MediaPlayer.MEDIA_INFO_NOT_SEEKABLE, 0);
                    }
                }
            }
            break;
        }
        case EventManager.MediaPlayerPausableChanged: {
            break;
        }
        case EventManager.MediaPlayerModuleChanged: {
            if (msg.obj != null) {
                mModuleInfo = ModuleInfo.parseModuleInfo((Bundle) msg.obj);
            }
            break;
        }
        default:
            return false;
        }

        return true;
    };

    /*---------------------------------
     * getter
     */
    @SuppressWarnings("unused")
    private Context getContext() {
        return mWeakContext.get();
    }

    /*---------------------------------
     * call from native
     */
    public void setSurfaceSize(int width, int height) {
        DebugLog.vfmt(TAG, "native setSurfaceSize %d, %d", width, height);
        mVideoWidth = width;
        mVideoHeight = height;

        Message msg = mVlcHandler
                .obtainMessage(LibVlcMessages.VLC_VIDEO_SIZE_CHANGED);
        mVlcHandler.sendMessage(msg);
    }

    public boolean vslLoad(boolean forceReload) {
        return mIndexResolver.loadIndex(forceReload);
    }

    public Bundle vslGetBundle() {
        return mIndexResolver.getIndexBundle();
    }
}
