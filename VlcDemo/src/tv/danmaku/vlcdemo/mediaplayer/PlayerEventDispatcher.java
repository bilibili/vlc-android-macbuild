package tv.danmaku.vlcdemo.mediaplayer;

import java.lang.ref.WeakReference;

import tv.danmaku.media.AbsMediaPlayer;
import tv.danmaku.media.AbsMediaPlayer.OnCompletionListener;
import tv.danmaku.media.AbsMediaPlayer.OnPreparedListener;

import android.os.Handler;
import android.os.Message;

public class PlayerEventDispatcher implements OnPreparedListener, OnCompletionListener {
    
    private WeakReference<Handler> mWeakHandler;
    
    public PlayerEventDispatcher(Handler handler) {
        mWeakHandler = new WeakReference<Handler>(handler);
    }

    private final void dispatchMessage(int what, AbsMediaPlayer mp) {
        if (mWeakHandler == null)
            return;

        Handler handler = mWeakHandler.get();
        if (handler == null)
            return;

        Message msg = handler.obtainMessage(what, mp);
        handler.sendMessage(msg);
    }
    
    @Override
    public void onPrepared(AbsMediaPlayer mp) {
        dispatchMessage(PlayerMessages.MEDIA_PLAYER_PREPARED, mp);
    }

    @Override
    public void onCompletion(AbsMediaPlayer mp) {
        dispatchMessage(PlayerMessages.MEDIA_PLAYER_COMPLETION, mp);
    }
}
