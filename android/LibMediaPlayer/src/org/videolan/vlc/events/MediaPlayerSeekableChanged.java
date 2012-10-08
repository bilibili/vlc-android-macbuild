package org.videolan.vlc.events;

import android.os.Bundle;

public class MediaPlayerSeekableChanged {
    private static final String BUNDLE_NEW_SEEKABLE = "new_seekable";

    public boolean mNewSeekable;

    public MediaPlayerSeekableChanged(Bundle args) {
        mNewSeekable = getNewSeekable(args);
    }

    public static boolean getNewSeekable(Bundle args) {
        return args.getInt(BUNDLE_NEW_SEEKABLE, 1) != 0;
    }
}
