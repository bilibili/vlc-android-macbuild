package tv.danmaku.media.list;

import java.io.IOException;

import tv.danmaku.android.util.DebugLog;
import tv.danmaku.media.resource.Segment;

import android.media.MediaPlayer;

public class DefMediaSegmentPlayer extends MediaPlayer {
    public static String TAG = DefMediaSegmentPlayer.class.getName();
    private int mOrder = -1;
    private Segment mSegment;
    private int mStartTime;

    final public int getOrder() {
        return mOrder;
    }

    final public int getStartTime() {
        return mStartTime;
    }

    final public Segment getSegment() {
        return mSegment;
    }

    final public void setSegment(int order, int startTime, Segment segment)
            throws IllegalArgumentException, IllegalStateException, IOException {
        mOrder = order;
        mStartTime = startTime;
        mSegment = segment;

        DebugLog.dfmt(TAG, "set item [%d] %d(%d) %s", order, startTime,
                segment.mDuration, segment.mUrl);
        super.setDataSource(segment.mUrl);
    }

    final public boolean hasSegment() {
        return mSegment != null;
    }

    @Override
    public int getCurrentPosition() {
        if (mSegment == null)
            return -1;

        int currentPosition = mStartTime + super.getCurrentPosition();
        return currentPosition;
    }
}
