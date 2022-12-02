package com.realtek.fullfactorymenu.api.impl;

import static com.realtek.fullfactorymenu.utils.Constants.MANUFACTURER_BVT;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.realtek.fullfactorymenu.utils.ByteTransformUtils;
import com.realtek.tv.Factory;
import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.api.listener.ICommandCallback;
import com.realtek.fullfactorymenu.utils.LogHelper;
import android.os.Handler.Callback;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Files;

public class UpgradeApi implements Callback {
    private static final String TAG = "UpgradeApi";

    private static UpgradeApi mInstance = null;
    private final FactoryApplication mFactoryApplication;
    private final Context mContext;

    private HandlerThread mBackgroundThread = new HandlerThread(TAG);
    private Handler mBackgroundHandler;


    private static boolean mAlreadyUpdateMac = false;
    private static boolean mAlreadyUpdateCi = false;
    private static boolean mAlreadyUpdateHdcp = false;
    private static boolean mAlreadyUpdateHdcp2 = false;
    private static boolean mAlreadyUpdateWidevine = false;
    private static boolean mAlreadyUpdatePlayready = false;
    private static boolean mAlreadyUpdateAttestation = false;
    private static boolean mAlreadyUpdateNetflixESN = false;
    private static boolean mAlreadyUpdateRmca = false;

    private String effectiveStoragePath = null;
    private String mBootlogoPath;
    private String mBootvideoPath;


    private static final String PQ_BIN_NAME = "vip_test_pq.bin";
    private static final String OSD_BIN_NAME = "vip_test_osd.bin";
    private static final String BOOT_LOGO_NAME = "bootfile.raw";
    private static final String BOOT_VIDEO_NAME = "bootvideo.ts";
    private static final String keyTemporary = "/tmp";

    private static final boolean KEYS_HDCP_1_4_TYPE = true;
    private static final boolean KEYS_HDCP_2_2_TYPE = false;
    public static final int KEYS_HDCP1_TYPE = -1;
    public static final int KEYS_HDCP2_TYPE = 0;
    public static final int KEYS_WIDEVINE_TYPE = 3;
    public static final int KEYS_PLAYREADY_TYPE = 6;
    public static final int KEYS_ATTESTATION_TYPE = 7;
    public static final int KEYS_Netflix_ESN_TYPE = 8;
    public static final int KEYS_CIKEY_TYPE = 9;
    public static final int KEYS_MAC_TYPE = 12;
    public static final int KEYS_RMCA = 16;


    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.obj instanceof Pair<?, ?>) {
            Pair<?, ?> pair = (Pair<?, ?>) msg.obj;
            if (pair.first instanceof String && pair.second instanceof ICommandCallback) {
                String command = (String) pair.first;
                ICommandCallback callback = (ICommandCallback) pair.second;
                handleSyncCommand(command, callback);
                return true;
            }
        }
        return false;
    }

    public UpgradeApi() {
        mInstance = this;
        mFactoryApplication = FactoryApplication.getInstance();
        mContext = mFactoryApplication.getApplicationContext();
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper(), this);
    }

    public static UpgradeApi getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (UpgradeApi.class) {
                if (mInstance == null) {
                    mInstance = new UpgradeApi();
                }
            }
        }
        return mInstance;
    }

    public void sendSyncCommand(String command, ICommandCallback callback) {
        Message msg = mBackgroundHandler.obtainMessage(0, Pair.create(command, callback));
        mBackgroundHandler.sendMessage(msg);
    }

    private void handleSyncCommand(String command, ICommandCallback callback) {
        try {
            LogHelper.d(TAG, "command : %s", command);
            if (command.startsWith("UPGRADE_MAC".concat("#"))) {
                Bundle bundle = new Bundle();
                String displayMacAddr = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_MAC_TYPE);
                if (SystemProperties.getInt("service.upgrade.mac", 0) == 1) {
                    if (!displayMacAddr.isEmpty()) {
                        bundle.putString("Failed", "MAC Already Upgrade!");
                        callback.complete(0, bundle);
                        return;
                    }
                }
                if (mAlreadyUpdateMac) {
                    bundle.putString("Failed", "MAC Already Upgrade!");
                    callback.complete(0, bundle);
                    return;
                }
                String[] parts = command.split("#");

                String usbPats = parts[1];
                String macAddress = "";
                String macAddressWithColon = "";
//                File internalFile = FileUtils.getInternalFile(mContext, new File(usbPats));
                File internalFile = new File(usbPats);
                macAddress = getMacAddressFromUsb(internalFile == null ? null : internalFile.getAbsolutePath());
                LogHelper.d(TAG, "macAddress : %s , length = %s", macAddress, macAddress.length());
                if (macAddress.length() != 0) {
                    macAddressWithColon = macAddress.substring(0, 2) + ":" + macAddress.substring(2, 4) + ":"
                            + macAddress.substring(4, 6) + ":" + macAddress.substring(6, 8) + ":"
                            + macAddress.substring(8, 10) + ":" + macAddress.substring(10, 12);
                    mFactoryApplication.getExtTv().extTv_tv001_SetValueString("LoadMACfromUSB", macAddressWithColon);
                    mFactoryApplication.getExtTv().extTv_tv001_SetTrustZoneKeysSerialNumber(KEYS_MAC_TYPE, macAddressWithColon);
                    if (doFactorySaveSync("mac")) {
                        displayMacAddr = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_MAC_TYPE);
                        if (displayMacAddr != null && !displayMacAddr.isEmpty()) {
                            bundle.putString("displayMacAddr", displayMacAddr);
                            deleteUpdateMacAddressInUsb("MAC" + macAddress + ".bin");
                            mAlreadyUpdateMac = true;
                            SystemProperties.set("service.upgrade.mac", "1");
                            bundle.putString("Success", "Load " + macAddressWithColon + " OK!");
                            callback.complete(1, bundle);
                        } else {
                            bundle.putString("failed", "get mac address failed");
                            callback.complete(2, bundle);
                        }
                    } else {
                        bundle.putString("Failed!", "Save " + macAddress + " fail!");
                        callback.complete(0, bundle);
                    }
                } else {
                    bundle.putString("Cannot", "Cannot find MAC info in MACIndex.ini!");
                    callback.complete(3, bundle);
                }
                return;
            }

            if (command.startsWith("GET_MAC")) {
                Bundle bundle = new Bundle();
                String displayMacAddr = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_MAC_TYPE);
                if (displayMacAddr != null && !displayMacAddr.isEmpty()) {
                    bundle.putString("displayMacAddr", displayMacAddr);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayMacAddr", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }

            if (command.startsWith("SET_HDCP_UPGRADE".concat("#"))) {
                Bundle bundle = new Bundle();
                String displayHDCP14 = mFactoryApplication.getExtTv().extTv_tv001_GetHdcpKeySerialNumber(KEYS_HDCP_1_4_TYPE);
                if (SystemProperties.getInt("service.upgrade.hdcp", 0) == 1) {
                    if (!displayHDCP14.isEmpty()) {
                        bundle.putString("Failed", "HDCP Already Upgrade!");
                        callback.complete(0, bundle);
                        return;
                    }
                }
                if (mAlreadyUpdateHdcp) {
                    bundle.putString("Failed", "HDCP Already Upgrade!");
                    callback.complete(0, bundle);
                    return;
                }
                String[] parts = command.split("#");
                String usbPats = parts[1];
//                String[] hdcpKey = new String[2];
//                File internalFile = FileUtils.getInternalFile(mContext, new File(usbPats));
                File internalFile = new File(usbPats);
                String hdcpKeyNameFromUsb = getHdcpKeyNameFromUsb(1, internalFile.getPath());
                byte[] hdcpKeyFromUsb = getHdcpKeyFromUsb(hdcpKeyNameFromUsb, usbPats);
//                hdcpKey = getHdcpKeyFromUsb(1, internalFile.getPath());
                if (hdcpKeyFromUsb != null && hdcpKeyFromUsb.length != 0) {
                    if (mFactoryApplication.getFactory().burnHdcp1xKey(hdcpKeyFromUsb, hdcpKeyFromUsb.length)) {
                        mFactoryApplication.getExtTv().extTv_tv001_SetHdcpKeySerialNumber(
                                hdcpKeyNameFromUsb.substring(hdcpKeyNameFromUsb.lastIndexOf("_") + 1, hdcpKeyNameFromUsb.lastIndexOf(".")), KEYS_HDCP_1_4_TYPE);
                        if (doFactorySaveSync("hdcp1.4")) {
                            displayHDCP14 = mFactoryApplication.getExtTv().extTv_tv001_GetHdcpKeySerialNumber(KEYS_HDCP_1_4_TYPE);
                            Log.d(TAG, "displayHDCP14: " + displayHDCP14);
                            if (displayHDCP14 != null && !displayHDCP14.isEmpty()) {
                                bundle.putString("displayHDCP14", displayHDCP14);
                                bundle.putString("Success", "Load " + hdcpKeyNameFromUsb + " OK!");
                                deleteFileInUsb(hdcpKeyNameFromUsb);
                                mAlreadyUpdateHdcp = true;
                                SystemProperties.set("service.upgrade.hdcp", "1");
                                callback.complete(1, bundle);
                            } else {
                                bundle.putString("Failed!", "Save " + hdcpKeyNameFromUsb + " fail!");
                                callback.complete(0, bundle);
                            }
                        }
                    }
                } else {
                    bundle.putString("Cannot", "Cannot find hdcp1 key file!");
                    callback.complete(2, bundle);
                }
            }

            if (command.startsWith("GET_HDCP")) {
                Log.d("mmm", "command.startsWith(GET_HDCP)");
                Bundle bundle = new Bundle();
                String displayHDCP14 = mFactoryApplication.getExtTv().extTv_tv001_GetHdcpKeySerialNumber(KEYS_HDCP_1_4_TYPE);
                Log.d(TAG, "displayHDCP14: " + displayHDCP14);
                if (displayHDCP14 != null && !displayHDCP14.isEmpty()) {
                    bundle.putString("displayHDCP14", displayHDCP14);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayHDCP14", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }

            if (command.startsWith("SET_HDCP_UPGRADE22".concat("#"))) {
                Bundle bundle = new Bundle();
                String displayHDCP22 = mFactoryApplication.getExtTv().extTv_tv001_GetHdcpKeySerialNumber(KEYS_HDCP_2_2_TYPE);
                if (SystemProperties.getInt("service.upgrade.hdcp2", 0) == 1) {
                    if (!displayHDCP22.isEmpty()) {
                        bundle.putString("Failed", "HDCP2.2 Already Upgrade!");
                        callback.complete(0, bundle);
                        return;
                    }
                }
                if (mAlreadyUpdateHdcp2) {
                    bundle.putString("Failed", "HDCP2.2 Already Upgrade!");
                    callback.complete(0, bundle);
                    return;
                }
                String[] parts = command.split("#");
                String usbPats = parts[1];
//                String[] hdcp2Key = new String[2];
//                File internalFile = FileUtils.getInternalFile(mContext, new File(usbPats));
                File internalFile = new File(usbPats);
                String hdcpKeyNameFromUsb = getHdcpKeyNameFromUsb(2, internalFile.getPath());
                byte[] hdcpKeyFromUsb = getHdcpKeyFromUsb(hdcpKeyNameFromUsb, usbPats);
//                hdcp2Key = getHdcpKeyFromUsb(2, internalFile.getPath());
                if (hdcpKeyFromUsb != null && hdcpKeyFromUsb.length != 0) {
                    if (mFactoryApplication.getFactory().burnHdcp2xKey(hdcpKeyFromUsb, hdcpKeyFromUsb.length)) {
                        mFactoryApplication.getExtTv().extTv_tv001_SetHdcpKeySerialNumber(hdcpKeyNameFromUsb
                                .substring(hdcpKeyNameFromUsb.lastIndexOf("_") + 1, hdcpKeyNameFromUsb.lastIndexOf(".")), KEYS_HDCP_2_2_TYPE);
                        if (doFactorySaveSync("hdcp2.2")) {
                            displayHDCP22 = mFactoryApplication.getExtTv().extTv_tv001_GetHdcpKeySerialNumber(KEYS_HDCP_2_2_TYPE);
                            Log.d(TAG, "displayHDCP22: " + displayHDCP22);
                            if (displayHDCP22 != null && !displayHDCP22.isEmpty()) {
                                bundle.putString("displayHDCP22", displayHDCP22);
                                bundle.putString("Success", "Load " + hdcpKeyNameFromUsb + " OK!");
                                deleteFileInUsb(hdcpKeyNameFromUsb);
                                mAlreadyUpdateHdcp2 = true;
                                SystemProperties.set("service.upgrade.hdcp2", "1");
                                callback.complete(1, bundle);
                            } else {
                                bundle.putString("Failed!", "Save " + hdcpKeyNameFromUsb + " fail!");
                                callback.complete(0, bundle);
                            }
                        }
                    }
                } else {
                    bundle.putString("Cannot", "Cannot find HDCP2 KEY file!");
                    callback.complete(2, bundle);
                }
            }

            if (command.startsWith("GET_UPGRADE_HDCP")) {
                Bundle bundle = new Bundle();
                String displayHDCP22 = mFactoryApplication.getExtTv().extTv_tv001_GetHdcpKeySerialNumber(KEYS_HDCP_2_2_TYPE);
                Log.d(TAG, "displayHDCP22: " + displayHDCP22);
                if (displayHDCP22 != null && !displayHDCP22.isEmpty()) {
                    bundle.putString("displayHDCP22", displayHDCP22);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayHDCP22", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }

            if (command.startsWith("SET_WIDEVINE_UPGRADE".concat("#"))) {
                Bundle bundle = new Bundle();
                String displayWvKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_WIDEVINE_TYPE);
                if (SystemProperties.getInt("service.upgrade.widevine", 0) == 1) {
                    if (!displayWvKey.isEmpty()) {
                        bundle.putString("Failed", "WIDEVINE Already Upgrade!");
                        callback.complete(0, bundle);
                        return;
                    }
                }
                if (mAlreadyUpdateWidevine) {
                    bundle.putString("Failed", "WIDEVINE Already Upgrade!");
                    callback.complete(0, bundle);
                    return;
                }
                String[] parts = command.split("#");
                String usbPats = parts[1];
                //String wvKeyFile = loadKeyNameFormUsb(usbPats, KEYS_WIDEVINE_TYPE);
                String wvKeyFile = getWidevineKeyFromUsb(usbPats);
                if (wvKeyFile != null && !wvKeyFile.isEmpty()) {
                    if (mFactoryApplication.getExtTv().extTv_tv001_BurnTrustZoneKeys(KEYS_WIDEVINE_TYPE, usbPats + File.separator + wvKeyFile)) {
                        String name = wvKeyFile.substring(0, wvKeyFile.length() - 4);
                        Log.d(TAG, "name:" + name);
                        if (mFactoryApplication.getExtTv().extTv_tv001_SetTrustZoneKeysSerialNumber(KEYS_WIDEVINE_TYPE, name)) {
                            bundle.putString("displayWvKey", name);
                            bundle.putString("Success", "Already upgrade " + wvKeyFile + " OK!");
                            mAlreadyUpdateWidevine = true;
                            SystemProperties.set("service.upgrade.widevine", "1");
                            callback.complete(1, bundle);
                            deleteFileInPath(usbPats, wvKeyFile);
                            return;
                        }
                    }
                    bundle.putString("Failed!", "Burn " + wvKeyFile + " fail!");
                    callback.complete(0, bundle);
                } else {
                    bundle.putString("Cannot", "Cannot find WIDEVINE key file!");
                    callback.complete(2, bundle);
                }
            }
            if (command.startsWith("SET_WIDEVINE_CLEAR".concat("#"))) {
                Log.d("mmm", "SET_WIDEVINE_CLEAR");
                Bundle bundle = new Bundle();
                String displaywvKey = mFactoryApplication.getSysCtrl().getValueString("GetWidevineKey");
                if (displaywvKey != null && "NULL".equals(displaywvKey)) {
                    bundle.putString("Failed", "Please update WideVine key first!");
                    callback.complete(2, bundle);
                    return;
                }
                boolean success = mFactoryApplication.getSysCtrl().setValueInt("Misc_DRMKey_ClearWidevinekey", 0);
                if (success) {
                    mFactoryApplication.getSysCtrl().setValueString("SetWidevineKey", "NULL");
                    if (doFactorySaveSync("widevine")) {
                        String displayWvKey = mFactoryApplication.getSysCtrl().getValueString("GetWidevineKey");
                        bundle.putString("displayWvKey", displayWvKey);
                    }
                    bundle.putString("Success", "Clear wv key OK!");
                    mAlreadyUpdateWidevine = false;
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("Fail", "Clear wv key NG!");
                    callback.complete(0, bundle);
                }
            }

            if (command.startsWith("GET_WIDEVINE")) {
                Bundle bundle = new Bundle();
                String displayWvKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_WIDEVINE_TYPE);
                Log.d(TAG, "displayWvKey: " + displayWvKey);
                if (displayWvKey != null && !displayWvKey.isEmpty()) {
                    bundle.putString("displayWvKey", displayWvKey);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayWvKey", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }

            if (command.startsWith("SET_PLAYREADY_UPGRADE".concat("#"))) {
                Bundle bundle = new Bundle();
                String displayPrKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_PLAYREADY_TYPE);
                if (SystemProperties.getInt("service.upgrade.playready", 0) == 1) {
                    if (!displayPrKey.isEmpty()) {
                        bundle.putString("Failed", "PLAYREADY Already Upgrade!");
                        callback.complete(0, bundle);
                        return;
                    }
                }
                if (mAlreadyUpdatePlayready) {
                    bundle.putString("Failed", "PLAYREADY Already Upgrade!");
                    callback.complete(0, bundle);
                    return;
                }
                String[] parts = command.split("#");
                String usbPats = parts[1];
                String prKeyFile = new String();
                File internalFile = new File(usbPats);
                prKeyFile = getPlayreadyKeyFromUsb(internalFile == null ? null : internalFile.getAbsolutePath());
                if (prKeyFile != null && !prKeyFile.isEmpty()) {
                    if (mFactoryApplication.getExtTv().extTv_tv001_BurnTrustZoneKeys(KEYS_PLAYREADY_TYPE,
                            effectiveStoragePath.concat(File.separator).concat(prKeyFile))) {
                        mFactoryApplication.getExtTv().extTv_tv001_SetTrustZoneKeysSerialNumber(KEYS_PLAYREADY_TYPE,
                                prKeyFile.substring(prKeyFile.lastIndexOf("_") + 1, prKeyFile.lastIndexOf(".")));
                        if (doFactorySaveSync("PLAYREADY")) {
                            displayPrKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(6);
                            if (displayPrKey != null && !displayPrKey.isEmpty()) {
                                bundle.putString("displayPrKey", displayPrKey);
                                bundle.putString("Success", "Already upgrade " + displayPrKey + " OK!");
                                mAlreadyUpdatePlayready = true;
                                SystemProperties.set("service.upgrade.playready", "1");
                                callback.complete(1, bundle);
                                return;
                            }
                        }
                    }
                    bundle.putString("Failed!", "Save " + prKeyFile + " fail!");
                    callback.complete(0, bundle);
                } else {
                    bundle.putString("Cannot", "Cannot find Playready key file!");
                    callback.complete(2, bundle);
                }
            }

            if (command.startsWith("SET_PLAYREADY_CLEAR".concat("#"))) {
                Bundle bundle = new Bundle();
                String displayPrKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_PLAYREADY_TYPE);
                if (displayPrKey != null && "NULL".equals(displayPrKey)) {
                    bundle.putString("Failed", "Please update Playready key first!");
                    callback.complete(2, bundle);
                    return;
                }
                boolean success = mFactoryApplication.getSysCtrl().setValueInt("Misc_PLAYREADYKey_ClearPlayReadykey", 0);
                if (success) {
                    mFactoryApplication.getSysCtrl().setValueString("SetPlayreadyKey", "NULL");
                    if (doFactorySaveSync("PLAYREADY")) {
                        displayPrKey = mFactoryApplication.getSysCtrl().getValueString("GetPlayreadyKey");
                        bundle.putString("displayPrKey", displayPrKey);
                    }
                    bundle.putString("Success", "Clear pr key OK!");
                    mAlreadyUpdatePlayready = false;
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("Fail", "Clear pr key NG!");
                    callback.complete(0, bundle);
                }
            }

            if (command.startsWith("GET_PLAYREADY")) {
                Bundle bundle = new Bundle();
                String displayPrKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_PLAYREADY_TYPE);
                Log.d(TAG, "displayPrKey: " + displayPrKey);
                if (displayPrKey != null && !displayPrKey.isEmpty()) {
                    bundle.putString("displayPrKey", displayPrKey);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayPrKey", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }

            if (command.startsWith("SET_CI_KEY_UPGRADE".concat("#"))) {
                Bundle bundle = new Bundle();
                String displayCIKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_CIKEY_TYPE);
                if (SystemProperties.getInt("service.upgrade.ci", 0) == 1) {
                    if (!displayCIKey.isEmpty()) {
                        bundle.putString("Failed", "CI_KEY Already Upgrade!");
                        callback.complete(0, bundle);
                        return;
                    }
                }

                if (mAlreadyUpdateCi) {
                    bundle.putString("Failed", "CI_KEY Already Upgrade!");
                    callback.complete(0, bundle);
                    return;
                }
                String[] parts = command.split("#");
                String usbPats = parts[1];
                //String CiKeyFile = loadKeyNameFormUsb(usbPats, KEYS_CIKEY_TYPE);
                String CiKeyFile = getCiKeyFromUsb(usbPats);
                if (CiKeyFile != null && !CiKeyFile.isEmpty()) {
                    if (mFactoryApplication.getExtTv().extTv_tv001_BurnTrustZoneKeys(KEYS_CIKEY_TYPE,usbPats + File.separator + CiKeyFile)) {
                        String name = CiKeyFile.substring(0, CiKeyFile.length() - 4);
                        Log.d(TAG, "name:" + name);
                        if (mFactoryApplication.getExtTv().extTv_tv001_SetTrustZoneKeysSerialNumber(KEYS_CIKEY_TYPE,name)) {
                            bundle.putString("displayCIKey", name);
                            bundle.putString("Success", "Load " + CiKeyFile + " OK!");
                            SystemProperties.set("service.upgrade.ci", "1");
                            mAlreadyUpdateCi = true;
                            callback.complete(1, bundle);
                            deleteFileInPath(usbPats, CiKeyFile);
                            return;
                        }
                    }
                    bundle.putString("Failed", "Save " + CiKeyFile + " fail!");
                    callback.complete(0, bundle);
                } else {
                    bundle.putString("Cannot", "Cannot find ci key file!");
                    callback.complete(2, bundle);
                }
                return;
            }

            if (command.startsWith("GET_CI_KEY")) {
                Bundle bundle = new Bundle();
                String displayCIKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_CIKEY_TYPE);
                Log.d(TAG, "displayCIKey: " + displayCIKey);
                if (displayCIKey != null && !displayCIKey.isEmpty()) {
                    bundle.putString("displayCIKey", displayCIKey);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayCIKey", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }

            if (command.startsWith("UPGRADE_PQ".concat("#"))) {
                Bundle arg1 = new Bundle();
                String[] parts = command.split("#");
                String filePath = parts[1];
                String pqFileName = new String(PQ_BIN_NAME);
                String path = filePath.concat("/").concat(pqFileName);
//                File file = FileUtils.getInternalFile(mContext, new File(path));
                File file = new File(path);
                if (!file.exists()) {
                    arg1.putString("NotFind", PQ_BIN_NAME);
                    callback.complete(0, arg1);
                    return;
                }
                mFactoryApplication.getSysCtrl().setValueString("PqUpgrade", file.getPath());
                callback.complete(1, arg1);
                return;
            }

            if (command.startsWith("USBUpgrade".concat("#"))) {
                String UPDATE_IMG = "install.img";
                Bundle arg1 = new Bundle();
                String[] parts = command.split("#");
                String externalStoragePath = parts[1];
                String update_Img = new String(UPDATE_IMG);
                String path = externalStoragePath.concat("/").concat(update_Img);
//                File updateFile = FileUtils.getInternalFile(mContext, new File(path));
                File updateFile = new File(path);
                if (!updateFile.exists()) {
                    arg1.putString("NotFind", UPDATE_IMG);
                    callback.complete(0, arg1);
                    return;
                }
                callback.complete(1, null);
                return;
            }

            if (command.startsWith("PQOSDUpgrade".concat("#"))) {
                String pqOsdFileName = new String(OSD_BIN_NAME);
                Bundle arg1 = new Bundle();
                String[] parts = command.split("#");
                String externalStoragePath = parts[1];
                String path = externalStoragePath.concat("/").concat(pqOsdFileName);
//                File file = FileUtils.getInternalFile(mContext, new File(path));
                File file = new File(path);
                if (!file.exists()) {
                    arg1.putString("NotFind", OSD_BIN_NAME);
                    callback.complete(0, arg1);
                    return;
                }
                mFactoryApplication.getSysCtrl().setValueString("PqUpgrade", file.getPath());
                callback.complete(1, null);
                return;
            }

            if (command.startsWith("Misc_Register_USBUpdate")) {
                Log.d("mmm", "Misc_Register_USBUpdate");
                mFactoryApplication.getSysCtrl().setValueInt("Misc_Register_USBUpdate", 1);
                PowerManager pm = (PowerManager) mContext.getApplicationContext()
                        .getSystemService(Context.POWER_SERVICE);
                pm.reboot(null);
            }

            if (command.startsWith("BootLogoUpgrade".concat("#"))) {
                Bundle arg1 = new Bundle();
                String[] parts = command.split("#");
                String bootlogoPath = parts[1];
                String bootlogoName = new String(BOOT_LOGO_NAME);
                String path = bootlogoPath.concat("/").concat(bootlogoName);
//                File updateFile = FileUtils.getInternalFile(mContext, new File(path));
                File updateFile = new File(path);
                if (!updateFile.exists()) {
                    arg1.putString("NotFind", BOOT_LOGO_NAME);
                    callback.complete(0, arg1);
                    return;
                }
                mBootlogoPath = updateFile.getAbsolutePath();
                callback.complete(1, null);
                return;
            }

            if (command.startsWith("BootVideoUpgrade".concat("#"))) {
                Bundle arg1 = new Bundle();
                String[] parts = command.split("#");
                String bootvideoPath = parts[1];
                String bootvideoName = new String(BOOT_VIDEO_NAME);
                String path = bootvideoPath.concat("/").concat(bootvideoName);
//                File updateFile = FileUtils.getInternalFile(mContext, new File(path));
                File updateFile = new File(path);
                if (!updateFile.exists()) {
                    arg1.putString("NotFind", BOOT_VIDEO_NAME);
                    callback.complete(0, arg1);
                    return;
                }
                mBootvideoPath = updateFile.getAbsolutePath();
                callback.complete(1, null);
                return;
            }

            if (command.startsWith("Misc_Register_BootLogoUpgrade")) {
                Log.d(TAG, "Misc_Register_BootLogoUpgrade mBootlogoPath:" + mBootlogoPath);
                mFactoryApplication.getSysCtrl().setValueString("Misc_Register_BootLogoUpgrade", mBootlogoPath);
                PowerManager pm = (PowerManager) mContext.getApplicationContext()
                        .getSystemService(Context.POWER_SERVICE);
                pm.reboot(null);
            }

            if (command.startsWith("Misc_Register_BootVideoUpgrade")) {
                Log.d("mmm", "Misc_Register_BootVideoUpgrade");
                mFactoryApplication.getSysCtrl().setValueString("Misc_Register_BootVideoUpgrade", mBootvideoPath);
                PowerManager pm = (PowerManager) mContext.getApplicationContext()
                        .getSystemService(Context.POWER_SERVICE);
                pm.reboot(null);
            }
            if (command.startsWith("GET_ATTESTATION_KEY")) {
                Bundle bundle = new Bundle();
                String attestationKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_ATTESTATION_TYPE);
                Log.d(TAG, "displayPrAttestation: " + attestationKey);
                if (attestationKey != null && !attestationKey.isEmpty()) {
                    bundle.putString("displayPrAttestation", attestationKey);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayPrAttestation", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }
            if (command.startsWith("UPGRADE_ATTESTATION_KEY".concat("#"))) {
                Bundle bundle = new Bundle();
                String displayAttestationKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_ATTESTATION_TYPE);
                if (SystemProperties.getInt("service.upgrade.Attestation", 0) == 1) {
                    if (!displayAttestationKey.isEmpty()) {
                        bundle.putString("Failed", "Attestation Key Already Upgrade!");
                        callback.complete(0, bundle);
                        return;
                    }
                }
                if (mAlreadyUpdateAttestation) {
                    bundle.putString("Failed", "Attestation Key Already Upgrade!");
                    callback.complete(0, bundle);
                    return;
                }
                String[] parts = command.split("#");
                String usbPats = parts[1];
                //String AttestationFile = loadKeyNameFormUsb(usbPats, KEYS_ATTESTATION_TYPE);
                String AttestationFile = getAttestationKeyFromUsb(usbPats);
                if (AttestationFile != null && !AttestationFile.isEmpty()) {
                    if (mFactoryApplication.getExtTv().extTv_tv001_BurnTrustZoneKeys(KEYS_ATTESTATION_TYPE, usbPats + File.separator + AttestationFile)) {
                        String name = AttestationFile.substring(0, AttestationFile.length() - 4);
                        Log.d(TAG, "name:" + name);
                        if (mFactoryApplication.getExtTv().extTv_tv001_SetTrustZoneKeysSerialNumber(KEYS_ATTESTATION_TYPE, name)) {
                            bundle.putString("displayAttestationKey", name);
                            bundle.putString("Success", "Already upgrade " + AttestationFile + " OK!");
                            mAlreadyUpdateAttestation = true;
                            SystemProperties.set("service.upgrade.Attestation", "1");
                            callback.complete(1, bundle);
                            deleteFileInPath(usbPats, AttestationFile);
                            return;
                        }
                    }
                    bundle.putString("Failed!", "Save " + AttestationFile + " fail!");
                    callback.complete(0, bundle);
                } else {
                    bundle.putString("Cannot", "Cannot find ATTESTATION key file!");
                    callback.complete(2, bundle);
                }
            }
            if (command.startsWith("GET_Netflix_ESN")) {
                Bundle bundle = new Bundle();
                String netflixESNkey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_Netflix_ESN_TYPE);
                Log.d(TAG, "displayPrNetflixESN key: " + netflixESNkey);
                if (netflixESNkey != null && !netflixESNkey.isEmpty()) {
                    bundle.putString("displayPrNetflixESN", netflixESNkey);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayPrNetflixESN", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }
            if (command.startsWith("UPGRADE_Netflix_ESN".concat("#"))) {
                Bundle bundle = new Bundle();
                String esnKey = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_Netflix_ESN_TYPE);
                if (SystemProperties.getInt("service.upgrade.NetflixESN", 0) == 1) {
                    if (!esnKey.isEmpty()) {
                        bundle.putString("Failed", "Netflix ESN Already Upgrade!");
                        callback.complete(0, bundle);
                        return;
                    }
                }
                if (mAlreadyUpdateNetflixESN) {
                    bundle.putString("Failed", "Netflix ESN Already Upgrade!");
                    callback.complete(0, bundle);
                    return;
                }
                String[] parts = command.split("#");
                String usbPats = parts[1];
                //String NetflixESNFile = loadKeyNameFormUsb(usbPats, KEYS_Netflix_ESN_TYPE);
                String NetflixESNFile = getNetflixESNFromUsb(usbPats);
                if (NetflixESNFile != null && !NetflixESNFile.isEmpty()) {
                    if (mFactoryApplication.getExtTv().extTv_tv001_BurnTrustZoneKeys(KEYS_Netflix_ESN_TYPE,usbPats + File.separator + NetflixESNFile)) {
                        String name = NetflixESNFile.substring(0, NetflixESNFile.length() - 4);
                        Log.d(TAG, "name:" + name);
                        if (mFactoryApplication.getExtTv().extTv_tv001_SetTrustZoneKeysSerialNumber(KEYS_Netflix_ESN_TYPE,name)) {
                            bundle.putString("displayNetflixESN", name);
                            bundle.putString("Success", "Already upgrade " + NetflixESNFile + " OK!");
                            mAlreadyUpdateNetflixESN = true;
                            SystemProperties.set("service.upgrade.NetflixESN", "1");
                            callback.complete(1, bundle);
                            deleteFileInPath(usbPats, NetflixESNFile);
                            return;
                        }
                    }
                    Log.d(TAG, "burned,bug get fail " + NetflixESNFile);
                    bundle.putString("Failed!", "Save " + NetflixESNFile + " fail!");
                    callback.complete(0, bundle);
                } else {
                    bundle.putString("Cannot", "Cannot find MGKID key file!");
                    callback.complete(2, bundle);
                }
            }
            if (command.startsWith("GET_RMCA")) {
                Bundle bundle = new Bundle();
                String rmcaKey = isRmcaUpgrade() ? "RMCA" : null;
                Log.d(TAG, "displayRMCA key: " + rmcaKey);
                if (rmcaKey != null && !rmcaKey.isEmpty()) {
                    bundle.putString("displayRMCA", rmcaKey);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayRMCA", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }
            if (command.startsWith("UPGRADE_RMCA".concat("#"))) {
                Bundle bundle = new Bundle();
                String rmcaKey = isRmcaUpgrade() ? "RMCA" : null;
                if (SystemProperties.getInt("service.upgrade.RMCA", 0) == 1) {
                    if (!rmcaKey.isEmpty()) {
                        bundle.putString("Failed", "RMCA Already Upgrade!");
                        callback.complete(0, bundle);
                        return;
                    }
                }
                if (mAlreadyUpdateRmca) {
                    bundle.putString("Failed", "RMCA Already Upgrade!");
                    callback.complete(0, bundle);
                    return;
                }
                String[] parts = command.split("#");
                String usbPats = parts[1];
                String RmcaFile = new String();
                File internalFile = new File(usbPats);
                RmcaFile = getRmcaFromUsb(internalFile == null ? null : internalFile.getAbsolutePath());
                if (RmcaFile != null && !RmcaFile.isEmpty()) {
                    if (writeCapabilityBin(effectiveStoragePath.concat(File.separator).concat(RmcaFile))) {
                        if (doFactorySaveSync("RMCA")) {
                            rmcaKey = isRmcaUpgrade() ? "RMCA" : null;
                            if (rmcaKey != null && !rmcaKey.isEmpty()) {
                                bundle.putString("displayRMCA", rmcaKey);
                                bundle.putString("Success", "Already upgrade " + rmcaKey + " OK!");
                                mAlreadyUpdateRmca = true;
                                SystemProperties.set("service.upgrade.RMCA", "1");
                                callback.complete(1, bundle);
                            } else {
                                Log.d(TAG, "burned,bug get fail " + RmcaFile);
                                bundle.putString("Failed!", "Save " + RmcaFile + " fail!");
                                callback.complete(0, bundle);
                            }
                        }
                    }
                } else {
                    bundle.putString("Cannot", "Cannot find RMCA key file!");
                    callback.complete(2, bundle);
                }
            }
            if (command.startsWith("UPGRADE_WIDEVINE_ATT".concat("#"))) {
                Bundle bundle = new Bundle();
                String[] parts = command.split("#");
                String usbPats = parts[1] + File.separator + "WIDEVINE_ATT";
                String WVATTFile = new String();
                File internalFile = new File(usbPats);
                WVATTFile = getWVATTFromUsb(internalFile == null ? null : internalFile.getAbsolutePath());
                if (WVATTFile != null && !WVATTFile.isEmpty() && splitToWvAndAtt(effectiveStoragePath + File.separator + WVATTFile)) {
                    String wvPath = fileSearch(keyTemporary, "^KEYBOX_\\w+\\.bin$");
                    String attPath = fileSearch(keyTemporary, "^VINSMART_\\w+\\.bin$");
                    if (wvPath != null && attPath != null && !TextUtils.isEmpty(wvPath) && !TextUtils.isEmpty(attPath)) {
                        boolean ret = false;
                        String wvSerNum = getSerialNumber(wvPath);
                        if (mFactoryApplication.getExtTv().extTv_tv001_BurnTrustZoneKeys(KEYS_WIDEVINE_TYPE, wvPath)) {
                            ret = mFactoryApplication.getExtTv().extTv_tv001_SetTrustZoneKeysSerialNumber(KEYS_WIDEVINE_TYPE, wvSerNum);
                            if (ret) {
                                new File(wvPath).delete();
                            }
                        }
                        String attSerNum = getSerialNumber(attPath);
                        if (ret && mFactoryApplication.getExtTv().extTv_tv001_BurnTrustZoneKeys(KEYS_ATTESTATION_TYPE, attPath)) {
                            ret = mFactoryApplication.getExtTv().extTv_tv001_SetTrustZoneKeysSerialNumber(KEYS_ATTESTATION_TYPE, attSerNum);
                            if (ret) {
                                new File(attPath).delete();
                            }
                        }
                        if (ret) {
                            deleteFileInUsb(WVATTFile);
                            bundle.putString("displayWINDEVINEATT_WV", wvSerNum);
                            bundle.putString("displayWINDEVINEATT_ATT", attSerNum);
                            bundle.putString("Success", "Already upgrade " + WVATTFile + " OK!");
                            SystemProperties.set("service.upgrade.RMCA", "1");
                            callback.complete(1, bundle);
                        } else {
                            Log.d(TAG, "burned,bug get fail " + WVATTFile);
                            bundle.putString("Failed!", "Save " + WVATTFile + " fail!");
                            callback.complete(0, bundle);
                        }
                    }
                } else {
                    bundle.putString("Cannot", "Cannot find KEYBOX_ATT key file!");
                    callback.complete(2, bundle);
                }
            }
            if (command.startsWith("GET_OEM")) {
                Bundle bundle = new Bundle();
                String oemKey = mFactoryApplication.getFactory().getOEMKey();
                Log.d(TAG, "displayOEM key: " + oemKey);
                if (oemKey != null && !oemKey.isEmpty()) {
                    bundle.putString("displayOEM", oemKey);
                    callback.complete(1, bundle);
                } else {
                    bundle.putString("displayOEM", "NULL");
                    callback.complete(0, bundle);
                }
                return;
            }
            if (command.startsWith("UPGRADE_OEM".concat("#"))) {
                Bundle bundle = new Bundle();
                String[] parts = command.split("#");
                String usbPats = parts[1];
                String OEMKeyFile = new String();
                File internalFile = new File(usbPats);
                OEMKeyFile = getOEMKeyFromUsb(internalFile.getAbsolutePath());
                if (OEMKeyFile != null && !OEMKeyFile.isEmpty()) {
                    String value = loadOEMValue(internalFile + File.separator + OEMKeyFile);
                    if (mFactoryApplication.getFactory().setOEMKey(value)) {
                        bundle.putString("displayOEM", value);
                        bundle.putString("Success", "Already upgrade " + OEMKeyFile + " OK!");
                        SystemProperties.set("service.upgrade.OEM", "1");
                        callback.complete(1, bundle);
                    } else {
                        Log.d(TAG, "burn fail " + OEMKeyFile);
                        bundle.putString("Failed!", "Save " + OEMKeyFile + " fail!");
                        callback.complete(0, bundle);
                    }
                } else {
                    bundle.putString("Cannot", "Cannot find OEM KEY key file!");
                    callback.complete(2, bundle);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isRmcaUpgrade() {
        File rmcaPath = new File("mnt/vendor/factory/RMCA");
        if (rmcaPath.exists() && rmcaPath.isFile()) {
            return true;
        }
        return false;
    }

    private boolean writeCapabilityBin(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File capabilityBinFile = new File(filePath);
        Long fileLength = capabilityBinFile.length();
        byte[] fileData = new byte[fileLength.intValue()];
        //Files.readAllBytes(path(file));

        try (FileInputStream in = new FileInputStream(capabilityBinFile)) {
            in.read(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean success = mFactoryApplication.getFactory().encryptAndSaveCapabilityTableFile(fileData, fileData.length);
        Log.d(TAG, "writeCapabilityBin, success:" + success);
        return success;

    }

    public String getMacAddressFromUsb(String externalStoragePath) {
        String indexFileName = new String("MACIndex.ini");
        String binFileName;
        String macAddress;
        BufferedReader br = null;
        String line = "";
        File fileIndex;
        File fileBin;
        effectiveStoragePath = externalStoragePath;
        boolean isBinFileFound = false;
        fileIndex = new File(externalStoragePath + "/" + indexFileName);
        if (!fileIndex.exists()) {
            effectiveStoragePath = externalStoragePath;
        }

        if (externalStoragePath == null) {
            return "";
        }
        try {
            br = new BufferedReader(new FileReader(externalStoragePath + "/" + indexFileName));
            while ((line = br.readLine()) != null) {
                if (line.contains("index=")) {
                    isBinFileFound = true;
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogHelper.d(TAG, "isBinFileFound : %s", isBinFileFound);
        if (isBinFileFound == false) {
            return "";
        }

        binFileName = line.substring(6);
        if (checkBinFileFormat(binFileName) == false) {
            return "";
        }
        macAddress = binFileName.substring(3, 15);

        fileBin = new File(externalStoragePath + "/" + binFileName);
        LogHelper.d(TAG, "bin file path : %s", fileBin.getAbsolutePath());
        if (!fileBin.exists()) {
            return "";
        }

        return macAddress;
    }

    private boolean doFactorySaveSync(String mtag) {
        return true;
    }

    public boolean checkBinFileFormat(String binFileName) {
        if (binFileName.length() != 19) {
            return false;
        }
        if (binFileName.substring(0, 3).equals("MAC") == false) {
            return false;
        }
        if (binFileName.substring(15, 19).equals(".bin") == false) {
            return false;
        }
        return checkMacAddressFormat(binFileName.substring(3, 15));
    }

    public void deleteUpdateMacAddressInUsb(String binFileName) {
        String externalStoragePath = effectiveStoragePath;
        String indexFileName = new String("MACIndex.ini");
        String newMacAddress;
        if (deleteFileInUsb(binFileName) == false) {
            return;
        }

        if (checkMacAddressFormat(binFileName.substring(3, 15)) == false) {
            return;
        }

        newMacAddress = updateMacAddress(binFileName.substring(3, 15));
        if (newMacAddress.length() == 0) {
            return;
        }

        if (true) {
            boolean finishSuccess = false;
            try {
                FileOutputStream fos = new FileOutputStream(externalStoragePath + File.separator + indexFileName);
                OutputStreamWriter fosw = new OutputStreamWriter(fos);
                fosw.write("[index]\n");
                fosw.write("index=MAC" + newMacAddress + ".bin\n");
                fosw.flush();
                fos.flush();

                fos.getChannel().force(true);
                fos.getFD().sync();
                fosw.close();
                finishSuccess = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.d(TAG, "running result: " + finishSuccess);
            }
        }
    }

    public String updateMacAddress(String macAddress) {
        Log.d(TAG, "macAddress: " + macAddress);
        int macAddressInDecimalUpper = (int) Long.parseLong(macAddress.substring(0, 6), 16);
        int macAddressInDecimalLower = (int) Long.parseLong(macAddress.substring(6, 12), 16);

        if (macAddressInDecimalLower == 16777215) {
            macAddressInDecimalLower = 0;
            if (macAddressInDecimalUpper == 16777215) {
                return "";
            }
            macAddressInDecimalUpper = macAddressInDecimalUpper + 1;
        } else {
            macAddressInDecimalLower = macAddressInDecimalLower + 1;
        }
        macAddress = String.format("%06X", macAddressInDecimalUpper) + String.format("%06X", macAddressInDecimalLower);
        Log.d(TAG, "Next MAC address: " + macAddress);
        return macAddress;
    }

    public boolean checkMacAddressFormat(String macAddress) {
        if (TextUtils.isEmpty(macAddress)) {
            return false;
        }
        if (macAddress.length() != 12) {
            return false;
        }
        return macAddress.matches("[0-9a-zA-Z]{12}");
    }

    public String getHdcpKeyNameFromUsb(int version, String usbPath) {
        String externalStoragePath = usbPath;
        String hDCPKeyName = new String();
        BufferedReader br = null;

        if (externalStoragePath == null || "".equals(externalStoragePath)) {
            return hDCPKeyName;
        }

        File directory = new File(externalStoragePath);
        String[] files = directory.list();

        if (files != null && files.length != 0) {
            FilenameFilter ff;
            if (version == 1) {
                ff = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.startsWith("HDCP_KEY_");
                    }
                };
            } else {
                ff = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.startsWith("HDCP22_KEY_");
                    }
                };
            }

            for (String file : files) {
                if (ff.accept(null, file)) {
                    hDCPKeyName = file;
                    Log.d(TAG, "Found key file name: " + hDCPKeyName);
                    break;
                }
            }
        }

        return hDCPKeyName;
    }

    public byte[] getHdcpKeyFromUsb(String hDCPKeyName, String usbPath) {
        byte[] fileData = null;
        if (hDCPKeyName.length() == 0) {
            return fileData;
        }

        try {
            File file = new File(usbPath + "/" + hDCPKeyName);
            fileData = new byte[(int) file.length()];
            Log.d(TAG, "Found key file length: " + file.length());
            effectiveStoragePath = usbPath;
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileData;
    }

    /*public String[] getHdcpKeyFromUsb(int version, String usbPath) {
        String externalStoragePath = usbPath;
        String[] returnStr = new String[2];
        returnStr[0] = "";
        returnStr[1] = "";
        effectiveStoragePath = null;
        File directory = new File(usbPath);
        String[] files = directory.list();
        if (files != null && files.length != 0) {
            FilenameFilter ff = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.length() < 14) {
                        return false;
                    }
                    return filename.substring(0, 9).equals("HDCP_KEY_");
                }

            };
            FilenameFilter ff2 = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.length() < 14) {
                        return false;
                    }
                    return filename.substring(0, 10).equals("HDCP2_KEY_");
                }

            };
            for (int i = 0; i < files.length; i++) {
                if (version == 1) {
                    if (ff.accept(null, files[i])) {
                        returnStr[0] = files[i];
                        externalStoragePath = usbPath;
                        break;
                    }
                } else if (version == 2) {
                    if (ff2.accept(null, files[i])) {
                        returnStr[0] = files[i];
                        externalStoragePath = usbPath;
                        break;
                    }
                }
            }
        }

        if (returnStr[0].length() == 0) {
            return returnStr;
        }

        try {
            File file = new File(externalStoragePath + "/" + returnStr[0]);
            byte[] fileData = new byte[(int) file.length()];
            Log.d(TAG, "Found key file length: " + file.length());
            effectiveStoragePath = externalStoragePath;
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();

            returnStr[1] = new String(fileData, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnStr;
    }
*/

    public boolean deleteFileInUsb(String binFileName) {
        String externalStoragePath = effectiveStoragePath;
        File fileBin;
        if (binFileName.length() == 0) {
            Log.d(TAG, "Error! Bin file name error");
            return false;
        }

        fileBin = new File(externalStoragePath + "/" + binFileName);
        if (!fileBin.delete()) {
            Log.d(TAG, "Error! Cannot delete " + binFileName + " !");
            return false;
        }
        Log.d(TAG, "Delete:" + binFileName + " success!");

        return true;
    }


    public String getWidevineKeyFromUsb(String usbPaths) {
        String externalStoragePath = null;
        String returnStr = "";
        effectiveStoragePath = null;

        String __driname = usbPaths;
        File directory = new File(__driname);
        String[] files = directory.list();
        if (files != null && files.length != 0) {
            for (String myfile : files) {
                if (myfile.startsWith("KEYBOX_")) {
                    returnStr = myfile;
                    externalStoragePath = __driname;
                    break;
                }
            }
        }

        if (returnStr.isEmpty()) {
            return returnStr;
        }

        effectiveStoragePath = externalStoragePath;
        return returnStr;
    }

    public String getAttestationKeyFromUsb(String usbPaths) {
        String externalStoragePath = null;
        String returnStr = "";
        effectiveStoragePath = null;

        String __driname = usbPaths;
        File directory = new File(__driname);
        String[] files = directory.list();
        if (files != null && files.length != 0) {
            for (String myfile : files) {
                if (myfile.startsWith("VINSMART_")) {
                    returnStr = myfile;
                    externalStoragePath = __driname;
                    break;
                }
            }
        }

        if (returnStr.isEmpty()) {
            return returnStr;
        }

        effectiveStoragePath = externalStoragePath;
        return returnStr;
    }

    public String getNetflixESNFromUsb(String usbPaths) {
        String externalStoragePath = null;
        String returnStr = "";
        effectiveStoragePath = null;

        String __driname = usbPaths;
        File directory = new File(__driname);
        String[] files = directory.list();
        if (files != null && files.length != 0) {
            for (String myfile : files) {
                if (myfile.startsWith("MGKID_")) {
                    returnStr = myfile;
                    externalStoragePath = __driname;
                    break;
                }
            }
        }

        if (returnStr.isEmpty()) {
            return returnStr;
        }

        effectiveStoragePath = externalStoragePath;
        return returnStr;
    }

    public String getRmcaFromUsb(String usbPaths) {
        String externalStoragePath = null;
        String returnStr = "";
        effectiveStoragePath = null;

        String match = getRMCAMatchName();
        String __driname = usbPaths + "/" + match;
        String fileName = match + ".bin";
        File directory = new File(__driname);
        String[] files = directory.list();
        Log.d(TAG, "getRmca __driname:" + __driname + " fileName:" + fileName);
        if (files != null && files.length != 0) {
            for (String myfile : files) {
                if (myfile.startsWith(fileName)) {
                    returnStr = myfile;
                    externalStoragePath = __driname;
                    break;
                }
            }
        }

        if (returnStr.isEmpty()) {
            return returnStr;
        }

        effectiveStoragePath = externalStoragePath;
        return returnStr;
    }

    private String getRMCAMatchName() {
        if ("rtd2841a".equals(SystemProperties.get("ro.board.platform", "rtd2851a"))) {
            return "RMCA_2K";
        }
        return "RMCA_4K";
    }

    private String loadOEMValue(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        try (FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(streamReader)) {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOEMKeyFromUsb(String usbPaths) {
        String externalStoragePath = null;
        String returnStr = "";
        effectiveStoragePath = null;

        String __driname = usbPaths;
        File directory = new File(__driname);
        String[] files = directory.list();
        if (files != null && files.length != 0) {
            for (String myfile : files) {
                if (myfile.matches("^OEM_KEY_\\w+\\.bin")) {
                    returnStr = myfile;
                    externalStoragePath = __driname;
                    break;
                }
            }
        }

        if (returnStr.isEmpty()) {
            return returnStr;
        }

        effectiveStoragePath = externalStoragePath;
        return returnStr;
    }

    public String getWVATTFromUsb(String usbPaths) {
        String externalStoragePath = null;
        String returnStr = "";
        effectiveStoragePath = null;

        String __driname = usbPaths;
        File directory = new File(__driname);
        String[] files = directory.list();
        if (files != null && files.length != 0) {
            for (String myfile : files) {
                if (myfile.matches("^KEYBOX_ATT_\\w+")) {
                    returnStr = myfile;
                    externalStoragePath = __driname;
                    break;
                }
            }
        }

        if (returnStr.isEmpty()) {
            return returnStr;
        }

        effectiveStoragePath = externalStoragePath;
        return returnStr;
    }

    private boolean splitToWvAndAtt(String fullPath) {
        File source = new File(fullPath);
        if (!source.exists() && !source.canRead()) {
            return false;
        }
        //String parent = /*source.getParent();*/ "/tmp";
        String parent = keyTemporary;
        Log.d(TAG, "splitToWvAndAtt:" + parent);
        int start = fullPath.lastIndexOf("_");
        int end = fullPath.lastIndexOf(".");
        String serialNumber = "0";
        if (start > 0 && start < end) {
            serialNumber = fullPath.substring(start + 1, end);
        }
        if (start + 1 < fullPath.length()) {
            serialNumber = fullPath.substring(start + 1);
        }
        String wvPath = String.format("%s%sKEYBOX_%s.bin",parent, File.separator, serialNumber);
        File wv = new File(wvPath);
        String attPath = String.format("%s/VINSMART_%s.bin", parent, serialNumber);
        File att = new File(attPath);
        Log.d(TAG, "WV File:" + wvPath + " ATT File:" + attPath);

        try (InputStream inputStream = new FileInputStream(source);
                OutputStream wvOutputStream = new FileOutputStream(wv);
                OutputStream attOutputStream = new FileOutputStream(att)) {

            byte[] wvStream = new byte[4192];
            int readWV = inputStream.read(wvStream);
            if (readWV != wvStream.length) {
                return false;
            }

            byte[] attStream = new byte[1024 * 11];
            int readAtt = inputStream.read(attStream);
            if (readAtt < 1) {
                return false;
            }
            Log.d(TAG, String.format("WV size:%d ATT size:%d", readWV, readAtt));

            wvOutputStream.write(wvStream);
            wvOutputStream.flush();

            // attOutputStream.write(attStream);
            attOutputStream.write(attStream, 0, readAtt);
            attOutputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String fileSearch(String path, String regular) {
        if (path == null || TextUtils.isEmpty(path) || TextUtils.isEmpty(regular)) {
            return null;
        }
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return null;
        }
        File[] listFile = file.listFiles();
        return fileSearch(listFile, regular);
    }

    private String fileSearch(File[] listFile, String regular) {
        if (listFile == null || listFile.length <= 0) {
            return null;
        }
        for (File f : listFile) {
            if (!f.isDirectory() && f.getName().matches(regular) && f.canRead()) {
                return f.getAbsolutePath();
            }
        }
        return null;
    }

    public String getCKDMac() {
        if (mFactoryApplication.getFactory().isCkdKeyBurned(8)) {
            String bootparamMac = readBootParamFile("mnt/vendor/factory/bin_panel/000BootParam.h",
                    "#define BOOT_MAC_ADDRESS");
            if (bootparamMac != null && bootparamMac.length() > 0) {
                return bootparamMac;
            }
        }
        return null;
    }

    /** Enum CKD Key position
     * HDCP_1_4        0
     * HDCP_2_2        1
     * WIDEVINE        2
     * MGKID           3
     * PLAYREADY       4
     * ATTESTATION     5
     * RMCA            6
     * CI              7
     * MAC             8
     * OEM             9
     * @param keyType KEY TYPE
     * @return CKD Position
     */
    private int getCKDKeyIndex(int keyType) {
        switch (keyType) {
        case KEYS_WIDEVINE_TYPE:
            return 2;
        case KEYS_PLAYREADY_TYPE:
            return 4;
        case KEYS_ATTESTATION_TYPE:
            return 5;
        case KEYS_Netflix_ESN_TYPE:
            return 3;
        case KEYS_CIKEY_TYPE:
            return 7;
        case KEYS_MAC_TYPE :
            return 8;
        // case KEYS_OEM_KEY :
        //     return 9;
        case KEYS_HDCP1_TYPE :
            return 0;
        case KEYS_HDCP2_TYPE :
            return 1;
        case KEYS_RMCA :
            return 6;
        }
        throw new IllegalArgumentException("Unknown Key Type:" + keyType);
    }

    public boolean isCKDBurned(int type) {
        Factory factory = mFactoryApplication.getFactory();
        int ckdKeyIndex = getCKDKeyIndex(type);
        return factory.isCkdKeyBurned(ckdKeyIndex);
    }

    public String readBootParamFile(String path, String key) {
        File iniFile = new File(path);
        if (!iniFile.exists() || !iniFile.canRead()) {
            return null;
        }
        if (key == null || key.length() == 0) {
            return null;
        }
        Log.d(TAG, "read:" + path + " key:" + key);
        String line = null;
        try (Reader reader = new FileReader(iniFile);
                BufferedReader br = new BufferedReader(reader)) {
            while ((line = br.readLine()) != null) {
                if (line.startsWith(key)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line != null && (line.length() > key.length() + 19)) {
            line = line.substring(key.length() + 2, key.length() + 19);
            return line;
        }
        return null;
    }

    public String getSerialNumber(int keyType) {
        Log.d(TAG,"keyType :" + keyType);
        switch (keyType) {
            case KEYS_HDCP1_TYPE:
                String hdcp1 = mFactoryApplication.getExtTv().extTv_tv001_GetHdcpKeySerialNumber(KEYS_HDCP_1_4_TYPE);
                Log.d(TAG,"hdcp1 :" + hdcp1);
                return hdcp1;
            case KEYS_HDCP2_TYPE:
                String hdcp2 = mFactoryApplication.getExtTv().extTv_tv001_GetHdcpKeySerialNumber(KEYS_HDCP_2_2_TYPE);
                Log.d(TAG,"hdcp2 :" + hdcp2);
                return hdcp2;
            default:
                String displayName = mFactoryApplication.getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(keyType);
                Log.d(TAG,"displayName :" + displayName);
                return displayName;
        }
    }
    private String getSerialNumber(String keyName) {
        if (TextUtils.isEmpty(keyName)) {
            return "unknown";
        }
        if (keyName.matches("^MAC[0-9a-fA-F]{12}\\.bin$")) {
            StringBuilder buffer = new StringBuilder(keyName.substring(3, 15));
            buffer.insert(10, ':').insert(8, ':').insert(6, ':').insert(4, ':').insert(2, ':');
            return buffer.toString();
        }
        int start = keyName.lastIndexOf("_");
        int end = keyName.lastIndexOf(".");
        Log.d(TAG, keyName + " -> " + start + " " + end);
        if (start + 1 == end) {
            return "0";
        }
        if (start < end) {
            return keyName.substring(start + 1, end);
        }
        if (start > 0 && end < 0) {
            return keyName.substring(start + 1);
        }
        return keyName;
    }

    public String getPlayreadyKeyFromUsb(String usbPaths) {
        String externalStoragePath = null;
        String returnStr = "";
        effectiveStoragePath = null;

        String __driname = usbPaths;
        File directory = new File(__driname);
        String[] files = directory.list();
        if (files != null && files.length != 0) {
            for (String myfile : files) {
                if (myfile.startsWith("PLAYREADY.bin")) {
                    returnStr = myfile;
                    externalStoragePath = __driname;
                    break;
                }
            }
        }

        if (returnStr.isEmpty()) {
            return returnStr;
        }

        effectiveStoragePath = externalStoragePath;
        Log.d(TAG, "effectiveStoragePath: " + effectiveStoragePath);
        return returnStr;
    }

    public String getCiKeyFromUsb(String usbPaths) {
        String externalStoragePath = null;
        String returnStr = "";
        effectiveStoragePath = null;

        String __driname = usbPaths;
        Log.d(TAG, "externalStoragePath=" + __driname);
        File directory = new File(__driname);
        String[] files = directory.list();
        if (files != null && files.length != 0) {
            for (String myfile : files) {
                if (myfile.startsWith("CI_KEY")) {
                    returnStr = myfile;
                    Log.d(TAG, "Found CI file name: " + returnStr);
                    externalStoragePath = __driname;
                    break;
                }
            }
        }

        if (returnStr.isEmpty()) {
            return returnStr;
        }

        effectiveStoragePath = externalStoragePath;

        return returnStr;
    }

    private String loadKeyNameFormUsb(String path, int type) {
        Log.d(TAG, "loadKeyFormUsb type:" + type + " path:" + path);
        //String usbPath = additionCustomerDir(type, optimalUSB);
        String platform = getPlatform();
        String regular = null;
        switch (type) {
        case KEYS_CIKEY_TYPE:
            regular = String.format("^%s202\\d{3,5}_CI_\\d+.bin$", platform);
            break;
        case KEYS_WIDEVINE_TYPE:
            regular = String.format("^%s202\\d{3,5}_WV_\\d+.bin$", platform);
            break;
        case KEYS_ATTESTATION_TYPE:
            regular = String.format("^%s202\\d{3,5}_ATT_\\d+.bin$", platform);
            break;
        case KEYS_Netflix_ESN_TYPE:
            regular = String.format("^%s202\\d{3,5}_MGK_\\d+.bin$", platform);
            break;
        default:
            return null;
        }
        return fileSearchName(path, regular);
    }

    private String getPlatform() {
        String platform = SystemProperties.get("ro.board.platform", "rtd2851a");
        if (platform.equalsIgnoreCase("rtd2851a")) {
            return ByteTransformUtils.asciiToString(MANUFACTURER_BVT) + "_2851A_";
        }
        if (platform.equalsIgnoreCase("rtd2841a")) {
            return ByteTransformUtils.asciiToString(MANUFACTURER_BVT) + "_2841A_";
        }
        return platform.toUpperCase();
    }

    private String fileSearchName(String path, String regular) {
        Log.d(TAG, "fileSearchName path:" + path + " regular:" + regular);
        if (path == null || TextUtils.isEmpty(path) || TextUtils.isEmpty(regular)) {
            return null;
        }
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return null;
        }
        File[] listFile = file.listFiles();
        if (listFile == null || listFile.length <= 0) {
            return null;
        }
        for (File f : listFile) {
            if (!f.isDirectory() && f.getName().matches(regular) && f.canRead()) {
                return f.getName();
            }
        }
        return null;
    }

    public boolean deleteFileInPath(String path, String name) {
        if (path == null || name == null) {
            return false;
        }
        File fileBin = new File(path + File.separator + name);
        if (!fileBin.delete()) {
            Log.d(TAG, "Error! Cannot delete " + name + " !");
            return false;
        }
        Log.d(TAG, "Delete:" + name + " success!");
        return true;
    }
}
