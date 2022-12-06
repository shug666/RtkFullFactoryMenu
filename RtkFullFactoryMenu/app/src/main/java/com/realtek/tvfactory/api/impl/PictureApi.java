package com.realtek.tvfactory.api.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.realtek.tv.VSC;
import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.api.listener.IActionCallback;
import com.realtek.tvfactory.api.manager.TvCommonManager;
import com.realtek.tvfactory.api.manager.TvFactoryManager;
import com.realtek.tvfactory.api.manager.TvPictureManager;
import com.realtek.tvfactory.utils.TvInputUtils;

import android.os.Handler.Callback;
import android.util.Log;


public class PictureApi implements Callback {
    private static final String TAG = "PictureApi";

    private static PictureApi mInstance = null;
    private final FactoryApplication mFactoryApplication;
    private final Context mContext;

    private int patternColorIndex = 0;
    private VSC.OverScanAdjInfo adjInfo;


    //color
    private static final String[] colors = {"User", "Standard", "Warmer", "Warm", "Cool", "Cooler"};

    //test pattern
    private static final String[] sTestPattern = {"EXIT", "White", "Red", "Green", "Blue", "Black"};

    //nonlinear
    private static final String[] typeOptions = {"Volume", "Brightness", "Contrast", "Saturation", "Sharpness", "Hue",
            "Backlight"};
    private int typeIndex = 1;
    private int OSD_0;
    private int OSD_25;
    private int OSD_50;
    private int OSD_75;
    private int OSD_100;

    private final BackLightHandler mBackLightHandler;
    private HandlerThread mBackgroundThread = new HandlerThread(TAG);
    private Handler mBackgroundHandler;

    private static final int MSG_BACKLIGHT_SAVE = 0;
    private static final int EXEC_AUTO_ADC = 1;

    // ssc Adjust
    private static final int LVDS_ENABLE = 0;
    private static final int LVDS_PERCENTAGE = 1;
    private static final int LVDS_PERIOD = 2;
    private static final int DDR_PERCENTAGE = 1;
    private static final int SSC_TYPE_MIU0 = 0;
    private static final int SSC_TYPE_MIU1 = 1;
    private static final int SSC_TYPE_LVDS = 2;
    private static final int DEFAULT_PERIOD = 330;

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case EXEC_AUTO_ADC:
                if (msg.obj instanceof IActionCallback) {
                    IActionCallback callback = (IActionCallback) msg.obj;
                    mFactoryApplication.getVsc().setWinBlank(0, 1, 0);
                    boolean ret = mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCAutoTune", 1);
                    try {
                        if (ret) {
                            callback.onCompleted(TvFactoryManager.ADC_AUTO_TUNE_RESULT_SUCCESS);
                        } else {
                            callback.onCompleted(TvFactoryManager.ADC_AUTO_TUNE_RESULT_FAIL);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    mFactoryApplication.getVsc().setWinBlank(0, 0, 0);
                }
                break;
            default:
                break;
        }
        return false;
    }

    private class BackLightHandler extends Handler {

        public BackLightHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BACKLIGHT_SAVE:
                    mFactoryApplication.getSysCtrl().setValueIntParam("BackLight", "Save", 1);
                    break;
                default:
                    break;
            }
        }
    }

    public PictureApi() {
        mInstance = this;
        mFactoryApplication = FactoryApplication.getInstance();
        mContext = mFactoryApplication.getApplicationContext();
        adjInfo = mFactoryApplication.getVsc().new OverScanAdjInfo(0,0,0,0);
        mBackLightHandler = new BackLightHandler();
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper(), this);
    }

    public static PictureApi getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (PictureApi.class) {
                if (mInstance == null) {
                    mInstance = new PictureApi();
                }
            }
        }
        return mInstance;
    }


    /**
     * ===========================================AdcAdjustApi=======================================
     */
    public int getAdcRedGain() {
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("VGA_ADCFunction_ADCR_Gain");
    }

    public int getAdcGreenGain() {
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("VGA_ADCFunction_ADCG_Gain");
    }

    public int getAdcBlueGain() {
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("VGA_ADCFunction_ADCB_Gain");
    }

    public int getAdcRedOffset() {
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("VGA_ADCFunction_ADCR_Offset");
    }

    public int getAdcGreenOffset() {
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("VGA_ADCFunction_ADCG_Offset");
    }

    public int getAdcBlueOffset() {
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("VGA_ADCFunction_ADCB_Offset");
    }

    public int getAdcPhase() {
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("VGA_VGAFunction_Phase");
    }

    public int getFactoryAdcIndex() {
        return mFactoryApplication.getInputSource(TvInputUtils.getCurrentInput(mContext));
    }

    public boolean setFactoryAdcIndex(int index) {
        switch (index) {
            case TvFactoryManager.ADC_SET_VGA:
                mFactoryApplication.getInstance().setInputSource(mFactoryApplication.getInputId(TvCommonManager.INPUT_SOURCE_VGA));
                break;
            case TvFactoryManager.ADC_SET_YPBPR_SD:
            case TvFactoryManager.ADC_SET_YPBPR_HD:
                mFactoryApplication.getInstance().setInputSource(mFactoryApplication.getInputId(TvCommonManager.INPUT_SOURCE_YPBPR));
                break;
            case TvFactoryManager.ADC_SET_YPBPR2_SD:
            case TvFactoryManager.ADC_SET_YPBPR2_HD:
                mFactoryApplication.getInstance().setInputSource(mFactoryApplication.getInputId(TvCommonManager.INPUT_SOURCE_YPBPR2));
                break;
            case TvFactoryManager.ADC_SET_YPBPR3_SD:
            case TvFactoryManager.ADC_SET_YPBPR3_HD:
                mFactoryApplication.getInstance().setInputSource(mFactoryApplication.getInputId(TvCommonManager.INPUT_SOURCE_YPBPR3));
                break;
            case TvFactoryManager.ADC_SET_SCART_RGB:
                mFactoryApplication.getInstance().setInputSource(mFactoryApplication.getInputId(TvCommonManager.INPUT_SOURCE_SCART));
                break;
            default:
                break;
        }
        return true;
    }

    public boolean setAdcRedGain(int redGain) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCR_Gain", redGain);
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCManuallyApply", 1);
        return true;
    }

    public boolean setAdcGreenGain(int greenGain) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCG_Gain", greenGain);
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCManuallyApply", 1);
        return true;
    }

    public boolean setAdcBlueGain(int blueGain) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCB_Gain", blueGain);
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCManuallyApply", 1);
        return true;
    }

    public boolean setAdcRedOffset(int redOffset) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCR_Offset", redOffset);
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCManuallyApply", 1);
        return true;
    }

    public boolean setAdcGreenOffset(int greenOffset) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCG_Offset", greenOffset);
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCManuallyApply", 1);
        return true;
    }

    public boolean setAdcBlueOffset(int blueOffset) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCB_Offset", blueOffset);
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_ADCFunction_ADCManuallyApply", 1);
        return true;
    }

    public boolean setAdcPhase(int phase) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("VGA_VGAFunction_Phase", phase);
        return true;
    }

    public boolean execAutoAdc(final IActionCallback callback) {
        if (mBackgroundHandler.hasMessages(EXEC_AUTO_ADC)) {
            return false;
        }
        Message msg = mBackgroundHandler.obtainMessage();
        msg.obj = callback;
        msg.what = EXEC_AUTO_ADC;
        mBackgroundHandler.sendMessage(msg);
        return true;
    }
    // ===========================================AdcAdjustApi=======================================

    /**
     * ===========================================NonLinearApi=======================================
     */
    public int getNlaCurveType() {
        return typeIndex;
    }

    public int getOsdV0Nonlinear() {
        OSD_0 = mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_Non-linearSetting_OSD0", typeOptions[typeIndex]);
        return OSD_0;
    }

    public int getOsdV25Nonlinear() {
        OSD_25 = mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_Non-linearSetting_OSD25", typeOptions[typeIndex]);
        return OSD_25;
    }

    public int getOsdV50Nonlinear() {
        OSD_50 = mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_Non-linearSetting_OSD50", typeOptions[typeIndex]);
        return OSD_50;
    }

    public int getOsdV75Nonlinear() {
        OSD_75 = mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_Non-linearSetting_OSD75", typeOptions[typeIndex]);
        return OSD_75;
    }

    public int getOsdV100Nonlinear() {
        OSD_100 = mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_Non-linearSetting_OSD100", typeOptions[typeIndex]);
        return OSD_100;
    }

    public boolean setNlaCurveType(int curveTypeIndex) {
        typeIndex = curveTypeIndex;
        return true;
    }

    public boolean setOsdV0Nonlinear(int nonlinearVal) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_Non-linearSetting_OSD0", typeOptions[typeIndex], nonlinearVal);
        return true;
    }

    public boolean setOsdV25Nonlinear(int nonlinearVal) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_Non-linearSetting_OSD25", typeOptions[typeIndex], nonlinearVal);
        return true;
    }

    public boolean setOsdV50Nonlinear(int nonlinearVal) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_Non-linearSetting_OSD50", typeOptions[typeIndex], nonlinearVal);
        return true;
    }

    public boolean setOsdV75Nonlinear(int nonlinearVal) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_Non-linearSetting_OSD75", typeOptions[typeIndex], nonlinearVal);
        return true;
    }

    public boolean setOsdV100Nonlinear(int nonlinearVal) {
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_Non-linearSetting_OSD100", typeOptions[typeIndex], nonlinearVal);
        return true;
    }
    //============================================NonLinerApi===========================================

    /**
     * ===========================================OverScanApi===========================================
     */

    public int getOverScanHPosition()  {
        mFactoryApplication.getVsc().getOverscanAdjust(0, adjInfo);
        return adjInfo.mLeft;
    }

    public int getOverScanHSize() {
        mFactoryApplication.getVsc().getOverscanAdjust(0, adjInfo);
        return adjInfo.mRight;
    }

    public int getOverScanVPosition() {
        mFactoryApplication.getVsc().getOverscanAdjust(0, adjInfo);
        return adjInfo.mTop;
    }

    public int getOverScanVSize() {
        mFactoryApplication.getVsc().getOverscanAdjust(0, adjInfo);
        return adjInfo.mBottom;
    }

    public boolean setOverScanHPosition(int hPosition) {
        mFactoryApplication.getVsc().getOverscanAdjust(0, adjInfo);
        adjInfo.mLeft = hPosition;
        return mFactoryApplication.getVsc().setOverscanAdjust(0, adjInfo);
    }

    public boolean setOverScanHSize(int hSize) {
        mFactoryApplication.getVsc().getOverscanAdjust(0, adjInfo);
        adjInfo.mRight = hSize;
        return mFactoryApplication.getVsc().setOverscanAdjust(0, adjInfo);
    }

    public boolean setOverScanVPosition(int vPosition) {
        mFactoryApplication.getVsc().getOverscanAdjust(0, adjInfo);
        adjInfo.mTop = vPosition;
        return mFactoryApplication.getVsc().setOverscanAdjust(0, adjInfo);
    }

    public boolean setOverScanVSize(int vSize) {
        mFactoryApplication.getVsc().getOverscanAdjust(0, adjInfo);
        adjInfo.mBottom = vSize;
        return mFactoryApplication.getVsc().setOverscanAdjust(0, adjInfo);
    }
    //============================================OverScanApi===========================================

    /**
     * ===========================================PictureModeApi===========================================
     */

    public int getPictureMode() {
        int mode = 0;
        try {
            if (mFactoryApplication.getPq().isSupportDolbyHDR()) {
                mode = mFactoryApplication.getPq().getDolbyHdrPicMode();
            } else {
                mode = mFactoryApplication.getPq().getPictureMode();
            }
            return mode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return TvPictureManager.PICTURE_MODE_NORMAL;
    }

    public int getVideoItem(int index) {
        int value = 0;
        switch (index) {
            case TvPictureManager.PICTURE_BRIGHTNESS:
                value = (int) mFactoryApplication.getPq().getBrightness();
                break;
            case TvPictureManager.PICTURE_CONTRAST:
                value = (int) mFactoryApplication.getPq().getContrast();
                break;
            case TvPictureManager.PICTURE_SATURATION:
                value = (int) mFactoryApplication.getPq().getSaturation();
                break;
            case TvPictureManager.PICTURE_SHARPNESS:
                value = (int) mFactoryApplication.getPq().getSharpness();
                break;
            case TvPictureManager.PICTURE_HUE:
                value = (int) mFactoryApplication.getPq().getHue();
                break;
            case TvPictureManager.PICTURE_BACKLIGHT:
                value = (int) mFactoryApplication.getPq().getBacklight();
                break;
            default:
                break;
        }
        return value;
    }

    public boolean setPictureMode(int ePicMode) {
        if (mFactoryApplication.getPq().isSupportDolbyHDR()) {
            mFactoryApplication.getPq().setDolbyHdrPicMode(ePicMode);
        } else {
            mFactoryApplication.getPq().setPictureMode(ePicMode);
        }
        return true;
    }

    public boolean setVideoItem(int index, int value) {
        switch (index) {
            case TvPictureManager.PICTURE_BRIGHTNESS:
                mFactoryApplication.getPq().setBrightness(value);
                break;
            case TvPictureManager.PICTURE_CONTRAST:
                mFactoryApplication.getPq().setContrast(value);
                break;
            case TvPictureManager.PICTURE_SATURATION:
                mFactoryApplication.getPq().setSaturation(value);
                break;
            case TvPictureManager.PICTURE_SHARPNESS:
                mFactoryApplication.getPq().setSharpness(value);
                break;
            case TvPictureManager.PICTURE_HUE:
                mFactoryApplication.getPq().setHue(value);
                break;
            case TvPictureManager.PICTURE_BACKLIGHT:
                mFactoryApplication.getPq().setBacklight(value);
                mBackLightHandler.removeMessages(MSG_BACKLIGHT_SAVE);
                mBackLightHandler.sendEmptyMessageDelayed(MSG_BACKLIGHT_SAVE, 500);
                break;
            default:
                break;
        }
        return true;
    }
    //============================================PictureModeApi===========================================

    /**
     * ===========================================PicturePageApi===========================================
     */
    public boolean setVideoTestPattern(int testPatternMode) {
        patternColorIndex = testPatternMode;
        mFactoryApplication.getExtTv().extTv_tv001_SetValueString("Misc_TestColor_TestPattern", sTestPattern[patternColorIndex]);
        return true;
    }
    //============================================PicturePageApi===========================================

    /**
     * ===========================================PQApi===========================================
     */
    //============================================PQApi===========================================

    /**
     * ===========================================SscAdjustApi===========================================
     */
    public boolean getLvdsenable() {
        return mFactoryApplication.getFactory().getSSCSpreadSpectrumPar(SSC_TYPE_LVDS, LVDS_ENABLE) == 1;
    }

    public int getLvdsPercentage() {
        return  mFactoryApplication.getFactory().getSSCSpreadSpectrumPar(SSC_TYPE_LVDS, LVDS_PERCENTAGE);
    }

    public int getLvdsPeriod() {
        int current =  mFactoryApplication.getFactory().getSSCSpreadSpectrumPar(SSC_TYPE_LVDS, LVDS_PERIOD);
        Log.d(TAG, "getLvdsPeriod:" + current);
        return current;
    }

    public boolean setLvdsEnable(boolean lvdsSscEnable, int progress) {
        return mFactoryApplication.getFactory().setSSCSpreadSpectrum(SSC_TYPE_LVDS, lvdsSscEnable, progress, getLvdsPeriod());
    }

    public boolean setLvdsPercentage(int lvdsSscStep, boolean lvdsSscEnable) {
        return mFactoryApplication.getFactory().setSSCSpreadSpectrum(SSC_TYPE_LVDS, lvdsSscEnable, lvdsSscStep, getLvdsPeriod());
    }

    public boolean setLvdsPeriod(int period) {
        return mFactoryApplication.getFactory().setSSCSpreadSpectrum(SSC_TYPE_LVDS, getLvdsenable(), getLvdsPercentage(), period);
    }

    //============================================SscAdjustApi===========================================

    /**
     * ===========================================WhiteBalanceAdjustApi===========================================
     */

    public int getWbColorTempIdx() {
        return mFactoryApplication.getPq().getColorTemp();
    }

    public int getWbRedGain() {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return -1;
        }
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_WhiteBalance_RGAIN", colors[colorTemp]);
    }

    public int getWbGreenGain() {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return -1;
        }
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_WhiteBalance_GGAIN", colors[colorTemp]);
    }

    public int getWbBlueGain() {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return -1;
        }
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_WhiteBalance_BGAIN", colors[colorTemp]);
    }

    public int getWbRedOffset() {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return -1;
        }
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_WhiteBalance_ROFFSET", colors[colorTemp]);
    }

    public int getWbGreenOffset() {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return -1;
        }
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_WhiteBalance_GOFFSET", colors[colorTemp]);
    }

    public int getWbBlueOffset() {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return -1;
        }
        return mFactoryApplication.getExtTv().extTv_tv001_GetValueIntParam("PQ2_WhiteBalance_BOFFSET", colors[colorTemp]);
    }

    public boolean setWbColorTempIdx(int idx) {
        if (idx < 0 || idx >= colors.length) {
            return false;
        }
        mFactoryApplication.getPq().setColorTemp(idx);
        return true;
    }

    public boolean setWbRedGain(int redGain) {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return false;
        }
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_WhiteBalance_RGAIN", colors[colorTemp], redGain);
        return true;
    }

    public boolean setWbGreenGain(int greenGain) {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return false;
        }
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_WhiteBalance_GGAIN", colors[colorTemp], greenGain);
        return true;
    }

    public boolean setWbBlueGain(int blueGain) {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return false;
        }
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_WhiteBalance_BGAIN", colors[colorTemp], blueGain);
        return true;
    }

    public boolean setWbRedOffset(int redOffset) {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return false;
        }
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_WhiteBalance_ROFFSET", colors[colorTemp], redOffset);
        return true;
    }

    public boolean setWbGreenOffset(int greenOffset) {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return false;
        }
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_WhiteBalance_GOFFSET", colors[colorTemp], greenOffset);
        return true;
    }

    public boolean setWbBlueOffset(int blueOffset) {
        int colorTemp = mFactoryApplication.getPq().getColorTemp();
        if (colorTemp < 0 || colorTemp >= colors.length) {
            return false;
        }
        mFactoryApplication.getExtTv().extTv_tv001_SetValueIntParam("PQ2_WhiteBalance_BOFFSET", colors[colorTemp], blueOffset);
        return true;
    }
    //============================================WhiteBalanceAdjustApi===========================================

    public int getIntegerValue(String key) {
        switch (key) {
            case TvCommonManager.COMB_BRIGHTNESS:
                return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("PQ2_VDColorSetting_Brightness");
            case TvCommonManager.COMB_CONTRAST:
                return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("PQ2_VDColorSetting_Contrast");
            case TvCommonManager.COMB_SATURATION:
                return mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("PQ2_VDColorSetting_Saturation");
            case TvCommonManager.DDRSPREAD_RATIO:
                int sscSpreadSpectrumPar = mFactoryApplication.getFactory().getSSCSpreadSpectrumPar(SSC_TYPE_MIU0, DDR_PERCENTAGE);
                Log.d(TAG, "sscSpreadSpectrumPar: "+sscSpreadSpectrumPar);
                return mFactoryApplication.getFactory().getSSCSpreadSpectrumPar(SSC_TYPE_MIU0, DDR_PERCENTAGE);
        }
        return 0;
    }

    public void setIntegerValue(String key, int value) {
        switch (key) {
            case TvCommonManager.COMB_BRIGHTNESS:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("PQ2_VDColorSetting_Brightness", value);
                break;
            case TvCommonManager.COMB_CONTRAST:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("PQ2_VDColorSetting_Contrast", value);
                break;
            case TvCommonManager.COMB_SATURATION:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("PQ2_VDColorSetting_Saturation", value);
                break;
            case TvCommonManager.DDRSPREAD_RATIO:
                mFactoryApplication.getFactory().setSSCSpreadSpectrum(SSC_TYPE_MIU0, true, value, 0);
                break;

        }
    }

    public boolean getBooleanValue(String key) {
        boolean result = false;
        switch (key) {

        }
        return result;
    }

    public int[] setTvosCommonCommand(String command) {
        ContentResolver cr = mContext.getContentResolver();
        if (command == null) {
            return null;
        }
        if (TextUtils.isEmpty(command)) {
            return null;
        }
        if (command.startsWith("copyDataToAllSource")) {
            boolean copyDataToAllSource = mFactoryApplication.getExtTv().extTv_tv056_SyncColorTempOneToAllSrc();
            int[] isFail = new int[1];
            isFail[0] = copyDataToAllSource ? 1 : 0;
            return isFail;
        }
        return null;
    }
}
