package tv.danmaku.media.resource;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.xml.sax.SAXException;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class PlayIndex implements Parcelable {
    private static final String BUNDLE_PLAY_TAG = "play_tag";
    private static final String BUNDLE_SEGMENT_LIST = "segment_list";

    public String mPlayTag;
    public ArrayList<Segment> mSegmentList = new ArrayList<Segment>();
    public long mPseudoBitrate;

    public PlayIndex(String playTag) {
        mPlayTag = playTag;
    }

    public PlayIndex(String playTag, String url) {
        this(playTag);
        mSegmentList.add(new Segment(url));
    }

    public PlayIndex(String playTag, Uri uri) {
        this(playTag, uri.toString());
    }

    public Segment getFirstSegment() {
        if (mSegmentList.size() >= 1)
            return mSegmentList.get(0);

        return null;
    }

    public String getFirstSegmentUrl() {
        Segment segment = getFirstSegment();
        if (segment == null || TextUtils.isEmpty(segment.mUrl))
            return null;

        return segment.mUrl;
    }

    public Segment getSingleSegment() {
        if (mSegmentList.size() == 1)
            return mSegmentList.get(0);

        return null;
    }

    public String getSingleSegmentUrl() {
        Segment segment = getSingleSegment();
        if (segment == null || TextUtils.isEmpty(segment.mUrl))
            return null;

        return segment.mUrl;
    }

    public long getBitrate() {
        long totalSize = 0;
        long totalDuration = 0;
        for (Segment seg : mSegmentList) {
            totalSize += seg.mBytes;
            totalDuration += seg.mDuration;
        }

        if (totalDuration <= 0 || totalSize <= 0)
            return mPseudoBitrate;

        return totalSize * 8 / (totalDuration / 1000);
    }

    public long getTotalDuration() {
        long totalDuration = 0;
        for (Segment seg : mSegmentList) {
            totalDuration += seg.mDuration;
        }

        return totalDuration;
    }

    public int getOrderByTime(int msec) {
        int order = 0;

        boolean found = false;
        long endTime = 0;
        for (Segment item : mSegmentList) {
            endTime += item.mDuration;
            if (msec < endTime) {
                found = true;
                break;
            }

            order += 1;
        }

        if (!found)
            return -1;

        return order;
    }

    public int getEndTime(int order) {
        return getStartTime(order + 1);
    }

    public int getStartTime(int order) {
        int startTime = 0;
        for (int i = 0; i < order; ++i) {
            if (order >= mSegmentList.size())
                break;

            Segment segment = mSegmentList.get(i);
            startTime += segment.mDuration;
        }

        return startTime;
    }

    // for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putString(BUNDLE_PLAY_TAG, mPlayTag);
        bundle.putParcelableArrayList(BUNDLE_SEGMENT_LIST, mSegmentList);

        dest.writeBundle(bundle);
    }

    private PlayIndex(Parcel in) {
        Bundle bundle = in.readBundle(PlayIndex.class.getClassLoader());

        mPlayTag = bundle.getString(BUNDLE_PLAY_TAG);
        mSegmentList = bundle.getParcelableArrayList(BUNDLE_SEGMENT_LIST);
    }

    public static final Parcelable.Creator<PlayIndex> CREATOR = new Parcelable.Creator<PlayIndex>() {
        public PlayIndex createFromParcel(Parcel in) {
            return new PlayIndex(in);
        }

        public PlayIndex[] newArray(int size) {
            return new PlayIndex[size];
        }
    };

    public static interface Resolver {
        public abstract PlayIndex resolve(Context context, boolean forceReload,
                int maxTry) throws IOException, HttpException, SAXException,
                ResolveException, JSONException;
    }
}
