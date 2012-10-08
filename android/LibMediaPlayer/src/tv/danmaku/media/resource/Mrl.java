package tv.danmaku.media.resource;

import tv.danmaku.android.util.DebugLog;
import android.text.TextUtils;

public class Mrl {
    private static final String TAG = Mrl.class.getSimpleName();

    private String mRawMrl; // "http/youku/mp4://location"
    private String mScheme; // "http", required
    private String mPseudoAccess; // "youku", required for index url
    private String mPseudoDemux; // "mp4", optional

    private String mUrl;

    private String mSchemeSpecificPart; // "//location"

    public static Mrl parse(String rawMrl) {
        Mrl mrl = new Mrl();

        mrl.mRawMrl = rawMrl;
        mrl.mScheme = null;
        mrl.mPseudoAccess = null;
        mrl.mPseudoDemux = null;
        mrl.mSchemeSpecificPart = null;

        String[] mrlComponents = mrl.mRawMrl.split(":", 2);
        if (mrlComponents == null || mrlComponents.length < 2)
            return mrl;
        mrl.mSchemeSpecificPart = mrlComponents[1];

        String[] moduleComponents = mrlComponents[0].split("/");
        if (mrlComponents == null || mrlComponents.length < 1)
            return mrl;

        mrl.mScheme = moduleComponents[0];
        if (TextUtils.isEmpty(mrl.mScheme)
                || TextUtils.isEmpty(mrl.mSchemeSpecificPart))
            return mrl;
        mrl.mUrl = String.format("%s:%s", mrl.mScheme, mrl.mSchemeSpecificPart);

        if (moduleComponents.length >= 2)
            mrl.mPseudoAccess = moduleComponents[1];
        if (moduleComponents.length >= 3)
            mrl.mPseudoDemux = moduleComponents[2];

        return mrl;
    }

    @Override
    public String toString() {
        return mRawMrl;
    }

    final public String getRawMrl() {
        return mRawMrl;
    }

    final public String getScheme() {
        return mScheme;
    }

    final public String getPseudoAccess() {
        return mPseudoAccess;
    }

    final public String getPseduoDemux() {
        return mPseudoDemux;
    }

    final public String getUrl() {
        return mUrl;
    }

    final public String getSchemeSpecificPart() {
        return mSchemeSpecificPart;
    }

    final public void dump() {
        DebugLog.ifmt(TAG, "mIndexMrl:              %s", mRawMrl);
        DebugLog.ifmt(TAG, "mScheme:                %s", mScheme);
        DebugLog.ifmt(TAG, "mPseudoAccess:          %s", mPseudoAccess);
        DebugLog.ifmt(TAG, "mPseudoDemux:           %s", mPseudoDemux);
        DebugLog.ifmt(TAG, "mSchemeSpecificPart:    %s", mSchemeSpecificPart);
        DebugLog.ifmt(TAG, "mRealIndexUrl:          %s", mUrl);
    }
}
