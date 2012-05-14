package tv.danmaku.util;

import java.lang.reflect.Field;

import android.os.Build;
import android.text.TextUtils;

public class AppBuild {

    public static int FROYO = 8; // June 2010: Android 2.2
    public static int GINGERBREAD = 9; // November 2010: Android 2.3
    public static int GINGERBREAD_MR1 = 10; // February 2011: Android 2.3.3.
    public static int HONEYCOMB = 11; // February 2011: Android 3.0.
    public static int HONEYCOMB_MR1 = 12; // May 2011: Android 3.1.
    public static int HONEYCOMB_MR2 = 13; // June 2011: Android 3.2.
    public static int ICE_CREAM_SANDWICH = 14; // October 2011: Android 4.0.
    public static int ICE_CREAM_SANDWICH_MR1 = 15; // Android 4.0.3.

    public static final String ABI_ARMv7a = "armeabi-v7a";
    public static final String ABI_ARM = "armeabi";

    public static final int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static final boolean isGingerBread() {
        int sdk = getSDKVersion();
        return sdk >= GINGERBREAD && sdk <= GINGERBREAD_MR1;
    }

    public static final boolean isHoneyComb() {
        int sdk = getSDKVersion();
        return sdk >= HONEYCOMB && sdk <= HONEYCOMB_MR2;
    }

    public static final boolean isHoneyCombOrLater() {
        return getSDKVersion() >= HONEYCOMB;
    }

    public static final boolean isIceCreamSandwichOrLater() {
        return getSDKVersion() >= ICE_CREAM_SANDWICH;
    }

    public static boolean supportARMv7a() {
        return supportABI(ABI_ARMv7a);
    }

    public static boolean supportABI(String requestAbi) {
        String abi = get_CPU_ABI();
        if (!TextUtils.isEmpty(abi) && abi.equalsIgnoreCase(requestAbi))
            return true;

        String abi2 = get_CPU_ABI2();
        if (!TextUtils.isEmpty(abi2) && abi.equalsIgnoreCase(requestAbi))
            return true;

        return false;
    }

    public static final String getParsedCpuAbiInfo() {
        StringBuilder cpuAbiInfo = new StringBuilder();
        String cpuAbi = AppBuild.get_CPU_ABI();
        if (!TextUtils.isEmpty(cpuAbi)) {
            cpuAbiInfo.append("CPU ABI : ");
            cpuAbiInfo.append(cpuAbi);
            cpuAbiInfo.append("\n");
        }

        String cpuAbi2 = AppBuild.get_CPU_ABI2();
        if (!TextUtils.isEmpty(cpuAbi)) {
            cpuAbiInfo.append("CPU ABI2 : ");
            cpuAbiInfo.append(cpuAbi2);
            cpuAbiInfo.append("\n");
        }

        return cpuAbiInfo.toString();
    }

    public static final String get_CPU_ABI() {
        return Build.CPU_ABI;
    }

    public static final String get_CPU_ABI2() {
        try {
            Field field = Build.class.getDeclaredField("CPU_ABI2");
            if (field == null)
                return null;

            Object fieldValue = field.get(null);
            if (field == null || !(fieldValue instanceof String)) {
                return null;
            }

            return (String) fieldValue;
        } catch (Exception e) {

        }

        return null;
    }
}
