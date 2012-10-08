package tv.danmaku.android.util;

import android.net.Uri;
import android.text.TextUtils;

public class UriHelper {
    // 不包括点号
    public static String getUriFileExtension(String url) {
        if (TextUtils.isEmpty(url))
            return url;

        Uri uri = Uri.parse(url);
        String path = uri.getLastPathSegment();
        if (TextUtils.isEmpty(path))
            return null;

        int extPos = path.lastIndexOf(".");
        if (extPos == -1 || extPos >= path.length())
            return null;

        return path.substring(extPos + 1);
    }
}
