package org.videolan.vlc.events;

import android.os.Bundle;

public class MediaPlayerModuleChanged {
    private static final String BUNDLE_VIDEO_DECODER = "video_decoder";
    private static final String BUNDLE_AUDIO_DECODER = "audio_decoder";
    private static final String BUNDLE_VIDEO_DECODER_IMPL = "video_decoder_impl";
    private static final String BUNDLE_AUDIO_DECODER_IMPL = "audio_decoder_impl";

    public String mVideoDecoder;
    public String mAudioDecoder;
    public String mVideoDecoderImpl;
    public String mAudioDecoderImpl;

    public MediaPlayerModuleChanged(Bundle args) {
        mVideoDecoder = getVideoDecoder(args);
        mAudioDecoder = getAudioDecoder(args);
        mVideoDecoderImpl = getVideoDecoderImpl(args);
        mAudioDecoderImpl = getAudioDecoderImpl(args);
    }

    public static String getVideoDecoder(Bundle args) {
        return args.getString(BUNDLE_VIDEO_DECODER);
    }

    public static String getAudioDecoder(Bundle args) {
        return args.getString(BUNDLE_AUDIO_DECODER);
    }

    public static String getVideoDecoderImpl(Bundle args) {
        return args.getString(BUNDLE_VIDEO_DECODER_IMPL);
    }

    public static String getAudioDecoderImpl(Bundle args) {
        return args.getString(BUNDLE_AUDIO_DECODER_IMPL);
    }
}
