package tv.danmaku.android;

import android.content.Context;
import android.media.AudioManager;

public class AudioManagerHelper {
    public static AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /*--------------------------------------
     * Music stream
     */
    public static int getStreamMaxVolume(Context context, int streamType) {
        AudioManager audioManager = getAudioManager(context);
        if (audioManager == null)
            return 0;

        return audioManager.getStreamMaxVolume(streamType);
    }

    public static int getStreamVolume(Context context, int streamType) {
        AudioManager audioManager = getAudioManager(context);
        if (audioManager == null)
            return 0;

        return audioManager.getStreamVolume(streamType);
    }

    public static void setStreamVolume(Context context, int streamType,
            int volume, int flag) {
        AudioManager audioManager = getAudioManager(context);
        if (audioManager == null)
            return;

        audioManager.setStreamVolume(streamType, volume, flag);
    }
}
