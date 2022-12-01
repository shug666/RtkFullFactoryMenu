
package com.realtek.fullfactorymenu.api.manager;


/**
 * <b>TvFactoryManager class is for purpose of controlling factory module
 * from client APK.</b><br/>
 */
public class TvFactoryManager {
    private final static String TAG = "TvFactoryManager";

    /**
     * Non Linear Adjust Set Index
     */
    /** Non linear adjust set index: volume */
    public static final int NLA_VOLUME = 0;
    /** Non linear adjust set index: brightness */
    public static final int NLA_BRIGHTNESS = 1;
    /** Non linear adjust set index: contrast */
    public static final int NLA_CONTRAST = 2;
    /** Non linear adjust set index: saturation */
    public static final int NLA_SATURATION = 3;
    /** Non linear adjust set index: sharpness */
    public static final int NLA_SHARPNESS = 4;
    /** Non linear adjust set index: hue */
    public static final int NLA_HUE = 5;
    /** Non linear adjust set index: backlight */
    public static final int NLA_BACKLIGHT = 6;

    /**
     * Power On Logo Mode
     */
    /** Power on logo mode: off */
    public static final int POWERON_LOGO_OFF = 0;
    /** Power on logo mode: default */
    public static final int POWERON_LOGO_DEFAULT = 1;
    /** Power on logo mode: capture1 */
    public static final int POWERON_LOGO_CAPTURE1 = 2;
    /** Power on logo mode: capture2 */
    public static final int POWERON_LOGO_CAPTURE2 = 3;

    /**
     * Power On Music Mode
     */
    /** Power on music mode: off */
    public static final int POWERON_MUSIC_OFF = 0;
    /** Power on music mode: default */
    public static final int POWERON_MUSIC_DEFAULT = 1;
    /** Power on music mode: music one */
    public static final int POWERON_MUSIC_ONE = 2;

    /**
     * Power On Mode
     */
    /** Power on mode: secondary */
    public static final int ACON_POWERON_SECONDARY = 0;
    /** Power on mode: memory */
    public static final int ACON_POWERON_MEMORY = 1;
    /** Power on mode: direct */
    public static final int ACON_POWERON_DIRECT = 2;

    /**
     * ADC Setting Index
     */
    /** ADC setting index: VGA */
    public static final int ADC_SET_VGA = 0;
    /** ADC setting index: YPBPR_SD */
    public static final int ADC_SET_YPBPR_SD = 1;
    /** ADC setting index: YPBPR_HD */
    public static final int ADC_SET_YPBPR_HD = 2;
    /** ADC setting index: SCART_RGB */
    public static final int ADC_SET_SCART_RGB = 3;
    /** ADC setting index: YPBPR2_SD */
    public static final int ADC_SET_YPBPR2_SD = 4;
    /** ADC setting index: sYPBPR2_HD */
    public static final int ADC_SET_YPBPR2_HD = 5;
    /** ADC setting index: YPBPR3_SD */
    public static final int ADC_SET_YPBPR3_SD = 6;
    /** ADC setting index: YPBPR3_HD */
    public static final int ADC_SET_YPBPR3_HD = 7;

    public static final int ADC_AUTO_TUNE_RESULT_SUCCESS = 0;
    public static final int ADC_AUTO_TUNE_RESULT_FAIL = 1;
    public static final int ADC_AUTO_TUNE_RESULT_TIMEOUT = 2;

    public static final int EXPORT_SETTINGS_RESULT_SUCCESS = 0;
    public static final int EXPORT_SETTINGS_RESULT_FAIL = 1;
    public static final int EXPORT_SETTINGS_RESULT_TIMEOUT = 2;

    public static final int IMPORT_SETTINGS_RESULT_SUCCESS = 3;
    public static final int IMPORT_SETTINGS_RESULT_FAIL = 4;
    public static final int IMPORT_SETTINGS_RESULT_TIMEOUT = 5;

    /**
     * Screen Mute Color
     */
    /** Screen mute color: off */
    public static final int SCREEN_MUTE_OFF = 0;
    /** Screen mute color: white */
    public static final int SCREEN_MUTE_WHITE = 1;
    /** Screen mute color: red */
    public static final int SCREEN_MUTE_RED = 2;
    /** Screen mute color: green */
    public static final int SCREEN_MUTE_GREEN = 3;
    /** Screen mute color: blue */
    public static final int SCREEN_MUTE_BLUE = 4;
    /** Screen mute color: black */
    public static final int SCREEN_MUTE_BLACK = 5;

    /**
     * Video Mute Color
     */
    /** Video mute color: black */
    public static final int VIDEO_MUTE_COLOR_BLACK = 0;
    /** Video mute color: white */
    public static final int VIDEO_MUTE_COLOR_WHITE = 1;
    /** Video mute color: red */
    public static final int VIDEO_MUTE_COLOR_RED = 2;
    /** Video mute color: green */
    public static final int VIDEO_MUTE_COLOR_GREEN = 3;
    /** Video mute color: blue */
    public static final int VIDEO_MUTE_COLOR_BLUE = 4;
    /** Video mute color: user */
    public static final int VIDEO_MUTE_COLOR_USER = 5;
    /** Video mute color: number of defined mute colors */
    public static final int VIDEO_MUTE_COLOR_NUMBER = 6;

    /**
     * Non Standard Audio Hidev Mode
     */
    /** Hidev filter off */
    public static final int AUDIO_HIDEV_OFF = 0;
    /** Hidev filter bw level 1 */
    public static final int AUDIO_HIDEV_BW_LV1 = 1;
    /** Hidev filter bw level 2 */
    public static final int AUDIO_HIDEV_BW_LV2 = 2;
    /** Hidev filter bw level 3 */
    public static final int AUDIO_HIDEV_BW_LV3 = 3;
    /** Hidev filter bw level numbers */
    public static final int AUDIO_HIDEV_BW_MAX = 4;

    /** 3D self adapative level low */
    public static final int THREE_DIMENSION_SELFADAPTIVE_LEVEL_LOW = 0;

    /** 3D self adapative level middle */
    public static final int THREE_DIMENSION_SELFADAPTIVE_LEVEL_MIDDLE = 1;

    /** 3D self adapative level high */
    public static final int THREE_DIMENSION_SELFADAPTIVE_LEVEL_HIGH = 2;

    /**
     * Dtv Demod Type
     */
    /** Dtv demod type dvbt */
    public static final int DEMOD_DVB_T = 0;
    /** Dtv demod type dvbc */
    public static final int DEMOD_DVB_C = 1;
    /** Dtv demod type dvbs */
    public static final int DEMOD_DVB_S = 2;
    /** Dtv demod type dtmb */
    public static final int DEMOD_DTMB = 3;
    /** Dtv demod type dvbt2 */
    public static final int DEMOD_DVB_T2 = 4;
    /** Dtv demod type max */
    public static final int DEMOD_MAX = 5;
    /** Dtv demod type null */
    public static final int DEMOD_NULL = 5;

    /**
     * Peq Adjust Setting
     */
    /** Peq adjust corase max */
    public static final int PEQ_ADJUST_COARSE_MAX = 160;
    /** Peq adjust corase min */
    public static final int PEQ_ADJUST_COARSE_MIN = 1;
    /** Peq adjust fine max */
    public static final int PEQ_ADJUST_FINE_MAX = 99;
    /** Peq adjust fine min */
    public static final int PEQ_ADJUST_FINE_MIN = 0;
    /** Peq adjust gain max */
    public static final int PEQ_ADJUST_GAIN_MAX = 240;
    /** Peq adjust gain min */
    public static final int PEQ_ADJUST_GAIN_MIN = 0;
    /** Peq adjust qvalue max */
    public static final int PEQ_ADJUST_QVALUE_MAX = 160;
    /** Peq adjust qvalue min */
    public static final int PEQ_ADJUST_QVALUE_MIN = 5;

    static TvFactoryManager mInstance = null;


    public static final String DISPLAY_LOGO = "display_logo";
    public static final String STRING_ON = "on";
    public static final String STRING_OFF = "off";

    private TvFactoryManager() {
    }

    public static TvFactoryManager getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (TvFactoryManager.class) {
                if (mInstance == null) {
                    mInstance = new TvFactoryManager();
                }
            }
        }

        return mInstance;
    }


}
