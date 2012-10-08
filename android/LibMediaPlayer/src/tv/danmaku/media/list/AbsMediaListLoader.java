package tv.danmaku.media.list;

import android.content.Context;

public abstract class AbsMediaListLoader {
    abstract public void setMediaListUrl(String url);

    abstract public boolean loadList(Context context);

    abstract public boolean isLoaded();

    abstract public int getTotalDuration();

    abstract public int getMediaCount();

    abstract public MediaSegment getMediaItem(int index);

    abstract public int getItemIndexByTime(int msec);
}
