package tv.danmaku.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppConnectivityManager {
    
    public static NetworkInfo getActivieNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return null;

        return manager.getActiveNetworkInfo();
    }
    
    public static boolean isConnectedOrConnecting(Context context) {
        NetworkInfo networkInfo = getActivieNetworkInfo(context);
        if (networkInfo == null)
            return false;

        return networkInfo.isConnectedOrConnecting();
    }
}
