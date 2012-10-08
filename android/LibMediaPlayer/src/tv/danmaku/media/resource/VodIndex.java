package tv.danmaku.media.resource;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.xml.sax.SAXException;

import tv.danmaku.android.util.DebugLog;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class VodIndex implements Parcelable {
    private static final String TAG = VodIndex.class.getSimpleName();

    private static final String BUNDLE_VOD_TAG = "vod_tag";
    private static final String BUNDLE_VOD_LIST = "vod_list";

    public static final String VOD_TAG__YOUKU = "youku";
    public static final String VOD_TAG__SINA = "sina";
    public static final String VOD_TAG__QQ = "qq";
    public static final String VOD_TAG__TUDOU = "tudou";
    public static final String VOD_TAG__6CN = "6cn";
    public static final String VOD_TAG__LINK = "link";
    public static final String VOD_TAG__LOCAL = "local";

    public String mVodTag;
    public ArrayList<PlayIndex> mVodList = new ArrayList<PlayIndex>();

    private PlayIndex mSelectedPlayIndex;

    public VodIndex(String vodTag) {
    }

    public VodIndex(String vodTag, PlayIndex playIndex) {
        this(vodTag);
        mVodList.add(playIndex);
    }

    public VodIndex(String vodTag, String url) {
        this(vodTag, new PlayIndex(null, url));
    }

    public VodIndex(String vodTag, Uri uri) {
        this(vodTag, uri.toString());
    }

    public PlayIndex getFirstPlayIndex() {
        if (mVodList.size() >= 1) {
            return mVodList.get(0);
        }

        return null;
    }

    public Segment getFirstSegment() {
        PlayIndex playIndex = getFirstPlayIndex();
        if (playIndex != null)
            return playIndex.getFirstSegment();

        return null;
    }

    public String getFirstSegmentUrl() {
        Segment segment = getFirstSegment();
        if (segment == null || TextUtils.isEmpty(segment.mUrl))
            return null;

        return segment.mUrl;
    }

    public PlayIndex getSinglePlayIndex() {
        if (mVodList.size() == 1) {
            return mVodList.get(0);
        }

        return null;
    }

    public Segment getSingleSegment() {
        PlayIndex playIndex = getSinglePlayIndex();
        if (playIndex != null)
            return playIndex.getSingleSegment();

        return null;
    }

    public String getSingleSegmentUrl() {
        Segment segment = getSingleSegment();
        if (segment == null || TextUtils.isEmpty(segment.mUrl))
            return null;

        return segment.mUrl;
    }

    public PlayIndex selectPlayIndex_First() {
        if (mVodList.isEmpty())
            return null;

        mSelectedPlayIndex = mVodList.get(0);
        return mSelectedPlayIndex;
    }

    public PlayIndex selectPlayIndex_ByTag(String tag) {
        for (PlayIndex playIndex : mVodList) {
            String playTag = playIndex.mPlayTag;
            if (!TextUtils.isEmpty(playTag) && playTag.equalsIgnoreCase(tag)) {
                mSelectedPlayIndex = playIndex;
                return playIndex;
            }
        }

        return null;
    }

    public PlayIndex selectPlayIndex_HighestBitrate() {
        return selectPlayIndex_HighestBitrate(Integer.MAX_VALUE);
    }

    public PlayIndex selectPlayIndex_HighestBitrate(int maxBitrate) {
        DebugLog.v(TAG, "selectPlayIndex_HighestBitrate");
        long selectedBitrate = 0;
        for (PlayIndex playIndex : mVodList) {
            long bitrate = playIndex.getBitrate();
            DebugLog.vfmt(TAG, "selectPlayIndex_HighestBitrate: found %s %d",
                    playIndex.mPlayTag, (int) bitrate);
            if (bitrate <= maxBitrate && bitrate > selectedBitrate) {
                mSelectedPlayIndex = playIndex;
                selectedBitrate = bitrate;
                DebugLog.vfmt(TAG,
                        "selectPlayIndex_HighestBitrate: choose %s %d",
                        playIndex.mPlayTag, (int) selectedBitrate);
            }
        }

        if (mSelectedPlayIndex == null) {
            mSelectedPlayIndex = selectPlayIndex_First();
            selectedBitrate = mSelectedPlayIndex.getBitrate();
            DebugLog.vfmt(TAG,
                    "selectPlayIndex_HighestBitrate: choose first %s %d",
                    mSelectedPlayIndex.mPlayTag, (int) selectedBitrate);
        }

        return mSelectedPlayIndex;
    }

    public PlayIndex selectPlayIndex_LowestBitrate() {
        long highestBitrate = Long.MAX_VALUE;
        PlayIndex result = null;

        for (PlayIndex playIndex : mVodList) {
            long bitrate = playIndex.getBitrate();
            if (bitrate < highestBitrate) {
                result = playIndex;
            }
        }

        if (result == null)
            result = selectPlayIndex_First();

        return result;
    }

    // for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putString(BUNDLE_VOD_TAG, mVodTag);
        bundle.putParcelableArrayList(BUNDLE_VOD_LIST, mVodList);

        dest.writeBundle(bundle);
    }

    private VodIndex(Parcel in) {
        Bundle bundle = in.readBundle(VodIndex.class.getClassLoader());

        mVodTag = bundle.getString(BUNDLE_VOD_TAG);
        mVodList = bundle.getParcelableArrayList(BUNDLE_VOD_LIST);
    }

    public static final Parcelable.Creator<VodIndex> CREATOR = new Parcelable.Creator<VodIndex>() {
        public VodIndex createFromParcel(Parcel in) {
            return new VodIndex(in);
        }

        public VodIndex[] newArray(int size) {
            return new VodIndex[size];
        }
    };

    public static interface Resolver {
        public abstract VodIndex resolve(Context context, boolean forceReload,
                int maxTry) throws IOException, HttpException, SAXException,
                ResolveException, JSONException;
    }
}
