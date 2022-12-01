package com.realtek.fullfactorymenu.api.manager;


/**
 * <b>TvAudioManager class is for purpose of controlling audio module
 * from client APK.</b><br/>
 */
public class TvAudioManager {
    private final static String TAG = "TvAudioManager";

    // sound Eqband
    public static final int AUDIO_EQBAND_0 = 0;

    public static final int AUDIO_EQBAND_1 = 1;

    public static final int AUDIO_EQBAND_2 = 2;

    public static final int AUDIO_EQBAND_3 = 3;

    public static final int AUDIO_EQBAND_4 = 4;

    public static final int AUDIO_EQBAND_5 = 5;

    public static final int AUDIO_EQBAND_6 = 6;


    private static TvAudioManager mInstance = null;


    public static TvAudioManager getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (TvAudioManager.class) {
                if (mInstance == null) {
                    mInstance = new TvAudioManager();
                }
            }
        }
        return mInstance;
    }


}
