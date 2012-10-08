/*****************************************************************************
 * AbsMediaPlayer.java
 * http://blog.moenyan.net/?p=18
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

import android.media.MediaPlayer;
import android.view.SurfaceHolder;

public abstract class AbsMediaPlayer {

    public static final String TAG = AbsMediaPlayer.class.getName();

    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK;
    public static final int MEDIA_ERROR_SERVER_DIED = MediaPlayer.MEDIA_ERROR_SERVER_DIED;
    public static final int MEDIA_ERROR_UNKNOWN = MediaPlayer.MEDIA_ERROR_UNKNOWN;

    protected AbsMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = null;
    protected AbsMediaPlayer.OnCompletionListener mOnCompletionListener = null;
    protected AbsMediaPlayer.OnErrorListener mOnErrorListener = null;
    protected AbsMediaPlayer.OnInfoListener mOnInfoListener = null;
    protected AbsMediaPlayer.OnPreparedListener mOnPreparedListener = null;
    protected AbsMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = null;
    protected AbsMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = null;

    public interface OnBufferingUpdateListener {
        public void onBufferingUpdate(AbsMediaPlayer mp, int percent);
    }

    public interface OnCompletionListener {
        public void onCompletion(AbsMediaPlayer mp);
    }

    public interface OnErrorListener {
        public boolean onError(AbsMediaPlayer mp, int what, int extra);
    }

    public interface OnInfoListener {
        public boolean onInfo(AbsMediaPlayer mp, int what, int extra);
    }

    public interface OnPreparedListener {
        public void onPrepared(AbsMediaPlayer mp);
    }

    public interface OnVideoSizeChangedListener {
        public void onVideoSizeChanged(AbsMediaPlayer mp, int width, int height);
    }

    public interface OnSeekCompleteListener {
        public abstract void onSeekComplete(AbsMediaPlayer mp);
    }

    public abstract int getCurrentPosition();

    public abstract int getDuration();

    public abstract int getVideoHeight();

    public abstract int getVideoWidth();

    public abstract boolean isPlaying();

    public abstract void start() throws IllegalStateException;

    public abstract void stop() throws IllegalStateException;

    public abstract void pause() throws IllegalStateException;

    public abstract void prepareAsync() throws IllegalStateException;

    public abstract void release();

    public abstract void reset();

    public abstract void seekTo(int msec) throws IllegalStateException;

    public abstract void setDataSource(String path) throws IOException,
            IllegalArgumentException, IllegalStateException;

    public abstract void setDisplay(SurfaceHolder holder);

    public abstract void setAudioStreamType(int streamMusic);

    public abstract void setScreenOnWhilePlaying(boolean screenOn);

    public abstract void enableLog(boolean enable);

    /* 仅当视频时间改变后,才通过下面的取值判断缓冲状态是否需要结束 */
    public abstract boolean isBufferingEnd();

    public abstract ModuleInfo getModuleInfo();

    public void setOnBufferingUpdateListener(
            AbsMediaPlayer.OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }

    public void setOnCompletionListener(
            AbsMediaPlayer.OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    public void setOnErrorListener(AbsMediaPlayer.OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    public void setOnInfoListener(AbsMediaPlayer.OnInfoListener listener) {
        mOnInfoListener = listener;
    }

    public void setOnPreparedListener(AbsMediaPlayer.OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    public void setOnVideoSizeChangedListener(
            AbsMediaPlayer.OnVideoSizeChangedListener listener) {
        mOnVideoSizeChangedListener = listener;
    }

    public void setOnSeekCompleteListener(
            AbsMediaPlayer.OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }
}