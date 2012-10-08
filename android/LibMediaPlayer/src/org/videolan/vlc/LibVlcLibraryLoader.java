package org.videolan.vlc;

import android.content.Context;

public interface LibVlcLibraryLoader {
    public abstract void loadVlcLibIomx(Context context)
            throws UnsatisfiedLinkError, SecurityException;

    public abstract void loadLibVlc(Context context)
            throws UnsatisfiedLinkError, SecurityException;
}
