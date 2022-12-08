package com.realtek.tvfactory.api.impl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.realtek.system.RtkConfigs;
import com.realtek.tv.Factory;
import com.realtek.system.RtkProjectConfigs;
import com.realtek.tv.SystemControl;

import com.realtek.tv.TVMediaTypeConstants;
import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.api.manager.TvCommonManager;
import com.realtek.tvfactory.utils.FileUtils;
import com.realtek.tvfactory.utils.LogHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FactoryMainApi {

    private static final String TAG = "MainPageApi";

    private static FactoryMainApi mInstance;
    private FactoryApplication mFactoryApplication;
    private Context mContext;
    private AudioManager mAudioManager;

    // VolumeCurve
    private static final int     SOURCE_NULL = 0;
    private static final int     SOURCE_ATV = 1;
    private static final int     SOURCE_DTV = 2;
    private static final int     SOURCE_AV = 4;
    private static final int     SOURCE_YPP = 6;
    private static final int     SOURCE_VGA = 7;
    private static final int     SOURCE_HDMI = 8;
    private static final int     SOURCE_USB = 11;

    public FactoryMainApi() {
        mInstance = this;
        mFactoryApplication = FactoryApplication.getInstance();
        mContext = mFactoryApplication.getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public static FactoryMainApi getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (FactoryMainApi.class) {
                if (mInstance == null) {
                    mInstance = new FactoryMainApi();
                }
            }
        }
        return mInstance;
    }

    public void setVolume(int volume,Context context){
        final int flags = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_VIBRATE
                | AudioManager.FLAG_FROM_KEY | AudioManager.FLAG_SHOW_UI;
        if (mAudioManager != null){
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC ,volume,0);
        }
//        MediaSessionLegacyHelper.getHelper(context).sendAdjustVolumeBy(AudioManager.USE_DEFAULT_STREAM_TYPE, 0, flags);
    }

    public String getPanelType() {
        String m_pPanelName = RtkProjectConfigs.getInstance().getConfig("[PANEL]", "m_pPanelName");
        File panelFile = new File(m_pPanelName.trim());
        return panelFile.getName();
    }

    public String getBootLogo() {
        String bootLogoPath = RtkProjectConfigs.getInstance().getConfig("[PowerLogoMode]", "PowerLogoPath");
        File bootLogoFile = new File(bootLogoPath.trim());
        return bootLogoFile.getName();
    }

    public String getCountryLang() {
        String countryLangPath = RtkProjectConfigs.getInstance().getConfig("[LiveTV]", "TvCountryLang");
        File fileFile = new File(countryLangPath.trim());
        if (fileFile.exists()) {
            return FileUtils.getFileContext(fileFile);
        } else {
            Log.e(TAG, String.format("%s is not exists.", fileFile.getPath()));
            return "unknown";
        }
    }

    public String getInputSource() {
        String sourceList =  RtkProjectConfigs.getInstance().getConfig("[LiveTV]", "inputSource");
        if (sourceList != null && !TextUtils.isEmpty(sourceList)) {
            String[] sources = sourceList.split(",");
            StringBuilder sb = new StringBuilder();
            for (String source : sources) {
                String[] tmp = source.split(":");
                if (tmp.length == 2 && !"NULL".equals(tmp[1].toUpperCase(Locale.ROOT))) {
                    sb.append(tmp[1]).append(", ");
                }
            }
            Log.e(TAG, "sb.toString()=" + sb);
            if (sb.toString().trim().length() > 1) {
                return sb.toString().trim().substring(0, sb.toString().trim().length() - 1);
            } else {
                Log.e(TAG, "sb.toString().trim().length()=0");
            }
        } else {
            Log.e(TAG, "sourceList=" + sourceList);
        }
        return "unknown";
    }

    public int getAcPowerOnMode() {
        return mFactoryApplication.getFactory().getPowerOnMode();
    }

    public boolean setAcPowerOnMode(int factoryPowerMode) {
        int anInt = Settings.Secure.getInt(mFactoryApplication.getContentResolver(), "default_power_on_mode", -1);
        if (anInt == -1) {
            int defaultPowerOnMode = mFactoryApplication.getFactory().getPowerOnMode();
            Settings.Secure.putInt(mFactoryApplication.getContentResolver(), "default_power_on_mode", defaultPowerOnMode);
        }
        mFactoryApplication.getFactory().setPowerOnMode(factoryPowerMode);
        return true;
    }

    public int getIntegerValue(String key) {
        switch (key) {
            case TvCommonManager.USER_VOLUME_PRESCALE:
                int PreScale = 7;
                int source;
                source = mFactoryApplication.getTv().getTvConnectedAudioSrc();
                if (source == SOURCE_NULL) {
                    source = SOURCE_USB;
                }
                Log.d(TAG, "-----already connected to ("+source+", " + PreScale +")-----");
                PreScale = mFactoryApplication.getTv().getFactoryVolumePreScale(source);
                return PreScale;
            case TvCommonManager.USER_VOLUME_CURVE_0:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(0);
            case TvCommonManager.USER_VOLUME_CURVE_10:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(1);
            case TvCommonManager.USER_VOLUME_CURVE_20:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(2);
            case TvCommonManager.USER_VOLUME_CURVE_30:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(3);
            case TvCommonManager.USER_VOLUME_CURVE_40:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(4);
            case TvCommonManager.USER_VOLUME_CURVE_50:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(5);
            case TvCommonManager.USER_VOLUME_CURVE_60:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(6);
            case TvCommonManager.USER_VOLUME_CURVE_70:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(7);
            case TvCommonManager.USER_VOLUME_CURVE_80:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(8);
            case TvCommonManager.USER_VOLUME_CURVE_90:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(9);
            case TvCommonManager.USER_VOLUME_CURVE_100:
                return mFactoryApplication.getTv().getFactoryVolumeTableValue(10);
        }
        return 0;
    }

    public boolean getBooleanValue(String key) {
        boolean result = false;
        switch (key) {
            case TvCommonManager.LCN_ON_OFF:
                LogHelper.d(TAG, "LCN--->%d", mFactoryApplication.getTv().getLcnOnOff(0));
                result = (mFactoryApplication.getTv().getLcnOnOff(0) == 1);
                LogHelper.d(TAG, "after LCN--->%d", mFactoryApplication.getTv().getLcnOnOff(0));
                break;
            case "SYSTEM_ISDB":
                return RtkConfigs.TvConfigs.TV_SYSTEM == RtkConfigs.TvConfigs.TvSystemConstants.ISDB;
            case "SYSTEM_ATSC":
                return RtkConfigs.TvConfigs.TV_SYSTEM == RtkConfigs.TvConfigs.TvSystemConstants.ATSC;
            case "SYSTEM_DTMB":
                return RtkConfigs.TvConfigs.TV_SYSTEM == RtkConfigs.TvConfigs.TvSystemConstants.DTMB;
            case "SYSTEM_NTSC":
                return RtkConfigs.TvConfigs.ATV_SYSTEM == RtkConfigs.TvConfigs.AtvSystemConstants.NTSC;
            case "SUPPORTED_ATV":
                return RtkConfigs.TvConfigs.SUPPORTED_ATV;
            case TvCommonManager.TELETEXT_ENABLED:
//                return RtkConfigs.getInstance().getTeltexEnabled();
                return true;


        }
        return result;
    }

    public String getStringValue(String key) {
        switch (key) {
            /*case TvCommonManager.FIRST_CONNECT_TIME: {
                File file = new File("tmp/factory/first_connect_time");
                String timeStr = null;
                FileReader fileReader;
                try {
                    fileReader = new FileReader(file);
                    BufferedReader bufReader = new BufferedReader(fileReader);
                    String line;
                    while ((line = bufReader.readLine()) != null) {
                        timeStr = line;
                    }
                    bufReader.close();
                } catch (IOException e) {
                    timeStr = null;
                }
                LogHelper.d(TAG, "FIRST_CONNECT_TIME %s", timeStr);
                return timeStr;
            }*/
            case TvCommonManager.TOTAL_RUN_TIME: {

                int totalTime = mFactoryApplication.getSysCtrl().getValueInt("Info_total_time");
                int runningTime = mFactoryApplication.getSysCtrl().getValueInt("Info_running_time");
                mFactoryApplication.getSysCtrl().setValueInt("Misc_factorySave", 0);
                Log.d(TAG, "total: " + totalTime + "running: " + runningTime);
                int[] time = formatTime(totalTime + runningTime);
                String ret = time[0] + " day " + time[1] + " H " + time[2] + " M " + time[3] + "S";
                return ret;
            }
        }
        return null;
    }

    public static int[] formatTime(long time) {
        //array[0]:day, array[1]:hour, array[2]:minute, array[3]:sec
        int[] array = new int[4];
        long tmpmin, tmphour;
        array[3] = (int) time % 60;
        tmpmin = time / 60;
        array[2] = (int) tmpmin % 60;
        tmphour = tmpmin / 60;
        array[1] = (int) tmphour % 24;
        array[0] = (int) tmphour / 24;
        return array;
    }

    public void setIntegerValue(String key, int value) {
        switch (key) {
            case TvCommonManager.BACKGROUND_LIGHT:
                mFactoryApplication.getSysCtrl().setValueInt("PIN_BL_ON_OFF", value);
                break;
            case TvCommonManager.COMMAND_SET_AUDIO_OUT_VOLUME:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Audio_SoundAdjust_AudioOutVolume", value);
                break;
            case TvCommonManager.COMMAND_SET_AVC_THL:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Audio_SoundAdjust_Avc1LoTh", value);
                break;
            case TvCommonManager.COMMAND_SET_AVC_THL_MAX:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Audio_SoundAdjust_Avc1HiTh", value);
                break;
            case TvCommonManager.COMMAND_SET_DRC_THL:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Audio_SoundAdjust_Avc2LoTh", value);
                break;
            case TvCommonManager.COMMAND_SET_DRC_THL_MAX:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Audio_SoundAdjust_Avc2HiTh", value);
                break;
            case TvCommonManager.COMMAND_SET_DIGITALAUDIO_OUT_VOLUME:
                mFactoryApplication.getAq().setDigitalAudioOutVolume(value);
                break;
            case TvCommonManager.COMMAND_SET_AUDIO_DELAY:
                mFactoryApplication.getAq().setSPDIFDelayTime(value);
                break;
            case TvCommonManager.COMMAND_SET_AVC_ENABLE:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Audio_SoundAdjust_Avc1Enable", value);
                break;
            case TvCommonManager.COMMAND_SET_DRC_ENABLE:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Audio_SoundAdjust_Avc2Enable", value);
                break;
            case TvCommonManager.COMMAND_SET_EFFECT:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Audio_SoundAdjust_Effect", value);
                break;
            case TvCommonManager.COMMAND_SET_AUDIO_NR:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Audio_SoundAdjust_AudioNR", value);
                break;
            case TvCommonManager.USER_VOLUME_PRESCALE:
                int source = mFactoryApplication.getTv().getTvConnectedAudioSrc();
                if (source == SOURCE_NULL) {
                    source = SOURCE_USB;
                }
                mFactoryApplication.getTv().setFactoryVolumePreScale(source, value);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_0:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(0, value);
                setStreamVolume(0);
                setVolume(0, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_10:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(1, value);
                setStreamVolume(10);
                setVolume(10, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_20:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(2, value);
                setStreamVolume(20);
                setVolume(20, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_30:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(3, value);
                setStreamVolume(30);
                setVolume(30, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_40:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(4, value);
                setStreamVolume(40);
                setVolume(40, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_50:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(5, value);
                setStreamVolume(50);
                setVolume(50, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_60:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(6, value);
                setStreamVolume(60);
                setVolume(60, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_70:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(7, value);
                setStreamVolume(70);
                setVolume(70, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_80:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(8, value);
                setStreamVolume(80);
                setVolume(80, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_90:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(9, value);
                setStreamVolume(90);
                setVolume(90, mContext);
                break;
            case TvCommonManager.USER_VOLUME_CURVE_100:
                mFactoryApplication.getTv().setFactoryVolumeTableValue(10, value);
                setStreamVolume(100);
                setVolume(100, mContext);
                break;
        }
    }

    private void setStreamVolume(int value) {
        mFactoryApplication.getTv().setAudioVolume(value);
    }

    public void setStringValue(String key, String value) {
        switch (key) {
            case TvCommonManager.USER_VOLUME_SOURCE:
                mFactoryApplication.getSysCtrl().setValueString("Audio_VolumeTable_source", value);
                break;
        }
    }

    public void setBooleanValue(String key, boolean value) {
        switch (key) {
            case TvCommonManager.LCN_ON_OFF:
                LogHelper.d(TAG, "setBooleanValue:%s", value);
                mFactoryApplication.getTv().openSource(TVMediaTypeConstants.RT_SERVICE_TYPE_DTV, 0, TVMediaTypeConstants.TV_SOURCE_PATH_MAIN, true);
                mFactoryApplication.getTv().enableChOrderByLCN(0, value);
                break;
            case TvCommonManager.TELETEXT_ENABLED:
//                RtkConfigs.getInstance().setTeltexEnabled(value);
//                mFactoryApplication.getSysCtrl().setValueInt("Teltex_Enabled", value ? 1 : 0);
            case "PVR_FUNCTION_ENABLED":
//                TvSettings.putBoolean(mContext.getContentResolver(), key, value);
        }
    }

    public int[] setTvosCommonCommand(String command) {
        if (command == null) {
            return null;
        }
        if (TextUtils.isEmpty(command)) {
            return null;
        }

        if (TvCommonManager.COMMAND_GET_SOUND_ADJUST_INFO.equals(command)) {
            int[] panelInfo = new int[11];
            panelInfo[0] = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Audio_SoundAdjust_AudioOutVolume");
            panelInfo[1] = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Audio_SoundAdjust_Avc1Enable");
            panelInfo[2] = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Audio_SoundAdjust_Avc1LoTh");
            panelInfo[3] = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Audio_SoundAdjust_Avc1HiTh");
            panelInfo[4] = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Audio_SoundAdjust_Avc2Enable");
            panelInfo[5] = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Audio_SoundAdjust_Avc2LoTh");
            panelInfo[6] = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Audio_SoundAdjust_Avc2HiTh");
            panelInfo[7] = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Audio_SoundAdjust_Effect");
            panelInfo[8] = mFactoryApplication.getAq().getSPDIFDelayTime();
            panelInfo[9] = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Audio_SoundAdjust_AudioNR");
            panelInfo[10] = mFactoryApplication.getAq().getDigitalAudioOutVolume();
            return panelInfo;
        }
        return null;
    }

    public boolean changeToFirstService(int sourceFilter, final int typeFilter) {
        return true;
    }

    public boolean androidRestoreToDefault() {
        Intent resetIntent = new Intent(Intent.ACTION_FACTORY_RESET);
        resetIntent.setPackage("android");
        resetIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        resetIntent.putExtra(Intent.EXTRA_REASON, "ResetConfirmFragment");
        restorePowerOnMode();
        SystemControl mSysCtrl = new SystemControl();
        mSysCtrl.setValueString("RestoreBootparam000", "0");
        SystemProperties.set("persist.sys.factory_boot_mode", "-1");
        Log.v(TAG, "RestoreBootparam000");
        mContext.sendBroadcast(resetIntent);
        return false;
    }

    public void restorePowerOnMode() {
        int defaultPowerOnMode = Settings.Secure.getInt(mFactoryApplication.getContentResolver(), "default_power_on_mode", -1);
        Log.d(TAG, "defaultPowerOnMode---> :" + defaultPowerOnMode);
        if (defaultPowerOnMode != -1) {
            new Factory().setPowerOnMode(defaultPowerOnMode);
        }
    }

    public boolean restoreToDefault() {
        FactoryReset();
        return true;
    }

    private static void restoreDefaultWifi(Context context){
        // clear wifi saveconfig
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager!=null){
            List<WifiConfiguration> config = (List<WifiConfiguration>) wifiManager.getConfiguredNetworks();
            if(config == null)
                return;
            for(int i = 0; i < config.size(); i++){
                wifiManager.removeNetwork(config.get(i).networkId);
            }
            wifiManager.saveConfiguration();
        }
    }

    private void FactoryReset() {
        Log.d(TAG, "start new reset!");
        // restoreDefaultWifi(mContext);
        List<String> packages = new ArrayList<>();
        packages.add("com.android.tv");
        packages.add("com.android.tv.settings");
        packages.add("com.android.providers.tv");
        packages.add("com.android.providers.settings");
        packages.add("com.realtek.dtv");
        restorePowerOnMode();
        String defaultLanguage = SystemProperties.get("ro.product.locale");
        if (defaultLanguage != null) {
            Log.d(TAG, "defaultLanguage---> :" + defaultLanguage);
            SystemProperties.set("persist.sys.locale", defaultLanguage);
        }
        SystemProperties.set("persist.sys.factory_boot_mode", "-1");
        mFactoryApplication.getExtTv().extTv_tv001_ResetUartPinmuxSettingValueFromConfig();
 
        List<String> files = new ArrayList<>();
        files.add("/data/system/users/0");
        files.add("/mnt/vendor/tvdata/database");
        files.add("/mnt/vendor/tvdata/aqTable");
        files.add("/mnt/vendor/tvdata/media");

        deleteFiles(files);

        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "cleanFiles...");
                for (; ; ) {
                    deleteFiles(files);
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
        
        clearApplicationUserData(packages, () -> {
            Thread thr = new Thread("ResetActivity") {
                @Override
                public void run() {
                    Log.d(TAG, "clear data and reboot");
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                        }
                    try {
                        Runtime.getRuntime().exec("reboot");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thr.start();
            // Wait for us to tell the power manager to shutdown.
            try {
                thr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void clearApplicationUserData(List<String> packages, final Runnable callback) {
        final List<String> tasks = new ArrayList<>(packages);
        IPackageDataObserver observer = new IPackageDataObserver.Stub() {

            @Override
            public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                Log.d(TAG, String.format("clear %s %b", packageName, succeeded));
                if (tasks.remove(packageName)) {
                    if (tasks.isEmpty()) {
                        Log.d(TAG, "callback run");
                        restoreDefaultWifi(mContext);
                        callback.run();
                    }
                }
            }
        };
        ActivityManager am = mContext.getSystemService(ActivityManager.class);
        for (String packageName : packages) {
            Log.d(TAG, "packagename :" + packageName);
            am.clearApplicationUserData(packageName, observer);
        }
    }
    
    public void deleteFiles(List<String> files) {
        for (String file : files) {
            delFolder(file);
        }
    }

    private static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            if (!folderPath.equals("/data/system/users/0")) {
                String filePath = folderPath;
                filePath = filePath.toString();
                File myFilePath = new File(filePath);
                myFilePath.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }
}
