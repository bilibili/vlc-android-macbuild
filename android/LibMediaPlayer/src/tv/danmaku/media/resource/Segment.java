package tv.danmaku.media.resource;

import org.videolan.vlc.segments.LibVlcVslSegment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Segment implements Parcelable {
    private static final String BUNDLE_DURATION = "duration";
    private static final String BUNDLE_URL = "url";

    public String mUrl;
    public int mDuration;
    public int mBytes;

    public Segment() {
    }

    public Segment(String url) {
        this(url, 0);
    }

    public Segment(String url, int duration) {
        this(url, duration, 0);
    }

    public Segment(String url, int duration, int bytes) {
        mUrl = url;
        mDuration = duration;
        mBytes = bytes;
    }

    public void putIntoVslBundle(Bundle args, int order) {
        LibVlcVslSegment.putSegmentUrl(args, order, mUrl);
        LibVlcVslSegment.putDuration(args, order, mDuration);
        LibVlcVslSegment.putBytes(args, order, mBytes);
    }

    // for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putInt(BUNDLE_DURATION, mDuration);
        bundle.putString(BUNDLE_URL, mUrl);

        dest.writeBundle(bundle);
    }

    private Segment(Parcel in) {
        Bundle bundle = in.readBundle(Segment.class.getClassLoader());

        mDuration = bundle.getInt(BUNDLE_DURATION);
        mUrl = bundle.getString(BUNDLE_URL);
    }

    public static final Segment.Creator<Segment> CREATOR = new Parcelable.Creator<Segment>() {
        public Segment createFromParcel(Parcel in) {
            return new Segment(in);
        }

        public Segment[] newArray(int size) {
            return new Segment[size];
        }
    };
}
