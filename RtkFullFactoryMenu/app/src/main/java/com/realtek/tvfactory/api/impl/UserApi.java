package com.realtek.tvfactory.api.impl;

import static com.realtek.tvfactory.FactoryApplication.MSG_KILL_PROCESS;
import static com.realtek.tvfactory.utils.Constants.ACTIVITY_MAIN;
import static com.realtek.tvfactory.utils.Constants.IMPORT_EXPORT_PATH;
import static com.realtek.tvfactory.utils.Constants.MSG_EXPORT_SETTINGS;
import static com.realtek.tvfactory.utils.Constants.MSG_IMPORT_SETTINGS;
import static com.realtek.tvfactory.utils.Constants.PACKAGE_NAME_AUTO_TEST;
import static com.realtek.tvfactory.utils.Constants.RECEIVER_GLOBAL_KEY;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.FactoryApplication.ChannelHandler;
import com.realtek.tvfactory.api.listener.IActionCallback;
import com.realtek.tvfactory.api.manager.TvFactoryManager;
import com.realtek.tvfactory.utils.LogHelper;
import com.realtek.tvfactory.utils.TvUtils;
import com.realtek.system.RtkProjectConfigs;
import com.realtek.tv.TVMediaTypeConstants;
import com.realtek.tv.TvContractEx;

import java.io.File;
import java.util.List;


public class UserApi implements Callback {
    private static final String TAG = "UserApi";

    private static UserApi mInstance;
    private final FactoryApplication mFactoryApplication;
    private final Context mContext;

    private final static int COPY_DB_DIRECTION_TO_USB = 1;
    private final static int COPY_DB_DIRECTION_TO_PROVIDER = 2;
    private final static String COPY_DB_STATUS_CHANGE = "com.rtk.engmenu.copytvdb.STATUS_CHANGE";
    private final static String KEY_DIRECT = "Direct";
    private final static String KEY_FROM_PATH = "FromPath";//String-String

    private ChannelHandler mChannelHandler;

    private HandlerThread mBackgroundThread = new HandlerThread(TAG);
    private Handler mBackgroundHandler;

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_EXPORT_SETTINGS:
                if (msg.obj instanceof IActionCallback) {
                    IActionCallback callback = (IActionCallback) msg.obj;
                    String usbPath = msg.getData().getString("path", "null");
                    if (TextUtils.isEmpty(usbPath) || "null".equals(usbPath)) {
                        Log.e(TAG, "handleMessage: usbPath " + usbPath);
                        return false;
                    }
                    Log.d(TAG, "handleMessage: usbPath " + usbPath);
                    boolean ret = msg.getData().getBoolean("ret", false);
                    Log.d(TAG, "handleMessage: ret " + ret);
                    // Intent intent = new Intent(COPY_DB_STATUS_CHANGE);
                    // intent.setFlags((intent.getFlags() | 0x01000000));
                    // intent.putExtra(KEY_DIRECT, COPY_DB_DIRECTION_TO_USB);
                    // intent.putExtra(KEY_FROM_PATH, usbPath);
                    // mContext.sendBroadcast(intent);
                    ret = ret && TvUtils.copyTvDBToUSB(usbPath);
                    Log.d(TAG, "copyTvDBToUSB: ret " + ret);
                    try {
                        if (ret) {
                            callback.onCompleted(TvFactoryManager.EXPORT_SETTINGS_RESULT_SUCCESS);
                        } else {
                            callback.onCompleted(TvFactoryManager.EXPORT_SETTINGS_RESULT_FAIL);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MSG_IMPORT_SETTINGS:
                if (msg.obj instanceof IActionCallback) {
                    IActionCallback callback = (IActionCallback) msg.obj;
                    String usbPath = msg.getData().getString("path", "null");
                    if (TextUtils.isEmpty(usbPath) || "null".equals(usbPath)) {
                        Toast.makeText(mContext, "No Channel List Folder Found.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Log.d(TAG, "loadChannels,usbPath : " + usbPath);
                    if (mChannelHandler.hasMessages(MSG_KILL_PROCESS)) {
                        mChannelHandler.removeMessages(MSG_KILL_PROCESS);
                    }
                    boolean ret =  false;
                    File file = new File(usbPath);
                    if (file.exists()) {
                        ret = importTvChFiles(usbPath);
                        Log.d(TAG, "importTvChFiles, ret : " + ret);
                        //copyTvDBFromUSBToTVProvider(usbPath);
                        ret = TvUtils.copyTvDBToProvider(usbPath);
                        Log.d(TAG, "copyTvDBToProvider, ret : " + ret);
                        mChannelHandler.removeMessages(MSG_KILL_PROCESS);
                        mChannelHandler.sendEmptyMessageDelayed(MSG_KILL_PROCESS, 1000);
                    } else {
                        Log.e(TAG, String.format("has no file(%s)!", usbPath));
                        Toast.makeText(mContext, "No channel database file found.", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        if (ret) {
                            callback.onCompleted(TvFactoryManager.IMPORT_SETTINGS_RESULT_SUCCESS);
                        } else {
                            callback.onCompleted(TvFactoryManager.IMPORT_SETTINGS_RESULT_FAIL);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    public UserApi() {
        mInstance = this;
        mFactoryApplication = FactoryApplication.getInstance();
        mContext = mFactoryApplication.getApplicationContext();
        mChannelHandler = mFactoryApplication.getChannelHandler();
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper(), this);
    }

    public static UserApi getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (UserApi.class) {
                if (mInstance == null) {
                    mInstance = new UserApi();
                }
            }
        }
        return mInstance;
    }

    public boolean getUartOnOff() {
        int result = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Misc_Register_UART");
        return result == 1;
    }

    public boolean getBVTOnOff() {
        int result = isBackground(mContext);
        return result == 1;
    }

    public boolean getFactoryRemoteControlOnOff() {
        int result = Settings.Secure.getInt(mContext.getContentResolver(), "customer_factory_remote_control_enable", 0);
        return result == 1;
    }

    public static int isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        String mainActivityClassName = PACKAGE_NAME_AUTO_TEST + "." + ACTIVITY_MAIN;
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(4);
        if (tasksInfo.size() > 0) {
            for (int i = 0; i < tasksInfo.size(); i++) {
                ComponentName topComponent = tasksInfo.get(i).topActivity;
                LogHelper.d(TAG, "topComponent.getPackageName()..." + topComponent.getPackageName());
                if (PACKAGE_NAME_AUTO_TEST.equals(topComponent.getPackageName())) {
                    LogHelper.d(TAG, "isBackground: " + topComponent.getPackageName());
                    if (topComponent.getClassName().equals(mainActivityClassName)) {
                        LogHelper.d(TAG, mainActivityClassName + " is running");
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    public int getVideoMuteColor() {
        String noSignalColor = Settings.Global.getString(mContext.getContentResolver(), TVMediaTypeConstants.STRING_NO_SIGNAL_BG_COLOR);
        Log.d(TAG, "noSignalColor" + noSignalColor);
        if (noSignalColor != null) {
            switch (noSignalColor) {
            case "Black":
                return TvFactoryManager.VIDEO_MUTE_COLOR_BLACK;
            case "White":
                return TvFactoryManager.VIDEO_MUTE_COLOR_WHITE;
            case "Red":
                return TvFactoryManager.VIDEO_MUTE_COLOR_RED;
            case "Green":
                return TvFactoryManager.VIDEO_MUTE_COLOR_GREEN;
            case "Blue":
                return TvFactoryManager.VIDEO_MUTE_COLOR_BLUE;
            }
        }
        return TvFactoryManager.VIDEO_MUTE_COLOR_BLACK;
    }

    public String getEnvironment(String name) {
        if (!TextUtils.isEmpty(name)) {
            if (TvFactoryManager.DISPLAY_LOGO.equals(name)) {
                int index = mFactoryApplication.getSysCtrl().getValueInt("Misc_Register_BootLogo");
                Log.d(TAG, " index = " + index);
                return index == 0 ? TvFactoryManager.STRING_OFF : TvFactoryManager.STRING_ON;
            }

        }
        return null;
    }

    public boolean setPvrRecordAll(boolean arg0, String usbPath) {
        if (arg0) {
            Log.d(TAG, "Debug_RecordTs_Start/Stop_Record" + "+" + usbPath + ", start record");
            if (mFactoryApplication.getExtTv() != null && usbPath != null) {
                return mFactoryApplication.getExtTv().extTv_tv001_DumpTS(usbPath, "start record");
            }
        } else {
            Log.d(TAG, "Debug_RecordTs_Start/Stop_Record" + "+" + usbPath + ", stop record");
            if (mFactoryApplication.getExtTv() != null && usbPath != null) {
                return mFactoryApplication.getExtTv().extTv_tv001_DumpTS(usbPath, "stop record");
            }
        }
        return false;
    }

    public boolean setVideoMuteColor(int color) {
        String value;
        Log.d(TAG, "color :" + color);
        switch (color) {
            case 0:
                value = "Black";
                break;
            case 1:
                value = "White";
                break;
            case 2:
                value = "Red";
                break;
            case 3:
                value = "Green";
                break;
            case 4:
                value = "Blue";
                break;
            default:
                value = "Black";
                break;
        }
        Settings.Global.putString(mContext.getContentResolver(), TVMediaTypeConstants.STRING_NO_SIGNAL_BG_COLOR, (String) value);
        return true;
    }

    public boolean setUartOnOff(boolean isEnable) {
        if (isEnable) {
            mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Misc_Register_UART", 1);
            return true;
        } else {
            mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Misc_Register_UART", 0);
            return true;
        }
    }

    public boolean setBVTCmdOnOff(boolean isEnable, boolean isManual) {
        if (isEnable) {
            ComponentName name = new ComponentName(PACKAGE_NAME_AUTO_TEST, PACKAGE_NAME_AUTO_TEST + "." + ACTIVITY_MAIN);
            Intent intent = new Intent();
            intent.setComponent(name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return true;
        } else {
            Intent intent = new Intent();
            intent.putExtra("finish",true);
            intent.putExtra("manual", isManual);
            intent.setAction("android.intent.action.GLOBAL_BUTTON");
            intent.setComponent(new ComponentName(PACKAGE_NAME_AUTO_TEST,PACKAGE_NAME_AUTO_TEST + "." + RECEIVER_GLOBAL_KEY));
            mContext.sendBroadcast(intent);
            return true;
        }
    }

    public boolean setFactoryRemoteControlOnOff(boolean isEnable) {
        if (isEnable) {
            Settings.Secure.putInt(mContext.getContentResolver(), "customer_factory_remote_control_enable", 1);
            Settings.Global.putInt(mContext.getContentResolver(), "general_function_key", 1);
            return true;
        } else {
            Settings.Secure.putInt(mContext.getContentResolver(), "customer_factory_remote_control_enable", 0);
            return true;
        }
    }

    public boolean setEnvironment(String name, String value) {
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
            if (TvFactoryManager.DISPLAY_LOGO.equals(name)) {
                int index = value.equals("on") ? 1 : 0;
                Log.d(TAG, " set index " + index);
                mFactoryApplication.getSysCtrl().setValueInt("Misc_Register_BootLogo", index);
                return true;
            }
        }

        return false;
    }

    public boolean restoreChannelTable() {
        mContext.getContentResolver().delete(TvContractEx.AtvChannelsConstants.CONTENT_URI, null, null);
        return true;
    }

    public boolean importChannelTable(String usbPath, final IActionCallback callback) {
        if (mBackgroundHandler.hasMessages(MSG_IMPORT_SETTINGS)) {
            return false;
        }
        final String channelListFolder = usbPath + IMPORT_EXPORT_PATH;
        Message msg = mBackgroundHandler.obtainMessage();
        msg.obj = callback;
        msg.what = MSG_IMPORT_SETTINGS;
        Bundle bundle = new Bundle();
        bundle.putString("path", channelListFolder);
        msg.setData(bundle);
        mBackgroundHandler.sendMessage(msg);
        return true;
    }

    private boolean importTvChFiles(String usbPath) {
        Log.d(TAG, "importTvChFiles : ***-*** ");
        boolean ret1 = mFactoryApplication.getTv().importTVChFiles(usbPath);
        boolean ret2 = mFactoryApplication.getTv().restoreChannelDB();
        return ret1 && ret2;
    }

    private void copyTvDBFromUSBToTVProvider(String usbPath) {
        Log.d(TAG, "copyTvDBFromUSBToTVProvider : ***-*** ");
        Intent intent = new Intent(COPY_DB_STATUS_CHANGE);
        intent.setFlags((intent.getFlags() | 0x01000000));
        intent.putExtra(KEY_DIRECT, COPY_DB_DIRECTION_TO_PROVIDER);
        intent.putExtra(KEY_FROM_PATH, usbPath);
        mContext.sendBroadcast(intent);
    }

    public boolean exportChannelTable(String usbPath, final IActionCallback callback) {
        Log.d(TAG, "exportFiles : ***-*** / usbPath :" + usbPath);
        if (mBackgroundHandler.hasMessages(MSG_EXPORT_SETTINGS)) {
            return false;
        }
        final String channelListFolder = usbPath + IMPORT_EXPORT_PATH;
        Log.d(TAG, "exportChannelTable: Channel_list Path " + channelListFolder);
        File file = new File(channelListFolder);
        if (file.exists() && file.isFile()) {
            boolean isDelete = file.delete();
            Log.d(TAG, String.format("delete file(%s) success!", isDelete));
        }
        if (!file.exists()) {
            try {
                if (file.mkdirs()) {
                    Log.d(TAG, String.format("mkdirs(%s) success!", channelListFolder));
                } else {
                    Log.e(TAG, String.format("mkdirs(%s) failed!", channelListFolder));
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("Error:%s!", e));
            }
        }
        new Thread(() -> {
            boolean ret = mFactoryApplication.getTv().exportTVChFiles(channelListFolder);
            Message msg = mBackgroundHandler.obtainMessage();
            msg.obj = callback;
            msg.what = MSG_EXPORT_SETTINGS;
            Bundle bundle = new Bundle();
            bundle.putString("path", channelListFolder);
            bundle.putBoolean("ret", ret);
            msg.setData(bundle);
            mBackgroundHandler.sendMessage(msg);
        }).start();
        return true;
    }

    public boolean exportPresetScanFile(String usbPath) {
//        return mFactoryApplication.getTv().exportDvbsPresetXmlFile(usbPath);
        return true;
    }


    public int getProjectMaxId() {
        return RtkProjectConfigs.getInstance().getProjectMaxIdx();
    }

    public List<String> getProjectIniList() {
        return RtkProjectConfigs.getInstance().getProjectIniList();
    }

    public int getCurrentProjectId() {
        return RtkProjectConfigs.getInstance().getProjectId();
    }

    public boolean setProjectId(int projectID) {
        return RtkProjectConfigs.getInstance().setProjectId(projectID);
    }

}
