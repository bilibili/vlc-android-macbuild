package tv.danmaku.media;

import org.videolan.vlc.events.MediaPlayerModuleChanged;

import android.os.Bundle;
import android.text.TextUtils;

public class ModuleInfo {
    public String mVideoDecoder;
    public String mVideoDecoderImpl;

    public String mAudioDecoder;
    public String mAudioDecoderImpl;

    public static ModuleInfo sAndroidModuleInfo;
    public static ModuleInfo sAndroidListModuleInfo;

    public static ModuleInfo parseModuleInfo(Bundle args) {
        ModuleInfo moduleInfo = new ModuleInfo();

        moduleInfo.mVideoDecoder = MediaPlayerModuleChanged
                .getVideoDecoder(args);
        moduleInfo.mVideoDecoderImpl = MediaPlayerModuleChanged
                .getVideoDecoderImpl(args);

        moduleInfo.mAudioDecoder = MediaPlayerModuleChanged
                .getAudioDecoder(args);
        moduleInfo.mAudioDecoderImpl = MediaPlayerModuleChanged
                .getAudioDecoderImpl(args);

        return moduleInfo;
    }

    public static ModuleInfo getAndroidModuleInfo() {
        if (sAndroidModuleInfo == null) {
            ModuleInfo module = new ModuleInfo();

            module.mVideoDecoder = "android";
            module.mVideoDecoderImpl = "HW";

            module.mAudioDecoder = "android";
            module.mAudioDecoderImpl = "HW";

            sAndroidModuleInfo = module;
        }

        return sAndroidModuleInfo;
    }

    public static ModuleInfo getAndroidListModuleInfo() {
        if (sAndroidModuleInfo == null) {
            ModuleInfo module = new ModuleInfo();

            module.mVideoDecoder = "android";
            module.mVideoDecoderImpl = "SYS-HW";

            module.mAudioDecoder = "android";
            module.mAudioDecoderImpl = "SYS-HW";

            sAndroidModuleInfo = module;
        }

        return sAndroidModuleInfo;
    }

    public static ModuleInfo getNullModuleInfo() {
        ModuleInfo module = new ModuleInfo();
        return module;
    }

    public final String getVideoDecoderInline() {
        if (TextUtils.isEmpty(mVideoDecoder))
            return "N/A";

        StringBuilder sb = new StringBuilder(mVideoDecoder);
        sb.append(": ");
        if (TextUtils.isEmpty(mVideoDecoderImpl)) {
            sb.append("SW");
        } else {
            sb.append(mVideoDecoderImpl);
        }

        return sb.toString();
    }

    public final String getAudioDecoderInline() {
        if (TextUtils.isEmpty(mAudioDecoder))
            return "N/A";

        StringBuilder sb = new StringBuilder(mAudioDecoder);
        sb.append(": ");
        if (TextUtils.isEmpty(mAudioDecoderImpl)) {
            sb.append("SW");
        } else {
            sb.append(mAudioDecoderImpl);
        }

        return sb.toString();
    }
}
