package com.realtek.fullfactorymenu.user;

import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;

import android.provider.Settings;

import com.realtek.fullfactorymenu.utils.Tools;

import java.util.List;

public class LogLogic extends LogicInterface {

    private static final String TAG = "LogLogic";

    private StatePreference mLogOptions;
    private StatePreference mLogOnOff;
    private static final String sLogCatService = "com.realtek.fullfactorymenu/.user.LogCatService";
    private static Context thisContext = null;
    private static final String COMMAND_LOG_LOGCAT = "Logcat";
    private static final String COMMAND_LOG_KERNEL = "Kernel";
    private static final String COMMAND_RECOVERY = "Recovery";
    private static final String COMMAND_ANR = "ANR";
    private static final String COMMAND_TOMBSTONE = "Tombstone";
    private static final String COMMAND_ALL = "All";
    private static final String COMMAND_LOG_LOGCAT_AND_KERNEL = "Kernel+Logcat";
    private static final String DEFAULT_CHOICE = "LogChoice";
    private static final String DEFAULT_SWITCH = "LogOnOff";
    private static final int SWITCH_ON = 0;
    private static final int SWITCH_OFF = 1;
    private static final int CHOOSE_LOGCAT = 0;
    private static final int CHOOSE_KERNEL = 1;
    private static final int CHOOSE_RECOVERY = 2;
    private static final int CHOOSE_ANR = 3;
    private static final int CHOOSE_TOMBSTONE = 4;
    private static final int CHOOSE_LOGCAT_KERNEL = 5;
    private static final int CHOOSE_ALL = 6;

    @Override
    public void init() {
        Log.d(TAG, "init");
        mLogOnOff = (StatePreference) mContainer.findPreferenceById(R.id.item_log_onoff);
        mLogOptions = (StatePreference) mContainer.findPreferenceById(R.id.item_log_options);

        int defaultChoice = Settings.Secure.getInt(mContext.getContentResolver(), DEFAULT_CHOICE, 0);
        int defaultOnOff = Settings.Secure.getInt(mContext.getContentResolver(), DEFAULT_SWITCH, SWITCH_OFF);
        mLogOptions.init(defaultChoice);

        String initUsb = initUsbList();
        if (initUsb == null) {
            mLogOnOff.init(SWITCH_OFF);
            Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_SWITCH, SWITCH_OFF);
        } else {
            mLogOnOff.init(defaultOnOff);
            if (defaultOnOff == SWITCH_ON) {
                mLogOptions.setEnabled(false);
                Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_SWITCH, SWITCH_ON);
            }
        }
        thisContext = mContext;
    }

    public void deinit() {
        String initUsb = initUsbList();
        if (thisContext != null) {
            thisContext = null;
        }
        if (initUsb == null) {
            Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_SWITCH, SWITCH_OFF);
        }
    }

    public LogLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        Log.d(TAG, "previous --> " + previous + "  current --> " + current);
        String initUsb = initUsbList();
        if (preference.getId() == R.id.item_log_onoff) {
            if (previous == current) {
                return;
            }
            if (current == SWITCH_ON) { /* on */

                if (initUsb == null) {
                    Tools.showDialog(mContext, mContext.getString(R.string.str_error), mContext.getString(R.string.str_cannot_find));
                    mLogOnOff.setEntryIndex(1);
                    Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_SWITCH, SWITCH_OFF);
                    return;
                }
                Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_SWITCH, SWITCH_ON);
                Settings.Secure.putInt(mContext.getContentResolver(), "isOn", 1);
                mLogOptions.setEnabled(false);
                int mode = Settings.Secure.getInt(mContext.getContentResolver(), DEFAULT_CHOICE, 0);
                Log.d(TAG, "while on mode is -->" + mode);

                if (mode == CHOOSE_LOGCAT || mode == CHOOSE_LOGCAT_KERNEL || mode == CHOOSE_ALL) {
                    notifyService("start", mode);
                }

            } else { /* off */
                if (initUsb == null) {
                    return;
                }
                Log.d(TAG, " switch off.................");
                int isON = Settings.Secure.getInt(mContext.getContentResolver(), "isOn", 0); //Only turn on and then turn off to perform copy
                if (isON == 1) {
                    Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_SWITCH, SWITCH_OFF);
                    mLogOptions.setEnabled(true);
                    int mode = Settings.Secure.getInt(mContext.getContentResolver(), DEFAULT_CHOICE, 0);
                    notifyService("stop", mode);
                }
            }
        } else if (preference.getId() == R.id.item_log_options) {
            Log.d(TAG, "getCurrentEntryName -->:" + preference.getCurrentEntryName());
            switch (preference.getCurrentEntryName()) {
                case COMMAND_LOG_LOGCAT:
                    Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_CHOICE, CHOOSE_LOGCAT);
                    break;
                case COMMAND_LOG_KERNEL:
                    Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_CHOICE, CHOOSE_KERNEL);
                    break;
                case COMMAND_LOG_LOGCAT_AND_KERNEL:
                    Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_CHOICE, CHOOSE_LOGCAT_KERNEL);
                    break;
                case COMMAND_RECOVERY:
                    Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_CHOICE, CHOOSE_RECOVERY);
                    break;
                case COMMAND_ANR:
                    Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_CHOICE, CHOOSE_ANR);
                    break;
                case COMMAND_TOMBSTONE:
                    Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_CHOICE, CHOOSE_TOMBSTONE);
                    break;
                case COMMAND_ALL:
                    Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_CHOICE, CHOOSE_ALL);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }


    private String initUsbList() {
        StorageManager storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        List<VolumeInfo> volumes = storageManager.getVolumes();
        int count = volumes == null ? 0 : volumes.size();
        VolumeInfo volume = null;
        for (int i = 0; i < count; i++) {
            volume = volumes.get(i);
            if (volume.disk != null && (volume.disk.isUsb() || volume.disk.isSd())) {
                if (volume.state != VolumeInfo.STATE_MOUNTED) {
                    continue;
                }
                return volume.path;
            }
        }
        return null;
    }


    private void notifyService(String what, int mode) {
        Bundle Bundle = new Bundle();
        Intent Service = new Intent(thisContext, LogCatService.class);
        Bundle.putString("action", what);
        Bundle.putInt("mode", mode);
        Service.putExtras(Bundle);
        thisContext.startService(Service);
    }

}