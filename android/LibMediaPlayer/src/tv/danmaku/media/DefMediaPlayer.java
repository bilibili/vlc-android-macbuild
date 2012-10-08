/*****************************************************************************
 * DefMediaPlayer.java
 * https://github.com/tewilove/faplayer
 * 
 * Modified:
 *  - 2012-03-13 --bbcallen <bbcallen@gmail.com>
 *      remove unused methods and fields
 *****************************************************************************
 * Copyright © 2011-2012 tewilove <tewilove@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *****************************************************************************/
package tv.danmaku.media;

import java.io.IOException;

import tv.danmaku.android.BuildHelper;
import tv.danmaku.android.util.DebugLog;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class DefMediaPlayer extends AbsMediaPlayer implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener {

    public static final String TAG = DefMediaPlayer.class.getName();

    private MediaPlayer mMediaPlayer = null;

    public static DefMediaPlayer create(Context context) {
        return new DefMediaPlayer();
    }

    protected DefMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
    }

    @Override
    public int getCurrentPosition() {
        try {
            return mMediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getDuration() {
        try {
            return mMediaPlayer.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    @Override
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    @Override
    public boolean isPlaying() {
        try {
            return mMediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void start() throws IllegalStateException {
        mMediaPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        mMediaPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mMediaPlayer.pause();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void release() {
        mMediaPlayer.release();
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        mMediaPlayer.seekTo(msec);
    }

    @Override
    public void setDataSource(String uri) throws IOException,
            IllegalArgumentException, IllegalStateException {
        if (BuildHelper.isApi11_HoneyCombOrLater()) {
            // uri is OK
        } else {
            // android 2.x only accepts httplive://......m3u8
            Uri uriComponent = Uri.parse(uri);
            String lastSegment = uriComponent.getLastPathSegment();
            if (!TextUtils.isEmpty(lastSegment)
                    && lastSegment.contains(".m3u8")) {
                DebugLog.v(TAG, "def play use httplive for m3u8");
                uri = uriComponent.buildUpon().scheme("httplive").toString();
            }
        }

        DebugLog.v(TAG, "def play " + uri);

        mMediaPlayer.setDataSource(uri);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
        if (holder != null)
            holder.addCallback(mSurfaceCallback);
    }

    private SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }
        }
    };

    @Override
    public void setAudioStreamType(int streamMusic) {
        mMediaPlayer.setAudioStreamType(streamMusic);
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        mMediaPlayer.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public void enableLog(boolean enable) {
        // not support
    }

    @Override
    public boolean isBufferingEnd() {
        return true;
    }

    @Override
    public ModuleInfo getModuleInfo() {
        return ModuleInfo.getAndroidModuleInfo();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mOnBufferingUpdateListener != null) {
            mOnBufferingUpdateListener.onBufferingUpdate(this, percent);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(this);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mOnErrorListener != null) {
            return mOnErrorListener.onError(this, what, extra);
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (mOnInfoListener != null) {
            return mOnInfoListener.onInfo(this, what, extra);
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared(this);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (mOnSeekCompleteListener != null) {
            mOnSeekCompleteListener.onSeekComplete(this);
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (mOnVideoSizeChangedListener != null) {
            mOnVideoSizeChangedListener.onVideoSizeChanged(this, width, height);
        }
    }

}