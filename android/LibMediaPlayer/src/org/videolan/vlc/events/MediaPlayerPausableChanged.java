package org.videolan.vlc.events;

import android.os.Bundle;

public class MediaPlayerPausableChanged {
    private static final String BUNDLE_NEW_PAUSABLE = "new_pausable";

    public boolean mNewPausable;

    public MediaPlayerPausableChanged(Bundle args) {
        mNewPausable = getNewPausable(args);
    }

    public static boolean getNewPausable(Bundle args) {
        return args.getInt(BUNDLE_NEW_PAUSABLE, 1) != 0;
    }
}
