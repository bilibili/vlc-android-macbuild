package tv.danmaku.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class AppWindowManager {
    public static final String TAG = AppWindowManager.class.getName();

    public enum AspectRadio {
        RADIO_ADJUST_CONTENT,
        RADIO_4_3,
        RADIO_16_9,
    };
    
    public static WindowManager getWindowManager(Context context) {
        Object service = context.getSystemService(Context.WINDOW_SERVICE);
        if (service == null)
            return null;

        return (WindowManager) service;
    }

    public static Display getDefaultDisplay (Context context) {
        WindowManager wm = getWindowManager(context);
        if (wm == null)
            return null;
        
        return wm.getDefaultDisplay();
    }
    
    public static Point GetClippedDisplaySize(Context context, AspectRadio aspectRadio) {
        Display display = getDefaultDisplay(context);
        if (display == null)
            return null;

        int displayHeight = display.getHeight();
        int displayWidth = display.getWidth();
        int clippedWidth = -1;
        int clippedHeight = -1;
        switch (aspectRadio) {
        case RADIO_4_3:
            clippedWidth = 4;
            clippedHeight = 3;
            break;
        case RADIO_16_9:
            clippedWidth = 16;
            clippedHeight = 9;
            break;
        case RADIO_ADJUST_CONTENT:
            // no break;
        default:
            //DebugLog.v(TAG, "adjust " + displayWidth + ":" + displayHeight);
            return new Point(displayWidth, displayHeight);
        }
        
        double displayRadio = (double) displayWidth / (double) displayHeight;
        double clipRadio = (double) clippedWidth / (double) clippedHeight;

        if (displayRadio > clipRadio) {
            displayWidth = (int)Math.round(displayHeight * clipRadio);
            displayWidth = Math.min(displayWidth, display.getWidth());
        } else {
            displayHeight = (int)Math.round(displayWidth / clipRadio);
            displayHeight = Math.min(displayHeight, display.getHeight());
        }
        
        //DebugLog.v(TAG, "adjust " + displayWidth + ":" + displayHeight);
        return new Point(displayWidth, displayHeight);
    }
}
