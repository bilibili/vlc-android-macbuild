package tv.danmaku.vlcdemo.mediaplayer;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class VideoUrlList implements Parcelable {
    public static final String BUNDLE_VIDEO_LIST = "video_list";
    public ArrayList<VideoUrl> mVideoList = new ArrayList<VideoUrl>();

    // 优酷的视频类型标签
    public String mYoukuFlag;

    public VideoUrlList() {
    }

    public VideoUrlList(String singleUrl) {
        mVideoList.add(new VideoUrl(singleUrl));
    }

    public VideoUrlList(VideoUrl singleUrl) {
        mVideoList.add(singleUrl);
    }

    public VideoUrlList(Bundle bundle) {
    }
    
    public final boolean isSingleSegment() {
        if (mVideoList == null)
            return false;

        if (mVideoList.isEmpty())
            return false;
        
        if (mVideoList.size() > 1)
            return false;
        
        return true;
    }

    public final boolean isSingleSegmentMP4() {
        if (mVideoList == null)
            return false;

        if (mVideoList.isEmpty())
            return false;
        
        if (mVideoList.size() > 1)
            return false;

        VideoUrl url = mVideoList.get(0);
        return url.isMP4();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_VIDEO_LIST, mVideoList);

        dest.writeBundle(bundle);
    }

    private VideoUrlList(Parcel in) {
        Bundle bundle = in.readBundle(VideoUrlList.class.getClassLoader());

        mVideoList = bundle.getParcelableArrayList(BUNDLE_VIDEO_LIST);
    }

    public static final Parcelable.Creator<VideoUrlList> CREATOR = new Parcelable.Creator<VideoUrlList>() {
        public VideoUrlList createFromParcel(Parcel in) {
            return new VideoUrlList(in);
        }

        public VideoUrlList[] newArray(int size) {
            return new VideoUrlList[size];
        }
    };
}