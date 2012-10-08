package org.videolan.vlc.segments;

import java.util.ArrayList;

import android.os.Bundle;

public class LibVlcVslIndex {
    private static final String BUNDLE_COUNT = "count";

    public ArrayList<LibVlcVslSegment> mList;

    public Bundle getBundle() {
        Bundle args = new Bundle();
        putInto(args);
        return args;
    }

    public void putInto(Bundle args) {
        if (mList != null && !mList.isEmpty()) {
            putCount(args, mList.size());
            for (LibVlcVslSegment segment : mList) {
                segment.putInto(args);
            }
        }
    }

    public static void putCount(Bundle args, int count) {
        args.putInt(BUNDLE_COUNT, count);
    }
}
