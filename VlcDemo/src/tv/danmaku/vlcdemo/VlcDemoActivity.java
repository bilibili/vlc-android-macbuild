package tv.danmaku.vlcdemo;

import tv.danmaku.vlcdemo.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class VlcDemoActivity extends Activity {
    public static final String TAG = VlcDemoActivity.class.getName();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);

		//String url = "http://cntv.itv.doplive.com.cn/live592/index_128k.m3u8";
        String url = "http://qqlive.hdl.lxdns.com/2674956498.flv";

		Intent intent = VlcPlayerActivity.createIntent(this, url);
		startActivity(intent);
    }
}