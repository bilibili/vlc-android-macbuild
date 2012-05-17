package tv.danmaku.media;

import java.io.IOException;
import java.util.ArrayList;

import org.videolan.vlc.EventManager;
import org.videolan.vlc.LibVLC;
import org.videolan.vlc.LibVlcEvent;
import org.videolan.vlc.LibVlcException;

import tv.danmaku.util.Assure;
import tv.danmaku.util.CollectionHelper;
import tv.danmaku.util.DebugLog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

// 用于原始的libvlc封装
public class LibVlcMediaPlayer extends AbsMediaPlayer {

    public static final String TAG = LibVlcMediaPlayer.class.getName();

    // audiotrack/opensles
    public static final String[] sAout_AudioTrack = new String[] { ":aout=android_audiotrack" };
    public static final String[] sAout_OpenSLES = new String[] { ":aout=opensles" };

    // HW/SW
    public static final String[] sCodec_Iomx = new String[] {
            ":codec=iomx,all", ":file-caching=3000", ":network-caching=3000" };
    public static final String[] sCodec_All = new String[] { ":codec=avformat,all", };

    public static final int MSG_VIDEO_SIZE_CHANGED = 0;
    public static final int MSG_FAKE_PREPARED = 1;

    private LibVLC mLibVLC;
    private String[] mVlcPlayerOptions;

    private String mVideoPath;
    private int mVideoHeight;
    private int mVideoWidth;

    private SurfaceHolder mSurfaceHolder;

    private boolean mHasReadMedia;

    private boolean mPrepared;
    private float mLastBufferingPercent;

    private boolean mCompleted;
    
    private int mSeekWhenDurationChanged;

    public static LibVlcMediaPlayer create(Context context, String[] extraParams)
            throws LibVlcException {
        DebugLog.v(TAG, "create");
        LibVlcMediaPlayer player = new LibVlcMediaPlayer();

        try {
            player.mLibVLC = LibVLC.getInstance();
            EventManager.getIntance().addHandler(player.mVlcHandler);

            // options
            ArrayList<String> options = new ArrayList<String>();

            CollectionHelper.Append(options, sCodec_All);
            CollectionHelper.Append(options, sAout_AudioTrack);

            player.mVlcPlayerOptions = options.toArray(new String[options
                    .size()]);

            return player;
        } catch (LibVlcException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static LibVlcMediaPlayer create(Context context)
            throws LibVlcException {
        return create(context, null);
    }

    protected LibVlcMediaPlayer() {
    }

    @Override
    public int getCurrentPosition() {
        return (int) mLibVLC.getTime();
    }

    @Override
    public int getDuration() {
        int duration = (int) mLibVLC.getLength();
        if (duration == 0) {
            // duration = 90000;
        }

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
            mLibVLC.readMediaEx(mVideoPath, mVlcPlayerOptions);
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
        Assure.checkNotNull(mVideoPath);

        DebugLog.v(TAG, "prepareAsync:readMediaEx");
        mLibVLC.readMediaEx(mVideoPath, mVlcPlayerOptions);
        mHasReadMedia = true;
        // Message msg = mSurfaceHandler.obtainMessage(MSG_FAKE_PREPARED);
        // mSurfaceHandler.sendMessage(msg);
    }

    @Override
    public void release() {
        DebugLog.v(TAG, "release");
        mLibVLC.stop();
        mSurfaceHolder = null;

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

        mVideoPath = uri;
    }

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

    // call from native
    public void setSurfaceSize(int width, int height) {
        DebugLog.v(TAG, "native setSurfaceSize");
        mVideoWidth = width;
        mVideoHeight = height;

        Message msg = mSurfaceHandler.obtainMessage(MSG_VIDEO_SIZE_CHANGED);
        mSurfaceHandler.sendMessage(msg);
    }

    private Handler mSurfaceHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_VIDEO_SIZE_CHANGED:
                if (mOnVideoSizeChangedListener != null) {
                    mOnVideoSizeChangedListener.onVideoSizeChanged(
                            LibVlcMediaPlayer.this, mVideoWidth, mVideoHeight);
                }
            case MSG_FAKE_PREPARED:
                if (mOnPreparedListener != null) {
                    mOnPreparedListener.onPrepared(LibVlcMediaPlayer.this);
                }
            }
        }
    };

    private Handler mVlcHandler = new Handler() {
        public void handleMessage(Message msg) {
            LibVlcEvent eventObject = (LibVlcEvent) msg.obj;

            switch (msg.what) {
            case EventManager.MediaPlayerLengthChanged:
            case EventManager.MediaDurationChanged: {
                DebugLog.v(TAG, String.format("vlc duration changed %d",
                        LibVlcMediaPlayer.this.getDuration()));
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
                if (mOnBufferingUpdateListener != null) {
                    if (eventObject != null) {
                        float percent = eventObject
                                .getMediaPlayerBuffering_percent();
                        // 这里返回的是缓冲百分比，而不是缓冲位置
                        // mOnBufferingUpdateListener.onBufferingUpdate(
                        // LibVlcMediaPlayer.this, (int) percent);

                        if (percent < mLastBufferingPercent) {
                            if (mOnInfoListener != null) {
                                mOnInfoListener
                                        .onInfo(LibVlcMediaPlayer.this,
                                        		AbsMediaPlayer.MEDIA_INFO_BUFFERING_START,
                                                0);
                            }
                        }

                        if (percent >= 100.0f) {
                            if (mOnInfoListener != null) {
                                mOnInfoListener.onInfo(LibVlcMediaPlayer.this,
                                		AbsMediaPlayer.MEDIA_INFO_BUFFERING_END,
                                        0);
                            }

                            if (!mPrepared) {
                                mPrepared = true;

                                if (mOnPreparedListener != null) {
                                    mOnPreparedListener
                                            .onPrepared(LibVlcMediaPlayer.this);
                                }
                            }
                        }

                        mLastBufferingPercent = percent;
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
                    mOnCompletionListener.onCompletion(LibVlcMediaPlayer.this);
                }
                break;
            }
            case EventManager.MediaPlayerEncounteredError: {
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(LibVlcMediaPlayer.this,
                            MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
                }
                break;
            }
            case EventManager.MediaPlayerSeekableChanged: {
                if (eventObject != null
                        && eventObject
                                .getMediaPlayerSeekableChanged_isSeekable()) {
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(LibVlcMediaPlayer.this,
                                MediaPlayer.MEDIA_INFO_NOT_SEEKABLE, 0);
                    }
                }
                break;
            }
            case EventManager.MediaPlayerPausableChanged: {
                break;
            }
            }
        }
    };
}
