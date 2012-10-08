package tv.danmaku.android;

import java.io.InputStream;

import tv.danmaku.android.util.StreamHelper;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class ResourcesHelper {

    public static Drawable getDrawable(Context context, int id) {
        Resources resources = context.getResources();
        if (resources == null)
            return null;

        return resources.getDrawable(id);
    }

    public static int getColor(Context context, int id) {
        Resources resources = context.getResources();
        if (resources == null)
            return 0;

        return resources.getColor(id);
    }

    public static ColorStateList getColorStateList(Context context, int id) {
        Resources resources = context.getResources();
        if (resources == null)
            return null;

        return resources.getColorStateList(id);
    }

    public static String getTextFile(Context context, int id) {
        InputStream inputStream = context.getResources().openRawResource(id);

        String text = StreamHelper.readStreamLineByLine(inputStream);
        StreamHelper.closeStream(inputStream);

        return text;
    }
}
