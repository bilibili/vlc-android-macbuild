package tv.danmaku.media;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import tv.danmaku.media.resource.VodIndex;

public class MediaResource implements Parcelable {
    private static final String BUNDLE_HIGH_QUAL_GROUP = "high_qual_group";
    private static final String BUNDLE_LOW_QUAL_GROUP = "low_qual_group";

    private static final String BUNDLE_VOD_INDEX = "vod_index";

    // 优先级 1st > mrl > 2nd
    // new params
    public MediaResourceGroup mHighQualGroup;
    public MediaResourceGroup mLowQualGroup;

    // 点播列表, 新浪和优酷源, 第三方播放器, MediaListPlayer用
    // TODO deprecate this field
    public VodIndex mVodIndex; // vod解析后的vod列表

    public MediaResource() {
        mHighQualGroup = new MediaResourceGroup();
        mLowQualGroup = new MediaResourceGroup();
    }

    // for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putParcelable(BUNDLE_HIGH_QUAL_GROUP, mHighQualGroup);
        bundle.putParcelable(BUNDLE_LOW_QUAL_GROUP, mLowQualGroup);

        bundle.putParcelable(BUNDLE_VOD_INDEX, mVodIndex);

        dest.writeBundle(bundle);
    }

    private MediaResource(Parcel in) {
        Bundle bundle = in.readBundle(MediaResource.class.getClassLoader());

        mHighQualGroup = bundle.getParcelable(BUNDLE_HIGH_QUAL_GROUP);
        mLowQualGroup = bundle.getParcelable(BUNDLE_LOW_QUAL_GROUP);

        mVodIndex = bundle.getParcelable(BUNDLE_VOD_INDEX);
    }

    public static final Parcelable.Creator<MediaResource> CREATOR = new Parcelable.Creator<MediaResource>() {
        public MediaResource createFromParcel(Parcel in) {
            return new MediaResource(in);
        }

        public MediaResource[] newArray(int size) {
            return new MediaResource[size];
        }
    };
}
