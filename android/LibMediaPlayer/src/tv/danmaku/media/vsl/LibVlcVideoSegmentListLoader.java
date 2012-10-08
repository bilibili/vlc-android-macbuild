package tv.danmaku.media.vsl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.videolan.vlc.segments.LibVlcVslIndex;
import org.videolan.vlc.segments.LibVlcVslSegment;
import org.xml.sax.SAXException;

import tv.danmaku.media.resource.PlayIndex;
import tv.danmaku.media.resource.ResolveException;
import tv.danmaku.media.resource.Segment;
import tv.danmaku.media.resource.Mrl;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

public class LibVlcVideoSegmentListLoader {
    private static final int MAX_TRY = 3;

    public static final String ACCESS_SINA = "sina";
    public static final String ACCESS_SINA_INDEX = "sinaindex";
    public static final String ACCESS_SINA_SEGMENT = "sinasegment";

    public static final String ACCESS_YOUKU = "youku";
    public static final String ACCESS_YOUKU_INDEX = "youkuindex";
    public static final String ACCESS_YOUKU_SEGMENT = "youkusegment";

    public static final String ACCESS_CNTV = "cntv";
    public static final String ACCESS_CNTV_INDEX = "cntvindex";
    public static final String ACCESS_CNTV_SEGMENT = "cntvsegment";

    public static final String ACCESS_SOHU = "sohu";
    public static final String ACCESS_SOHU_INDEX = "sohuindex";
    public static final String ACCESS_SOHU_SEGMENT = "sohusegment";

    public static final String ACCESS_LETV = "letv";
    public static final String ACCESS_LETV_INDEX = "letvindex";
    public static final String ACCESS_LETV_SEGMENT = "letvsegment";

    public static final String ACCESS_VSL = "vsl";
    public static final String ACCESS_VSL_INDEX = "vslindex";
    public static final String ACCESS_VSL_SEGMENT = "vslsegment";

    private WeakReference<Context> mWeakContext;

    private Mrl mMrl;

    public String mVlcIndexAccess;
    public String mVlcSegmentAccess;

    private String mIndexLock = "lock";
    private PlayIndex mPlayIndex;
    private Bundle mIndexBundle;

    private PlayIndex.Resolver mResolver;

    private boolean mIsVslIndex;

    public LibVlcVideoSegmentListLoader(Context context,
            PlayIndex.Resolver resolver) {
        mWeakContext = new WeakReference<Context>(context);
        mResolver = resolver;
    }

    public boolean parseIndexMrl(String mrl) {
        mMrl = Mrl.parse(mrl);
        mIsVslIndex = false;

        String pseduoAccess = mMrl.getPseudoAccess();
        if (TextUtils.isEmpty(pseduoAccess))
            return false;

        if (pseduoAccess.equalsIgnoreCase(ACCESS_SINA)) {
            mVlcIndexAccess = ACCESS_SINA_INDEX;
            mVlcSegmentAccess = ACCESS_SINA_SEGMENT;
        } else if (pseduoAccess.equalsIgnoreCase(ACCESS_YOUKU)) {
            mVlcIndexAccess = ACCESS_YOUKU_INDEX;
            mVlcSegmentAccess = ACCESS_YOUKU_SEGMENT;
        } else if (pseduoAccess.equalsIgnoreCase(ACCESS_CNTV)) {
            mVlcIndexAccess = ACCESS_CNTV_INDEX;
            mVlcSegmentAccess = ACCESS_CNTV_SEGMENT;
        } else if (pseduoAccess.equalsIgnoreCase(ACCESS_SOHU)) {
            mVlcIndexAccess = ACCESS_SOHU_INDEX;
            mVlcSegmentAccess = ACCESS_SOHU_SEGMENT;
        } else if (pseduoAccess.equalsIgnoreCase(ACCESS_LETV)) {
            mVlcIndexAccess = ACCESS_LETV_INDEX;
            mVlcSegmentAccess = ACCESS_LETV_SEGMENT;
        } else {
            mVlcIndexAccess = ACCESS_VSL_INDEX;
            mVlcSegmentAccess = ACCESS_VSL_SEGMENT;
        }

        mMrl.dump();
        if (TextUtils.isEmpty(mVlcIndexAccess)
                || TextUtils.isEmpty(mVlcSegmentAccess))
            return false;

        mIsVslIndex = true;
        return true;
    }

    final public String getIndexMrlForVlcPlayer() {
        if (!mIsVslIndex)
            return mMrl.getRawMrl();

        return String.format("%s:%s", mVlcIndexAccess,
                mMrl.getSchemeSpecificPart());
    }

    final public String getSegmentMrlForVlcPlayer(PlayIndex playIndex, int order) {
        if (!mIsVslIndex) {
            ArrayList<Segment> segList = playIndex.mSegmentList;
            if (segList != null && segList.size() > order) {
                return segList.get(order).mUrl;
            }
        }

        return String.format("%s://%d", mVlcSegmentAccess, order);
    }

    public boolean loadIndex(boolean forceReload) {
        Context context = getContext();
        if (context == null)
            return false;

        if (!forceReload && getIndexBundle() != null)
            return true;

        try {
            PlayIndex playIndex = mResolver.resolve(context, forceReload,
                    MAX_TRY);
            if (playIndex == null || playIndex.mSegmentList == null
                    || playIndex.mSegmentList.isEmpty())
                return false;

            Bundle indexBundle = new Bundle();
            LibVlcVslIndex.putCount(indexBundle, playIndex.mSegmentList.size());

            int order = 0;
            for (Segment segment : playIndex.mSegmentList) {
                segment.putIntoVslBundle(indexBundle, order);

                LibVlcVslSegment.putSegmentMrl(indexBundle, order,
                        getSegmentMrlForVlcPlayer(playIndex, order));
                order += 1;
            }

            setIndexBundle(indexBundle, playIndex);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ResolveException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Context getContext() {
        return mWeakContext.get();
    }

    public void setIndexBundle(Bundle indexBundle, PlayIndex playIndex) {
        synchronized (mIndexLock) {
            mIndexBundle = indexBundle;
            mPlayIndex = playIndex;
        }
    }

    public Bundle getIndexBundle() {
        synchronized (mIndexLock) {
            return mIndexBundle;
        }
    }

    public PlayIndex getPlayIndex() {
        synchronized (mIndexLock) {
            return mPlayIndex;
        }
    }
}
