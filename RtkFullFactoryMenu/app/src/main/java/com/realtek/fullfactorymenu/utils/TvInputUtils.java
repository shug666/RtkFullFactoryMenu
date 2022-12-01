package com.realtek.fullfactorymenu.utils;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;

import com.realtek.system.RtkConfigs;
import com.realtek.tv.TVMediaTypeConstants;
import com.realtek.tv.TvContractEx;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.realtek.tv.RtkSettingProviderHelper.getSignalType;

public class TvInputUtils {

    public static final String TAG = "TvInputUtils";

    public static final String PREFIX_HARDWARE_DEVICE = "HW";

    private static final String PACKAGE_NAME_ATV = "com.realtek.tv.atv";
    private static final String PACKAGE_NAME_DTV = "com.realtek.dtv";
    private static final String PACKAGE_NAME_AV = "com.realtek.tv.avtvinput";
    private static final String PACKAGE_NAME_YPBPR = "com.realtek.tv.ypptvinput";
    private static final String PACKAGE_NAME_HDMI = "com.realtek.tv.hdmitvinput";
    private static final String PACKAGE_NAME_VGA = "com.realtek.tv.vgatvinput";
    private static final String PACKAGE_NAME_GOOGLEPLAY = "com.google.android.videos";
    public static final String SERVICE_NAME_ATSC = "AtscTvInputService";
    public static final String SERVICE_NAME_ISDB = "IsdbTvInputService";
    public static final String SERVICE_NAME_ATV = "AtvInputService";
    public static final String SERVICE_NAME_DTV = "DTVTvInputService";
    public static final String SERVICE_NAME_AV = "AVTvInputService";
    public static final String SERVICE_NAME_YPP = "YPPTvInputService";
    public static final String SERVICE_NAME_VGA = "VGATvInputService";
    public static final String SERVICE_NAME_HDMI = "HDMITvInputService";
    private static final String INPUT_ID_FOR_TV = "com.realtek.tv.atv/com.realtek.dtv";
    public static final String PACKAGE_NAME_PASSTHROUGH = "com.realtek.tv.passthrough";

    public static String getCurrentInput(Context context) {
        String input = null;
        input = Settings.Global.getString(context.getContentResolver(), "com.android.tv_current_input_id");
        return input;
    }

    public static boolean setCurrentInput(Context context, String inputID) {
        return Settings.Global.putString(context.getContentResolver(), "com.android.tv_current_input_id", inputID);
    }

    public static Uri buildChannelUri(Context context, String inputId, TvInputInfo tvInputInfo) {
        if (inputId == null) {
            return null;
        }
        long channelId = -1;
        Log.d(TAG, "buildChannelUri isAtv:" + isAtv(inputId));
        Log.d(TAG, "buildChannelUri isDtv:" + isDtv(inputId));
        if (isAtv(inputId) || isDtv(inputId) || isAtscInput(inputId)) {
            channelId = getInputChannelForATVAndDTV(context, inputId, tvInputInfo);
        } else {
            channelId = TvInputUtils.getInputChannel(context, inputId);
        }
        Log.d(TAG, "buildChannelUri channelId: " + channelId);
        Uri channelUri = buildChannelUri1(context, inputId, channelId, tvInputInfo);

        return channelUri;
    }

    public static boolean isStreamingInput(TvInputInfo info) {
        if (info == null) {
            Log.d(TAG, "input info is null, just return false");
            return false;
        }
        return !info.isHardwareInput();
    }

    public static int parseInputType(String inputId, Context context, TvInputInfo tvInputInfo) {
        int type = -1;
        Log.d(TAG, "inputId = " + inputId);
        if (isStreamingInput(tvInputInfo)) {
            type = TvContractEx.CHANNEL_TYPE_OTHERS;
        } else if (isPassthrough(inputId)) {
            type = TvContractEx.CHANNEL_TYPE_PASSTHROUGH;
        } else if (isAtscInput(inputId)) {
            if (getSignalType(context) == TVMediaTypeConstants.TV_SIGNAL_TYPE_CABLE) {
                type = TvContractEx.CHANNEL_TYPE_ATSC_CABLE;
            } else if (getSignalType(context) == TVMediaTypeConstants.TV_SIGNAL_TYPE_ANTENNA) {
                type = TvContractEx.CHANNEL_TYPE_ATSC_ANTENNA;
            }
        } else if (isIsdbInput(inputId)) {
            if (getSignalType(context) == TVMediaTypeConstants.TV_SIGNAL_TYPE_CABLE) {
                type = TvContractEx.CHANNEL_TYPE_ISDB_CABLE;
            } else if (getSignalType(context) == TVMediaTypeConstants.TV_SIGNAL_TYPE_ANTENNA) {
                type = TvContractEx.CHANNEL_TYPE_ISDB_ANTENNA;
            }
        } else if (isAtv(inputId)) {
            // this is DVB system
            if (RtkConfigs.TvConfigs.HAS_ATV_CABLE) {
                // for DVB + NTSC, in Columbia?
                type = getAtvChannelInputType(context);
            } else {
                type = TvContractEx.CHANNEL_TYPE_ATV;
            }
        } else if (isDtv(inputId)) {
            String segments[] = inputId.split(PREFIX_HARDWARE_DEVICE);
            if (segments.length == 2) {
                String deviceString = segments[1];
                if (TextUtils.isDigitsOnly(deviceString)) {
                    int deviceId = Integer.parseInt(deviceString);
                    type = deviceId & 0x000000ff;
                }
            }
        }
        Log.d(TAG, "parseInputType, inputString = " + inputId + ", type = " + type);
        return type;
    }

    public static boolean isAtv(String inputId) {
        return inputId != null && inputId.contains(PACKAGE_NAME_ATV);
    }

    public static boolean isDtv(String inputId) {
        return inputId != null && inputId.contains(PACKAGE_NAME_DTV);
    }

    public static boolean isAv(String inputId) {
        return inputId != null && inputId.contains(PACKAGE_NAME_AV);
    }

    public static boolean isYpbpr(String inputId) {
        return inputId != null && inputId.contains(SERVICE_NAME_YPP);
    }

    public static boolean isHdmi(String inputId) {
        return inputId != null && inputId.contains(PACKAGE_NAME_HDMI);
    }

    public static boolean isVga(String inputId) {
        return inputId != null && inputId.contains(SERVICE_NAME_VGA);
    }

    public static boolean isGooglePlay(String inputId) {
        return inputId != null && inputId.contains(PACKAGE_NAME_GOOGLEPLAY);
    }

    public static boolean isPassthrough(String inputId) {
        return inputId != null && inputId.contains(PACKAGE_NAME_PASSTHROUGH);
    }

    public static boolean isATSC() {
        return RtkConfigs.TvConfigs.TV_SYSTEM == RtkConfigs.TvConfigs.TvSystemConstants.ATSC;
    }

    public static boolean isISDB() {
        return RtkConfigs.TvConfigs.TV_SYSTEM == RtkConfigs.TvConfigs.TvSystemConstants.ISDB;
    }

    public static boolean isDVB() {
        return RtkConfigs.TvConfigs.TV_SYSTEM == RtkConfigs.TvConfigs.TvSystemConstants.DVB;
    }

    public static boolean isAtscInput(String inputId) {
        return inputId != null && inputId.contains(SERVICE_NAME_ATSC);
    }

    public static boolean isIsdbInput(String inputId) {
        return inputId != null && inputId.contains(SERVICE_NAME_ISDB);
    }

    public static int getCurrentInputSetupType(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "com.android.tv_current_input_setup_type", TvContractEx.CHANNEL_TYPE_DTV_ANTENNA);
    }

    public static void setCurrentInputSetupType(Context context, int type) {
        Settings.Global.putInt(context.getContentResolver(), "com.android.tv_current_input_setup_type", type);
    }

    public static void setCurrentAgingTime(Context context, int time) {
        Settings.Global.putInt(context.getContentResolver(), "aging_time", time);
    }

    public static int getCurrentAgingTime(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "aging_time", 0);
    }


    public static int getAtvChannelInputType(Context context) {
        if (getCurrentInputSetupType(context) == TvContractEx.CHANNEL_TYPE_DTV_CABLE)
            return TvContractEx.CHANNEL_TYPE_ATV_CABLE;
        else
            return TvContractEx.CHANNEL_TYPE_ATV;
    }

    public static long getInputChannelForATVAndDTV(Context context, String input, TvInputInfo tvInputInfo) {
        Log.d(TAG, "getInputChannelForATVAndDTV ************************input: " + input);
        long channelId = -1;
        String key = "channelidforinput_".concat(input);
        try {
            channelId = Settings.Global.getLong(context.getContentResolver(), key, -1);
            Log.d(TAG,"hxr channelId:"+channelId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (channelId == -1) {
            Uri uri = TvContractEx.buildChannelUri(TvInputUtils.parseInputType(input, context, tvInputInfo));
            ContentResolver contentResolver = context.getContentResolver();
            String[] projection = {TvContract.Channels._ID};
            String select = TvContract.Channels.COLUMN_PACKAGE_NAME + "=?";
            LogHelper.d(TAG, "uri :" + uri);
            Cursor cursor = contentResolver.query(uri, projection, select,
                    new String[]{input.split("/")[0]}, null);
            while (cursor.moveToNext()) {
                channelId = cursor.getLong(0);
            }
            cursor.close();
        }
        Log.d(TAG, "getInputChannelForATVAndDTV ****************channelId: " + channelId);
        return checkValidChannel(context, input, channelId, tvInputInfo);
    }

    public static long checkValidChannel(Context context, String input, long channelId, TvInputInfo tvInputInfo) {
        Uri uri = TvContractEx.buildChannelUri(TvInputUtils.parseInputType(input, context, tvInputInfo));
        Log.d(TAG, "checkValidChannel checkValidChannel: " + uri);
        long result = channelId;
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {TvContract.Channels._ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        while (cursor.moveToNext()) {
            if (channelId == (result = cursor.getLong(0))) {
                return channelId;
            }
        }
        cursor.close();
        return result;
    }

    public static long getInputChannel(Context context, String input) {
        long channel = -1;
        try {
            input = adjustInputIdIfNeed(input);
            Log.d(TAG, "getInputChannel:");
            if (input != null && input.equals("")) {
                channel = Settings.Global.getLong(context.getContentResolver(), "channelidforinput_".concat(input));
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return channel;
    }

    private static String adjustInputIdIfNeed(String inputId) {
        String result = inputId;
        if (isATSC() || isISDB()) {
            if (isAtv(inputId) || isDtv(inputId)) {
                result = INPUT_ID_FOR_TV;
            }
        }
        return result;
    }

    public static Uri buildChannelUri1(Context context, String inputId, long channelId, TvInputInfo tvInputInfo) {
        if (inputId == null)
            return null;
        Uri channelUri = null;
        if (isAtv(inputId) || isDtv(inputId) || isAtscInput(inputId) || isIsdbInput(inputId)) {
            channelUri = TvContractEx.buildChannelUri(channelId, parseInputType(inputId, context, tvInputInfo));
        } else if (isPassthrough(inputId)) {
            channelUri = TvContract.buildChannelUriForPassthroughInput(inputId);
        } else {
            channelUri = TvContract.buildChannelUri(channelId);
        }
        return channelUri;
    }

    public static Boolean checkDisk(String subName) {
        File Usbfile = new File("/sys/bus/usb/devices");
        File[] files = Usbfile.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.canRead()) {
                        if (file.getName().indexOf(subName) > -1 || file.getName().indexOf(subName.toUpperCase()) > -1) {
                            Log.d("fujia", file.getAbsolutePath() + file.getName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String getUSBInternalPath(Context context) {
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getVolumesMethod = mStorageManager.getClass().getMethod("getVolumes");
            List<?> volumeInfoList = (List<?>) getVolumesMethod.invoke(mStorageManager);
            for (int i = 0; i < volumeInfoList.size(); i++) {
                Object volumeInfo = volumeInfoList.get(i);
                Method getDiskMethod = volumeInfoClazz.getMethod("getDisk");
                Object diskInfo = getDiskMethod.invoke(volumeInfo);
                if (diskInfo != null) {
                    Class<?> diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
                    Method isUsbMethod = diskInfoClazz.getDeclaredMethod("isUsb");
                    Boolean isUsb = (Boolean) isUsbMethod.invoke(diskInfo);
                    Log.d(TAG, "getUSBPath isUsb = " + isUsb);
                    if (isUsb) {
                        //Method getPathMethod = volumeInfoClazz.getMethod("getPath");
                        Method getInternalPathMethod = volumeInfoClazz.getMethod("getInternalPath");
                        //File path = (File) getPathMethod.invoke(volumeInfo);
                        File internalPath = (File) getInternalPathMethod.invoke(volumeInfo);
                        //if(DEBUG) Log.d(TAG,"getUSBPath path = "+(path==null?"":path.toString())); /* /storage/ECF3-E813 */
                        Log.d(TAG, "getUSBPath internalPath = " + (internalPath == null ? "" : internalPath.toString()));/* /mnt/media_rw/ECF3-E813 */
                        return internalPath == null ? null : internalPath.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void enableApplication(Context context, ComponentName component, boolean flag) {
        PackageManager pm = context.getPackageManager();
        int DisableState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        int EnableState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        pm.setComponentEnabledSetting(component, flag ? EnableState : DisableState, PackageManager.DONT_KILL_APP);
    }

    public static void injectKeyEvent(int keyCode) {
        long now = SystemClock.uptimeMillis();
        KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0);
        KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0);

        try {
            Class<?> ServiceManager = Class.forName("android.os.ServiceManager");
            Method getService = ServiceManager.getMethod("getService", String.class);
            Object remoteService = getService.invoke(null, Context.INPUT_SERVICE);

            Class<?> cStub = Class.forName("android.hardware.input.IInputManager$Stub");
            Method asInterface = cStub.getMethod("asInterface", IBinder.class);
            Object IInputManager = asInterface.invoke(null, remoteService);

            Method injectInputEvent = IInputManager.getClass().getMethod("injectInputEvent", InputEvent.class, int.class);
            injectInputEvent.invoke(IInputManager, down, 0);
            injectInputEvent.invoke(IInputManager, up, 0);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}