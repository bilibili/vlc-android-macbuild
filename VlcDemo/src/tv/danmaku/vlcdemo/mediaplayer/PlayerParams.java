package tv.danmaku.vlcdemo.mediaplayer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class PlayerParams implements Parcelable {
	public static final String BUNDLE_PLAY_MODE = "play_mode";
	public static final String BUNDLE_VIDEO_QUALITY = "video_quality";
	public static final String BUNDLE_AVID = "avid";

	public static final String BUNDLE_TITLE = "title";
	public static final String BUNDLE_FROM = "from";
	public static final String BUNDLE_VID = "vid";
	public static final String BUNDLE_LINK = "link";

	public static final String BUNDLE_MOBILE_URL = "mobile_url";

	public static final String BUNDLE_NORMAL_URL_LIST = "normal_url_list";
	public static final String BUNDLE_SEGMENT_TO_PLAY = "segment_to_play";

	public static final int SEGMENT_PLAY_ALL = -1;

	// params
	public int mPlayMode = PlayerAdapter.USE_AUTO_PLAYER;
	public int mVideoQuality = PlayerAdapter.DEFAULT_VIDEO_QUALITY;
	public int mAvid;

	public String mTitle;
	public String mFrom;
	public String mVid;
	public String mLink;

	public VideoUrl mMobileUrl; // 低清源

	public VideoUrlList mNormalUrlList; // 高清源
	public int mSegmentToPlay = SEGMENT_PLAY_ALL; // 高清源的播放指示

	public PlayerParams() {
	}

	// for mobile
	public String getMobileUrl() {
		if (mMobileUrl == null)
			return null;

		return mMobileUrl.mUrl;
	}

	// for high quality
	public boolean isNormalUrlLoaded() {
		if (mNormalUrlList == null)
			return false;

		return true;
	}

	public boolean isFromYouku() {
		if (TextUtils.isEmpty(mFrom) || !mFrom.equalsIgnoreCase("youku")) {
			return false;
		}

		return true;
	}

	// for Parcelable
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bundle = new Bundle();
		bundle.putInt(BUNDLE_PLAY_MODE, mPlayMode);
		bundle.putInt(BUNDLE_VIDEO_QUALITY, mVideoQuality);
		bundle.putInt(BUNDLE_AVID, mAvid);

		bundle.putString(BUNDLE_TITLE, mTitle);
		bundle.putString(BUNDLE_FROM, mFrom);
		bundle.putString(BUNDLE_VID, mVid);
		bundle.putString(BUNDLE_LINK, mLink);

		bundle.putParcelable(BUNDLE_MOBILE_URL, mMobileUrl);

		bundle.putParcelable(BUNDLE_NORMAL_URL_LIST, mNormalUrlList);
		bundle.putInt(BUNDLE_SEGMENT_TO_PLAY, mSegmentToPlay);

		dest.writeBundle(bundle);
	}

	private PlayerParams(Parcel in) {
		Bundle bundle = in.readBundle(PlayerParams.class.getClassLoader());

		mPlayMode = bundle.getInt(BUNDLE_PLAY_MODE,
				PlayerAdapter.USE_AUTO_PLAYER);
		mVideoQuality = bundle.getInt(BUNDLE_VIDEO_QUALITY,
				PlayerAdapter.DEFAULT_VIDEO_QUALITY);
		mAvid = bundle.getInt(BUNDLE_AVID);

		mTitle = bundle.getString(BUNDLE_TITLE);
		mFrom = bundle.getString(BUNDLE_FROM);
		mVid = bundle.getString(BUNDLE_VID);
		mLink = bundle.getString(BUNDLE_LINK);

		mMobileUrl = bundle.getParcelable(BUNDLE_MOBILE_URL);

		mNormalUrlList = bundle.getParcelable(BUNDLE_NORMAL_URL_LIST);
		mSegmentToPlay = bundle
				.getInt(BUNDLE_SEGMENT_TO_PLAY, SEGMENT_PLAY_ALL);
	}

	public static final Parcelable.Creator<PlayerParams> CREATOR = new Parcelable.Creator<PlayerParams>() {
		public PlayerParams createFromParcel(Parcel in) {
			return new PlayerParams(in);
		}

		public PlayerParams[] newArray(int size) {
			return new PlayerParams[size];
		}
	};
}
