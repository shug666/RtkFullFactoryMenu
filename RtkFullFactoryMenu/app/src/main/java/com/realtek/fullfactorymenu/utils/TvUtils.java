package com.realtek.fullfactorymenu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.realtek.fullfactorymenu.api.manager.TvCommonManager;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class TvUtils {
    private static boolean DEBUG = true;
    private static String TAG = "TvUtils";
    public static final String AUTHORITY_TV_SERVICE = "com.toptech.tv.service";

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

    public static void copyTvDBToUSB(String path){
        String db = path + "/" + "tv.db";
        String dbShm = path + "/" + "tv.db-shm";
        String dbWal = path + "/" + "tv.db-wal";

        copyFile("/data/data/com.android.providers.tv/databases/tv.db", db);
        copyFile("/data/data/com.android.providers.tv/databases/tv.db-shm", dbShm);
        copyFile("/data/data/com.android.providers.tv/databases/tv.db-wal", dbWal);
    }

    public static void copyTvDBToProvider(String path){
        String db = path + "/" + "tv.db";
        String dbShm = path + "/" + "tv.db-shm";
        String dbWal = path + "/" + "tv.db-wal";

        copyFile(db, "/data/data/com.android.providers.tv/databases/tv.db");
        copyFile(dbShm, "/data/data/com.android.providers.tv/databases/tv.db-shm");
        copyFile(dbWal, "/data/data/com.android.providers.tv/databases/tv.db-wal");
    }

    public static void copyFile(String fromeFilePath, String toTilePath){
        File fromFile   = new File(fromeFilePath);
        File toFile = new File(toTilePath);
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        boolean success = false;
        if(fromFile.exists()){
            try {
                if(toFile.exists()){
                    toFile.delete();
                }
                toFile.createNewFile();
                inChannel = new FileInputStream(fromFile).getChannel();
                outChannel = new FileOutputStream(toFile).getChannel();
                if(DEBUG) Log.d(TAG, fromeFilePath+" file position: "+inChannel.position());
                if(DEBUG) Log.d(TAG, fromeFilePath+" file size: "+inChannel.size());
                if(DEBUG) Log.d(TAG, toFile+" file size: "+outChannel.size());
                inChannel.transferTo(0, inChannel.size(), outChannel);
                success = true;
                //Toast.makeText(context,"copy dtv data done", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                if(DEBUG) Log.d(TAG, "toFile error: "+toTilePath);
                e.printStackTrace();
            }finally{
                try {
                    if (inChannel != null) {
                        inChannel.close();
                        inChannel = null;
                    }
                    if(outChannel != null){
                        outChannel.close();
                        outChannel = null;
                    }
                } catch (IOException e) {
                    if(DEBUG) Log.d(TAG, "file close error:"+toTilePath);
                    e.printStackTrace();
                }
            }
            if(success){
                // sendDoneBroadcast(context);
            }
        }else{
            if(DEBUG) Log.d(TAG, "fromFile did not exists "+fromeFilePath);
            //Toast.makeText(context, context.getResources().getString(R.string.toast_source_file_not_exist), Toast.LENGTH_SHORT).show();
        }
    }
}
