package tv.danmaku.util;

import java.util.TreeMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

public class AppTypeFace {

    private static TreeMap<String, Typeface> mTypefaceCache = new TreeMap<String, Typeface>();  
    
    public static Typeface createDanmakuFontFromAsset(Context context) {
        return createFromAsset(context, "fonts/danmaku.ttf");
    }

    public static Typeface createFromAsset(Context context, String path) {
        Typeface tf = mTypefaceCache.get(path);
        if (tf != null)
            return tf;
        
        AssetManager am = context.getAssets();
        if (am == null)
            return null;
        
        try {
            tf = Typeface.createFromAsset(am, path);
            mTypefaceCache.put(path, tf);
        } catch (RuntimeException e) {
            mTypefaceCache.put(path, null);
        }
        
        return tf;
    }
}
