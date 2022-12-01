package com.realtek.fullfactorymenu.user;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.Toast;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.os.PowerManager;
import android.os.SystemProperties;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.FactoryMainApi;
import com.realtek.fullfactorymenu.api.impl.UserApi;
import com.realtek.fullfactorymenu.api.manager.TvChannelManager;
import com.realtek.fullfactorymenu.api.manager.TvChannelManager.*;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;
import com.realtek.fullfactorymenu.utils.AppToast;


public class ProgramPresetFragment extends PreferenceFragment {

    public static final String TAG = "ProgramPresetFragment";

    private static final int REQUEST_IMPORT_SETTINGS = 1;
    private static final int MSG_IMPORT_SETTINGS = 1;
    private static final int REQUEST_EXPORT_SETTINGS = 2;
    private static final int MSG_EXPORT_SETTINGS = 2;
    private static final int REQUEST_EXPORT_PRESET_FILE = 3;

    private ProgramPresetLogic mProgramPresetLogic;
    private UserApi mUserApi;
    private Context mContext;

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_user_program_preset);
        PreferenceContainer preferenceContainer =  builder.create();
        mProgramPresetLogic = (ProgramPresetLogic) preferenceContainer.getPreferenceLogic(R.id.backup_db_to_usb);
        mUserApi = UserApi.getInstance();
        mContext = getContext();
        return preferenceContainer;
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
        super.onPreferenceItemClick(preference);
        Intent intent;
        switch (preference.getId()){
        case R.id.factory_program_reset:
            mUserApi.restoreChannelTable();
            if (mProgramPresetLogic != null){
                mProgramPresetLogic.setFactoryProgramResetValue(getString(R.string.item_operation_text_pass));
                IPowerManager pm = IPowerManager.Stub.asInterface(
                        ServiceManager.getService(Context.POWER_SERVICE));
                try {
                    pm.reboot(false, null, false);
                } catch (RemoteException e) {
                }
            }
            break;
        case R.id.backup_db_to_usb:
            intent = new Intent(getActivity(), UsbSelectorActivity.class);
            intent.putExtra(UsbSelectorActivity.EXTRA_RETURN_RESULT_WHEN_SINGLE, true);
            startActivityForResult(intent, REQUEST_EXPORT_SETTINGS);
            if (mProgramPresetLogic != null){
                mProgramPresetLogic.setBackupDBToUSBValue(getString(R.string.item_operation_text_loading));
            }
            break;
        case R.id.restore_db_from_usb:
            intent = new Intent(getActivity(), UsbSelectorActivity.class);
            intent.putExtra(UsbSelectorActivity.EXTRA_RETURN_RESULT_WHEN_SINGLE, true);
            startActivityForResult(intent, REQUEST_IMPORT_SETTINGS);
            if (mProgramPresetLogic != null){
                mProgramPresetLogic.setRestoreDBFromUSB(getString(R.string.item_operation_text_loading));
            }
            break;
        case R.id.export_preset_file_to_usb:
            intent = new Intent(getActivity(), UsbSelectorActivity.class);
            intent.putExtra(UsbSelectorActivity.EXTRA_RETURN_RESULT_WHEN_SINGLE, true);
            startActivityForResult(intent, REQUEST_EXPORT_PRESET_FILE);
            if (mProgramPresetLogic != null){
                mProgramPresetLogic.setExportPresetFileToUSB(getString(R.string.item_operation_text_pass));
            }
            break;
        case R.id.enter_customer_factory_mode:
            if (SystemProperties.getBoolean("persist.h048.factorymode.enable",false)) {
                SystemProperties.set("persist.sys.HKCFactoryTestEnable", "1");
                FactoryMainApi.getInstance().setAcPowerOnMode(0);
                rebootSystemDelayed(500);
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode){
            if (data == null) {
                AppToast.showToast(getActivity(), R.string.str_no_usb_device, Toast.LENGTH_SHORT);
                updateItem(requestCode, false);
            } else {
                switch (requestCode) {
                case REQUEST_IMPORT_SETTINGS: {
                    String path = data.getStringExtra(UsbSelectorActivity.EXTRA_PATH);
                    TvChannelManager.getInstance().registerOnChannelInfoEventListener(onChannelInfoEventListener);
                    mProgramPresetLogic.importChannelTable(path);
                    updateItem(requestCode, true);
                    break;
                }
                case REQUEST_EXPORT_SETTINGS: {
                    String path = data.getStringExtra(UsbSelectorActivity.EXTRA_PATH);
                    mProgramPresetLogic.exportChannelTable(path);
                    updateItem(requestCode, true);
                    break;
                }
                case REQUEST_EXPORT_PRESET_FILE:
                    String path = data.getStringExtra(UsbSelectorActivity.EXTRA_PATH);
                    boolean result = mUserApi.exportPresetScanFile(path);
                    if (result) {
                        AppToast.showToast(getActivity(), R.string.str_success, Toast.LENGTH_SHORT);
                    } else {
                        AppToast.showToast(getActivity(), R.string.str_fail, Toast.LENGTH_SHORT);
                    }
                    break;
                default:
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateItem(int requestCode, boolean result) {
        if (mProgramPresetLogic == null){
            return;
        }
        int res = result ? R.string.item_operation_text_pass : R.string.item_operation_text_fail;
        if (requestCode == REQUEST_EXPORT_SETTINGS) {
            mProgramPresetLogic.setBackupDBToUSBValue(getString(res));
        } else if (requestCode == REQUEST_IMPORT_SETTINGS) {
            mProgramPresetLogic.setRestoreDBFromUSB(getString(res));
        }
    }

    public void rebootSystemDelayed(long delayMillis) {
        getActivity().getMainThreadHandler().postDelayed(() -> {
            PowerManager powerManager = getActivity().getSystemService(PowerManager.class);
            powerManager.reboot("");
        }, delayMillis);
    }

    private OnChannelInfoEventListener onChannelInfoEventListener = new OnChannelInfoEventListener() {

        @Override
        public boolean onChannelInfoEvent(int what, int arg1, int arg2, Object obj) {
            if (what == TvChannelManager.TVPLAYER_IMPORT_CHANNEL_COMPLETED) {
                TvChannelManager.getInstance().unregisterOnChannelInfoEventListener(onChannelInfoEventListener);
                return true;
            }
            return false;
        }

        @Override
        public boolean onAtvProgramInfoReady(int what, int arg1, int arg2, Object obj) {
            return false;
        }

        @Override
        public boolean onDtvProgramInfoReady(int what, int arg1, int arg2, Object obj) {
            return false;
        }

        @Override
        public boolean onTvProgramInfoReady(int what, int arg1, int arg2, Object obj) {
            return false;
        }
    };

}
