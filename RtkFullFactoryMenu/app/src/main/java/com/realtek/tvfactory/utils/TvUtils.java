package com.realtek.tvfactory.utils;

import static com.realtek.tvfactory.utils.Constants.MANUFACTURER_TT;

import android.content.Context;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.realtek.tvfactory.api.manager.TvCommonManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TvUtils {
    private static boolean DEBUG = true;
    private static String TAG = "TvUtils";
    public static final String AUTHORITY_TV_SERVICE = "com." + ByteTransformUtils.asciiToString(MANUFACTURER_TT) + ".tv.service";

    private TvUtils() {
    }

    public static boolean isAtv(int tvInputSource) {
        return tvInputSource == TvCommonManager.INPUT_SOURCE_ATV;
    }

    public static boolean isDtv(int tvInputSource) {
        return tvInputSource == TvCommonManager.INPUT_SOURCE_DTV
                || tvInputSource == TvCommonManager.INPUT_SOURCE_DVBT
                || tvInputSource == TvCommonManager.INPUT_SOURCE_DVBC
                || tvInputSource == TvCommonManager.INPUT_SOURCE_DTMB
                || tvInputSource == TvCommonManager.INPUT_SOURCE_ATSC
                || tvInputSource == TvCommonManager.INPUT_SOURCE_DVBS
                || tvInputSource == TvCommonManager.INPUT_SOURCE_ISDB;
    }

    public static boolean isAv(int tvInputSource) {
        return tvInputSource == TvCommonManager.INPUT_SOURCE_CVBS
                || tvInputSource == TvCommonManager.INPUT_SOURCE_CVBS2
                || tvInputSource == TvCommonManager.INPUT_SOURCE_CVBS3
                || tvInputSource == TvCommonManager.INPUT_SOURCE_CVBS4
                || tvInputSource == TvCommonManager.INPUT_SOURCE_CVBS5
                || tvInputSource == TvCommonManager.INPUT_SOURCE_CVBS6
                || tvInputSource == TvCommonManager.INPUT_SOURCE_CVBS7
                || tvInputSource == TvCommonManager.INPUT_SOURCE_CVBS8;
    }

    public static boolean isYpbpr(int tvInputSource) {
        return tvInputSource == TvCommonManager.INPUT_SOURCE_YPBPR
                || tvInputSource == TvCommonManager.INPUT_SOURCE_YPBPR2
                || tvInputSource == TvCommonManager.INPUT_SOURCE_YPBPR3;
    }

    public static boolean isVga(int tvInputSource) {
        return tvInputSource == TvCommonManager.INPUT_SOURCE_VGA
                || tvInputSource == TvCommonManager.INPUT_SOURCE_VGA2
                || tvInputSource == TvCommonManager.INPUT_SOURCE_VGA3;
    }

    public static boolean isHdmi(int tvInputSource) {
        return tvInputSource == TvCommonManager.INPUT_SOURCE_HDMI
                || tvInputSource == TvCommonManager.INPUT_SOURCE_HDMI2
                || tvInputSource == TvCommonManager.INPUT_SOURCE_HDMI3
                || tvInputSource == TvCommonManager.INPUT_SOURCE_HDMI4;
    }


    public static Uri buildChannelWithId(int channelId) {
        Builder builder = new Uri.Builder();
        builder.authority(AUTHORITY_TV_SERVICE);
        builder.appendQueryParameter("channelId", Integer.toString(channelId));
        return builder.build();
    }

    public static Uri buildChannelWithNumber(int channelNumber) {
        Builder builder = new Uri.Builder();
        builder.authority(AUTHORITY_TV_SERVICE);
        builder.appendQueryParameter("channelNumber", Integer.toString(channelNumber));
        return builder.build();
    }

    public static String freqUnitChangeMHZ(int frequency) {
        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toString(frequency / 1000));
        builder.append(".");
        builder.append(Integer.toString((frequency % 1000) / 10));
        builder.append("MHZ");
        return builder.toString();
    }

    public static void interceptSpecialKeyEvent(Window window, boolean intercept, int... noNeedIntercept) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        /*int keyFeatures = attributes.keyFeatures;
        if (intercept) {
            keyFeatures |= WindowManager.LayoutParams.KEY_FEATURE_POWER_PASS_TO_USER;
            keyFeatures |= WindowManager.LayoutParams.KEY_FEATURE_HOME_PASS_TO_USER;
            keyFeatures |= WindowManager.LayoutParams.KEY_FEATURE_VOLUME_PASS_TO_USER;
            keyFeatures |= WindowManager.LayoutParams.KEY_FEATURE_APP_LAUNCHER_PASS_TO_USER;
            for (int i = 0; i < noNeedIntercept.length; i++) {
                keyFeatures &= ~noNeedIntercept[i];
            }
        } else {
            keyFeatures &= ~WindowManager.LayoutParams.KEY_FEATURE_POWER_PASS_TO_USER;
            keyFeatures &= ~WindowManager.LayoutParams.KEY_FEATURE_HOME_PASS_TO_USER;
            keyFeatures &= ~WindowManager.LayoutParams.KEY_FEATURE_VOLUME_PASS_TO_USER;
            keyFeatures &= ~WindowManager.LayoutParams.KEY_FEATURE_APP_LAUNCHER_PASS_TO_USER;
            for (int i = 0; i < noNeedIntercept.length; i++) {
                keyFeatures |= noNeedIntercept[i];
            }
        }
        if (keyFeatures != attributes.keyFeatures) {
            attributes.keyFeatures = keyFeatures;
            window.setAttributes(attributes);
        }*/
    }

    public static boolean copyTvDBToUSB(String path){
        String db = path + "/" + "tv.db";
        String dbShm = path + "/" + "tv.db-shm";
        String dbWal = path + "/" + "tv.db-wal";

        boolean copyStatus = copyFile("/data/data/com.android.providers.tv/databases/tv.db", db);
        copyStatus = copyStatus && copyFile("/data/data/com.android.providers.tv/databases/tv.db-shm", dbShm);
        copyStatus = copyStatus && copyFile("/data/data/com.android.providers.tv/databases/tv.db-wal", dbWal);
        return copyStatus;
    }

    public static boolean copyTvDBToProvider(String path){
        String db = path + "/" + "tv.db";
        String dbShm = path + "/" + "tv.db-shm";
        String dbWal = path + "/" + "tv.db-wal";

        boolean copyStatus = copyFile(db, "/data/data/com.android.providers.tv/databases/tv.db");
        copyStatus = copyStatus && copyFile(dbShm, "/data/data/com.android.providers.tv/databases/tv.db-shm");
        copyStatus = copyStatus && copyFile(dbWal, "/data/data/com.android.providers.tv/databases/tv.db-wal");
        return copyStatus;
    }

    public static boolean copyFile(String fromeFilePath, String toTilePath){
        File fromFile   = new File(fromeFilePath);
        File toFile = new File(toTilePath);
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        boolean success = false;
        if(fromFile.exists()){
            try {
                if(toFile.exists()){
                    FileUtils.chmodFile(toFile, 0777);
                    toFile.delete();
                    toFile.createNewFile();
                } else {
                    try {
                        toFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        if(DEBUG) Log.d(TAG, "createNewFile:"+e);
                        String path = toFile.getParent();
                        if(DEBUG) Log.d(TAG, "getParent:"+path);
                        if (path == null || TextUtils.isEmpty(path))
                            return false;
                        toFile = new File(path);
                        if (toFile.exists() && toFile.isFile()) {
                            boolean isDelete = toFile.delete();
                            Log.d(TAG, String.format("delete file(%s) success!", isDelete));
                        }
                        if (toFile.mkdirs()) {
                            if (DEBUG) Log.d(TAG, "mkdirs success!");
                            toFile = new File(toTilePath);
                            if (!toFile.createNewFile()) {
                                if(DEBUG) Log.e(TAG, "create " + toTilePath + " failed!");
                                return false;
                            }
                        } else {
                            if(DEBUG) Log.e(TAG, "mkdirs " + path + " failed!");
                            return false;
                        }
                    }
                }
                inChannel = new FileInputStream(fromFile).getChannel();
                outChannel = new FileOutputStream(toFile).getChannel();
                if(DEBUG) Log.d(TAG, fromeFilePath+" file position: "+inChannel.position());
                if(DEBUG) Log.d(TAG, fromeFilePath+" file size: "+inChannel.size());
                if(DEBUG) Log.d(TAG, toFile+" file size: "+outChannel.size());
                inChannel.transferTo(0, inChannel.size(), outChannel);
                success = true;
                //Toast.makeText(context,"copy dtv data done", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                if(DEBUG) Log.e(TAG, "toFile error: "+toTilePath);
                e.printStackTrace();
            } finally {
                if (inChannel != null) {
                    try {
                        inChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        if(DEBUG) Log.e(TAG, "inChannel close error:"+toTilePath);
                    }
                }

                if(outChannel != null){
                    try {
                        outChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        if(DEBUG) Log.e(TAG, "outChannel close error:"+e);
                    }
                }
            }
            if(success){
                // sendDoneBroadcast(context);
            }
        }else{
            if(DEBUG) Log.d(TAG, "fromFile did not exists "+fromeFilePath);
            //Toast.makeText(context, context.getResources().getString(R.string.toast_source_file_not_exist), Toast.LENGTH_SHORT).show();
        }
        return success;
    }

    public static void sendVirtualKey(int keyCode){
        long now = SystemClock.uptimeMillis();
        KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0);
        KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0);
        int mode = InputManager.INJECT_INPUT_EVENT_MODE_ASYNC;
        InputManager.getInstance().injectInputEvent(down, mode);
        InputManager.getInstance().injectInputEvent(up, mode);
    }

    public static void setCurrentAgingTime(Context context, int time) {
        Settings.Global.putInt(context.getContentResolver(), "aging_time", time);
    }

    public static int getCurrentAgingTime(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "aging_time", 0);
    }
}
