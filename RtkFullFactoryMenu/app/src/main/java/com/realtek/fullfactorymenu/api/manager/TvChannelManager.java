
package com.realtek.fullfactorymenu.api.manager;


import com.realtek.fullfactorymenu.utils.Constants;

import java.util.ArrayList;

/**
 * <b>TvChannelManager class is for purpose of controlling common channel
 * management from client APK.</b><br/>
 */
public class TvChannelManager {
    private final static String TAG = "TvChannelManager";

    /**
     * TODO
     *
     * @see OnChannelInfoEventListener
     */
    public final static int TVPLAYER_IMPORT_CHANNEL_COMPLETED = Constants.TVPLAYER_IMPORT_CHANNEL_COMPLETED;



    protected static TvChannelManager mInstance = null;

    /**
     * ATV only scan mode.
     */
    public static final int TV_SCAN_ATV = 0;

    /**
     * DTV only scan mode.
     */
    public static final int TV_SCAN_DTV = 1;

    /**
     * ATV and DTV scan mode.
     */
    public static final int TV_SCAN_ALL = 2;

    /**
     * Service types
     */
    /** Service Type : None */
    public static final int SERVICE_TYPE_NONE = 1 << 0;
    /** Service Type : ATV */
    public static final int SERVICE_TYPE_ATV = 1 << 1;
    /** Service Type : DTV */
    public static final int SERVICE_TYPE_DTV = 1 << 2;
    /** Service Type : Radio */
    public static final int SERVICE_TYPE_RADIO = 1 << 3;
    /** Service Type : Data */
    public static final int SERVICE_TYPE_DATA = 1 << 4;

    public static final int SERVICE_TYPE_DTV_ALL = SERVICE_TYPE_DTV | SERVICE_TYPE_RADIO | SERVICE_TYPE_DATA;
    public static final int SERVICE_TYPE_ATV_ALL = SERVICE_TYPE_ATV;
    public static final int SERVICE_TYPE_TV_ALL = SERVICE_TYPE_ATV | SERVICE_TYPE_DTV_ALL;



    // default tuning scan type
    private static int mTuningScanType = TV_SCAN_ATV;

    /**
    * First Service Input types
    */
    // First service type is ATV
    public static final int FIRST_SERVICE_INPUT_SOURCE_TYPE_ATV = 1 << 1;
    // First service type is DTV
    public static final int FIRST_SERVICE_INPUT_SOURCE_TYPE_DVBT = 1 << 2;
    public static final int FIRST_SERVICE_INPUT_SOURCE_TYPE_DVBC = 1 << 3;
    public static final int FIRST_SERVICE_INPUT_SOURCE_TYPE_DVBS = 1 << 4;
    public static final int FIRST_SERVICE_INPUT_SOURCE_TYPE_ATSC = 1 << 5;
    public static final int FIRST_SERVICE_INPUT_SOURCE_TYPE_ISDB = 1 << 6;
    public static final int FIRST_SERVICE_INPUT_SOURCE_TYPE_DTMB = 1 << 7;
    public static final int FIRST_SERVICE_INPUT_SOURCE_TYPE_ALL = FIRST_SERVICE_INPUT_SOURCE_TYPE_ATV
            | FIRST_SERVICE_INPUT_SOURCE_TYPE_DVBT | FIRST_SERVICE_INPUT_SOURCE_TYPE_DVBC
            | FIRST_SERVICE_INPUT_SOURCE_TYPE_DVBS | FIRST_SERVICE_INPUT_SOURCE_TYPE_ATSC
            | FIRST_SERVICE_INPUT_SOURCE_TYPE_ISDB | FIRST_SERVICE_INPUT_SOURCE_TYPE_DTMB;

    /**
    * First Service types.
    */
    // Boot type is on time function of EPG
    public static final int FIRST_SERVICE_ON_TIME_BOOT = 0;
    // Boot type is AC or DC
    public static final int FIRST_SERVICE_AC_DC_BOOT = 1;
    // Auto scan type
    public static final int FIRST_SERVICE_AUTO_SCAN = 2;
    // Menu scan type
    public static final int FIRST_SERVICE_MENU_SCAN = 3;
    // DEFAULT type
    public static final int FIRST_SERVICE_DEFAULT = FIRST_SERVICE_AC_DC_BOOT;

    /**
    * ATV Audio System Standard
    */
    /* This value is mapping to ATV_SYSTEM_STANDARDS(supernova) */
    /** ATV audio standard : BG
     * @deprecate Use {@link ATV_SOUND_SYSTEM_BG}.
     */
    @Deprecated
    public static final int ATV_AUDIO_STANDARD_BG = 0;
    /** ATV audio standard : DK
     * @deprecate Use {@link ATV_SOUND_SYSTEM_DK}.
     */
    @Deprecated
    public static final int ATV_AUDIO_STANDARD_DK = 1;
    /** ATV audio standard : I
     * @deprecate Use {@link ATV_SOUND_SYSTEM_I}.
     */
    @Deprecated
    public static final int ATV_AUDIO_STANDARD_I = 2;
    /** ATV audio standard : L
     * @deprecate Use {@link ATV_SOUND_SYSTEM_L}.
     */
    @Deprecated
    public static final int ATV_AUDIO_STANDARD_L = 3;
    /** ATV audio standard : M
     * @deprecate Use {@link ATV_SOUND_SYSTEM_M}.
     */
    @Deprecated
    public static final int ATV_AUDIO_STANDARD_M = 4;


    public interface OnChannelInfoEventListener {

        boolean onChannelInfoEvent(int what, int arg1, int arg2, Object obj);

        boolean onAtvProgramInfoReady(int what, int arg1, int arg2, Object obj);

        boolean onDtvProgramInfoReady(int what, int arg1, int arg2, Object obj);

        boolean onTvProgramInfoReady(int what, int arg1, int arg2, Object obj);
    }

    private ArrayList<OnChannelInfoEventListener> mChannelInfoEventListeners = new ArrayList<OnChannelInfoEventListener>();

    public TvChannelManager() {}

    public static TvChannelManager getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (TvChannelManager.class) {
                if (mInstance == null) {
                    mInstance = new TvChannelManager();
                }
            }
        }

        return mInstance;
    }
    /**
     * Register ChannelInfo event listener. Your listener will be triggered when the events
     * posted from native code.
     * Note: Remeber to unregister the listener before your application destroyed.
     *
     * @param listener OnChannelInfoEventListener
     * @return TRUE - register success, FALSE - register fail.
     */
    public boolean registerOnChannelInfoEventListener(OnChannelInfoEventListener listener) {
        synchronized (mChannelInfoEventListeners) {
            mChannelInfoEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unregister ChannelInfo event listener from service.
     *
     * @param listener OnChannelInfoEventListener
     * @return TRUE - unregister success, FALSE - unregister fail.
     */
    public boolean unregisterOnChannelInfoEventListener(OnChannelInfoEventListener listener) {
        synchronized (mChannelInfoEventListeners) {
            mChannelInfoEventListeners.remove(listener);
        }
        return true;
    }


}
