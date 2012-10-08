package tv.danmaku.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.net.ConnectivityManagerCompat;
import android.text.TextUtils;

public class ConnectivityManagerHelper {
    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static NetworkInfo getActivieNetworkInfo(Context context) {
        ConnectivityManager manager = getConnectivityManager(context);
        if (manager == null)
            return null;

        return manager.getActiveNetworkInfo();
    }

    /*-
     * FIXME: for USB connect, this method can not be reliable.
     * Only use this method to retrieve error reason
     */
    public static boolean isConnectedOrConnecting(Context context) {
        NetworkInfo networkInfo = getActivieNetworkInfo(context);
        if (networkInfo == null)
            return false;

        return networkInfo.isConnectedOrConnecting();
    }

    public static boolean isActiveNetworkMetered(Context context) {
        ConnectivityManager manager = getConnectivityManager(context);
        if (manager == null)
            return false;

        ConnectivityManagerCompat compat = new ConnectivityManagerCompat();
        return compat.isActiveNetworkMetered(manager);
    }

    public static String getActiveNetworkName(Context context) {
        try {
            NetworkInfo info = getActivieNetworkInfo(context);
            String typeName = info.getTypeName().toLowerCase();
            if (typeName.equals("wifi")) {
                return typeName;
            } else {
                String extraInfoName = info.getExtraInfo().toLowerCase();
                if (!TextUtils.isEmpty(extraInfoName))
                    return extraInfoName;
            }
            return typeName;
        } catch (Exception e) {
            return null;
        }
    }
}
