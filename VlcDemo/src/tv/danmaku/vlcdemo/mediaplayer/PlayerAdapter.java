package tv.danmaku.vlcdemo.mediaplayer;

import java.lang.ref.WeakReference;

import tv.danmaku.vlcdemo.R;
import tv.danmaku.media.AbsMediaPlayer;
import tv.danmaku.util.AppWindowManager.AspectRadio;
import tv.danmaku.vlcdemo.mediaplayer.PlayerMessages.PlayerParamsHolder;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class PlayerAdapter implements Handler.Callback {
	public static final String TAG = PlayerAdapter.class.getName();

	// ChoosePlayMode
	public static final int USE_DEFAULT_PLAYER = 0;
	public static final int USE_FLASH_PLAYER = 2;
	public static final int USE_3RD_PLAYER = 5;
	public static final int USE_REDIRECT_TO_WEB = 6;

	public static final int USE_AUTO_PLAYER = 8;
	public static final int USE_ANDROID_PLAYER = 9;
	public static final int USE_VLC_PLAYER = 10;
	
    // ChooseVideoQuality
    public static final int VIDEO_LOW_QUALITY = 0;
    public static final int VIDEO_HIGH_QUALITY = 1;
    public static final int DEFAULT_VIDEO_QUALITY = VIDEO_LOW_QUALITY;

	// not defined before 2.3
	public static final int MEDIA_INFO_BUFFERING_END = 702;
	public static final int MEDIA_INFO_BUFFERING_START = 701;
	// private static final int MEDIA_INFO_BUFFERING_END =
	// MediaPlayer.MEDIA_INFO_BUFFERING_END;
	// private static final int MEDIA_INFO_BUFFERING_START =
	// MediaPlayer.MEDIA_INFO_BUFFERING_START;

	private static final int CHECK_BUFFERING = PlayerMessages.CHECK_BUFFERING;

	private WeakReference<Activity> mWeakActivity;

	private PlayerVideoView mVideoView;
	private View mBufferingView;
	private View mControllerUnderlay;
	private PlayerControllerView mControllerView;

	private PlayerParamsHolder mParamsHolder;

	private boolean mPrepared = false;

	private boolean mIsAdapaterValid;
	private Handler mHandler;

	private PlayerEventDispatcher mPlayerEventDispatcher;

	final public void initAdapter(Activity activity,
			PlayerParamsHolder paramsHolder) {
		mWeakActivity = new WeakReference<Activity>(activity);

		mParamsHolder = paramsHolder;

		// get views
		mVideoView = (PlayerVideoView) activity.findViewById(R.id.video_view);
		mBufferingView = activity.findViewById(R.id.buffering_group);
		mControllerUnderlay = activity.findViewById(R.id.controller_underlay);
		mControllerView = (PlayerControllerView) activity
				.findViewById(R.id.controller_view);

		// handler
		mIsAdapaterValid = true;
		mHandler = new Handler(this);
		mPlayerEventDispatcher = new PlayerEventDispatcher(mHandler);

		// set listeners
		mVideoView.setUIHandler(mHandler);
		mVideoView.setOnInfoListener(mOnInfoListener);
		mVideoView.setOnPreparedListener(mPlayerEventDispatcher);
		mVideoView.setOnCompletionListener(mPlayerEventDispatcher);
		mControllerUnderlay.setOnClickListener(mOnUnderlayClicked);
		mControllerView.setOnClickListener(mOnControllerClicked);
		mControllerView.setOnBackClickedListener(mOnBackClicked);
		mControllerView.setOnToggleAspectRadioListener(mOnToggleAspectRadio);

		// bind player and controller
		mControllerView.setPlayer(mVideoView);

		setVid(paramsHolder.mParams.mVid);
		setTitle(paramsHolder.mParams.mTitle);

		// 对于优酷的m3u8, 尽量使用vlc
		if (mParamsHolder.mParams.mFrom.equalsIgnoreCase("youku")) {
			paramsHolder.mParams.mPlayMode = USE_VLC_PLAYER;
		}

		mVideoView.setPlayMode(paramsHolder.mParams.mPlayMode);

		Message msg = mHandler.obtainMessage(
				PlayerMessages.SELECT_MEDIA_PLAYER,
				paramsHolder.mParams.mPlayMode, 0);
		mHandler.sendMessage(msg);
	}

	final public void release() {
		mIsAdapaterValid = false;

		mHandler = null;

		if (mVideoView != null) {
			mVideoView.setOnInfoListener(null);
			mVideoView.setOnPreparedListener(null);
			mVideoView.stopPlayback();
		}

		if (mControllerUnderlay != null) {
			mControllerUnderlay.setOnClickListener(null);
		}

		if (mControllerView != null) {
			mControllerView.removeListeners();
		}
	}

	final public void setVid(String vid) {
		mParamsHolder.mParams.mVid = vid;
	}

	final public void setTitle(String title) {
		mControllerView.setTitle(title);
	}

	final public void setVideoUrl(String url) {
		setVideoPath(url);
	}

	final public void setVideoPath(String path) {
		mVideoView.setVideoPath(path);
	}

	final public void start() {
		mVideoView.setVisibility(View.VISIBLE);
		mVideoView.setVideoPath(mParamsHolder.mResolvedUrl);
		mVideoView.mAvid = mParamsHolder.mParams.mAvid;
		mVideoView.start();
	}

	final public void stop() {
		if (mVideoView != null) {
			mVideoView.setVisibility(View.GONE);
			mVideoView.stopPlayback();
		}
	}

	final public void pause() {
		mVideoView.pause();
	}

	final public void resume() {
		mVideoView.resume();
	}

	final private Activity getActivity() {
		if (mWeakActivity == null)
			return null;

		return mWeakActivity.get();
	}

	final private Context getContext() {
		return getActivity();
	}

	private AbsMediaPlayer.OnInfoListener mOnInfoListener = new AbsMediaPlayer.OnInfoListener() {

		@Override
		public boolean onInfo(AbsMediaPlayer mp, int what, int extra) {
			switch (what) {
			case MEDIA_INFO_BUFFERING_START:
				mBufferingView.setVisibility(View.VISIBLE);

				// MEDIA_INFO_BUFFERING_END may not return
				// so we start checking buffering by ourselves
				Message msg = mHandler.obtainMessage(CHECK_BUFFERING);
				msg.arg1 = mVideoView.getCurrentPosition();
				mHandler.sendMessage(msg);

				// DebugLog.v(TAG, "buffering start at " + msg.arg1);
				break;

			case MEDIA_INFO_BUFFERING_END:
				// DebugLog.v(TAG, "buffering end");
				mBufferingView.setVisibility(View.GONE);

				// stop checking buffering
				mHandler.removeMessages(CHECK_BUFFERING);
				break;

			case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
				// DebugLog.v(TAG, "media not seekable");
				break;
			}

			return false;
		}

	};

	// 修改长宽比
	private View.OnClickListener mOnToggleAspectRadio = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			AspectRadio aspectRadio = mControllerView.toggleAspectRadio();
			mVideoView.setAspectRadio(aspectRadio);
		}
	};

	// 点击空白位置,显示播放控制面板
	private View.OnClickListener mOnUnderlayClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mPrepared) {
				mControllerView.showAndFade();
			}
		}
	};

	// 点击空白位置,隐藏播放控制面板
	private View.OnClickListener mOnControllerClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mControllerView.hide();
		}
	};

	// 点击退出
	private View.OnClickListener mOnBackClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Activity activity = getActivity();
			if (activity == null)
				return;

			activity.finish();
		}
	};

	@Override
	public boolean handleMessage(Message msg) {

		if (!mIsAdapaterValid)
			return false;

		switch (msg.what) {
		case PlayerMessages.MEDIA_PLAYER_PREPARED:

			mPrepared = true;
			mBufferingView.setVisibility(View.GONE);
			// DanmakuView.SystemAnimationTicker ticker = new
			// DanmakuView.SystemAnimationTicker();
			// ticker.startTicker();
			// mDanmakuView.start(mParamsHodler.mDanmakuDocument, ticker);
			break;

		case PlayerMessages.MEDIA_PLAYER_COMPLETION:

			mControllerView.showNoFade();
			break;

		case PlayerMessages.AUTO_PLAY_MODE_CHANGED:
			mControllerView.showPlayMode();
			break;

		}

		switch (msg.what) {
		case CHECK_BUFFERING:
			int lastPos = msg.arg1;
			int pos = mVideoView.getCurrentPosition();
			if (pos != lastPos) {
				// buffering end
				// DebugLog.v(TAG, "buffering end at " + pos
				// + " which start at " + lastPos);
				mBufferingView.setVisibility(View.GONE);
			} else {
				mHandler.removeMessages(CHECK_BUFFERING);
				msg = mHandler.obtainMessage(CHECK_BUFFERING);
				msg.arg1 = lastPos;
				mHandler.sendMessageDelayed(msg, 500);
			}

			break;
		}

		return true;
	}
}
