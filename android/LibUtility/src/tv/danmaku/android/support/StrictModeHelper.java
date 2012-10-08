package tv.danmaku.android.support;

import tv.danmaku.android.BuildHelper;
import android.annotation.TargetApi;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;

public class StrictModeHelper {
    /*--------------------------------------
     * Thread Policy
     */
    private static final boolean THREAD__DETECT_CUSTOM_SLOW_CALLS = true;
    private static final boolean THREAD__DETECT_DISK_READS = false;
    private static final boolean THREAD__DETECT_DISK_WRITES = false;
    private static final boolean THREAD__DETECT_NETWORK = true;

    private enum ThreadPenalty {
        Death, DeathOnNetwork, Dialog, DropBox, FlashScreen, Log
    };

    private static ThreadPenalty sThreadPenalty = ThreadPenalty.Log;

    /*--------------------------------------
     * VM Policy
     */
    private static final boolean VM__DETECT_LEAKED_CLOSABLE_OBJECT = true;
    private static final boolean VM__DETECT_LEAKED_REGISTRATION_OBJECTS = true;
    private static final boolean VM__DETECT_LEAKED_SQLLITE_OBJECTS = true;

    private enum VmPenalty {
        Death, DropBox, Log
    };

    private static VmPenalty sVmPenalty = VmPenalty.Log;

    /*--------------------------------------
     * Methods
     */
    @TargetApi(16)
    public static void setStrictMode_Predefined() {
        if (!BuildHelper.isApi9_GingerBreadOrLater())
            return;

        /*--------------------------------------
         * Thread Policy
         */
        ThreadPolicy.Builder threadPolicyBuilder = new ThreadPolicy.Builder();
        if (THREAD__DETECT_CUSTOM_SLOW_CALLS)
            if (BuildHelper.isApi11_HoneyCombOrLater())
                threadPolicyBuilder = threadPolicyBuilder
                        .detectCustomSlowCalls();

        if (THREAD__DETECT_DISK_READS)
            threadPolicyBuilder = threadPolicyBuilder.detectDiskReads();

        if (THREAD__DETECT_DISK_WRITES)
            threadPolicyBuilder = threadPolicyBuilder.detectDiskWrites();

        if (THREAD__DETECT_NETWORK)
            threadPolicyBuilder = threadPolicyBuilder.detectNetwork();

        switch (sThreadPenalty) {
        case Death:
            threadPolicyBuilder = threadPolicyBuilder.penaltyDeath();
            break;
        case Dialog:
            threadPolicyBuilder = threadPolicyBuilder.penaltyDialog();
            break;
        case DropBox:
            threadPolicyBuilder = threadPolicyBuilder.penaltyDropBox();
            break;
        case Log:
            threadPolicyBuilder = threadPolicyBuilder.penaltyLog();
            break;
        case DeathOnNetwork:
            if (BuildHelper.isApi11_HoneyCombOrLater())
                threadPolicyBuilder = threadPolicyBuilder
                        .penaltyDeathOnNetwork();
            else
                threadPolicyBuilder = threadPolicyBuilder.penaltyLog();
            break;
        case FlashScreen:
            if (BuildHelper.isApi11_HoneyCombOrLater())
                threadPolicyBuilder = threadPolicyBuilder.penaltyFlashScreen();
            else
                threadPolicyBuilder = threadPolicyBuilder.penaltyLog();
            break;
        }
        StrictMode.setThreadPolicy(threadPolicyBuilder.build());

        /*--------------------------------------
         * VM Policy
         */
        VmPolicy.Builder vmPolicyBuilder = new VmPolicy.Builder();
        if (VM__DETECT_LEAKED_CLOSABLE_OBJECT)
            if (BuildHelper.isApi11_HoneyCombOrLater())
                vmPolicyBuilder = vmPolicyBuilder.detectLeakedClosableObjects();

        if (VM__DETECT_LEAKED_REGISTRATION_OBJECTS)
            if (BuildHelper.isApi16_JellyBeanOrLater())
                vmPolicyBuilder = vmPolicyBuilder
                        .detectLeakedRegistrationObjects();

        if (VM__DETECT_LEAKED_SQLLITE_OBJECTS)
            vmPolicyBuilder = vmPolicyBuilder.detectLeakedSqlLiteObjects();

        switch (sVmPenalty) {
        case Death:
            vmPolicyBuilder = vmPolicyBuilder.penaltyDeath();
            break;
        case DropBox:
            vmPolicyBuilder = vmPolicyBuilder.penaltyDropBox();
            break;
        case Log:
            vmPolicyBuilder = vmPolicyBuilder.penaltyLog();
            break;
        }
        StrictMode.setVmPolicy(vmPolicyBuilder.build());

    }

    @TargetApi(9)
    public static void setStrictMode_DetectAll_PenaltyDeatch() {
        if (BuildHelper.isApi9_GingerBreadOrLater()) {
            StrictMode.setThreadPolicy(new ThreadPolicy.Builder().detectAll()
                    .penaltyDeath().build());
            StrictMode.setVmPolicy(new VmPolicy.Builder().detectAll()
                    .penaltyDeath().build());
        }
    }
}
