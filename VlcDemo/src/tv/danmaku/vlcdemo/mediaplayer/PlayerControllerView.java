package tv.danmaku.vlcdemo.mediaplayer;

import tv.danmaku.vlcdemo.R;
import tv.danmaku.util.AppWindowManager.AspectRadio;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PlayerControllerView extends RelativeLayout {
    public static final String TAG = PlayerControllerView.class.getName();

    private static final int DEFAULT_FADE_DELAY_MS = 10000;
    @SuppressWarnings("unused")
    private static final int NEVER_FADE = Integer.MAX_VALUE;

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private PlayerVideoView mPlayer;

    private View mBackButton;
    private TextView mTitle;

    private SeekBar mSeekBar;
    private TextView mTimeEnd;
    private TextView mTimeCurrent;
    private ImageView mPlayPauseButton;
    private TextView mPlayMode;
    private ImageView mToggleAspectRadioButton;

    private boolean mShowing;
    private boolean mDragging;
    private boolean mSeekOnlyBuffered;

    public PlayerControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    final private void initView(Context context) {
        ViewGroup viewGroup = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.bili_player_controller_view, this,
                true);

        mBackButton = viewGroup.findViewById(R.id.back);
        mTitle = (TextView) viewGroup.findViewById(R.id.title);

        // MediaPlayer do not support seekTo unbuffered position
        mSeekBar = (SeekBar) viewGroup.findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(mSeekListener);
        mSeekBar.setThumb(null);
        mSeekBar.setMax(1000);

        mTimeCurrent = (TextView) viewGroup.findViewById(R.id.time_current);
        mTimeEnd = (TextView) viewGroup.findViewById(R.id.time_total);

        mPlayPauseButton = (ImageView) viewGroup.findViewById(R.id.play_pause);
        mPlayPauseButton.setOnClickListener(mPlayPauseListener);
        mPlayPauseButton.setImageLevel(LEVEL_CAN_PAUSE);
        
        mPlayMode = (TextView) viewGroup.findViewById(R.id.play_mode);

        mToggleAspectRadioButton = (ImageView) viewGroup.findViewById(R.id.toggle_aspect_radio_button);

        mShowing = false;
        setVisibility(View.GONE);
    }

    final public void removeListeners() {
        mBackButton.setOnClickListener(null);
        
        mSeekBar.setOnSeekBarChangeListener(null);

        mPlayPauseButton.setOnClickListener(null);
        mToggleAspectRadioButton.setOnClickListener(null);
    }

    final public void setPlayer(PlayerVideoView player) {
        mPlayer = player;
        showPlayMode();
    }
    
    final public void setTitle(String title) {
        mTitle.setText(title);
    }

    final public void setOnBackClickedListener(View.OnClickListener listener) {
        mBackButton.setOnClickListener(listener);
    }

    final public void setOnToggleAspectRadioListener(
            View.OnClickListener listener) {
        mToggleAspectRadioButton.setOnClickListener(listener);
    }

    // play-pause
    private static final int LEVEL_CAN_PLAY = 0;
    private static final int LEVEL_CAN_PAUSE = 1;

    final private View.OnClickListener mPlayPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            togglePlayPause();
            showAndFade();
        }
    };

    final public void togglePlayPause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        showPlayPause();
    }

    final public void showPlayPause() {
        if (mPlayer.isPlaying()) {
            // can pause
            mPlayPauseButton.setImageLevel(LEVEL_CAN_PAUSE);
        } else {
            // can play
            mPlayPauseButton.setImageLevel(LEVEL_CAN_PLAY);
        }
    }
    
    // playmode switch() {
    final public void showPlayMode() {
        int playMode = mPlayer.getCurrentPlayMode();
        switch (playMode) {
        case PlayerAdapter.USE_ANDROID_PLAYER:
            mPlayMode.setText(R.string.PlayMode_android_player_hw);
            break;
        case PlayerAdapter.USE_VLC_PLAYER: {
            if (mPlayer.getEnableIomx()) {
                mPlayMode.setText(R.string.PlayMode_vlc_player_hw_sw);
            } else {
                mPlayMode.setText(R.string.PlayMode_vlc_player_sw);
            }
            break;
        }
        default:
            break;
        }
    }

    // resize video view
    public static final int LEVEL_ASPECT_RADIO_ADJUST_VIDEO = 0;
    public static final int LEVEL_ASPECT_RADIO_IS_4_3 = 1;
    public static final int LEVEL_ASPECT_RADIO_IS_16_9 = 2;
    public int mAspectRadioLevel = LEVEL_ASPECT_RADIO_ADJUST_VIDEO;

    final public AspectRadio toggleAspectRadio() {
        if (mAspectRadioLevel >= LEVEL_ASPECT_RADIO_IS_16_9) {
            mAspectRadioLevel = LEVEL_ASPECT_RADIO_ADJUST_VIDEO;
        } else {
            mAspectRadioLevel += 1;
        }

        mToggleAspectRadioButton.setImageLevel(mAspectRadioLevel);
        switch (mAspectRadioLevel) {
        case LEVEL_ASPECT_RADIO_IS_4_3:
            return AspectRadio.RADIO_4_3;
        case LEVEL_ASPECT_RADIO_IS_16_9:
            return AspectRadio.RADIO_16_9;
        case LEVEL_ASPECT_RADIO_ADJUST_VIDEO:
        default:
            return AspectRadio.RADIO_ADJUST_CONTENT;
        }
    }

    // controller show and fade
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
            case FADE_OUT:
                hide();
                break;
            case SHOW_PROGRESS:
                pos = setProgress();
                assert (mPlayer != null);
                if (!mDragging && mShowing && mPlayer.isPlaying()) {
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                }
                break;
            }
        }
    };
    
    public void show() {
        if (!mShowing) {
            setProgress();
            setVisibility(View.VISIBLE);

            mShowing = true;
        }

        showPlayPause();

        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }

    public void showNoFade() {
        show();
        mHandler.removeMessages(FADE_OUT);
    }

    public void showAndFade() {
        //show(NO_FADE_DELAY_MS);
        showAndFade(DEFAULT_FADE_DELAY_MS);
    }

    public void showAndFade(int timeout) {
        show();

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public void hide() {
        if (mShowing) {
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                setVisibility(View.GONE);
            } catch (IllegalArgumentException ex) {
            }
            mShowing = false;
        }
    }

    final private int setProgress() {
        if (mDragging) {
            return 0;
        }

        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (duration > 0) {
            // use long to avoid overflow
            long pos = 1000L * position / duration;
            mSeekBar.setProgress((int) pos);
        }
        int percent = mPlayer.getBufferPercentage();
        mSeekBar.setSecondaryProgress(percent * 10);

        mTimeCurrent.setText(formatTime(position));
        mTimeEnd.setText(formatTime(duration));

        showPlayPause();

        return position;
    }

    private String formatTime(int position) {
        int totalSeconds = position / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return String.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by
    // onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the
    // dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch
    // notifications,
    // we will simply apply the updated position without suspending regular
    // updates.
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            showAndFade(3600000);

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress,
                boolean fromuser) {
            assert (mPlayer != null);
            if (!fromuser || mPlayer == null) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            
            if (mSeekOnlyBuffered) {
                // 只seek到已缓冲的区域
                long seekableBegin = mPlayer.getCurrentPosition() + 5000L;
                long seekableEnd = duration * mPlayer.getBufferPercentage() / 100L - 30000L; 
    
                newposition = Math.min(newposition, seekableEnd);
                if (newposition < seekableBegin) {
                    // do not seek back
                    return;
                }
            }
            
            mPlayer.seekTo((int) newposition);
            mTimeCurrent.setText(formatTime((int) newposition));
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            showPlayPause();
            showAndFade();

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };
}
