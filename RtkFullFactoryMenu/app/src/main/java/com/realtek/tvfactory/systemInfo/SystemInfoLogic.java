package com.realtek.tvfactory.systemInfo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.TextView;

import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.UpgradeApi;
import com.realtek.tvfactory.api.listener.ICommandCallback;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.preference.SumaryPreference;
import com.realtek.tvfactory.utils.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SystemInfoLogic extends LogicInterface implements Handler.Callback {

    public static final int CMD_UPGRADE_PQ = 0;
    public static final int CMD_UPGRADE_USB = 1;
    public static final int CMD_UPGRADE_PQ_OSD = 2;
    public static final int CMD_UPGRADE_HDCP = 3;
    public static final int CMD_GET_HDCP = 4;
    public static final int CMD_UPGRADE_HDCP22 = 5;
    public static final int COM_GET_UPGRADE_HDCP = 6;
    public static final int CMD_UPGRADE_MAC = 7;
    public static final int CMD_GET_MAC = 8;
    public static final int CMD_UPGRADE_WIDEVINE = 9;
    public static final int CMD_GET_WIDEVINE = 10;
    public static final int CMD_CLEAR_WIDEVINE = 11;
    public static final int CMD_UPGRADE_CI_KEY = 12;
    public static final int CMD_GET_CI_KEY = 13;
    public static final int CMD_UPGRADE_PQ_CLICK = 14;
    public static final int CMD_UPGRADE_USB_CLICK = 15;
    public static final int CMD_UPGRADE_PQ_OSD_CLICK = 16;
    public static final int CMD_UPGRADE_BOOTLOGO = 17;
    public static final int CMD_UPGRADE_BOOTLOGO_CLICK = 18;
    public static final int CMD_UPGRADE_BOOTVIDEO = 19;
    public static final int CMD_UPGRADE_BOOTVIDEO_CLICK = 20;
    public static final int CMD_UPGRADE_PLAYREADY = 21;
    public static final int CMD_GET_PLAYREADY = 22;
    public static final int CMD_CLEAR_PLAYREADY = 23;
    public static final int CMD_UPGRADE_Attestation = 24;
    public static final int CMD_GET_Attestation = 25;
    public static final int CMD_UPGRADE_Netflix_ESN = 26;
    public static final int CMD_GET_Netflix_ESN = 27;
    public static final int CMD_UPGRADE_RMCA = 28;
    public static final int CMD_GET_RMCA = 29;
    public static final int CMD_UPGRADE_WIDEVINE_ATT = 30;
    public static final int CMD_UPGRADE_OEM = 32;
    public static final int CMD_GET_OEM = 33;
    public static final int CMD_UPGRADE_MAC_MANUAL = 34;

    public static final SparseArray<String> sCommands = new SparseArray<>();

    static {
        sCommands.put(CMD_UPGRADE_PQ, "UPGRADE_PQ");
        sCommands.put(CMD_UPGRADE_USB, "USBUpgrade");
        sCommands.put(CMD_UPGRADE_PQ_OSD, "PQOSDUpgrade");
        sCommands.put(CMD_UPGRADE_HDCP, "SET_HDCP_UPGRADE");
        sCommands.put(CMD_GET_HDCP, "GET_HDCP");
        sCommands.put(CMD_UPGRADE_HDCP22, "SET_HDCP_UPGRADE22");
        sCommands.put(COM_GET_UPGRADE_HDCP, "GET_UPGRADE_HDCP");
        sCommands.put(CMD_UPGRADE_MAC, "UPGRADE_MAC");
        sCommands.put(CMD_GET_MAC, "GET_MAC");
        sCommands.put(CMD_UPGRADE_WIDEVINE, "SET_WIDEVINE_UPGRADE");
        sCommands.put(CMD_GET_WIDEVINE, "GET_WIDEVINE");
        sCommands.put(CMD_CLEAR_WIDEVINE, "SET_WIDEVINE_CLEAR");
        sCommands.put(CMD_UPGRADE_CI_KEY, "SET_CI_KEY_UPGRADE");
        sCommands.put(CMD_GET_CI_KEY, "GET_CI_KEY");
        sCommands.put(CMD_UPGRADE_PQ_CLICK, "Misc_Register_PQUpdate");
        sCommands.put(CMD_UPGRADE_USB_CLICK, "Misc_Register_USBUpdate");
        sCommands.put(CMD_UPGRADE_PQ_OSD_CLICK, "Misc_Register_PQOSDUpdate");
        sCommands.put(CMD_UPGRADE_BOOTLOGO, "BootLogoUpgrade");
        sCommands.put(CMD_UPGRADE_BOOTLOGO_CLICK, "Misc_Register_BootLogoUpgrade");
        sCommands.put(CMD_UPGRADE_BOOTVIDEO, "BootVideoUpgrade");
        sCommands.put(CMD_UPGRADE_BOOTVIDEO_CLICK, "Misc_Register_BootVideoUpgrade");
        sCommands.put(CMD_UPGRADE_PLAYREADY, "SET_PLAYREADY_UPGRADE");
        sCommands.put(CMD_GET_PLAYREADY, "GET_PLAYREADY");
        sCommands.put(CMD_CLEAR_PLAYREADY, "SET_PLAYREADY_CLEAR");
        sCommands.put(CMD_UPGRADE_Attestation, "UPGRADE_ATTESTATION_KEY");
        sCommands.put(CMD_GET_Attestation, "GET_ATTESTATION_KEY");
        sCommands.put(CMD_UPGRADE_Netflix_ESN, "UPGRADE_Netflix_ESN");
        sCommands.put(CMD_GET_Netflix_ESN, "GET_Netflix_ESN");
        sCommands.put(CMD_UPGRADE_RMCA, "UPGRADE_RMCA");
        sCommands.put(CMD_GET_RMCA, "GET_RMCA");
        sCommands.put(CMD_UPGRADE_WIDEVINE_ATT, "UPGRADE_WIDEVINE_ATT");
        sCommands.put(CMD_UPGRADE_OEM, "UPGRADE_OEM");
        sCommands.put(CMD_GET_OEM, "GET_OEM");
        sCommands.put(CMD_UPGRADE_MAC_MANUAL, "UPGRADE_MAC_MANUAL");
    }

    private static Handler mHandler;

    private SumaryPreference mCheckNetwork;
    private SumaryPreference mMACUpgrade;
    private SumaryPreference mOemUpgrade;
    private SumaryPreference mNetflixEsnUpgrade;
    private SumaryPreference mPlayreadyUpgrade;
    private SumaryPreference mHDCPUpgrade;
    private SumaryPreference mHDCPUpgrade2;
    private SumaryPreference mWidevineUpgrade;
    private SumaryPreference mCiUpgrade;
    private SumaryPreference mAttestationUpgrade;
    private SumaryPreference mRmcaUpgrade;
    private AlertDialog mAlertDialog;
    private boolean allKeyUpgrade = false;
    private StringBuilder successfulKey;
    private StringBuilder failedKey;
    private StringBuilder alreadyKey;
    private TextView mMessageView;

    public SystemInfoLogic(PreferenceContainer containter) {
        super(containter);
        mHandler = new Handler(this);
    }

    @Override
    public void init() {
        mCheckNetwork = (SumaryPreference) mContainer.findPreferenceById(R.id.check_network);
        mMACUpgrade = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrede_mac);
        mOemUpgrade = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrade_oem);
        mNetflixEsnUpgrade = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrade_netflix_esn);
        mPlayreadyUpgrade = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrade_playready);
        mHDCPUpgrade = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrade_hdcp);
        mHDCPUpgrade2 = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrade_hdcp2);
        mWidevineUpgrade = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrade_widevine);
        mCiUpgrade = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrade_ci);
        mAttestationUpgrade = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrade_attestation);
        mRmcaUpgrade = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrade_rmca);

        sendSyncCommand(CMD_GET_MAC);
        sendSyncCommand(CMD_GET_OEM);
        sendSyncCommand(CMD_GET_Netflix_ESN);
        sendSyncCommand(CMD_GET_PLAYREADY);
        sendSyncCommand(CMD_GET_HDCP);
        sendSyncCommand(COM_GET_UPGRADE_HDCP);
        sendSyncCommand(CMD_GET_WIDEVINE);
        sendSyncCommand(CMD_GET_CI_KEY);
        sendSyncCommand(CMD_GET_Attestation);
        sendSyncCommand(CMD_GET_RMCA);
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    @Override
    public boolean handleMessage(Message msg) {
        final Bundle bundle = (Bundle) msg.obj;
        switch (msg.what) {
            case CMD_UPGRADE_PQ:
                if (msg.arg1 == 0) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error),
                            "Cannot find " + bundle.getString("NotFind") + "!");
                } else if (msg.arg1 == 1) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), "success");
                }
                break;
            case CMD_UPGRADE_USB:
                if (msg.arg1 == 1) {
                    AlertDialog dialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle(mContext.getResources().getString(R.string.item_upgrade_main))
                            .setMessage(mContext.getResources().getString(R.string.item_restart))
                            .setPositiveButton("Yes", (dialog1, which) -> sendSyncCommand(CMD_UPGRADE_USB_CLICK)).create();
                    dialog.show();
                } else if (msg.arg1 == 0) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error),
                            "Cannot find " + bundle.getString("NotFind") + "!");
                }
                break;
            case CMD_UPGRADE_BOOTLOGO:
                if (msg.arg1 == 1) {
                    AlertDialog dialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle(mContext.getResources().getString(R.string.item_upgrade_bootlogo))
                            .setMessage(mContext.getResources().getString(R.string.item_restart))
                            .setPositiveButton("Yes", (dialog12, which) -> sendSyncCommand(CMD_UPGRADE_BOOTLOGO_CLICK)).create();
                    dialog.show();
                } else if (msg.arg1 == 0) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error),
                            "Cannot find " + bundle.getString("NotFind") + "!");
                }
                break;
            case CMD_UPGRADE_BOOTVIDEO:
                if (msg.arg1 == 1) {
                    AlertDialog dialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle(mContext.getResources().getString(R.string.item_upgrade_bootvideo))
                            .setMessage(mContext.getResources().getString(R.string.item_restart))
                            .setPositiveButton("Yes", (dialog13, which) -> sendSyncCommand(CMD_UPGRADE_BOOTVIDEO_CLICK)).create();
                    dialog.show();
                } else if (msg.arg1 == 0) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error),
                            "Cannot find " + bundle.getString("NotFind") + "!");
                }
                break;
            case CMD_UPGRADE_PQ_OSD:
                if (msg.arg1 == 0) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error),
                            "Cannot find " + bundle.getString("NotFind") + "!");
                } else if (msg.arg1 == 1) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), "success");
                }
                break;
            case CMD_UPGRADE_HDCP:
                if (keyMessages("HDCP",msg.arg1,mHDCPUpgrade,bundle.getString("displayHDCP14"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_HDCP)) break;

                if (msg.arg1 == 1) {
                    String displayHDCP14 = bundle.getString("displayHDCP14");
                    if (displayHDCP14 != null) {
                        mHDCPUpgrade.setSumary(displayHDCP14);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mHDCPUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mHDCPUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_GET_HDCP:
                String displayHDCP14 = bundle.getString("displayHDCP14");
                if (displayHDCP14 != null) {
                    mHDCPUpgrade.setSumary(displayHDCP14);
                }
                break;
            case CMD_UPGRADE_HDCP22:
                if (keyMessages("HDCP2",msg.arg1,mHDCPUpgrade2,bundle.getString("displayHDCP22"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_HDCP22)) break;

                if (msg.arg1 == 1) {
                    String displayHDCP22 = bundle.getString("displayHDCP22");
                    if (displayHDCP22 != null) {
                        mHDCPUpgrade2.setSumary(displayHDCP22);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mHDCPUpgrade2.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mHDCPUpgrade2.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case COM_GET_UPGRADE_HDCP:
                String displayHDCP22 = bundle.getString("displayHDCP22");
                if (displayHDCP22 != null) {
                    mHDCPUpgrade2.setSumary(displayHDCP22);
                }
                break;
            case CMD_UPGRADE_MAC_MANUAL:
                if (msg.arg1 == 1) {
                    String displayMacAddr = bundle.getString("displayMacAddr");
                    if (displayMacAddr != null) {
                        mMACUpgrade.setSumary(displayMacAddr);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mMACUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mMACUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 3) {
                    mMACUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_UPGRADE_MAC:
                if (keyMessages("MAC",msg.arg1,mMACUpgrade,bundle.getString("displayMacAddr"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_MAC)) break;

                if (msg.arg1 == 1) {
                    String displayMacAddr = bundle.getString("displayMacAddr");
                    if (displayMacAddr != null) {
                        mMACUpgrade.setSumary(displayMacAddr);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mMACUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mMACUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 3) {
                    mMACUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_GET_MAC:
                String displayMacAddr = bundle.getString("displayMacAddr");
                if (displayMacAddr != null) {
                    mMACUpgrade.setSumary(displayMacAddr);
                }
                break;
            case CMD_UPGRADE_WIDEVINE:
                if (keyMessages("Widevine",msg.arg1,mWidevineUpgrade,bundle.getString("displayWvKey"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_WIDEVINE)) break;

                if (msg.arg1 == 1) {
                    String displayWvKey = bundle.getString("displayWvKey");
                    if (displayWvKey != null) {
                        mWidevineUpgrade.setSumary(displayWvKey);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mWidevineUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mWidevineUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Cannot"));
                }
                break;
            case CMD_GET_WIDEVINE:
                String displayWvKey = bundle.getString("displayWvKey");
                if (displayWvKey != null) {
                    mWidevineUpgrade.setSumary(displayWvKey);
                }
                break;
            case CMD_UPGRADE_CI_KEY:
                if (keyMessages("Ci",msg.arg1,mCiUpgrade,bundle.getString("displayCIKey"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_CI_KEY)) break;

                if (msg.arg1 == 1) {
                    String displayCIKey = bundle.getString("displayCIKey");
                    if (displayCIKey != null) {
                        mCiUpgrade.setSumary(displayCIKey);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mCiUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mCiUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_GET_CI_KEY:
                String displayCIKey = bundle.getString("displayCIKey");
                if (displayCIKey != null) {
                    mCiUpgrade.setSumary(displayCIKey);
                }
                break;
            case CMD_UPGRADE_PQ_CLICK:
                if (msg.arg1 == 0) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), "PQ update fail");
                }
                break;
            case CMD_UPGRADE_PQ_OSD_CLICK:
                if (msg.arg1 == 0) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), "PQ OSD update fail");
                }
                break;
            case CMD_UPGRADE_PLAYREADY:
                if (keyMessages("Playready",msg.arg1,mPlayreadyUpgrade,bundle.getString("displayPrKey"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_PLAYREADY)) break;

                if (msg.arg1 == 1) {
                    String displayPrKey = bundle.getString("displayPrKey");
                    if (displayPrKey != null) {
                        mPlayreadyUpgrade.setSumary(displayPrKey);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mPlayreadyUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mPlayreadyUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Cannot"));
                }
                break;
            case CMD_GET_PLAYREADY:
                String displayPrKey = bundle.getString("displayPrKey");
                if (displayPrKey != null) {
                    mPlayreadyUpgrade.setSumary(displayPrKey);
                }
                break;
            case CMD_UPGRADE_Attestation:
                if (keyMessages("Attestation",msg.arg1,mAttestationUpgrade,bundle.getString("displayAttestationKey"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_Attestation)) break;

                if (msg.arg1 == 1) {
                    String displayAttestationKey = bundle.getString("displayAttestationKey");
                    if (displayAttestationKey != null) {
                        mAttestationUpgrade.setSumary(displayAttestationKey);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mAttestationUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mAttestationUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_GET_Attestation:
                String displayAttestationKey = bundle.getString("displayPrAttestation");
                if (displayAttestationKey != null) {
                    mAttestationUpgrade.setSumary(displayAttestationKey);
                }
                break;
            case CMD_UPGRADE_Netflix_ESN:
                if (keyMessages("Mgkid",msg.arg1,mNetflixEsnUpgrade,bundle.getString("displayNetflixESN"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_Netflix_ESN)) break;

                if (msg.arg1 == 1) {
                    String esn = bundle.getString("displayNetflixESN");
                    if (esn != null) {
                        mNetflixEsnUpgrade.setSumary(esn);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mNetflixEsnUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mNetflixEsnUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_GET_Netflix_ESN:
                String esn = bundle.getString("displayPrNetflixESN");
                if (esn != null) {
                    mNetflixEsnUpgrade.setSumary(esn);
                }
                break;
            case CMD_UPGRADE_RMCA:
                if (keyMessages("Rmca",msg.arg1,mRmcaUpgrade,bundle.getString("displayRMCA"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_RMCA)) break;

                if (msg.arg1 == 1) {
                    String rmca = bundle.getString("displayRMCA");
                    if (rmca != null) {
                        mRmcaUpgrade.setSumary(rmca);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mRmcaUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mRmcaUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_GET_RMCA:
                String rmca = bundle.getString("displayRMCA");
                if (rmca != null) {
                    mRmcaUpgrade.setSumary(rmca);
                }
                break;
            case CMD_UPGRADE_WIDEVINE_ATT:
                if (msg.arg1 == 1) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_UPGRADE_OEM:
                if (keyMessages("Oem",msg.arg1,mOemUpgrade,bundle.getString("displayOEM"))) break;
                if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext) && checkKeyUpgrade(CMD_UPGRADE_OEM)) break;

                if (msg.arg1 == 1) {
                    String oem = bundle.getString("displayOEM");
                    if (oem != null) {
                        mOemUpgrade.setSumary(oem);
                    }
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mOemUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mOemUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_GET_OEM:
                String oem = bundle.getString("displayOEM");
                if (oem != null) {
                    mOemUpgrade.setSumary(oem);
                }
                break;
            default:
                break;
        }
        return false;
    }

    public static void sendSyncCommand(final int command, String... args) {
        String sCommand = sCommands.get(command);
        if (TextUtils.isEmpty(sCommand)) {
            return;
        }
        UpgradeApi mUpgradeApi = UpgradeApi.getInstance();
        ICommandCallback commandCallback = new ICommandCallback.Stub() {

            @Override
            public void complete(int result, Bundle extra) throws RemoteException {
                Message msg = mHandler.obtainMessage(command, result, 0, extra);
                mHandler.sendMessage(msg);
            }
        };
        if (args == null || args.length == 0) {
            mUpgradeApi.sendSyncCommand(sCommand, commandCallback);
        } else {
            String argsText = TextUtils.join("#", args);
            CharSequence cmd = TextUtils.concat(sCommand, "#", argsText);
            mUpgradeApi.sendSyncCommand(cmd.toString(), commandCallback);
        }
    }

    public void checkNetwork(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
                mCheckNetwork.setSumary(mContext.getString(R.string.str_ok));
                return;
            }
        }
        mCheckNetwork.setSumary(mContext.getString(R.string.str_ng));
    }

    public void openDialog(){
        mAlertDialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("Upgrade Keys State").setMessage("").setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        allKeyUpgrade = false;
                    }
                }).setCancelable(false).create();
        mAlertDialog.show();
        mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).requestFocus();

        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(mAlertDialog);
            //通过反射修改message字体大小和颜色
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            mMessageView = (TextView) mMessage.get(mAlertController);
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        }

        allKeyUpgrade = true;
        successfulKey = new StringBuilder("Success: ");
        failedKey = new StringBuilder("Failed: ");
        alreadyKey = new StringBuilder("Already: ");
    }

    @SuppressLint("SetTextI18n")
    private boolean keyMessages(String keyStr, int msg1,SumaryPreference sumaryPreference,String result){
        if (!allKeyUpgrade){
            return false;
        }
        if (msg1 == 1){
            successfulKey.append(keyStr).append(", ");
            if (result != null) {
                sumaryPreference.setSumary(result);
            }
        }else {
            failedKey.append(keyStr).append(", ");
            sumaryPreference.setSumary(mContext.getString(R.string.str_ng));
        }

        if (FactoryApplication.CUSTOMER_IS_CH){
            mMessageView.setText(alreadyKey+"\n\n"+successfulKey + "\n\n" + failedKey);
            return true;
        }
        mMessageView.setText(successfulKey + "\n\n" + failedKey);
        return true;
    }

    private boolean getKeyState(int type) {
        String serialNumber = UpgradeApi.getInstance().getSerialNumber(type);
        if (serialNumber == null || serialNumber.isEmpty()) {
            return false;
        }
        return true;
    }

    private byte[] getBytesFromKey(String path, int size) {
        File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            return null;
        }
        byte[] bytes;
        if (size <= 0) {
            int length = (int) file.length();
            bytes = new byte[length];
        } else {
            bytes = new byte[size];
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private boolean checkKeyUpgrade(int cmd){
        boolean isUpgrade = false;
        switch (cmd){
            case CMD_UPGRADE_MAC:
               isUpgrade = getKeyState(UpgradeApi.KEYS_MAC_TYPE);
               break;
            case CMD_UPGRADE_OEM:
                String oemKey = FactoryApplication.getInstance().getFactory().getOEMKey();
                if (oemKey != null && !oemKey.isEmpty()){
                    isUpgrade = true;
                }
                break;
            case CMD_UPGRADE_Netflix_ESN:
                isUpgrade = getKeyState(UpgradeApi.KEYS_Netflix_ESN_TYPE);
                break;
            case CMD_UPGRADE_PLAYREADY:
                isUpgrade = getKeyState(UpgradeApi.KEYS_PLAYREADY_TYPE);
                break;
            case CMD_UPGRADE_HDCP:
                isUpgrade = getKeyState(UpgradeApi.KEYS_HDCP1_TYPE);
                break;
            case CMD_UPGRADE_HDCP22:
                isUpgrade = getKeyState(UpgradeApi.KEYS_HDCP2_TYPE);
                break;
            case CMD_UPGRADE_WIDEVINE:
                isUpgrade = getKeyState(UpgradeApi.KEYS_WIDEVINE_TYPE);
                break;
            case CMD_UPGRADE_CI_KEY:
                isUpgrade = getKeyState(UpgradeApi.KEYS_CIKEY_TYPE);
                break;
            case CMD_UPGRADE_Attestation:
                isUpgrade = getKeyState(UpgradeApi.KEYS_ATTESTATION_TYPE);
                break;
            case CMD_UPGRADE_RMCA:
                byte[] bytesFromKey = getBytesFromKey("mnt/vendor/factory/RMCA", 0);
                if (bytesFromKey != null && bytesFromKey.length != 0 ){
                    isUpgrade = true;
                }
                break;
            default:
        }
        if (isUpgrade && !allKeyUpgrade){
            Tools.showDialog(mContext, mContext.getString(R.string.str_fail), "Key Already Upgrade!");
        }
        return isUpgrade;
    }

    private boolean forceUpgradeKeys(Map<Integer,String> keys){
        SparseArray<String> sparseArray = new SparseArray<>();
        sparseArray.put(CMD_UPGRADE_MAC, "MAC");
        sparseArray.put(CMD_UPGRADE_OEM, "Oem");
        sparseArray.put(CMD_UPGRADE_Netflix_ESN,"Mgkid");
        sparseArray.put(CMD_UPGRADE_PLAYREADY,"Playready");
        sparseArray.put(CMD_UPGRADE_HDCP, "HDCP");
        sparseArray.put(CMD_UPGRADE_HDCP22, "HDCP2");
        sparseArray.put(CMD_UPGRADE_WIDEVINE, "Widevine");
        sparseArray.put(CMD_UPGRADE_CI_KEY, "Ci");
        sparseArray.put(CMD_UPGRADE_Attestation, "Attestation");
        sparseArray.put(CMD_UPGRADE_RMCA, "Rmca");

        Set<Integer> keyNames = keys.keySet();
        for (Integer keyName : keyNames) {
            if (checkKeyUpgrade(keyName)){
                alreadyKey.append(sparseArray.get(keyName)).append(", ");
                continue;
            }
            sendSyncCommand(keyName,keys.get(keyName));
        }
        mMessageView.setText(alreadyKey+"\n\n"+successfulKey + "\n\n" + failedKey);
        return false;
    }


    public void keyAllUpgrade() {
        boolean forceFlag = true;
        String usbPath = Tools.getFisrtUsbStroagePath(mContext);
        if (usbPath == null) return;
        openDialog();

        Map<Integer,String> keys = new HashMap<>();
        keys.put(CMD_UPGRADE_MAC, usbPath + SystemInfoFragment.PATH_MAC);
        keys.put(CMD_UPGRADE_OEM, usbPath + SystemInfoFragment.PATH_OEM);
        keys.put(CMD_UPGRADE_Netflix_ESN, usbPath + SystemInfoFragment.PATH_NETFLIX_ESN);
        keys.put(CMD_UPGRADE_PLAYREADY, usbPath + SystemInfoFragment.PATH_PLAYREADY);
        keys.put(CMD_UPGRADE_HDCP, usbPath + SystemInfoFragment.PATH_HDCP);
        keys.put(CMD_UPGRADE_HDCP22, usbPath + SystemInfoFragment.PATH_HDCP22);
        keys.put(CMD_UPGRADE_WIDEVINE, usbPath + SystemInfoFragment.PATH_WIDEVINE);
        keys.put(CMD_UPGRADE_CI_KEY, usbPath + SystemInfoFragment.PATH_CI_KEY);
        keys.put(CMD_UPGRADE_Attestation, usbPath + SystemInfoFragment.PATH_ATTESTATION_KEY);
        keys.put(CMD_UPGRADE_RMCA, usbPath + SystemInfoFragment.PATH_RMCA);

        if (FactoryApplication.CUSTOMER_IS_CH && !Tools.isKeyUpgradeForce(mContext)){
            forceFlag = forceUpgradeKeys(keys);
        }
        if (!forceFlag) return;

        Set<Integer> keyNames = keys.keySet();
        for (Integer keyName : keyNames) {
            sendSyncCommand(keyName,keys.get(keyName));
        }
    }
}
