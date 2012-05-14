package tv.danmaku.vlcdemo;

import tv.danmaku.util.AppPackageManager;
import tv.danmaku.vlcdemo.mediaplayer.PlayerAdapter;
import tv.danmaku.vlcdemo.mediaplayer.PlayerParams;
import tv.danmaku.vlcdemo.mediaplayer.PlayerMessages.PlayerParamsHolder;
import tv.danmaku.vlcdemo.mediaplayer.VideoUrl;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

public class VlcPlayerActivity extends Activity {
    public static final String TAG = VlcPlayerActivity.class.getName();

    public static final String BUNDLE_PLAYER_PARAMS = "player_params";

    private PlayerParamsHolder mPlayerParamsHolder = new PlayerParamsHolder();
    private PlayerAdapter mPlayerAdapter = new PlayerAdapter();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.bili_player_view);

        setupView();
    }

    final private void setupView() {
    	mPlayerParamsHolder.mParams = getIntent().getParcelableExtra(
    			BUNDLE_PLAYER_PARAMS);
        if (mPlayerParamsHolder.mParams == null) {
            
            if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
                String location = getIntent().getDataString();
                if (TextUtils.isEmpty(location)) {
                    finish();
                    return;
                }

                PlayerParams params = new PlayerParams();
                
                params.mFrom = "link";
                params.mLink = location;

                mPlayerParamsHolder.mParams = params;
                mPlayerParamsHolder.mResolvedUrl = location;
            }
        }

        mPlayerAdapter.initAdapter(this, mPlayerParamsHolder);
        mPlayerAdapter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPlayerAdapter.release();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mPlayerAdapter.release();
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPlayerAdapter.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    public static Intent createIntent(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setDataAndType(Uri.parse(url), "video/*");
        intent.setComponent(new ComponentName("tv.danmaku.vlcdemo", "tv.danmaku.vlcdemo.VlcPlayerActivity"));

        return intent;
    }

//    public static Intent createIntent(Context context, PlayerParams playerParams) {
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(BUNDLE_PLAYER_PARAMS, playerParams);
//
//        Intent intent = new Intent(context, VlcPlayerActivity.class);
//        intent.putExtras(bundle);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//
//        return intent;
//    }
}
