package org.videolan.vlc.segments;

import android.os.Bundle;

public class LibVlcVslSegment {
    private static final String BUNDLE_FMT1_SEGMENT_MRL = "%d_segment_mrl";
    private static final String BUNDLE_FMT1_SEGMENT_URL = "%d_segment_url";
    private static final String BUNDLE_FMT1_DURATION = "%d_duration";
    private static final String BUNDLE_FMT1_BYTES = "%d_bytes";

    public int mOrder; // 0-based
    public String mSegmentMrl;
    public String mSegmentUrl;
    public int mDuration; // in milliseconds
    public long mBytes; // 0 for unknown

    public Bundle getBundle() {
        Bundle args = new Bundle();
        putInto(args);
        return args;
    }

    public void putInto(Bundle args) {
        putSegmentMrl(args, mOrder, mSegmentMrl);
        putSegmentUrl(args, mOrder, mSegmentUrl);
        putDuration(args, mOrder, mDuration);
        putBytes(args, mOrder, mBytes);
    }

    public static void putSegmentMrl(Bundle args, int order, String segmentMrl) {
        args.putString(String.format(BUNDLE_FMT1_SEGMENT_MRL, order),
                segmentMrl);
    }

    public static void putSegmentUrl(Bundle args, int order, String segmentUrl) {
        args.putString(String.format(BUNDLE_FMT1_SEGMENT_URL, order),
                segmentUrl);
    }

    public static void putDuration(Bundle args, int order, int duration) {
        args.putInt(String.format(BUNDLE_FMT1_DURATION, order), duration);
    }

    public static void putBytes(Bundle args, int order, long bytes) {
        args.putLong(String.format(BUNDLE_FMT1_BYTES, order), bytes);
    }
}
