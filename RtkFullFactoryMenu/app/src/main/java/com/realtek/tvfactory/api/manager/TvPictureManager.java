
package com.realtek.tvfactory.api.manager;



/**
 * <b>TvPictureManager class is for purpose of controlling picture management
 * from client APK.</b><br/>
 */
public class TvPictureManager {
    private final static String TAG = "TvPictureManager";

    /* This value is mapping to EN_MFC_MODE */
    /** Mfc mode Off */
    public static final int MFC_MODE_OFF = 0;
    /** Mfc mode low */
    public static final int MFC_MODE_LOW = 1;
    /** Mfc mode high */
    public static final int MFC_MODE_HIGH = 2;
    /** Mfc mode middle */
    public static final int MFC_MODE_MIDDLE = 3;
    /** Mfc mode bypass */
    public static final int MFC_MODE_BYPASS = 4;

    /* This value is mapping to EN_MS_PIC_NR */
    /** noise reduction off */
    public static final int NR_MODE_OFF = 0;
    /** noise reduction low */
    public static final int NR_MODE_LOW = 1;
    /** noise reduction middle */
    public static final int NR_MODE_MIDDLE = 2;
    /** noise reduction high */
    public static final int NR_MODE_HIGH = 3;
    /** noise reduction auto */
    public static final int NR_MODE_AUTO = 4;

    /* This value is mapping to EN_MS_PIC_MPEG_NR */
    /** Mpeg noise reduction off */
    public static final int MPEG_NR_MODE_OFF = 0;
    /** Mpeg noise reduction low */
    public static final int MPEG_NR_MODE_LOW = 1;
    /** Mpeg noise reduction middle */
    public static final int MPEG_NR_MODE_MIDDLE = 2;
    /** Mpeg noise reduction high */
    public static final int MPEG_NR_MODE_HIGH = 3;

    /* This value is mapping to EN_MS_MWE_TYPE */
    /** MWE demo mode off */
    public static final int MWE_DEMO_MODE_OFF = 0;
    /** MWE demo mode optimize */
    public static final int MWE_DEMO_MODE_OPTIMIZE = 1;
    /** MWE demo mode enhance */
    public static final int MWE_DEMO_MODE_ENHANC = 2;
    /** MWE demo mode side by side */
    public static final int MWE_DEMO_MODE_SIDE_BY_SIDE = 3;
    /** MWE demo mode dynamic compare */
    public static final int MWE_DEMO_MODE_DYNAMICCOMPARE = 4;
    /** MWE demo mode center based scale */
    public static final int MWE_DEMO_MODE_CENTERBASEDSCALE = 5;
    /** MWE demo mode move along */
    public static final int MWE_DEMO_MODE_MOVEALON = 6;
    /** MWE demo mode golden eyes */
    public static final int MWE_DEMO_MODE_GOLDENEYES = 7;
    /** MWE demo mode true color analysis ascension */
    public static final int MWE_DEMO_MODE_TRUE_COLOR_ANALYSIS_ASCENSION = 8;
    /** MWE demo mode led backlight control */
    public static final int MWE_DEMO_MODE_LED_BACKLIGHT_CONTROL = 9;
    /** MWE demo mode high speed movement processing */
    public static final int MWE_DEMO_MODE_HIGH_SPEED_MOVEMENT_PROCESSINGF = 10;
    /** MWE demo mode square move */
    public static final int MWE_DEMO_MODE_SQUAREMOVE = 11;
    /** MWE demo mode reserve for customer1 */
    public static final int MWE_DEMO_MODE_CUSTOMER1 = 12;
    /** MWE demo mode reserve for customer2 */
    public static final int MWE_DEMO_MODE_CUSTOMER2 = 13;
    /** MWE demo mode reserve for customer3 */
    public static final int MWE_DEMO_MODE_CUSTOMER3 = 14;
    /** MWE demo mode reserve for customer4 */
    public static final int MWE_DEMO_MODE_CUSTOMER4 = 15;
    /** MWE demo mode reserve for customer5 */
    public static final int MWE_DEMO_MODE_CUSTOMER5 = 16;
    /** MWE demo mode reserve for customer6 */
    public static final int MWE_DEMO_MODE_CUSTOMER6 = 17;
    /** MWE demo mode reserve for customer7 */
    public static final int MWE_DEMO_MODE_CUSTOMER7 = 18;
    /** MWE demo mode reserve for customer8 */
    public static final int MWE_DEMO_MODE_CUSTOMER8 = 19;

    /** picture brightness */
    public static final int PICTURE_BRIGHTNESS = 0;
    /** picture contrast */
    public static final int PICTURE_CONTRAST = 1;
    /** picture saturation */
    public static final int PICTURE_SATURATION = 2;
    /** picture sharpness */
    public static final int PICTURE_SHARPNESS = 3;
    /** picture hue */
    public static final int PICTURE_HUE = 4;
    /** picture backlight */
    public static final int PICTURE_BACKLIGHT = 5;

    /** picture mode dynamic */
    public static final int PICTURE_MODE_DYNAMIC = 0;
    /** picture mode normal */
    public static final int PICTURE_MODE_NORMAL = 1;
    /** picture mode mild */
    public static final int PICTURE_MODE_SOFT = 2;
    /** picture mode user */
    public static final int PICTURE_MODE_USER = 3;
    /** picture game mode */
    public static final int PICTURE_MODE_GAME = 4;
    /** picture auto mode */
    public static final int PICTURE_MODE_AUTO = 5;
    /** picture pc mode */
    public static final int PICTURE_MODE_PC = 6;
    /** picture mode vivid */
    public static final int PICTURE_MODE_VIVID = 7;
    /** picture mode natural */
    public static final int PICTURE_MODE_NATURAL = 8;
    /** picture mode sports */
    public static final int PICTURE_MODE_SPORTS = 9;

    /* This value is mapping to EN_MS_FILM */
    /** film mode off */
    public static final int FILM_MODE_OFF = 0;
    /** film mode on */
    public static final int FILM_MODE_ON = 1;

    /* This value is mapping to MAPI_VIDEO_ARC_Type */
    /** video ARC type default */
    public static final int VIDEO_ARC_DEFAULT = 0;
    /** video ARC type 16x9 */
    public static final int VIDEO_ARC_16x9 = 1;
    /** video ARC type 4x3 */
    public static final int VIDEO_ARC_4x3 = 2;
    /** video ARC type auto */
    public static final int VIDEO_ARC_AUTO = 3;
    /** video ARC type panorama */
    public static final int VIDEO_ARC_PANORAMA = 4;
    /** video ARC type just scan */
    public static final int VIDEO_ARC_JUSTSCAN = 5;
    /** video ARC type zoom 1 */
    public static final int VIDEO_ARC_ZOOM1 = 6;
    /** video ARC type zoom 2 */
    public static final int VIDEO_ARC_ZOOM2 = 7;
    /** video ARC type 14x9 */
    public static final int VIDEO_ARC_14x9 = 8;
    /** video ARC type point-to-point */
    public static final int VIDEO_ARC_DOTBYDOT = 9;
    /** video ARC type subtitle */
    public static final int VIDEO_ARC_SUBTITLE = 10;
    /** video ARC type movie */
    public static final int VIDEO_ARC_MOVIE = 11;
    /** video ARC type personal */
    public static final int VIDEO_ARC_PERSONAL = 12;
    /** video ARC type 4x3 panorama */
    public static final int VIDEO_ARC_4x3_PANSCAN = 13;
    /** video ARC type letter box */
    public static final int VIDEO_ARC_4x3_LETTERBOX = 14;
    /** video ARC type pillar box */
    public static final int VIDEO_ARC_16x9_PILLARBOX = 15;
    /** video ARC type 16x9 panorama */
    public static final int VIDEO_ARC_16x9_PANSCAN = 16;
    /** video ARC type 4x3 combind */
    public static final int VIDEO_ARC_4x3_COMBIND = 17;
    /** video ARC type 16x9 combind */
    public static final int VIDEO_ARC_16x9_COMBIND = 18;
    /** video ARC type zoom 2x */
    public static final int VIDEO_ARC_Zoom_2x = 19;
    /** video ARC type zoom 3x */
    public static final int VIDEO_ARC_Zoom_3x = 20;
    /** video ARC type zoom 4x */
    public static final int VIDEO_ARC_Zoom_4x = 21;
    /** video ARC type cutomize */
    public static final int VIDEO_ARC_CUSTOMIZE = 32;
    /** video ARC type maximum value */
    public static final int VIDEO_ARC_MAX = 64;

    /* This value is mapping to EN_MS_COLOR_TEMP */
    /** color temperature cool */
    public static final int COLOR_TEMP_COOL = 0;
    /** color temperature standard */
    public static final int COLOR_TEMP_NATURE = 1;
    /** color temperature warm */
    public static final int COLOR_TEMP_WARM = 2;
    /** color temperature user 1 */
    public static final int COLOR_TEMP_USER1 = 3;
    /** color temperature user 2 */
    public static final int COLOR_TEMP_USER2 = 4;

    /* This value is HDMI Color Format */
    /**HDMI RGB 444 Color Format*/
    public static final int HDMI_COLOR_RGB = 0;
    /**HDMI YUV 422 Color Format*/
    public static final int HDMI_COLOR_YUV_422 = 1;
    /**HDMI YUV 444 Color Format*/
    public static final int HDMI_COLOR_YUV_444 = 2;

    public static final String COMMAND_UPDATE_ALL_VIDEO_ARC = "UpdateAllVideoARC";

    static TvPictureManager mInstance = null;

    private TvPictureManager() {
    }

    public static TvPictureManager getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (TvPictureManager.class) {
                if (mInstance == null) {
                    mInstance = new TvPictureManager();
                }
            }
        }
        return mInstance;
    }

}
