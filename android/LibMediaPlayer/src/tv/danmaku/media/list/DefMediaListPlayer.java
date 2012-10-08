package tv.danmaku.media.list;

import java.io.IOException;
import java.lang.ref.WeakReference;

import tv.danmaku.android.util.DebugLog;
import tv.danmaku.media.AbsMediaPlayer;
import tv.danmaku.media.ModuleInfo;
import tv.danmaku.media.resource.PlayIndex;
import tv.danmaku.media.resource.Segment;
import tv.danmaku.media.vsl.LibVlcVideoSegmentListLoader;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class DefMediaListPlayer extends AbsMediaPlayer implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener {

    public static final String TAG = DefMediaListPlayer.class.getName();

    private String mMetaListUrl;
    private DefMediaSegmentPlayer mSegmentPlayer;
    private LibVlcVideoSegmentListLoader mIndexResolver;

    private PlayIndex mPlayIndex;
    private long mTotalDuration;

    private SurfaceHolder mSurfaceHolder;

    boolean mListPlayerPrepared;
    boolean mIsMediaSwitchEnd;

    public static DefMediaListPlayer create(Context context,
            PlayIndex.Resolver resolver) {
        return new DefMediaListPlayer(context, resolver);
    }

    protected DefMediaListPlayer(Context context, PlayIndex.Resolver resolver) {
        mIndexResolver = new LibVlcVideoSegmentListLoader(context, resolver);
    }

    private DefMediaSegmentPlayer createItemPlayer() {
        DefMediaSegmentPlayer itemPlayer = new DefMediaSegmentPlayer();
        itemPlayer.setOnBufferingUpdateListener(this);
        itemPlayer.setOnCompletionListener(this);
        itemPlayer.setOnErrorListener(this);
        itemPlayer.setOnInfoListener(this);
        itemPlayer.setOnPreparedListener(this);
        itemPlayer.setOnSeekCompleteListener(this);
        itemPlayer.setOnVideoSizeChangedListener(this);
        itemPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        itemPlayer.setScreenOnWhilePlaying(true);

        SurfaceHolder holder = mSurfaceHolder;
        if (holder != null)
            itemPlayer.setDisplay(holder);

        return itemPlayer;
    }

    @Override
    public int getCurrentPosition() {
        if (mSegmentPlayer == null)
            return 0;

        try {
            return mSegmentPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getDuration() {
        if (mPlayIndex == null)
            return 0;

        return (int) mTotalDuration;
    }

    @Override
    public int getVideoHeight() {
        if (mSegmentPlayer == null)
            return 0;
        return mSegmentPlayer.getVideoHeight();
    }

    @Override
    public int getVideoWidth() {
        if (mSegmentPlayer == null)
            return 0;
        return mSegmentPlayer.getVideoWidth();
    }

    @Override
    public boolean isPlaying() {
        try {
            return mSegmentPlayer.isPlaying();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void start() throws IllegalStateException {
        if (mSegmentPlayer == null)
            return;
        mSegmentPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        if (mSegmentPlayer == null)
            return;
        mSegmentPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        if (mSegmentPlayer == null)
            return;
        mSegmentPlayer.pause();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        AsyncLoader loader = new AsyncLoader(this);
        loader.execute(mMetaListUrl);
    }

    private static class AsyncLoader extends AsyncTask<String, Void, PlayIndex> {
        private WeakReference<DefMediaListPlayer> mWeakPlayer;

        public AsyncLoader(DefMediaListPlayer player) {
            mWeakPlayer = new WeakReference<DefMediaListPlayer>(player);
        }

        @Override
        protected PlayIndex doInBackground(String... params) {
            if (params.length <= 0)
                return null;

            DefMediaListPlayer player = mWeakPlayer.get();
            if (player == null)
                return null;

            if (!player.mIndexResolver.loadIndex(true))
                return null;

            return player.mIndexResolver.getPlayIndex();
        }

        @Override
        protected void onPostExecute(PlayIndex result) {
            DefMediaListPlayer player = mWeakPlayer.get();
            if (player == null)
                return;

            if (result == null) {
                if (player.mOnErrorListener != null) {
                    player.mOnErrorListener.onError(player, 1, 1);
                }
                return;
            }

            if (result.mSegmentList == null || result.mSegmentList.isEmpty()) {
                if (player.mOnErrorListener != null) {
                    player.mOnErrorListener.onError(player, 1, 1);
                }
                return;
            }

            try {
                DefMediaSegmentPlayer itemPlayer = player.createItemPlayer();
                itemPlayer.setSegment(0, 0, result.mSegmentList.get(0));

                player.mSegmentPlayer = itemPlayer;
                player.mPlayIndex = result;
                player.mTotalDuration = result.getTotalDuration();
                player.mSegmentPlayer.prepareAsync();
                return;

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (player.mOnErrorListener != null) {
                player.mOnErrorListener.onError(player, 1, 1);
            }
        }
    }

    @Override
    public void release() {
        if (mSegmentPlayer == null)
            return;
        mSegmentPlayer.release();
    }

    @Override
    public void reset() {
        if (mSegmentPlayer == null)
            return;
        mSegmentPlayer.reset();
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        DebugLog.e(TAG, "seek to " + msec);
        if (mPlayIndex == null)
            return;

        int order = mPlayIndex.getOrderByTime(msec);
        if (order < 0) {
            DebugLog.e(TAG, "seek to invalid segment " + order);
            return;
        }

        Segment segment = mPlayIndex.mSegmentList.get(order);
        if (segment == null) {
            DebugLog.e(TAG, "seek to null segment " + order);
            return;
        }

        int startTime = mPlayIndex.getStartTime(order);
        int msecOffset = msec - startTime;
        DebugLog.e(TAG, String.format("seek to segment[%d:%d] %d", order,
                startTime, msecOffset));

        if (mSegmentPlayer != null) {
            int playerIndex = mSegmentPlayer.getOrder();
            if (order == playerIndex) {
                mSegmentPlayer.seekTo(msecOffset);
                return;
            }

            mSegmentPlayer.release();
        }

        mSegmentPlayer = createItemPlayer();
        try {
            mSegmentPlayer.setSegment(order, startTime, segment);
            mSegmentPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TOOD: init seek not supported by stagefright yet
        /*
         * mItemPlayer.seekTo(msec);
         */
    }

    @Override
    public void setDataSource(String uri) throws IOException,
            IllegalArgumentException, IllegalStateException {
        DebugLog.v(TAG, "def play list " + uri);

        if (!mIndexResolver.parseIndexMrl(uri))
            throw new IllegalArgumentException("unsupport mrl");

        mMetaListUrl = uri;
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mSurfaceHolder = holder;

        if (holder != null)
            holder.addCallback(mSurfaceCallback);

        if (mSegmentPlayer != null)
            mSegmentPlayer.setDisplay(holder);
    }

    private SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mSegmentPlayer != null) {
                mSegmentPlayer.release();
            }
        }
    };

    @Override
    public void setAudioStreamType(int streamMusic) {
        if (mSegmentPlayer == null)
            return;

        mSegmentPlayer.setAudioStreamType(streamMusic);
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mSegmentPlayer == null)
            return;

        mSegmentPlayer.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public void enableLog(boolean enable) {
        // not support
    }

    @Override
    public boolean isBufferingEnd() {
        return mIsMediaSwitchEnd;
    }

    @Override
    public ModuleInfo getModuleInfo() {
        return ModuleInfo.getAndroidListModuleInfo();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mOnBufferingUpdateListener != null) {
            DefMediaSegmentPlayer segmentPlayer = mSegmentPlayer;
            if (segmentPlayer == null)
                return;

            int totalDuration = getDuration();
            if (totalDuration <= 0)
                return;

            Segment segment = segmentPlayer.getSegment();
            if (segment == null || segment.mDuration <= 0)
                return;

            int bufferedTime = segment.mDuration * percent / 100;
            int totalPercent = (segmentPlayer.getStartTime() + bufferedTime)
                    * 100 / totalDuration;

            mOnBufferingUpdateListener.onBufferingUpdate(this, totalPercent);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        DebugLog.v(TAG, "onCompletion");
        try {
            if (mPlayIndex != null && mSegmentPlayer != null) {
                int count = mPlayIndex.mSegmentList.size();
                int nextOrder = mSegmentPlayer.getOrder() + 1;
                if (nextOrder < count) {
                    mSegmentPlayer.release();

                    mIsMediaSwitchEnd = false;
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(this,
                                MediaPlayer.MEDIA_INFO_BUFFERING_START, 0);
                    }

                    Segment nextSegment = mPlayIndex.mSegmentList
                            .get(nextOrder);
                    if (nextSegment != null) {
                        mSegmentPlayer = createItemPlayer();
                        int startTime = mPlayIndex.getStartTime(nextOrder);
                        mSegmentPlayer.setSegment(nextOrder, startTime,
                                nextSegment);
                        mSegmentPlayer.prepareAsync();
                    }
                    return;
                }

            }

            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(this);
            }

            return;
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (mOnErrorListener != null) {
            mOnErrorListener.onError(this, 1, 1);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        DebugLog.v(TAG, "onError");
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
        mIsMediaSwitchEnd = true;
        if (mListPlayerPrepared) {
            // TOOD: init seek not supported by stagefright yet
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(this,
                        MediaPlayer.MEDIA_INFO_BUFFERING_START, 0);
            }
            mp.start();
        } else {
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(this);
            }
            mListPlayerPrepared = true;
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
