package tv.danmaku.vlcdemo.mediaplayer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/*- 
 * 需要redirect的url包括
 * 1. QQ通过B站跳转获取原始url
 *      https://secure.bilibili.tv/offset
 * 2. Sina通过iask跳转获取手机url 
 *      http://v.iask.com/v_play_ipad.php
 * 3. UP主通过sinaapp.com跳转获取原始url
 *      注意:这里如果是https，可能需要url忽略证书验证
 *      http://***.sinaapp.com
 */
public class VideoUrl implements Parcelable {
    public static final String BUNDLE_URL = "url";
    public static final String BUNDLE_URL_2ND = "url_2nd";
    public static final String BUNDLE_YOUKU_FLAG = "youku_flag";

    public String mUrl;
    public String mUrl2nd;  // 备选的url，可能无法使用

    // 优酷的视频类型标签
    public String mYoukuFlag;

    public VideoUrl() {
    }

    public VideoUrl(String url) {
        mUrl = url;
    }

    public final boolean isMP4() {
        if (mYoukuFlag == null)
            return false;

        return mYoukuFlag.equalsIgnoreCase("mp4");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putString(BUNDLE_URL, mUrl);
        bundle.putString(BUNDLE_URL_2ND, mUrl2nd);
        bundle.putString(BUNDLE_YOUKU_FLAG, mYoukuFlag);

        dest.writeBundle(bundle);
    }

    private VideoUrl(Parcel in) {
        Bundle bundle = in.readBundle(VideoUrl.class.getClassLoader());
        
        mUrl = bundle.getString(BUNDLE_URL);
        mUrl2nd = bundle.getString(BUNDLE_URL_2ND);
        mYoukuFlag = bundle.getString(BUNDLE_YOUKU_FLAG);
    }

    public static final Parcelable.Creator<VideoUrl> CREATOR = new Parcelable.Creator<VideoUrl>() {
        public VideoUrl createFromParcel(Parcel in) {
            return new VideoUrl(in);
        }

        public VideoUrl[] newArray(int size) {
            return new VideoUrl[size];
        }
    };
}