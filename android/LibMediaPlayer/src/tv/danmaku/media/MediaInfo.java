package tv.danmaku.media;

public class MediaInfo {
    public int mVideoWidth;
    public int mVideoHeight;

    public ModuleInfo mModuleInfo;

    public final String getResolution() {
        if (mVideoWidth <= 0 || mVideoHeight <= 0)
            return "N/A";

        return String.format("%dx%d", mVideoWidth, mVideoHeight);
    }

    public final String getVideoDecoderInline() {
        if (mModuleInfo == null)
            return "N/A";

        return mModuleInfo.getVideoDecoderInline();
    }

    public final String getAudioDecoderInline() {
        if (mModuleInfo == null)
            return "N/A";

        return mModuleInfo.getAudioDecoderInline();
    }
}
