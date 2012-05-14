package tv.danmaku.vlcdemo.mediaplayer;

public class PlayerMessages {

    // forward url
    public static final int PLAYABLE_URL_START_TO_RESOLVE = 10100;
    // msg.obj = PlayerParamsHolder
    public static final int PLAYABLE_URL_SUCCEEDED_TO_RESOLVE = 10101;
    // msg.obj = Exception or null
    public static final int PLAYABLE_URL_FAILED_TO_RESOLVE = 10102;
    
    // forward url
    public static final int YOUKU_BIT_RATE_START_TO_SELECT = 10150;
    // msg.arg1 = bit rate (kb/s)
    public static final int YOUKU_BIT_RATE_FOUND_STREAM = 10151;
    // msg.arg1 = bit rate (kb/s)
    public static final int YOUKU_BIT_RATE_SUCCEEDED_TO_SELECT = 10152;
    // msg.obj = Exception or null
    public static final int YOUKU_BIT_RATE_FAILED_TO_SELECT = 10153;
    
    // resolve params
    public static final int PLAYER_PARAMS_START_TO_RESOLVE = 10200;
    // msg.obj = PlayerParamsHolder
    public static final int PLAYER_PARAMS_SUCCEEDED_TO_RESOLVE = 10201;
    // msg.obj = Exception or null
    public static final int PLAYER_PARAMS_FAILED_TO_RESOLVE = 10202;
    public static final int PLAYER_PARAMS_RESOLVING_CANCELLED = 10203;

    // msg.arg1 = play mode
    public static final int SELECT_MEDIA_PLAYER = 10300;
    
    public static final int CHECK_BUFFERING = 20100;
    // msg.arg1 = segment id
    public static final int START_RESOLVE_SEGMENT = 20200;
    public static final int READY_FOR_NEXT_SEGMENT = 20201;
    
    public static final int AUTO_PLAY_MODE_CHANGED = 20300;
    // msg.arg1 = framwork error
    // msg.arg2 = implement error
    public static final int AUTO_PLAY_MODE_ANDROID_FAILED = 20301;
    public static final int AUTO_PLAY_MODE_TRY_VLC = 20302;

    // MediaPlayer
    // msg.obj = MediaPlayer
    public static final int MEDIA_PLAYER_PREPARED = 50100;
    // msg.obj = MediaPlayer
    public static final int MEDIA_PLAYER_COMPLETION = 50101;
    
    public static class PlayerParamsHolder {
        public PlayerParams mParams;
        public String mResolvedUrl;
        public int mForwardSteps;
    }
}
