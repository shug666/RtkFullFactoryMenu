package com.realtek.fullfactorymenu.systemInfo;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.UpgradeApi;
import com.realtek.fullfactorymenu.api.listener.ICommandCallback;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.preference.SumaryPreference;
import com.realtek.fullfactorymenu.utils.Tools;

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
    private SumaryPreference mMACUpgradeManual;
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

    public SystemInfoLogic(PreferenceContainer containter) {
        super(containter);
        mHandler = new Handler(this);
    }

    @Override
    public void init() {
        mCheckNetwork = (SumaryPreference) mContainer.findPreferenceById(R.id.check_network);
        mMACUpgradeManual = (SumaryPreference) mContainer.findPreferenceById(R.id.upgrede_mac_manual);
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
                if (msg.arg1 == 1) {
                    mHDCPUpgrade.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mHDCPUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mHDCPUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_UPGRADE_HDCP22:
                if (msg.arg1 == 1) {
                    mHDCPUpgrade2.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mHDCPUpgrade2.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mHDCPUpgrade2.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_UPGRADE_MAC_MANUAL:
                if (msg.arg1 == 1) {
                    mMACUpgradeManual.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mMACUpgradeManual.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mMACUpgradeManual.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 3) {
                    mMACUpgradeManual.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_UPGRADE_MAC:
                if (msg.arg1 == 1) {
                    mMACUpgrade.setSumary(mContext.getString(R.string.str_ok));
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
            case CMD_UPGRADE_WIDEVINE:
                if (msg.arg1 == 1) {
                    mWidevineUpgrade.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mWidevineUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mWidevineUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Cannot"));
                }
                break;
            case CMD_UPGRADE_CI_KEY:
                if (msg.arg1 == 1) {
                    mCiUpgrade.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mCiUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mCiUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
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
                if (msg.arg1 == 1) {
                    mPlayreadyUpgrade.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mPlayreadyUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mPlayreadyUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Cannot"));
                }
                break;
            case CMD_UPGRADE_Attestation:
                if (msg.arg1 == 1) {
                    mAttestationUpgrade.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mAttestationUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mAttestationUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_UPGRADE_Netflix_ESN:
                if (msg.arg1 == 1) {
                    mNetflixEsnUpgrade.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mNetflixEsnUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mNetflixEsnUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
                }
                break;
            case CMD_UPGRADE_RMCA:
                if (msg.arg1 == 1) {
                    mRmcaUpgrade.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mRmcaUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mRmcaUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
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
                if (msg.arg1 == 1) {
                    mOemUpgrade.setSumary(mContext.getString(R.string.str_ok));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_success), bundle.getString("Success"));
                } else if (msg.arg1 == 0) {
                    mOemUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_failed), bundle.getString("Failed"));
                } else if (msg.arg1 == 2) {
                    mOemUpgrade.setSumary(mContext.getString(R.string.str_ng));
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), bundle.getString("Cannot"));
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

}
