package tv.danmaku.vlcdemo.mediaplayer;

import java.util.HashMap;

public class PlayerErrorCodes {
    // OpenCore error codes
    /*
     * Definition of first error event in range (not an actual error code).
     */
    public static int PVMFErrFirst = (-1);
    /*
     * Return code for general failure
     */
    public static int PVMFFailure = (-1);
    /*
     * Error due to cancellation
     */
    public static int PVMFErrCancelled = (-2);
    /*
     * Error due to no memory being available
     */
    public static int PVMFErrNoMemory = (-3);
    /*
     * Error due to request not being supported
     */
    public static int PVMFErrNotSupported = (-4);
    /*
     * Error due to invalid argument
     */
    public static int PVMFErrArgument = (-5);
    /*
     * Error due to invalid resource handle being specified
     */
    public static int PVMFErrBadHandle = (-6);
    /*
     * Error due to resource already exists and another one cannot be created
     */
    public static int PVMFErrAlreadyExists = (-7);
    /*
     * Error due to resource being busy and request cannot be handled
     */
    public static int PVMFErrBusy = (-8);
    /*
     * Error due to resource not ready to accept request
     */
    public static int PVMFErrNotReady = (-9);
    /*
     * Error due to data corruption being detected
     */
    public static int PVMFErrCorrupt = (-10);
    /*
     * Error due to request timing out
     */
    public static int PVMFErrTimeout = (-11);
    /*
     * Error due to general overflow
     */
    public static int PVMFErrOverflow = (-12);
    /*
     * Error due to general underflow
     */
    public static int PVMFErrUnderflow = (-13);
    /*
     * Error due to resource being in wrong state to handle request
     */
    public static int PVMFErrInvalidState = (-14);
    /*
     * Error due to resource not being available
     */
    public static int PVMFErrNoResources = (-15);
    /*
     * Error due to invalid configuration of resource
     */
    public static int PVMFErrResourceConfiguration = (-16);
    /*
     * Error due to general error in underlying resource
     */
    public static int PVMFErrResource = (-17);
    /*
     * Error due to general data processing
     */
    public static int PVMFErrProcessing = (-18);
    /*
     * Error due to general port processing
     */
    public static int PVMFErrPortProcessing = (-19);
    /*
     * Error due to lack of authorization to access a resource.
     */
    public static int PVMFErrAccessDenied = (-20);
    /*
     * Error due to the lack of a valid license for the content
     */
    public static int PVMFErrLicenseRequired = (-21);
    /*
     * Error due to the lack of a valid license for the content. However a
     * preview is available.
     */
    public static int PVMFErrLicenseRequiredPreviewAvailable = (-22);
    /*
     * Error due to the download content length larger than the maximum request
     * size
     */
    public static int PVMFErrContentTooLarge = (-23);
    /*
     * Error due to a maximum number of objects in use
     */
    public static int PVMFErrMaxReached = (-24);
    /*
     * Return code for low disk space
     */
    public static int PVMFLowDiskSpace = (-25);
    /*
     * Error due to the requirement of user-id and password input from app for
     * HTTP basic/digest authentication
     */
    public static int PVMFErrHTTPAuthenticationRequired = (-26);
    /*
     * PVMFMediaClock specific error. Callback has become invalid due to change
     * in direction of NPT clock.
     */
    public static int PVMFErrCallbackHasBecomeInvalid = (-27);
    /*
     * PVMFMediaClock specific error. Callback is called as clock has stopped.
     */
    public static int PVMFErrCallbackClockStopped = (-28);
    /*
     * Error due to missing call for ReleaseMatadataValue() API
     */
    public static int PVMFErrReleaseMetadataValueNotDone = (-29);
    /*
     * Error due to the redirect error
     */
    public static int PVMFErrRedirect = (-30);
    /*
     * Error if a given method or API is not implemented. This is NOT the same
     * as PVMFErrNotSupported.
     */
    public static int PVMFErrNotImplemented = (-31);
    /*
     * Error: the video container is not valid for progressive playback.
     */
    public static int PVMFErrContentInvalidForProgressivePlayback = (-32);
    /*
     * Placeholder for last event in range.
     */
    public static int PVMFErrLast = (-100);

    // StageFright error codes
    public static int STGFErrBase = -1000;
    public static int STGFErrAlreadyConnected = STGFErrBase;
    public static int STGFErrNoConnected = STGFErrBase - 1;
    public static int STGFErrUnknownHost = STGFErrBase - 2;
    public static int STGFErrCannotConnected = STGFErrBase - 3;
    public static int STGFErrIO = STGFErrBase - 4;

    public static int STGFConnectionLost = STGFErrBase - 5;
    public static int STGFMalformed = STGFErrBase - 7;
    public static int STGFOutOfRange = STGFErrBase - 8;
    public static int STGFBufferTooSmall = STGFErrBase - 9;
    public static int STGFUnsupported = STGFErrBase - 10;

    public static int STGFEndOfStream = STGFErrBase - 11;
    public static int STGFFormatChanged = STGFErrBase - 12;
    public static int STGFDiscontinuity = STGFErrBase - 13;

    public static String getMediaPlayerErrorMessage(int code) {
        return PMVF_ERROR_MAP.get(code);
    }

    public static HashMap<Integer, String> PMVF_ERROR_MAP = new HashMap<Integer, String>() {
        private static final long serialVersionUID = -6559120906624148361L;

        {
            // -1, -10
            put(PVMFFailure, "PVMFFailure");
            put(PVMFErrCancelled, "PVMFErrCancelled");
            put(PVMFErrNoMemory, "PVMFErrNoMemory");
            put(PVMFErrNotSupported, "PVMFErrNotSupported");
            put(PVMFErrArgument, "PVMFErrArgument");
            put(PVMFErrBadHandle, "PVMFErrBadHandle");
            put(PVMFErrAlreadyExists, "PVMFErrAlreadyExists");
            put(PVMFErrBusy, "PVMFErrBusy");
            put(PVMFErrNotReady, "PVMFErrNotReady");
            put(PVMFErrCorrupt, "PVMFErrCorrupt");

            // -11, -20
            put(PVMFErrTimeout, "PVMFErrTimeout");
            put(PVMFErrOverflow, "PVMFErrOverflow");
            put(PVMFErrUnderflow, "PVMFErrUnderflow");
            put(PVMFErrInvalidState, "PVMFErrInvalidState");
            put(PVMFErrNoResources, "PVMFErrNoResources");
            put(PVMFErrResourceConfiguration, "PVMFErrResourceConfiguration");
            put(PVMFErrResource, "PVMFErrResource");
            put(PVMFErrProcessing, "PVMFErrProcessing");
            put(PVMFErrPortProcessing, "PVMFErrPortProcessing");
            put(PVMFErrAccessDenied, "PVMFErrAccessDenied");

            // -21, -30
            put(PVMFErrLicenseRequired, "PVMFErrLicenseRequired");
            put(PVMFErrLicenseRequiredPreviewAvailable,
                    "PVMFErrLicenseRequiredPreviewAvailable");
            put(PVMFErrContentTooLarge, "PVMFErrContentTooLarge");
            put(PVMFErrMaxReached, "PVMFErrMaxReached");
            put(PVMFLowDiskSpace, "PVMFLowDiskSpace");
            put(PVMFErrHTTPAuthenticationRequired,
                    "PVMFErrHTTPAuthenticationRequired");
            put(PVMFErrCallbackHasBecomeInvalid,
                    "PVMFErrCallbackHasBecomeInvalid");
            put(PVMFErrCallbackClockStopped, "PVMFErrCallbackClockStopped");
            put(PVMFErrReleaseMetadataValueNotDone,
                    "PVMFErrReleaseMetadataValueNotDone");
            put(PVMFErrRedirect, "PVMFErrRedirect");

            // -31, -32
            put(PVMFErrNotImplemented, "PVMFErrNotImplemented");
            put(PVMFErrContentInvalidForProgressivePlayback,
                    "PVMFErrContentInvalidForProgressivePlayback");

            // -100
            put(PVMFErrLast, "PVMFErrLast");
            
            // -1000, -1010
            put(STGFErrAlreadyConnected, "STGFErrAlreadyConnected");
            put(STGFErrNoConnected, "STGFErrNoConnected");
            put(STGFErrUnknownHost, "STGFErrUnknownHost");
            put(STGFErrCannotConnected, "STGFErrCannotConnected");
            put(STGFErrIO, "STGFErrIO");
            put(STGFConnectionLost, "STGFConnectionLost");
            put(STGFMalformed, "STGFMalformed");
            put(STGFOutOfRange, "STGFOutOfRange");
            put(STGFBufferTooSmall, "STGFBufferTooSmall");
            put(STGFUnsupported, "STGFUnsupported");
            
            // -1011, 1012
            put(STGFEndOfStream, "STGFEndOfStream");
            put(STGFFormatChanged, "STGFFormatChanged");
            put(STGFDiscontinuity, "STGFDiscontinuity");
        }
    };
}
