package com.realtek.fullfactorymenu.utils;

import android.os.SystemProperties;
import android.text.format.DateUtils;

public final class Constants {

    public static final int TVCA_IPPV_DIALOG_EVENT_START = 0;
    public static final int TVCA_IPPV_SHOW_BUY_DIALOG = 1;
    public static final int TVCA_IPPV_HIDE_DIALOG = 2;
    public static final int TVCA_IPPV_DIALOG_EVENT_END = 999;

    public static final int TVCA_EMAIL_EVENT_START = 1000;
    public static final int TVCA_EMAIL_NOTIFY_ICON = 1001;
    public static final int TVCA_EMAIL_EVENT_END = 1999;

    public static final int TVCA_OSD_MESSAGE_EVENT_START = 2000;
    public static final int TVCA_SHOW_OSD_MESSAGE = 2001;
    public static final int TVCA_HIDE_OSD_MESSAGE = 2002;
    public static final int TVCA_SHOW_BUY_MESSAGE = 2003;
    public static final int TVCA_SHOW_FINGER_MESSAGE = 2004;
    public static final int TVCA_SHOW_PROGRESS_STRIP = 2005;
    public static final int TVCA_OSD_MESSAGE_EVENT_END = 2999;

    public static final int TVCA_REQUEST_EVENT_START = 3000;
    public static final int TVCA_REQUEST_FEEDING = 3001;
    public static final int TVCA_ACTION_REQUEST = 3002;
    public static final int TVCA_REQUEST_EVENT_END = 3999;

    public static final int TVCA_ENTITLE_EVENT_START = 4000;
    public static final int TVCA_ENTITLE_CHANGED = 4001;
    public static final int TVCA_ENTITLE_EVENT_END = 4999;

    public static final int TVCA_DETITLE_EVENT_START = 5000;
    public static final int TVCA_DETITLE_RECEIVED = 5001;
    public static final int TVCA_DETITLE_EVENT_END = 5999;

    public static final int TVCA_LOCK_EVENT_START = 6000;
    public static final int TVCA_LOCK_SERVICE = 6001;
    public static final int TVCA_UNLOCK_SERVICE = 6002;
    public static final int TVCA_LOCK_EVENT_END = 6999;

    public static final int TVCA_OTA_EVENT_START = 7000;
    public static final int TVCA_OTA_STATE = 7001;
    public static final int TVCA_OTA_EVENT_END = 7999;


    //PlayerImpl
    public static final int TVPLAYER_DTV_SCAN_EVENT_START = 0;
    public static final int TVPLAYER_DTV_AUTO_TUNING_SCAN_INFO = 1;
    public static final int TVPLAYER_DTV_SCAN_EVENT_END = 999;

    public static final int TVPLAYER_ATV_SCAN_EVENT_START = 1000;
    public static final int TVPLAYER_ATV_MANUAL_TUNING_SCAN_INFO = 1001;
    public static final int TVPLAYER_ATV_AUTO_TUNING_SCAN_INFO = 1002;
    public static final int TVPLAYER_ATV_MANUAL_TUNING_LOCK_SINGAL = 1003;
    public static final int TVPLAYER_ATV_SCAN_EVENT_END = 1999;

    public static final int TVPLAYER_CHANNEL_INFO_EVENT_START = 2000;
    public static final int TVPLAYER_DTV_CHANNEL_NAME_READY = 2001;
    public static final int TVPLAYER_PROGRAM_INFO_READY = 2002;
    public static final int TVPLAYER_DTV_CHANNEL_INFO_UPDATE = 2003;
    public static final int TVPLAYER_TS_CHANGE = 2004;
    public static final int TVPLAYER_DTV_PSIP_TS_UPDATE = 2005;
    public static final int TVPLAYER_DTV_PRI_COMPONENT_MISSING = 2006;
    public static final int TVPLAYER_CHANNEL_LIST_UPDATE = 2007;
    public static final int TVPLAYER_IMPORT_CHANNEL_COMPLETED = 2008;
    public static final int TVPLAYER_CHANNEL_INFO_EVENT_END = 2999;

    public static final int TVPLAYER_EPG_EVENT_START = 3000;
    public static final int TVPLAYER_EPG_TIMER_SIMULCAST = 3001;
    public static final int TVPLAYER_EPG_UPDATE_LIST = 3002;
    public static final int TVPLAYER_EPG_UPDATE = 3003;
    public static final int TVPLAYER_EPG_EVENT_END = 3999;

    public static final int TVPLAYER_HBBTV_EVENT_START = 4000;
    public static final int TVPLAYER_HBBTV_STATUS_MODE = 4001;
    public static final int TVPLAYER_HBBTV_UI_EVENT = 4002;
    public static final int TVPLAYER_HBBTV_EVENT_END = 4999;

    public static final int TVPLAYER_MHEG5_EVENT_START = 5000;
    public static final int TVPLAYER_MHEG5_STATUS_MODE = 5001;
    public static final int TVPLAYER_MHEG5_RETURN_KEY = 5002;
    public static final int TVPLAYER_MHEG5_EVENT_HANDLER = 5003;
    public static final int TVPLAYER_MHEG5_EVENT_END = 5999;

    public static final int TVPLAYER_RESCAN_EVENT_START = 6000;
    public static final int TVPLAYER_DTV_AUTO_UPDATE_SCAN = 6001;
    public static final int TVPLAYER_POPUP_SCAN_DIALOG_LOSS_SIGNAL = 6002;
    public static final int TVPLAYER_POPUP_SCAN_DIALOG_NEW_MULTIPLEX = 6003;
    public static final int TVPLAYER_POPUP_SCAN_DIALOG_FREQ_CHANGE = 6004;
    public static final int TVPLAYER_RESCAN_EVENT_END = 6999;

    public static final int TVPLAYER_TELETEXT_EVENT_START = 7000;
    public static final int TVPLAYER_CHANGE_TTX_STATUS = 7001;
    public static final int TVPLAYER_TELETEXT_EVENT_END = 7999;

    public static final int TVPLAYER_AUDIO_MODE_CHANGE_EVENT_START = 8000;
    public static final int TVPLAYER_AUDIO_MODE_CHANGE = 8001;
    public static final int TVPLAYER_AUDIO_MODE_CHANGE_EVENT_END = 8999;

    public static final int TVPLAYER_OAD_EVENT_START = 9000;
    public static final int TVPLAYER_OAD_TIMEOUT = 9001;
    public static final int TVPLAYER_OAD_HANDLER = 9002;
    public static final int TVPLAYER_OAD_DOWNLOAD = 9003;
    public static final int TVPLAYER_OAD_EVENT_END = 9999;

    public static final int TVPLAYER_GINGA_EVENT_START = 10000;
    public static final int TVPLAYER_GINGA_STATUS_MODE = 10001;
    public static final int TVPLAYER_GINGA_EVENT_END = 10999;

    public static final int TVPLAYER_SIGNAL_EVENT_START = 11000;
    public static final int TVPLAYER_SIGNAL_LOCK = 11001;
    public static final int TVPLAYER_SIGNAL_UNLOCK = 11002;
    public static final int TVPLAYER_SIGNAL_EVENT_END = 11999;

    public static final int TVPLAYER_CI_EVENT_START = 12000;
    public static final int TVPLAYER_CI_LOAD_CREDENTIAL_FAIL = 12001;
    public static final int TVPLAYER_CI_UI_OP_REFRESH_QUERY = 12002;
    public static final int TVPLAYER_CI_UI_OP_SERVICE_LIST = 12003;
    public static final int TVPLAYER_CI_UI_OP_EXIT_SERVICE_LIST = 12004;
    public static final int TVPLAYER_CI_EVENT_END = 12999;

    public static final int TVPLAYER_SCREEN_SAVER_EVENT_START = 13000;
    public static final int TVPLAYER_SCREEN_SAVER_MODE = 13001;
    public static final int TVPLAYER_SCREEN_SAVER_EVENT_END = 13999;

    public static final int TVPLAYER_PROGRAM_BLOCK_DIALOG_EVENT_START = 14000;
    public static final int TVPLAYER_POPUP_DIALOG = 14001;
    public static final int TVPLAYER_PROGRAM_BLOCK_DIALOG_EVENT_END = 14999;

    public static final int TVPLAYER_PVR_EVENT_START = 15000;
    public static final int TVPLAYER_PVR_PLAYBACK_TIME = 15001;
    public static final int TVPLAYER_PVR_PLAYBACK_SPEED_CHANGE = 15002;
    public static final int TVPLAYER_PVR_PLAYBACK_STOP = 15003;
    public static final int TVPLAYER_PVR_PLAYBACK_BEGIN = 15004;
    public static final int TVPLAYER_PVR_RECORD_TIME = 15005;
    public static final int TVPLAYER_PVR_RECORD_SIZE = 15006;
    public static final int TVPLAYER_PVR_RECORD_STOP = 15007;
    public static final int TVPLAYER_PVR_TIMESHIFT_OVERWRITE_BEFORE = 15008;
    public static final int TVPLAYER_PVR_TIMESHIFT_OVERWRITE_AFTER = 15009;
    public static final int TVPLAYER_PVR_OVERRUN = 15010;
    public static final int TVPLAYER_PVR_USB_REMOVED = 15011;
    public static final int TVPLAYER_PVR_CI_PLUS_PROTECTION = 15012;
    public static final int TVPLAYER_PVR_CI_PLUS_RETENTION_LIMIT_UPDATE = 15013;
    public static final int TVPLAYER_PVR_PARENTAL_CONTROL = 15014;
    public static final int TVPLAYER_PVR_ALWAYS_TIMESHIFT_PROGRAM_READY = 15015;
    public static final int TVPLAYER_PVR_ALWAYS_TIMESHIFT_PROGRAM_NOT_READY = 15016;
    public static final int TVPLAYER_RCT_PRESENCE = 15017;
    public static final int TVPLAYER_PVR_FILE_DELETED = 15018;
    public static final int TVPLAYER_PVR_EVENT_END = 15999;

    public static final int TVPLAYER_EMERGENCY_ALERT_EVENT_START = 16000;
    public static final int TVPLAYER_EMERGENCY_ALERT = 16001;
    public static final int TVPLAYER_EMERGENCY_ALERT_EVENT_END = 16999;

    public static final int TVPLAYER_4K_UHD_EVENT_START = 17000;
    public static final int TVPLAYER_4K_UHD_HDMI_DISABLE_PIP = 17001;
    public static final int TVPLAYER_4K_UHD_HDMI_DISABLE_POP = 17002;
    public static final int TVPLAYER_4K_UHD_HDMI_DISABLE_DUAL_VIEW = 17003;
    public static final int TVPLAYER_4K_UHD_HDMI_DISABLE_TRAVELING_MODE = 17004;
    public static final int TVPLAYER_4K_UHD_EVENT_END = 17999;

    public static final int TVPLAYER_INPUT_SOURCE_EVENT_START = 18000;
    public static final int TVPLAYER_INPUT_SOURCE_LOCK = 18001;
    public static final int TVPLAYER_INPUT_SOURCE_END = 18999;


    //PvrManager
    public static final int TVPVR_USB_EVENT_START = 0;
    public static final int TVPVR_NOTIFY_USB_INSERTED = 1;
    public static final int TVPVR_NOTIFY_USB_REMOVED = 2;
    public static final int TVPVR_NOTIFY_USB_NOT_MOUNTED = 3;
    public static final int TVPVR_USB_EVENT_END = 999;

    public static final int TVPVR_FORMAT_EVENT_START = 1000;
    public static final int TVPVR_NOTIFY_FORMAT_FINISHED = 1001;
    public static final int TVPVR_FORMAT_EVENT_END = 1999;

    public static final int TVPVR_PLAYBACK_EVENT_START = 2000;
    public static final int TVPVR_NOTIFY_PLAYBACK_STOP = 2001;
    public static final int TVPVR_NOTIFY_PLAYBACK_BEGIN = 2002;
    public static final int TVPVR_PLAYBACK_EVENT_END = 2999;


    //TvManager
    public static final int TV_DIALOG_EVENT_START = 0;
    public static final int TV_DTV_READY_POPUP_DIALOG = 1;
    public static final int TV_ATSC_POPUP_DIALOG = 2;
    public static final int TV_DIALOG_EVENT_END = 999;

    public static final int TV_SCART_EVENT_START = 1000;
    public static final int TV_SCART_MUTE_OSD_MODE = 1001;
    public static final int TV_SCART_EVENT_END = 1999;

    public static final int TV_SIGNAL_EVENT_START = 2000;
    public static final int TV_SIGNAL_UNLOCK = 2001;
    public static final int TV_SIGNAL_LOCK = 2002;
    public static final int TV_SIGNAL_EVENT_END = 2999;

    public static final int TV_UNITY_EVENT_START = 3000;
    public static final int TV_UNITY_EVENT = 3001;
    public static final int TV_UNITY_EVENT_END = 3999;

    public static final int TV_SCREEN_SAVER_EVENT_START = 4000;
    public static final int TV_SCREEN_SAVER_MODE = 4001;
    public static final int TV_SCREEN_SAVER_EVENT_END = 4999;

    public static final int TV_4K_UHD_EVENT_START = 5000;
    public static final int TV_4K_UHD_HDMI_DISABLE_PIP = 5001;
    public static final int TV_4K_UHD_HDMI_DISABLE_POP = 5002;
    public static final int TV_4K_UHD_HDMI_DISABLE_DUAL_VIEW = 5003;
    public static final int TV_4K_UHD_HDMI_DISABLE_TRAVELING_MODE = 5004;
    public static final int TV_4K_UHD_EVENT_END = 5999;

    public static final int TV_PREVIEW_EVENT_START = 6000;
    public static final int TV_REFRESH_PREVIEW_MODE_WINDOW = 6001;
    public static final int TV_PREVIEW_EVENT_END = 6999;

    public static final int TV_BLUETOOTH_STATUS = 8000;

    public static final int SETTING_CHANGE_PRIMARY_AUDIO_LANG_CHANGE = 0;
    public static final int SETTING_CHANGE_SECONDARY_AUDIO_LANG_CHANGE = 1;
    public static final int SETTING_CHANGE_AUDIO_SPDIF_MODE_CHANGE = 2;
    public static final int SETTING_CHANGE_PRIMARY_SUBTITLE_LANG_CHANGE = 3;
    public static final int SETTING_CHANGE_SECONDARY_SUBTITLE_LANG_CHANGE = 4;
    public static final int SETTING_CHANGE_HEARING_IMPAIRED_CHANGE = 4;
    public static final int SETTING_CHANGE_SUBTITLE_OPTION_CHANGE = 5;
    public static final int SETTING_CHANGE_AUDIO_TYPE_CHANGE = 6;

    public static final String TVOS_COMMON_CMD_LOAD_HDCPKEY_DIRECT="LoadHDCPKey_direct";
    public static final String TVOS_COMMON_CMD_LOAD_HDCP2KEY_DIRECT="LoadHDCP2Key_direct";
    public static final String TVOS_COMMON_CMD_LOAD_MAC_DIRECT="LoadMACKey_direct";
    public static final String TVOS_COMMON_CMD_SWITCH_TI_MODE="Switch_TI_Mode";
    public static final String TVOS_COMMON_CMD_SWITCH_SWAP_MODE="Switch_Swap_Mode";
    public static final String TVOS_COMMON_CMD_PRESET_ATV_FREQ="PresetATVFreq";
    public static final String TVOS_COMMON_CMD_PRESET_DTV_FREQ="PresetDTVFreq";
    public static final String TVOS_COMMON_CMD_PRESET_CUSTOM_ATV_FREQ="PresetCustomATVFreq";
    public static final String TVOS_COMMON_CMD_PRESET_CUSTOM_DTV_FREQ="PresetCustomDTVFreq";
    public static final String TVOS_COMMON_CMD_LED_TO_GREEN="LedToGreen";
    public static final String TVOS_COMMON_CMD_LED_TO_RED="LedToRed";
    public static final String TVOS_COMMON_CMD_LOAD_CIKEY_DIRECT="LoadCIKey_direct";
    public static final String TVOS_COMMON_CMD_LOAD_SN_DIRECT="LoadSNKey_direct";

    public static final String SAVE_SETTING_SELECT = "save_setting_select";

    public static final String EVENT_SCHEDULER_RECORDING = "scheduler_recording";
    public static final String EVENT_SCHEDULER_RECORDING_STATUS = "status";
    public static final String EVENT_SCHEDULER_RECORDING_START = "start";
    public static final String EVENT_SCHEDULER_RECORDING_STOP = "stop";

    /**
     * show which fragment page by Intent.getInt(SHOW_FRAGMENT_PAGE, -1);
     */
    public static final String SHOW_FRAGMENT_PAGE = "show_fragment_page";
    public static final int SHOW_FRAGMENT_SW_INFO = 1;
    public static final int SHOW_FRAGMENT_SYSTEM_INFO = 2;
    public static final int SHOW_FRAGMENT_WHITE_BALANCE = 3;
    public static final int SHOW_FRAGMENT_WHITE_PATTERN = 4;
    public static final int SHOW_FRAGMENT_DESIGN_MODE = 5;
    public static final int SHOW_FRAGMENT_ADC_ADJUST = 6;

    //AudioManager
    public static final int TVAUDIO_VOLUME_EVENT_START = 0;
    public static final int TVAUDIO_AP_SET_VOLUME = 1;
    public static final int TVAUDIO_VOLUME_EVENT_END = 999;

    public static final int E_ATVPLAYER_AUTO_TUNING_RECEIVE_EVENT_INTERVAL = (800 * 1000);

    //timerManager
    public static final int LINUX_TIMESOURCE_DTV = 0;
    /** Linux time configure by Ntp */
    public static final int LINUX_TIMESOURCE_NTP = 1;

    private static final int TVTIMER_DESTROY_COUNTDOWN = 0;

    private static final int TVTIMER_LAST_MINUTE_WARN = 1;

    private static final int TVTIMER_LAST_MINUTE_UPDATE = 2;

    private static final int TVTIMER_POWER_DOWNTIME = 3;

    private static final int TVTIMER_BEAT_ONE_SECOND = 4;

    private static final int TVTIMER_SYSTEM_CLOCK_CHANGE = 5;

    private static final int TVTIMER_SIGNAL_LOCK = 6;

    private static final int TVTIMER_EPG_TIME_UP = 7;

    private static final int TVTIMER_EPG_TIMER_COUNTDOWN = 8;

    private static final int TVTIMER_EPG_TIMER_RECORD_START = 9;

    private static final int TVTIMER_PVR_NOTIFY_RECORD_STOP = 10;

    private static final int TVTIMER_OAD_TIME_SCAN = 11;

    public static final int TVTIMER_TIME_ZONE_CHG = 12;

    public static final int DEFAULT_DREAM_TIME_MS = (int) (30 * DateUtils.MINUTE_IN_MILLIS);

    public static final String BRAND = SystemProperties.get("ro.product.vendor.brand", "");

    public static final String ACTIVITY_AGING = "AgingActivity";
    public static final String ACTIVITY_MAIN = "MainActivity";
    public static final String ACTIVITY_MMODE = "MModeActivity";
    public static final String ACTIVITY_RTK_MENU_MAIN = "com.realtek.menu/.MainActivity";
    public static final String EXTRA_M_MODE_STATUS = "M_MODE_STATUS";
    public static final String PACKAGE_NAME_AUTO_TEST = "com.realtek.rtkfactoryirautotest";
    public static final String RECEIVER_GLOBAL_KEY = "GlobalKeyReceiver";
    public static final String SERVICE_MKEY_EVENT = "com.realtek.rtkfactoryirautotest/.mmode.service.MKeyEventService";
    public static final int MSG_ACTION_START_CH_MMODE = 0x01;
    public static final int MSG_ACTION_STOP_CH_MMODE = 0x02;

    /**
     * Import Channel and Export Channel MSG
     */
    public static final int REQUEST_IMPORT_SETTINGS = 1;
    public static final int REQUEST_EXPORT_SETTINGS = 2;
    public static final int REQUEST_EXPORT_PRESET_FILE = 3;
    public static final int MSG_IMPORT_SETTINGS = 1;
    public static final int MSG_EXPORT_SETTINGS = 2;
    public static final String IMPORT_EXPORT_PATH = "/Channel_list";

    /**
     * manufacturer
     */
    public static final String MANUFACTURER_BVT = "424F455654";
    public static final String MANUFACTURER_CH = "4348414E47484F4E47";
    public static final String MANUFACTURER_KK = "4b4f4e4b41";
    public static final String MANUFACTURER_TT = "746f7074656368";
}
