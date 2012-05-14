package org.videolan.vlc;

public class LibVlcEvent {
    public int mEventType;
    
    public int mIntValue;
    public long mLongValue;
    public float mFloatValue;
    public String mStringValue;
    
    public boolean getMediaPlayerSeekableChanged_isSeekable() {
        return mIntValue != 0;
    }
    
    public boolean getMediaPlayerPausableChanged_isPausable() {
        return mIntValue != 0;
    }
    
    public float getMediaPlayerBuffering_percent() {
        return mFloatValue;
    }
}
