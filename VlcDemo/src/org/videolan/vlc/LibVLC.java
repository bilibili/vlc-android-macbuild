/*****************************************************************************
 * LibVLC.java
 *****************************************************************************
 * Copyright © 2010-2012 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc;

import java.util.ArrayList;

import org.videolan.vlc.LibVlcException;

import tv.danmaku.util.AppBuild;
import tv.danmaku.util.AppBuildConfig;
import tv.danmaku.util.AppCpuInfo;
import tv.danmaku.util.CollectionHelper;
import tv.danmaku.util.DebugLog;

import android.view.Surface;
import android.content.Context;

public class LibVLC {
    private static final String TAG = "VLC/LibVLC";

    private static final String[] sDefaultParameters = new String[] { "--intf",
            "dummy", "--no-osd", "--no-plugins-cache", "--no-drop-late-frames",
            "--no-video-title-show", "--no-stats", "--ffmpeg-fast",
            "--http-hosts-reject-range",
            "v.youku.com,f.youku.com", "--ts-seek-percent" };

    public static final String[] sDebugLogParameters = new String[] { "-vvv",
            "--logmode", "android", "--control", "logger", };

    private static boolean mLibIomxLoaded = false;

    private static boolean mLibVlcLoaded = false;
    private static LibVLC sInstance;

    /** libVLC instance C pointer */
    private int mLibVlcInstance = 0; // Read-only, reserved for JNI
    private int mMediaPlayerInstance = 0; // Read-only, reserved for JNI

    private Aout mAout;

    /** Keep screen bright */
    // private WakeLock mWakeLock;

    /** Check in libVLC already initialized otherwise crash */
    private boolean mIsInitialized = false;

    public native void attachSurface(Surface surface, Object parentView,
            int width, int height);

    public native void detachSurface();

    /* Load library before object instantiation */

    private static void loadLib() throws LibVlcException {
        if (!mLibIomxLoaded) {
            try {
                if (AppBuild.isGingerBread()) {
                    System.loadLibrary("iomx-gingerbread");
                    mLibIomxLoaded = true;
                } else if (AppBuild.isIceCreamSandwichOrLater()) {
                    System.loadLibrary("iomx-ics");
                    mLibIomxLoaded = true;
                } else {
                    /* No honeycomb build for now */
                }
            } catch (Throwable t) {
                DebugLog.w(TAG, "Unable to load the iomx library: " + t);
            }
        }

        if (mLibVlcLoaded)
            return;

        try {
            if (!AppBuild.supportARMv7a()) {
                throw new LibVlcException("need armv7a support");
            }

            AppCpuInfo cpuInfo = AppCpuInfo.parseCpuInfo();
            if (cpuInfo.isCortexA8()) {

                if (!cpuInfo.supportNeon()) {
                    throw new LibVlcException("need neon support");
                }
                DebugLog.v(TAG, "(cortex-a8) loading libvlcjni.so");
                System.loadLibrary("vlcjni");

            } else if (cpuInfo.supportVfpv3D16()) {

                // vfpv3-d16 识别为tegra2
                DebugLog.v(TAG, "(vfpv3-d16) loading libvlcjni-tegra2.so");
                System.loadLibrary("vlcjni-tegra2");

            } else {

                DebugLog.v(TAG, "(unknown) loading libvlcjni.so");
                System.loadLibrary("vlcjni");

            }

            System.loadLibrary("vlcjni");
            DebugLog.d(TAG, "vlcjni loaded: ");
            mLibVlcLoaded = true;
        } catch (UnsatisfiedLinkError ule) {
            DebugLog.e(TAG, "Can't load vlcjni library: " + ule);
            // / FIXME Alert user
            System.exit(1);
        } catch (SecurityException se) {
            DebugLog.e(TAG,
                    "Encountered a security issue when loading vlcjni library: "
                            + se);
            // / FIXME Alert user
            System.exit(1);
        }
    }

    /**
     * Singleton constructor Without surface and vout to create the thumbnail
     * and get information e.g. on the MediaLibraryAcitvity
     * 
     * @return
     * @throws LibVlcException
     */
    public static LibVLC getInstance() throws LibVlcException {
        if (sInstance == null) {
            /* First call */
            loadLib();
            sInstance = new LibVLC();

            ArrayList<String> params = new ArrayList<String>();
            CollectionHelper.Append(params, sDefaultParameters);

            if (AppBuildConfig.DEBUG) {
                CollectionHelper.Append(params, sDebugLogParameters);
            }

            DebugLog.v(TAG, "libvlc arguments:");
            for (String par : params) {
                DebugLog.v(TAG, "    " + par);
            }

            sInstance.initEx(params.toArray(new String[params.size()]));
        }

        return sInstance;
    }

    static LibVLC getExistingInstance() {
        return sInstance;
    }

    public static void destroyExistingInstance() {
        if (sInstance != null) {
            sInstance.destroy();
            sInstance = null;
        }
    }

    /**
     * Constructor It is private because this class is a singleton.
     */
    private LibVLC() {
        mAout = new Aout();
    }

    /**
     * Destructor: It is bad practice to rely on them, so please don't forget to
     * call destroy() before exiting.
     */
    public void finalize() {
        if (mLibVlcInstance != 0) {
            DebugLog.d(TAG, "LibVLC is was destroyed yet before finalize()");
            destroy();
        }
    }

    /**
     * Give to LibVLC the surface to draw the video.
     * 
     * @param f
     *            the surface to draw
     */
    public native void setSurface(Surface f);

    /**
     *
     */
    public boolean useIOMX() {
        // nothing to do here
        // we choose iomx in mediaplayer
        return false;
    }

    public static synchronized void useIOMX(boolean enable) {
        // nothing to do here
        // we choose iomx in mediaplayer
    }

    public static synchronized void useIOMX(Context context) {
        // nothing to do here
        // we choose iomx in mediaplayer
    }

    /**
     * Initialize the libVLC class
     */
    private void initEx(String[] params) throws LibVlcException {
        DebugLog.v(TAG, "Initializing LibVLC");
        if (!mIsInitialized) {
            nativeInitEx(params);
            setEventManager(EventManager.getIntance());
            mIsInitialized = true;
        }
    }

    /**
     * Destroy this libVLC instance
     * 
     * @note You must call it before exiting
     */
    public void destroy() {
        DebugLog.v(TAG, "Destroying LibVLC instance");
        nativeDestroy();
        detachEventManager();
        mIsInitialized = false;
    }

    /**
     * Open the Java audio output. This function is called by the native code
     */
    public void initAout(int sampleRateInHz, int channels, int samples) {
        DebugLog.d(TAG, "Opening the java audio output");
        mAout.init(sampleRateInHz, channels, samples);
    }

    /**
     * Play an audio buffer taken from the native code This function is called
     * by the native code
     */
    public void playAudio(byte[] audioData, int bufferSize) {
        mAout.playBuffer(audioData, bufferSize);
    }

    /**
     * Close the Java audio output This function is called by the native code
     */
    public void closeAout() {
        DebugLog.d(TAG, "Closing the java audio output");
        mAout.release();
    }

    /**
     * Read a media.
     */
    public void readMediaEx(String mrl, String[] options) {
        DebugLog.v(TAG, "Reading " + mrl);

        DebugLog.v(TAG, "libvlcplayer options:");
        for (String opt : options) {
            DebugLog.v(TAG, "    " + opt);
        }

        readMediaEx(mLibVlcInstance, mrl, options);
    }

    public String[] readMediaMeta(String mrl) {
        return readMediaMeta(mLibVlcInstance, mrl);
    }

    public TrackInfo[] readTracksInfo(String mrl) {
        return readTracksInfo(mLibVlcInstance, mrl);
    }

    /**
     * Get a media thumbnail.
     */
    public byte[] getThumbnail(String mrl, int i_width, int i_height) {
        return getThumbnail(mLibVlcInstance, mrl, i_width, i_height);
    }

    /**
     * Return true if there is a video track in the file
     */
    public boolean hasVideoTrack(String mrl) {
        return hasVideoTrack(mLibVlcInstance, mrl);
    }

    /**
     * Return true if there is a video track in the file
     */
    public long getLengthFromLocation(String mrl) {
        return getLengthFromLocation(mLibVlcInstance, mrl);
    }

    /**
     * Initialize the libvlc C library
     * 
     * @return a pointer to the libvlc instance
     */
    private native void nativeInitEx(String[] params) throws LibVlcException;

    /**
     * Close the libvlc C library
     * 
     * @note mLibVlcInstance should be 0 after a call to destroy()
     */
    private native void nativeDestroy();

    /**
     * Read a media
     * 
     * @param instance
     *            : the instance of libVLC
     * @param mrl
     *            : the media mrl
     */
    private native void readMediaEx(int instance, String mrl, String[] options);

    /**
     * Return true if there is currently a running media player.
     */
    public native boolean hasMediaPlayer();

    /**
     * Returns true if any media is playing
     */
    public native boolean isPlaying();

    /**
     * Returns true if any media is seekable
     */
    public native boolean isSeekable();

    /**
     * Plays any loaded media
     */
    public native void play();

    /**
     * Pauses any playing media
     */
    public native void pause();

    /**
     * Stops any playing media
     */
    public native void stop();

    /**
     * Gets volume as integer
     */
    public native int getVolume();

    /**
     * Gets volume as integer
     * 
     * @param volume
     *            : Volume level passed as integer
     */
    public native int setVolume(int volume);

    /**
     * Gets the current movie time (in ms).
     * 
     * @return the movie time (in ms), or -1 if there is no media.
     */
    public native long getTime();

    /**
     * Sets the movie time (in ms), if any media is being played.
     * 
     * @param time
     *            : Time in ms.
     * @return the movie time (in ms), or -1 if there is no media.
     */
    public native long setTime(long time);

    /**
     * Gets the movie position.
     * 
     * @return the movie position, or -1 for any error.
     */
    public native float getPosition();

    /**
     * Sets the movie position.
     * 
     * @param pos
     *            : movie position.
     */
    public native void setPosition(float pos);

    /**
     * Gets current movie's length in ms.
     * 
     * @return the movie length (in ms), or -1 if there is no media.
     */
    public native long getLength();

    /**
     * Get the libVLC version
     * 
     * @return the libVLC version string
     */
    public native String version();

    /**
     * Get the libVLC compiler
     * 
     * @return the libVLC compiler string
     */
    public native String compiler();

    /**
     * Get the libVLC changeset
     * 
     * @return the libVLC changeset string
     */
    public native String changeset();

    /**
     * Get a media thumbnail.
     * 
     * @return a bytearray with the RGBA thumbnail data inside.
     */
    private native byte[] getThumbnail(int instance, String mrl, int i_width,
            int i_height);

    /**
     * Return true if there is a video track in the file
     */
    private native boolean hasVideoTrack(int instance, String mrl);

    private native String[] readMediaMeta(int instance, String mrl);

    private native TrackInfo[] readTracksInfo(int instance, String mrl);

    public native int getAudioTracksCount();

    public native String[] getAudioTrackDescription();

    public native int getAudioTrack();

    public native int setAudioTrack(int index);

    public native int getVideoTracksCount();

    public native int getSpuTracksCount();

    public native String nativeToURI(String path);

    /**
     * Return true if there is a video track in the file
     */
    private native long getLengthFromLocation(int instance, String mrl);

    private native void setEventManager(EventManager eventManager);

    private native void detachEventManager();
}
