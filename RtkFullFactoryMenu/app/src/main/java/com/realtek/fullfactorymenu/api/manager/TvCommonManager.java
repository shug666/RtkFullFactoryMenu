package com.realtek.fullfactorymenu.api.manager;

import static com.realtek.fullfactorymenu.utils.Constants.MANUFACTURER_TT;

import android.util.SparseArray;

import com.realtek.fullfactorymenu.utils.ByteTransformUtils;
import com.realtek.fullfactorymenu.utils.Constants;

/**
 * <b>TvCommonManager class is for purpose of general management
 * from client APK.</b><br/>
 */
public class TvCommonManager {
    private static final String TAG = "TvCommonManager";

    /**
     * Input sources.
     */
    public static final int INPUT_SOURCE_NONE = -1;
    public static final int INPUT_SOURCE_VGA = 0;
    public static final int INPUT_SOURCE_ATV = 1;
    public static final int INPUT_SOURCE_CVBS = 2;
    public static final int INPUT_SOURCE_CVBS2 = 3;
    public static final int INPUT_SOURCE_CVBS3 = 4;
    public static final int INPUT_SOURCE_CVBS4 = 5;
    public static final int INPUT_SOURCE_CVBS5 = 6;
    public static final int INPUT_SOURCE_CVBS6 = 7;
    public static final int INPUT_SOURCE_CVBS7 = 8;
    public static final int INPUT_SOURCE_CVBS8 = 9;
    public static final int INPUT_SOURCE_CVBS_MAX = 10;
    public static final int INPUT_SOURCE_SVIDEO = 11;
    public static final int INPUT_SOURCE_SVIDEO2 = 12;
    public static final int INPUT_SOURCE_SVIDEO3 = 13;
    public static final int INPUT_SOURCE_SVIDEO4 = 14;
    public static final int INPUT_SOURCE_SVIDEO_MAX = 15;
    public static final int INPUT_SOURCE_YPBPR = 16;
    public static final int INPUT_SOURCE_YPBPR2 = 17;
    public static final int INPUT_SOURCE_YPBPR3 = 18;
    public static final int INPUT_SOURCE_YPBPR_MAX = 19;
    public static final int INPUT_SOURCE_SCART = 20;
    public static final int INPUT_SOURCE_SCART2 = 21;
    public static final int INPUT_SOURCE_SCART_MAX = 22;
    public static final int INPUT_SOURCE_HDMI = 23;
    public static final int INPUT_SOURCE_HDMI2 = 24;
    public static final int INPUT_SOURCE_HDMI3 = 25;
    public static final int INPUT_SOURCE_HDMI4 = 26;
    public static final int INPUT_SOURCE_HDMI_MAX = 27;
    public static final int INPUT_SOURCE_DTV = 28;
    public static final int INPUT_SOURCE_DVI = 29;
    public static final int INPUT_SOURCE_DVI2 = 30;
    public static final int INPUT_SOURCE_DVI3 = 31;
    public static final int INPUT_SOURCE_DVI4 = 32;
    public static final int INPUT_SOURCE_DVI_MAX = 33;
    public static final int INPUT_SOURCE_STORAGE = 34;
    public static final int INPUT_SOURCE_KTV = 35;
    public static final int INPUT_SOURCE_JPEG = 36;
    public static final int INPUT_SOURCE_DTV2 = 37;
    public static final int INPUT_SOURCE_STORAGE2 = 38;
    public static final int INPUT_SOURCE_DTV3 = 39;
    public static final int INPUT_SOURCE_SCALER_OP = 40;
    public static final int INPUT_SOURCE_RVU = 41;
    public static final int INPUT_SOURCE_VGA2 = 42;
    public static final int INPUT_SOURCE_VGA3 = 43;
    public static final int INPUT_SOURCE_AUTO = 44;
    public static final int INPUT_SOURCE_DVBT = 45;
    public static final int INPUT_SOURCE_DVBC = 46;
    public static final int INPUT_SOURCE_DTMB = 47;
    public static final int INPUT_SOURCE_ATSC = 48;
    public static final int INPUT_SOURCE_DVBS = 49;
    public static final int INPUT_SOURCE_ISDB = 50;
    public static final int INPUT_SOURCE_NUM = 51;

    /**
     * Screen Mute types.
     */
    public static final int SCREEN_MUTE_BLACK = 0;
    public static final int SCREEN_MUTE_WHITE = 1;
    public static final int SCREEN_MUTE_RED = 2;
    public static final int SCREEN_MUTE_BLUE = 3;
    public static final int SCREEN_MUTE_GREEN = 4;
    public static final int SCREEN_MUTE_NUM = 5;

    /**
     * Modules belong to compiler flag catagory
     */
    /** Module: PIP */
    public static final int MODULE_PIP = 0;
    /** Module: TRAVELING */
    public static final int MODULE_TRAVELING = 1;
    /** Module: OFFLINE_DETECT */
    public static final int MODULE_OFFLINE_DETECT = 2;
    /** Module: PREVIEW_MODE */
    public static final int MODULE_PREVIEW_MODE = 3;
    /** Module: FREEVIEW_AU */
    public static final int MODULE_FREEVIEW_AU = 4;
    // TODO: deprecated MODULE_CC/MODULE_BRAZIL_CC after SN remove it
    /** Module: CC */
    public static final int MODULE_CC = 5;
    /** Module: BRAZIL_CC */
    public static final int MODULE_BRAZIL_CC = 6;
    /** Module: KOREAN_CC */
    public static final int MODULE_KOREAN_CC = 7;
    /** For ATSC system, enable ATSC_CC_ENABLE and NTSC_CC_ENABLE. */
    /** For ISDB system, enable ISDB_CC_ENABLE and NTSC_CC_ENABLE. */
    /** For ASIA_NTSC system, only enable NTSC_CC_ENABLE. */
    /** Module: ATSC_CC_ENABLE */
    public static final int MODULE_ATSC_CC_ENABLE = 8;
    /** Module: ISDB_CC_ENABLE */
    public static final int MODULE_ISDB_CC_ENABLE = 9;
    /** Module: NTSC_CC_ENABLE */
    public static final int MODULE_NTSC_CC_ENABLE = 10;
    /** Module: ATV_NTSC_ENABLE */
    public static final int MODULE_ATV_NTSC_ENABLE = 11;
    /** Module: ATV_PAL_ENABLE */
    public static final int MODULE_ATV_PAL_ENABLE = 12;
    /** Module: ATV_CHINA_ENABLE */
    public static final int MODULE_ATV_CHINA_ENABLE = 13;
    /** Module: ATV_CABLE_ENABLE */
    public static final int MODULE_ATV_CABLE_ENABLE = 14;
    /** Module: ATV_PAL_M_ENABLE */
    public static final int MODULE_ATV_PAL_M_ENABLE = 15;
    /** Module: HDMITX */
    public static final int MODULE_HDMITX = 16;
    /** Module: HBBTV */
    public static final int MODULE_HBBTV = 17;
    /** Module: DTV_ENABLE */
    public static final int MODULE_DTV_ENABLE = 18;
    /** Module: ATV_ENABLE */
    public static final int MODULE_ATV_ENABLE = 19;

    /**
     * Module not belong to compiler flag catagory
     */
    /** Start number of modules that are not compiler flags */
    public static final int MODULE_NOT_COMPILE_FLAG_START = 0x00001000;
    /** Module: ATV_MANUAL_TUNING */
    public static final int MODULE_TV_CONFIG_ATV_MANUAL_TUNING = MODULE_NOT_COMPILE_FLAG_START;
    /** Module: AUTO_HOH */
    public static final int MODULE_TV_CONFIG_AUTO_HOH = MODULE_NOT_COMPILE_FLAG_START + 1;
    /** Module: AUDIO_DESCRIPTION */
    public static final int MODULE_TV_CONFIG_AUDIO_DESCRIPTION = MODULE_NOT_COMPILE_FLAG_START + 2;
    /** Module: THREED_DEPTH */
    public static final int MODULE_TV_CONFIG_THREED_DEPTH = MODULE_NOT_COMPILE_FLAG_START + 3;
    /** Module: SELF_DETECT */
    public static final int MODULE_TV_CONFIG_SELF_DETECT = MODULE_NOT_COMPILE_FLAG_START + 4;
    /** Module: THREED_CONVERSION_TWODTOTHREED */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_TWODTOTHREED = MODULE_NOT_COMPILE_FLAG_START + 5;
    /** Module: THREED_CONVERSION_AUTO */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_AUTO = MODULE_NOT_COMPILE_FLAG_START + 6;
    /** Module: THREED_CONVERSION_PIXEL_ALTERNATIVE */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_PIXEL_ALTERNATIVE = MODULE_NOT_COMPILE_FLAG_START + 7;
    /** Module: THREED_CONVERSION_FRAME_ALTERNATIVE */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_FRAME_ALTERNATIVE = MODULE_NOT_COMPILE_FLAG_START + 8;
    /** Module: THREED_CONVERSION_CHECK_BOARD */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_CHECK_BOARD = MODULE_NOT_COMPILE_FLAG_START + 9;
    /** Module: THREED_TWOD_AUTO */
    public static final int MODULE_TV_CONFIG_THREED_TWOD_AUTO = MODULE_NOT_COMPILE_FLAG_START + 10;
    /** Module: THREED_TWOD_PIXEL_ALTERNATIVE */
    public static final int MODULE_TV_CONFIG_THREED_TWOD_PIXEL_ALTERNATIVE = MODULE_NOT_COMPILE_FLAG_START + 11;
    /** Module: THREED_TWOD_FRAME_ALTERNATIVE */
    public static final int MODULE_TV_CONFIG_THREED_TWOD_FRAME_ALTERNATIVE = MODULE_NOT_COMPILE_FLAG_START + 12;
    /** Module: THREED_TWOD_CHECK_BOARD */
    public static final int MODULE_TV_CONFIG_THREED_TWOD_CHECK_BOARD = MODULE_NOT_COMPILE_FLAG_START + 13;
    /** Module: INPUT_SOURCE_LOCK */
    public static final int MODULE_TV_CONFIG_INPUT_SOURCE_LOCK = MODULE_NOT_COMPILE_FLAG_START + 14;

    /** ATV Audio Mode */
    /* This value is mapping to ATV_AUDIOMODE_TYPE(supernova). */
    /** Audio Mode Invalid */
    public static final int ATV_AUDIOMODE_INVALID = 0;
    /** Audio Mode MONO */
    public static final int ATV_AUDIOMODE_MONO = 1;
    /** Audio Mode Forced MONO */
    public static final int ATV_AUDIOMODE_FORCED_MONO = 2;
    /** Audio Mode G Stereo */
    public static final int ATV_AUDIOMODE_G_STEREO = 3;
    /** Audio Mode K Stereo */
    public static final int ATV_AUDIOMODE_K_STEREO = 4;
    /** Audio Mode Mono SAP */
    public static final int ATV_AUDIOMODE_MONO_SAP = 5;
    /** Audio Mode Stereo SAP */
    public static final int ATV_AUDIOMODE_STEREO_SAP = 6;
    /** Audio Mode Dual A */
    public static final int ATV_AUDIOMODE_DUAL_A = 7;
    /** Audio Mode Dual B */
    public static final int ATV_AUDIOMODE_DUAL_B = 8;
    /** Audio Mode Dual AB */
    public static final int ATV_AUDIOMODE_DUAL_AB = 9;
    /** Audio Mode NICAM MONO */
    public static final int ATV_AUDIOMODE_NICAM_MONO = 10;
    /** Audio Mode NICAM Stereo */
    public static final int ATV_AUDIOMODE_NICAM_STEREO = 11;
    /** Audio Mode NICAM DUAL A */
    public static final int ATV_AUDIOMODE_NICAM_DUAL_A = 12;
    /** Audio Mode NICAM DUAL B */
    public static final int ATV_AUDIOMODE_NICAM_DUAL_B = 13;
    /** Audio Mode NICAM DUAL AB */
    public static final int ATV_AUDIOMODE_NICAM_DUAL_AB = 14;
    /** Audio Mode HIDEV MONO */
    public static final int ATV_AUDIOMODE_HIDEV_MONO = 15;
    /** Audio Mode left left */
    public static final int ATV_AUDIOMODE_LEFT_LEFT = 16;
    /** Audio Mode right right */
    public static final int ATV_AUDIOMODE_RIGHT_RIGHT = 17;
    /** Audio Mode left right */
    public static final int ATV_AUDIOMODE_LEFT_RIGHT = 18;

    /** Audio Function Return Value Type */
    /* This value is mapping to MSRV_SSSND_RET(supernova). */
    /** Return not Okay */
    public static final int AUDIO_RETURN_NOT_OK = 0;
    /** Return Okay */
    public static final int AUDIO_RETURN_OK = 1;
    /** Return unsupport format */
    public static final int AUDIO_RETURN_UNSUPPORT = 2;

    /**
     * Parameter of onPopupDialog Event for show Password Inputbox
     */
    public static final int POPUP_DIALOG_SHOW = 0;
    /**
     * Parameter of onPopupDialog Event for hide Password Inputbox
     */
    public static final int POPUP_DIALOG_HIDE = 1;

    /** Events */
    /** NIT auto update scan notification */
    public static final int EV_DTV_AUTO_UPDATE_SCAN = Constants.TVPLAYER_DTV_AUTO_UPDATE_SCAN;
    /** EPG update notification*/
    public static final int EV_EPG_UPDATE = Constants.TVPLAYER_EPG_UPDATE;

    /** NIT Event sub types */
    /** NIT update type - None */
    public static final int NIT_UPDATE_NONE = 0;
    /** NIT update type - Multiplexer add */
    public static final int NIT_UPDATE_MUX_ADD = 1;
    /** NIT update type - Frequency change */
    public static final int NIT_UPDATE_FREQ_CHANGE = 2;
    /** NIT update type - Multiplexer remove */
    public static final int NIT_UPDATE_MUX_REMOVE = 3;
    /** NIT update type - Cell remove */
    public static final int NIT_UPDATE_CELL_REMOVE = 4;

    /** HDMI EDID version */
    /** EDID version default */
    public static final int HDMI_EDID_DEFAULT = 0;
    /** EDID version 1.4 */
    public static final int HDMI_EDID_1_4 = 1;
    /** EDID version 2.0 */
    public static final int HDMI_EDID_2_0 = 2;

    /** Value of each element of HDMI EDID version list */
    /** EDID version unsupport */
    public static final int EDID_VERSION_UNSUPPORT = 0;
    /** EDID version support */
    public static final int EDID_VERSION_SUPPORT = 1;

    /** TV reset */
    public static final int TV_USER_RESET = 0;
    public static final int TV_FACTORY_RESET = 1;
    public static final int TV_PROGRAM_RESET = 2;
    public static final int TV_TV_REBOOT = 3;
    public static final int TV_PQ_RESET = 4;
    public static final int TV_OSD_RESET = 5;

    private final static int TV_DIALOG_EVENT_START = Constants.TV_DIALOG_EVENT_START;
    private final static int TV_DIALOG_EVENT_END = Constants.TV_DIALOG_EVENT_END;
    private final static int TV_SCART_EVENT_START = Constants.TV_SCART_EVENT_START;
    private final static int TV_SCART_EVENT_END = Constants.TV_SCART_EVENT_END;
    private final static int TV_SIGNAL_EVENT_START = Constants.TV_SIGNAL_EVENT_START;
    private final static int TV_SIGNAL_EVENT_END = Constants.TV_SIGNAL_EVENT_END;
    private final static int TV_UNITY_EVENT_START = Constants.TV_UNITY_EVENT_START;
    private final static int TV_UNITY_EVENT_END = Constants.TV_UNITY_EVENT_END;
    private final static int TV_SCREEN_SAVER_EVENT_START = Constants.TV_SCREEN_SAVER_EVENT_START;
    private final static int TV_SCREEN_SAVER_EVENT_END = Constants.TV_SCREEN_SAVER_EVENT_END;
    private final static int TV_4K_UHD_EVENT_START = Constants.TV_4K_UHD_EVENT_START;
    private final static int TV_4K_UHD_EVENT_END = Constants.TV_4K_UHD_EVENT_END;
    private final static int TV_PREVIEW_EVENT_START = Constants.TV_PREVIEW_EVENT_START;
    private final static int TV_PREVIEW_EVENT_END = Constants.TV_PREVIEW_EVENT_END;

    public static final String MIRROR_NORMAL = "NORMAL";

    public static final String MIRROR_FLIP = "FLIP";

    public static final String MIRROR_MIRROR = "MIRROR";

    public static final String MIRROR_MAF = "MAF";

    public static final String GET_PANELINFO = "GetPanelInfo";

    public static final String COMMAND_GET_PANEL_INFO = "COMMAND_GET_PANEL_INFO";

    public static final String COMMAND_SET_TI_MODE = "COMMAND_SET_TI_MODE";

    public static final String COMMAND_SET_SWAP_MODE = "COMMAND_SET_SWAP_MODE";

    public static final String COMMAND_GET_SOUND_ADJUST_INFO = "COMMAND_GET_SOUND_ADJUST_INFO";

    public static final String COMMAND_SET_AUDIO_OUT_VOLUME = "COMMAND_SET_AUDIO_OUT_VOLUME";

    public static final String COMMAND_SET_AVC_ENABLE = "COMMAND_SET_AVC_ENABLE";

    public static final String COMMAND_SET_AVC_THL = "COMMAND_SET_AVC_THL";

    public static final String COMMAND_SET_AVC_THL_MAX = "COMMAND_SET_AVC_THL_MAX";

    public static final String COMMAND_SET_DRC_ENABLE = "COMMAND_SET_DRC_ENABLE";

    public static final String COMMAND_SET_DRC_THL = "COMMAND_SET_DRC_THL";

    public static final String COMMAND_SET_DRC_THL_MAX = "COMMAND_SET_DRC_THL_MAX";

    public static final String COMMAND_SET_DIGITALAUDIO_OUT_VOLUME = "COMMAND_SET_DIGITALAUDIO_OUT_VOLUME";

    public static final String COMMAND_SET_EFFECT = "COMMAND_SET_EFFECT";

    public static final String COMMAND_SET_AUDIO_DELAY = "COMMAND_SET_AUDIO_DELAY";

    public static final String COMMAND_SET_AUDIO_NR = "COMMAND_SET_AUDIO_NR";

    public static final String COMMAND_SET_SURROUND = "COMMAND_SET_SURROUND";

    public static final String BIT_MODE = "BIT_MODE";

    public static final String MIRROR_MODE = "MIRROR_MODE";

    public static final String LAST_SHIFT_SIZE = "LAST_SHIFT_SIZE";

    public static final String LED_FLASH_ON_OFF = "LED_FLASH_ON_OFF";

    public static final String LCN_ON_OFF = "LCN_ON_OFF";

    public static final String CI_ENQUIRY_DIALOG = "CI_ENQUIRY_DIALOG";

    public static final String USER_VOLUME_SOURCE = "USER_VOLUME_SOURCE";

    public static final String USER_VOLUME_PRESCALE = "USER_VOLUME_PRESCALE";

    public static final String USER_VOLUME_CURVE_0 = "USER_VOLUME_CURVE_0";

    public static final String USER_VOLUME_CURVE_10 = "USER_VOLUME_CURVE_10";

    public static final String USER_VOLUME_CURVE_20 = "USER_VOLUME_CURVE_20";

    public static final String USER_VOLUME_CURVE_30 = "USER_VOLUME_CURVE_30";

    public static final String USER_VOLUME_CURVE_40 = "USER_VOLUME_CURVE_40";

    public static final String USER_VOLUME_CURVE_50 = "USER_VOLUME_CURVE_50";

    public static final String USER_VOLUME_CURVE_60 = "USER_VOLUME_CURVE_60";

    public static final String USER_VOLUME_CURVE_70 = "USER_VOLUME_CURVE_70";

    public static final String USER_VOLUME_CURVE_80 = "USER_VOLUME_CURVE_80";

    public static final String USER_VOLUME_CURVE_90 = "USER_VOLUME_CURVE_90";

    public static final String USER_VOLUME_CURVE_100 = "USER_VOLUME_CURVE_100";

    public static final String TELETEXT_LANGUAGE_DTV = "TELETEXT_LANGUAGE_DTV";

    public static final String TELETEXT_LANGUAGE_ATV = "TELETEXT_LANGUAGE_ATV";

    public static final String TOTAL_RUN_TIME = "TOTAL_RUN_TIME";

    public static final String WIPE_RUN_TIME = "WIPE_RUN_TIME";

    public static final String FIRST_CONNECT_TIME = "FIRST_CONNECT_TIME";

    public static final String AGING_MODE_TEST = "Misc_AgingMod_AgingModTest";

    public static final String GET_INI_FILE_TEST_MODE = ByteTransformUtils.asciiToString(MANUFACTURER_TT) + "_factory_test";

    public static final String UART_SWITCH_PORT = "UART_SWITCH_PORT";

    public static final String TELETEXT_ENABLED = "TELETEXT_ENABLED";

    public static final String BAND_WIDTH = "BAND_WIDTH";

    public static final String COMB_BRIGHTNESS = "COMB_BRIGHTNESS";

    public static final String COMB_CONTRAST = "COMB_CONTRAST";

    public static final String COMB_SATURATION = "COMB_SATURATION";

    public static final String DUAL_MODE = "DUAL_MODE";

    public static final String TPC = "TPC";

    public static final String CPC = "CPC";

    public static final String LEA = "LEA";

    public static final String Orbit = "Orbit";

    public static final String PLC = "PLC";

    public static final String AUDIO_DESCRIPTION_MAX_RANGE = "AUDIO_DESCRIPTION_MAX_RANGE";

    public static final String BACKGROUND_LIGHT = "BACKGROUND_LIGHT";

    public static final String GET_ARC_HDMI_CHANNEL = "GET_ARC_HDMI_CHANNEL";

    public static final String DDRSPREAD_RATIO = "Misc_DDRSpread_DDRSpreadRatio";

    public static final String EDID_HDMI1 = "EDID_HDMI1";

    public static final String EDID_HDMI2 = "EDID_HDMI2";

    public static final String EDID_HDMI3 = "EDID_HDMI3";

    public static final String EDID_VGA = "EDID_VGA";

    public static final String HDCP1_KEY = "HDCP1_KEY";

    public static final String HDCP_KSV = "HDCP_KSV";

    public static final String PANEL_WIDTH = "Misc_Panel_Width";

    public static final String PANEL_HEIGTH = "Misc_Panel_height";
    /**
     * When DTV notify to pop-up a dialog event received
     *
     * @see OnDialogEventListener
     */
    public final static int TV_DTV_READY_POPUP_DIALOG = Constants.TV_DTV_READY_POPUP_DIALOG;

    /**
     * When ATSC system notify to pop-up a dialog event received
     *
     * @see OnDialogEventListener
     */
    public final static int TV_ATSC_POPUP_DIALOG = Constants.TV_ATSC_POPUP_DIALOG;

    /**
     * When a osd scart mute notify event received
     *
     * @see OnScartEventListener
     */
    public final static int TV_SCART_MUTE_OSD_MODE = Constants.TV_SCART_MUTE_OSD_MODE;

    /**
     * When a signal unlock notify event received
     *
     * @see OnSignalEventListener
     */
    public final static int TV_SIGNAL_UNLOCK = Constants.TV_SIGNAL_UNLOCK;

    /**
     * When a signal lock notify event received
     *
     * @see OnSignalEventListener
     */
    public final static int TV_SIGNAL_LOCK = Constants.TV_SIGNAL_LOCK;

    /**
     * When a unity notify event received
     *
     * @see OnUnityEventListener
     */
    public final static int TV_UNITY_EVENT = Constants.TV_UNITY_EVENT;

    /**
     * When a screen save notify event received
     *
     * @see OnScreenSaverEventListener
     */
    public final static int TV_SCREEN_SAVER_MODE = Constants.TV_SCREEN_SAVER_MODE;

    /**
     * When a disable PIP under HDMI event received
     *
     * @see On4kUhdEventListener
     */
    public final static int TV_4K_UHD_HDMI_DISABLE_PIP = Constants.TV_4K_UHD_HDMI_DISABLE_PIP;

    /**
     * When a disable POP under HDMI event received
     *
     * @see On4kUhdEventListener
     */
    public final static int TV_4K_UHD_HDMI_DISABLE_POP = Constants.TV_4K_UHD_HDMI_DISABLE_POP;

    /**
     * When a disable dual view under HDMI event received
     *
     * @see On4kUhdEventListener
     */
    public final static int TV_4K_UHD_HDMI_DISABLE_DUAL_VIEW = Constants.TV_4K_UHD_HDMI_DISABLE_DUAL_VIEW;

    /**
     * When a disable traveling mode under HDMI event received
     *
     * @see On4kUhdEventListener
     */
    public final static int TV_4K_UHD_HDMI_DISABLE_TRAVELING_MODE = Constants.TV_4K_UHD_HDMI_DISABLE_TRAVELING_MODE;

    /**
     * When refresh preview mode event received
     *
     * @see OnPreviewEventListener
     */
    public final static int TV_REFRESH_PREVIEW_MODE_WINDOW = Constants.TV_REFRESH_PREVIEW_MODE_WINDOW;

    public final static String TVOS_COMMON_CMD_AGING_START = "StartLedRedToGreen";
    public final static String TVOS_COMMON_CMD_AGING_STOP = "StopLedRedToGreen";
    public final static String TVOS_COMMON_CMD_LOAD_SN_DIRECT = "LoadSNKey_direct";
    public final static String TVOS_COMMOM_CMD_SET_RUN_TIME = "UpdateRunningTime";

    public final static String VALUE = "value";

    public interface OnDialogEventListener {
        /**
         * Called when pop-up dialog event received.
         *
         * @param what the type of pop-up dialog event occurred:
         *          <ul>
         *          <li>{@link #TV_DTV_READY_POPUP_DIALOG}
         *          <li>{@link #TV_ATSC_POPUP_DIALOG}
         *          </ul>
         * @param arg1
         *          <ul>
         *          <li>for {@link #TV_DTV_READY_POPUP_DIALOG}: status
         *          <li>for {@link #TV_ATSC_POPUP_DIALOG}: mode
         *          </ul>
         * @param arg2
         *          <ul>
         *          <li>for {@link #TV_ATSC_POPUP_DIALOG}: type
         *          <li>for other events: reserved
         *          </ul>
         * @param obj reserved
         * @return reserved
         */
        boolean onDialogEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnScartEventListener {
        /**
         * Called when scart event received.
         *
         * @param what the type of scart event occurred:
         *          <ul>
         *          <li>{@link #TV_SCART_MUTE_OSD_MODE}
         *          </ul>
         * @param arg1 reserved
         * @param arg2 reserved
         * @param obj reserved
         * @return reserved
         */
        boolean onScartEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnSignalEventListener {
        /**
         * Called when signal event received.
         *
         * @param what the type of signal event occurred:
         *          <ul>
         *          <li>{@link #TV_SIGNAL_UNLOCK}
         *          <li>{@link #TV_SIGNAL_LOCK}
         *          </ul>
         * @param arg1 reserved
         * @param arg2 reserved
         * @param obj reserved
         * @return reserved
         */
        boolean onSignalEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnUnityEventListener {
        /**
         * Called when unity event received.
         *
         * @param what the type of unity event occurred:
         *          <ul>
         *          <li>{@link #TV_UNITY_EVENT}
         *          </ul>
         * @param arg1 option code
         * @param arg2 parameter
         * @param obj reserved
         * @return reserved
         */
        boolean onUnityEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnScreenSaverEventListener {
        /**
         * Called when screen saver event received.
         *
         * @param what the type of screen saver event occurred:
         *          <ul>
         *          <li>{@link #TV_SCREEN_SAVER_MODE}
         *          </ul>
         * @param arg1 mode
         * @param arg2 reserved
         * @param obj reserved
         * @return reserved
         */
        boolean onScreenSaverEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface On4kUhdEventListener {
        /**
         * Called when 4k UHD event received.
         *
         * @param what the type of 4k UHD event occurred:
         *          <ul>
         *          <li>{@link #TV_4K_UHD_HDMI_DISABLE_PIP}
         *          <li>{@link #TV_4K_UHD_HDMI_DISABLE_POP}
         *          <li>{@link #TV_4K_UHD_HDMI_DISABLE_DUAL_VIEW}
         *          <li>{@link #TV_4K_UHD_HDMI_DISABLE_DUAL_VIEW}
         *          </ul>
         * @param arg1 reserved
         * @param arg2 reserved
         * @param obj reserved
         * @return reserved
         */
        boolean on4kUhdEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnPreviewEventListener {
        /**
         * Called when preview mode event received.
         *
         * @param what the type of preview event occurred:
         *          <ul>
         *          <li>{@link #TV_REFRESH_PREVIEW_MODE_WINDOW}
         *          </ul>
         * @param arg1 input source
         * @param arg2 previewInfo
         * @param obj reserved
         * @return reserved
         */
        boolean onPreviewEvent(int what, int arg1, int arg2, Object obj);
    }


    private static final SparseArray<String> sModuleMapTable = new SparseArray<String>();

    static {
        initModuleMap(MODULE_PIP, "PIP_ENABLE");
        initModuleMap(MODULE_TRAVELING, "TRAVELING_ENABLE");
        initModuleMap(MODULE_OFFLINE_DETECT, "OFL_DET");
        initModuleMap(MODULE_PREVIEW_MODE, "PREVIEW_MODE_ENABLE");
        initModuleMap(MODULE_FREEVIEW_AU, "FREEVIEW_AU_ENABLE");
        initModuleMap(MODULE_CC, "CC_ENABLE");
        initModuleMap(MODULE_BRAZIL_CC, "BRAZIL_CC_ENABLE");
        initModuleMap(MODULE_KOREAN_CC, "KOREAN_CC_ENABLE");
        initModuleMap(MODULE_HDMITX, "HDMITX_ENABLE");
        initModuleMap(MODULE_HBBTV, "HBBTV_ENABLE");
        initModuleMap(MODULE_DTV_ENABLE, "DTV_ENABLE");
        initModuleMap(MODULE_ATV_ENABLE, "ATV_ENABLE");
        initModuleMap(MODULE_ATSC_CC_ENABLE, "ATSC_CC_ENABLE");
        initModuleMap(MODULE_ISDB_CC_ENABLE, "ISDB_CC_ENABLE");
        initModuleMap(MODULE_NTSC_CC_ENABLE, "NTSC_CC_ENABLE");
        initModuleMap(MODULE_ATV_NTSC_ENABLE, "ATV_NTSC_ENABLE");
        initModuleMap(MODULE_ATV_CABLE_ENABLE, "ATV_CABLE_ENABLE");
        initModuleMap(MODULE_ATV_PAL_ENABLE, "ATV_PAL_ENABLE");
        initModuleMap(MODULE_ATV_CHINA_ENABLE, "ATV_CHINA_ENABLE");
        initModuleMap(MODULE_ATV_PAL_M_ENABLE, "ATV_PAL_M_ENABLE");
        initModuleMap(MODULE_TV_CONFIG_ATV_MANUAL_TUNING, "ATV_MANUAL_TUNING_ENABLE");
        initModuleMap(MODULE_TV_CONFIG_AUTO_HOH, "AUTO_HOH_ENABLE");
        initModuleMap(MODULE_TV_CONFIG_AUDIO_DESCRIPTION, "AUDIO_DESCRIPTION_ENABLE");
        initModuleMap(MODULE_TV_CONFIG_THREED_DEPTH, "THREED_DEPTH_ENABLE");
        initModuleMap(MODULE_TV_CONFIG_SELF_DETECT, "SELF_DETECT_ENABLE");
        initModuleMap(MODULE_TV_CONFIG_THREED_CONVERSION_TWODTOTHREED, "THREED_CONVERSION_TWODTOTHREED");
        initModuleMap(MODULE_TV_CONFIG_THREED_CONVERSION_AUTO, "THREED_CONVERSION_AUTO");
        initModuleMap(MODULE_TV_CONFIG_THREED_CONVERSION_PIXEL_ALTERNATIVE, "THREED_CONVERSION_PIXEL_ALTERNATIVE");
        initModuleMap(MODULE_TV_CONFIG_THREED_CONVERSION_FRAME_ALTERNATIVE, "THREED_CONVERSION_FRAME_ALTERNATIVE");
        initModuleMap(MODULE_TV_CONFIG_THREED_CONVERSION_CHECK_BOARD, "THREED_CONVERSION_CHECK_BOARD");
        initModuleMap(MODULE_TV_CONFIG_THREED_TWOD_AUTO, "THREED_TWOD_AUTO");
        initModuleMap(MODULE_TV_CONFIG_THREED_TWOD_PIXEL_ALTERNATIVE, "THREED_TWOD_PIXEL_ALTERNATIVE");
        initModuleMap(MODULE_TV_CONFIG_THREED_TWOD_FRAME_ALTERNATIVE, "THREED_TWOD_FRAME_ALTERNATIVE");
        initModuleMap(MODULE_TV_CONFIG_THREED_TWOD_CHECK_BOARD, "THREED_TWOD_CHECK_BOARD");
        initModuleMap(MODULE_TV_CONFIG_INPUT_SOURCE_LOCK, "INPUT_SOURCE_LOCK");
    }



    private static void initModuleMap(int module, String name) {
        sModuleMapTable.put(module, name);
    }


}
