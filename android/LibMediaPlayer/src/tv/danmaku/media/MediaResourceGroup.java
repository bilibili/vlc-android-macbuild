package tv.danmaku.media;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class MediaResourceGroup implements Parcelable {
    private static final String BUNDLE_RES_LIST = "res_list";

    private static final int RES_FLAGS__ANY = 0x0;
    private static final int RES_FLAGS__NORMAL_URL = 0x1;

    private ArrayList<MediaResourceItem> mResourceList;

    public MediaResourceGroup() {
        mResourceList = new ArrayList<MediaResourceItem>();
    }

    final public MediaResourceItem getItem() {
        return findItem(RES_FLAGS__ANY);
    }

    final public MediaResourceItem getItem_NormalUrl() {
        return findItem(RES_FLAGS__NORMAL_URL);
    }

    final private MediaResourceItem findItem(int flags) {
        if (mResourceList == null || mResourceList.isEmpty())
            return null;

        for (MediaResourceItem item : mResourceList) {
            if (((flags & RES_FLAGS__NORMAL_URL) != 0)
                    && !item.isNormalMediaUrl())
                continue;

            return item;
        }

        return null;
    }

    final public boolean hasNormalUrlToPlay() {
        return findItem(RES_FLAGS__NORMAL_URL) != null;
    }

    final public void addIndex_any_membuf(String from, String indexMrl) {
        MediaResourceItem item = new MediaResourceItem();
        item.mFromTag = from;
        item.mTypeTag = MediaResourceItem.TYPE_TAG__ANY;
        item.mMrl = indexMrl;
        item.mIsIndexMrl = true;
        item.mNeedMembuf = true;

        mResourceList.add(item);
    }

    final public void addIndex_flv_membuf(String from, String indexMrl) {
        MediaResourceItem item = new MediaResourceItem();
        item.mFromTag = from;
        item.mTypeTag = MediaResourceItem.TYPE_TAG__FLV;
        item.mMrl = indexMrl;
        item.mIsIndexMrl = true;
        item.mNeedMembuf = true;

        mResourceList.add(item);
    }

    final public void addUrl_any(String from, String url) {
        MediaResourceItem item = new MediaResourceItem();
        item.mFromTag = from;
        item.mTypeTag = MediaResourceItem.TYPE_TAG__ANY;
        item.mMrl = url;

        mResourceList.add(item);
    }

    final public void addUrl_hls(String from, String url) {
        MediaResourceItem item = new MediaResourceItem();
        item.mFromTag = from;
        item.mTypeTag = MediaResourceItem.TYPE_TAG__HLS;
        item.mMrl = url;

        mResourceList.add(item);
    }

    final public void addUrl_mp4(String from, String url) {
        MediaResourceItem item = new MediaResourceItem();
        item.mFromTag = from;
        item.mTypeTag = MediaResourceItem.TYPE_TAG__MP4;
        item.mMrl = url;

        mResourceList.add(item);
    }

    // for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putParcelableArrayList(BUNDLE_RES_LIST, mResourceList);

        dest.writeBundle(bundle);
    }

    private MediaResourceGroup(Parcel in) {
        Bundle bundle = in
                .readBundle(MediaResourceGroup.class.getClassLoader());

        mResourceList = bundle.getParcelableArrayList(BUNDLE_RES_LIST);
    }

    public static final Parcelable.Creator<MediaResourceGroup> CREATOR = new Parcelable.Creator<MediaResourceGroup>() {
        public MediaResourceGroup createFromParcel(Parcel in) {
            return new MediaResourceGroup(in);
        }

        public MediaResourceGroup[] newArray(int size) {
            return new MediaResourceGroup[size];
        }
    };
}
