package tv.danmaku.media;

import tv.danmaku.android.util.Assure;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class MediaResourceItem implements Parcelable {
    private static final String BUNDLE_FROM_TAG = "from_tag";
    private static final String BUNDLE_TYPE_TAG = "type_tag";
    private static final String BUNDLE_MRL = "mrl";
    private static final String BUNDLE_IS_INDEX_MRL = "is_index_mrl";
    private static final String BUNDLE_NEED_MEMBUF = "need_membuf";

    public static final String TYPE_TAG__ANY = "any"; // try HW
    public static final String TYPE_TAG__HLS = "hls"; // SW only
    public static final String TYPE_TAG__MP4 = "mp4"; // try HW
    public static final String TYPE_TAG__FLV = "flv"; // try HW

    public String mFromTag;
    public String mTypeTag;
    public String mMrl;
    public boolean mIsIndexMrl;
    public boolean mNeedMembuf;

    public MediaResourceItem() {
    }

    final public boolean isNormalMediaUrl() {
        if (isIndexMrl())
            return false;

        return true;
    }

    final public boolean isIndexMrl() {
        return mIsIndexMrl;
    }

    final public boolean isType(String typeTag) {
        Assure.checkNotEmptyString(typeTag);
        if (TextUtils.isEmpty(mTypeTag))
            return false;

        return mTypeTag.equalsIgnoreCase(typeTag);
    }

    final public boolean isType_hls() {
        return isType(TYPE_TAG__HLS);
    }

    final public boolean isType_mp4() {
        return isType(TYPE_TAG__MP4);
    }

    final public boolean isSWOnly() {
        return isType(TYPE_TAG__HLS);
    }

    final public boolean needMembuf() {
        return mNeedMembuf;
    }

    // for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putString(BUNDLE_FROM_TAG, mFromTag);
        bundle.putString(BUNDLE_TYPE_TAG, mTypeTag);
        bundle.putString(BUNDLE_MRL, mMrl);
        bundle.putBoolean(BUNDLE_IS_INDEX_MRL, mIsIndexMrl);
        bundle.putBoolean(BUNDLE_NEED_MEMBUF, mNeedMembuf);

        dest.writeBundle(bundle);
    }

    private MediaResourceItem(Parcel in) {
        Bundle bundle = in.readBundle(MediaResourceItem.class.getClassLoader());

        mFromTag = bundle.getString(BUNDLE_FROM_TAG);
        mTypeTag = bundle.getString(BUNDLE_TYPE_TAG);
        mMrl = bundle.getString(BUNDLE_MRL);
        mIsIndexMrl = bundle.getBoolean(BUNDLE_IS_INDEX_MRL);
        mNeedMembuf = bundle.getBoolean(BUNDLE_NEED_MEMBUF);
    }

    public static final Parcelable.Creator<MediaResourceItem> CREATOR = new Parcelable.Creator<MediaResourceItem>() {
        public MediaResourceItem createFromParcel(Parcel in) {
            return new MediaResourceItem(in);
        }

        public MediaResourceItem[] newArray(int size) {
            return new MediaResourceItem[size];
        }
    };
}
