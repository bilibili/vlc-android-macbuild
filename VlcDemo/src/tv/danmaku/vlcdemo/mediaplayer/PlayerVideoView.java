/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * modified from VideoView in Android 4.0.3_r1
 */

package tv.danmaku.vlcdemo.mediaplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.MediaController.MediaPlayerControl;

import java.io.IOException;
import java.util.Map;

import org.videolan.vlc.LibVlcException;

import tv.danmaku.vlcdemo.R;
import tv.danmaku.media.AbsMediaPlayer;
import tv.danmaku.media.DefMediaPlayer;
import tv.danmaku.media.AbsMediaPlayer.OnCompletionListener;
import tv.danmaku.media.AbsMediaPlayer.OnErrorListener;
import tv.danmaku.media.AbsMediaPlayer.OnInfoListener;
import tv.danmaku.media.LibVlcMediaPlayer;
import tv.danmaku.util.AppBuild;
import tv.danmaku.util.AppWindowManager;
import tv.danmaku.util.AppWindowManager.AspectRadio;
import tv.danmaku.util.DebugLog;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 */
public class PlayerVideoView extends SurfaceView implements MediaPlayerControl {
    private String TAG = "VideoView";
    // settable by the client
    private Uri mUri;
    @SuppressWarnings("unused")
    private Map<String, String> mHeaders;
    private int mDuration;

    // for feed
    public int mAvid;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    // All the stuff we need for playing and showing a video
    private SurfaceHolder mSurfaceHolder = null;
    private AbsMediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private AbsMediaPlayer.OnCompletionListener mOnCompletionListener;
    private AbsMediaPlayer.OnPreparedListener mOnPreparedListener;
    private AbsMediaPlayer.OnInfoListener mOnInfoListener;
    private AbsMediaPlayer.OnErrorListener mOnErrorListener;
    private int mCurrentBufferPercentage;
    private int mSeekWhenPrepared; // recording the seek position while
                                   // preparing
    private boolean mCanPause;
    private boolean mCanSeekBack;
    private boolean mCanSeekForward;

    private AspectRadio mAspectRadio = AspectRadio.RADIO_ADJUST_CONTENT;

    // 混合播放模式下,优先使用系统播放器,如果系统播放器失败,则切换到vlc播放器
    public static final int USE_VLC_PLAYER = PlayerAdapter.USE_VLC_PLAYER;
    public static final int USE_ANDROID_PLAYER = PlayerAdapter.USE_ANDROID_PLAYER;
    public static final int USE_AUTO_PLAYER = PlayerAdapter.USE_AUTO_PLAYER;

    private int mPlayMode;
    private int mCurrentAutoPlayMode = PlayerAdapter.USE_ANDROID_PLAYER;
    private int mLastGoodPosition;
    private Handler mUIHandler;

    public PlayerVideoView(Context context) {
        super(context);
        initVideoView();
    }

    public PlayerVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initVideoView();
    }

    public PlayerVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

        if (mAspectRadio == AspectRadio.RADIO_ADJUST_CONTENT) {
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (mVideoWidth * height > width * mVideoHeight) {
                    Log.i(TAG, "image too tall, correcting " + width + " "
                            + height);
                    Log.i(TAG, "image too tall, video " + mVideoWidth + " "
                            + mVideoHeight);
                    height = width * mVideoHeight / mVideoWidth;
                    Log.i(TAG, "image too tall, after correcting " + width
                            + " " + height);
                } else if (mVideoWidth * height < width * mVideoHeight) {
                    Log.i(TAG, "image too wide, correcting " + width + " "
                            + height);
                    Log.i(TAG, "image too wide, video " + mVideoWidth + " "
                            + mVideoHeight);
                    width = height * mVideoWidth / mVideoHeight;
                    Log.i(TAG, "image too wide, after correcting " + width
                            + " " + height);
                } else {
                    Log.i(TAG, "aspect ratio is correct: " + width + "/"
                            + height + "=" + mVideoWidth + "/" + mVideoHeight);
                }
            }
        }

        setMeasuredDimension(width, height);
    }

    public void setAspectRadio(AspectRadio aspectRadio) {
        mAspectRadio = aspectRadio;

        Point layoutSize = AppWindowManager.GetClippedDisplaySize(getContext(),
                aspectRadio);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = layoutSize.x;
        layoutParams.height = layoutSize.y;
        setLayoutParams(layoutParams);
    }

    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    public void setUIHandler(Handler handler) {
        mUIHandler = handler;
    }

    public void setPlayMode(int playMode) {
        mPlayMode = playMode;
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    /**
     * @hide
     */
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    public int getCurrentPlayMode() {
        switch (mPlayMode) {
        case PlayerAdapter.USE_ANDROID_PLAYER:
        case PlayerAdapter.USE_VLC_PLAYER:
            return mPlayMode;

        case PlayerAdapter.USE_AUTO_PLAYER:
        case PlayerAdapter.USE_DEFAULT_PLAYER:
        default:
            return mCurrentAutoPlayMode;
        }
    }

    public boolean getEnableIomx() {
        return false;
    }

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // Tell the music playback service to pause
        // TODO: these constants need to be published somewhere in the
        // framework.
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        getContext().sendBroadcast(i);

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        try {
            int currentPlayMode = getCurrentPlayMode();
            switch (currentPlayMode) {
            case PlayerAdapter.USE_ANDROID_PLAYER:
                mMediaPlayer = DefMediaPlayer.create(getContext());
                break;
            case PlayerAdapter.USE_VLC_PLAYER:
                mMediaPlayer = LibVlcMediaPlayer.create(getContext());
                break;
            default:
                throw new IllegalArgumentException("invalid play mode");
            }

            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mDuration = -1;
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mUri.toString());
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer,
                    AbsMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer,
                    AbsMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (LibVlcException e) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.Vlc_load_error)
                    .setMessage(e.getMessage())
                    .setPositiveButton(R.string.VideoView_error_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                }
                            }).setCancelable(false).show();
        }
    }

    public String getString(int resId) {
        return getContext().getString(resId);
    }

    AbsMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new AbsMediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(AbsMediaPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            }
        }
    };

    AbsMediaPlayer.OnPreparedListener mPreparedListener = new AbsMediaPlayer.OnPreparedListener() {
        public void onPrepared(AbsMediaPlayer mp) {
            mCurrentState = STATE_PREPARED;

            // Get the capabilities of the player for this stream
            mCanPause = mCanSeekBack = mCanSeekForward = true;

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            int seekToPosition = mSeekWhenPrepared; // mSeekWhenPrepared may be
                                                    // changed after seekTo()
                                                    // call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                // Log.i("@@@@", "video mSize: " + mVideoWidth +"/"+
                // mVideoHeight);
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mSurfaceWidth == mVideoWidth
                        && mSurfaceHeight == mVideoHeight) {
                    // We didn't actually change the mSize (it was already at
                    // the
                    // mSize
                    // we need), so we won't get a "surface changed" callback,
                    // so
                    // start the video here instead of in the callback.
                    if (mTargetState == STATE_PLAYING) {
                        start();
                    }
                }
            } else {
                // We don't know the video mSize yet, but should start anyway.
                // The video mSize might be reported to us later.
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            }
        }
    };

    private AbsMediaPlayer.OnCompletionListener mCompletionListener = new AbsMediaPlayer.OnCompletionListener() {
        public void onCompletion(AbsMediaPlayer mp) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;

            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    private AbsMediaPlayer.OnErrorListener mErrorListener = new AbsMediaPlayer.OnErrorListener() {
        public boolean onError(AbsMediaPlayer mp, int framework_err,
                int impl_err) {
            Log.d(TAG, "Error: " + framework_err + "," + impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;

            /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err,
                        impl_err)) {
                    return true;
                }
            }

            // retry with another player
            if (mPlayMode == USE_AUTO_PLAYER
                    && mCurrentAutoPlayMode == USE_ANDROID_PLAYER) {
                DebugLog.e(TAG, "v1(H/W) failed, switch to v2(S/W)");
                PlayerVideoView.this.release(true);

                mCurrentAutoPlayMode = USE_VLC_PLAYER;

                if (mUIHandler != null) {
                    Message msg = mUIHandler.obtainMessage(
                            PlayerMessages.AUTO_PLAY_MODE_ANDROID_FAILED,
                            framework_err, impl_err);
                    mUIHandler.sendMessage(msg);
                }

                // 仅当armv7a的cpu时，自动切换到vlc播放器
                if (AppBuild.supportARMv7a()) {
                    mUIHandler
                            .sendEmptyMessage(PlayerMessages.AUTO_PLAY_MODE_TRY_VLC);
                    mUIHandler
                            .sendEmptyMessage(PlayerMessages.AUTO_PLAY_MODE_CHANGED);

                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mMediaPlayer, PlayerAdapter.MEDIA_INFO_BUFFERING_START, 0);
                    }

                    PlayerVideoView.this.openVideo();

                    DebugLog.v(TAG, String.format("would seek to %d%%",
                            mLastGoodPosition));

                    if (mMediaPlayer != null && mLastGoodPosition > 0) {
                        mMediaPlayer.seekTo(mLastGoodPosition);
                        mLastGoodPosition = 0;
                    }

                    return true;
                }
            }

            /*
             * Otherwise, pop up an error dialog so the user knows that
             * something bad has happened. Only try and pop up the dialog if
             * we're attached to a window. When we're going away and no longer
             * have a window, don't bother showing the user an error.
             */
            if (getWindowToken() != null) {
                String message = null;
                int messageId = R.string.VideoView_error_text_unknown;

                if (framework_err == AbsMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                    messageId = R.string.VideoView_error_text_invalid_progressive_playback;
                } else if (framework_err == AbsMediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    messageId = R.string.VideoView_error_text_server_died;
                } else if (framework_err == AbsMediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    message = PlayerErrorCodes
                            .getMediaPlayerErrorMessage(impl_err);
                } else {
                    messageId = R.string.VideoView_error_text_unknown;
                }

                String title = String.format("%s (%d,%d)", getContext()
                        .getString(R.string.VideoView_error_title),
                        framework_err, impl_err);

                if (message == null) {
                    message = getString(messageId);
                }

                new AlertDialog.Builder(getContext())
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(R.string.VideoView_error_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                            int whichButton) {
                                        /*
                                         * If we get here, there is no onError
                                         * listener, so at least inform them
                                         * that the video is over.
                                         */
                                        if (mOnCompletionListener != null) {
                                            mOnCompletionListener
                                                    .onCompletion(mMediaPlayer);
                                        }
                                    }
                                }).setCancelable(false).show();
            }
            return true;
        }
    };

    private AbsMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new AbsMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(AbsMediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };

    /**
     * Register a callback to be invoked when the media file is loaded and ready
     * to go.
     * 
     * @param l
     *            The callback that will be run
     */
    public void setOnPreparedListener(AbsMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file has been
     * reached during playback.
     * 
     * @param l
     *            The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs during playback or
     * setup. If no listener is specified, or if the listener returned false,
     * VideoView will inform the user of any errors.
     * 
     * @param l
     *            The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                int h) {
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(holder);
            }

            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;

            // SurfaceHolder.setFormat may cause recreate surface
            if (mMediaPlayer == null) {
                openVideo();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;

            // release may cause vlcplayer deadlock
            // so we detach surface in MediaPlayer's callback
            if (mMediaPlayer != null && mMediaPlayer instanceof DefMediaPlayer) {
                release(true);
            }
        }
    };

    /*
     * release the media player in any state
     */
    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
        }
    }

    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        openVideo();
    }

    // cache duration as mDuration for faster access
    public int getDuration() {
        if (isInPlaybackState()) {
            if (mDuration <= 0) {
                mDuration = mMediaPlayer.getDuration();
            }

            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            int position = mMediaPlayer.getCurrentPosition();
            if (mDuration > 0 && position > 0 && position < mDuration) {
                mLastGoodPosition = position;
            }
            return position;
        }

        return 0;
    }

    public void seekTo(int msec) {
        int duration = getDuration();
        if (duration <= 0)
            return;

        if (isInPlaybackState()) {
            int currentPlayMode = getCurrentPlayMode();
            switch (currentPlayMode) {
            case USE_VLC_PLAYER:
                mMediaPlayer.seekTo(msec);
                break;
            case USE_ANDROID_PLAYER:
                mMediaPlayer.seekTo(msec);
                break;
            }
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
            mLastGoodPosition = msec;
        }
    }

    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    public boolean canPause() {
        return mCanPause;
    }

    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    // extended implements
    public void setOnInfoListener(OnInfoListener listener) {
        mOnInfoListener = listener;
    }
}
