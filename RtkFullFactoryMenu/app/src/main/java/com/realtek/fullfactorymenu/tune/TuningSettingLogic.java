package com.realtek.fullfactorymenu.tune;

import android.app.Activity;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.FactoryMainApi;
import com.realtek.fullfactorymenu.api.impl.UserApi;
import com.realtek.fullfactorymenu.api.listener.IActionCallback;
import com.realtek.fullfactorymenu.api.manager.TvFactoryManager;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.preference.SumaryPreference;
import com.realtek.fullfactorymenu.utils.AppToast;
import com.realtek.fullfactorymenu.utils.ProgressDialog;
import com.realtek.fullfactorymenu.utils.TvInputUtils;
import com.realtek.fullfactorymenu.utils.Utils;

import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_DVBS;

public class TuningSettingLogic extends LogicInterface {

    private final String TAG = TuningSettingLogic.class.getSimpleName();
    private SumaryPreference mFactoryProgramReset;
    private SumaryPreference mExportChannel;
    private SumaryPreference mImportChannel;
    private SumaryPreference mExportPresetFileToUSB;
    private SumaryPreference mEnterCustomerFactoryMode;

    private UserApi mUserApi;
    private ProgressDialog mProgressDialog;
    private FactoryMainApi mFactoryMainApi;

    public TuningSettingLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mUserApi = UserApi.getInstance();
        mFactoryMainApi = FactoryMainApi.getInstance();
        StatePreference mMuteColor = (StatePreference) mContainer.findPreferenceById(R.id.mute_color);
        mFactoryProgramReset = (SumaryPreference) mContainer.findPreferenceById(R.id.factory_program_reset);
        mExportChannel = (SumaryPreference) mContainer.findPreferenceById(R.id.export_channel);
        mImportChannel = (SumaryPreference) mContainer.findPreferenceById(R.id.import_channel);
        mExportPresetFileToUSB = (SumaryPreference) mContainer.findPreferenceById(R.id.export_preset_file_to_usb);
        mEnterCustomerFactoryMode = (SumaryPreference) mContainer.findPreferenceById(R.id.enter_customer_factory_mode);
        StatePreference mPvrEnable = (StatePreference) mContainer.findPreferenceById(R.id.pvr_enable);
        StatePreference mRecordAll = (StatePreference) mContainer.findPreferenceById(R.id.record_all);

        mMuteColor.init(mUserApi.getVideoMuteColor());
        mFactoryProgramReset.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mExportChannel.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mImportChannel.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mExportPresetFileToUSB.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mEnterCustomerFactoryMode.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mPvrEnable.init(mFactoryMainApi.getBooleanValue("PVR_FUNCTION_ENABLED") ? 1 : 0);
        mRecordAll.init(0);

        if (SystemProperties.getBoolean("persist.sys.preset.scan.enable", false)) {
            mExportPresetFileToUSB.setVisibility(View.VISIBLE);
        } else {
            mExportPresetFileToUSB.setVisibility(View.GONE);
        }

        if (SystemProperties.getBoolean("persist.h048.factorymode.enable", false)) {
            mEnterCustomerFactoryMode.setVisibility(View.VISIBLE);
        } else {
            mEnterCustomerFactoryMode.setVisibility(View.GONE);
        }
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()) {
            case R.id.mute_color:
                mUserApi.setVideoMuteColor(current);
                break;
            case R.id.pvr_enable: {
                boolean status = 0 != current;
                mFactoryMainApi.setBooleanValue("PVR_FUNCTION_ENABLED", status);
                break;
            }
            case R.id.record_all: {
                boolean status = 0 != current;
                mUserApi.setPvrRecordAll(status, Utils.getUSBInternalPath(mContext));
                break;
            }
            default:
                break;
        }
    }

    public void setFactoryProgramResetValue(String value) {
        mFactoryProgramReset.setSumary(value);
    }

    public void setBackupDBToUSBValue(String value) {
        mExportChannel.setSumary(value);
    }

    public void setRestoreDBFromUSB(String value) {
        mImportChannel.setSumary(value);
    }

    public void setExportPresetFileToUSB(String value) {
        mExportPresetFileToUSB.setSumary(value);
    }

    public void setEnterCustomerFactoryModeValue(String value) {
        mEnterCustomerFactoryMode.setSumary(value);
    }

    public void exportChannelTable(String usbPath) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.str_wait));
            mProgressDialog.setCancelable(false);
        }
        if (!mProgressDialog.isShowing()) {
            Settings.Secure.putInt(mContext.getContentResolver(), "tv_user_setup_complete", 0);
            mProgressDialog.show();
        }
        mUserApi.exportChannelTable(usbPath, mActionCallback);
    }

    public void importChannelTable(String usbPath) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.str_wait));
            mProgressDialog.setCancelable(false);
        }
        if (!mProgressDialog.isShowing()) {
            Settings.Secure.putInt(mContext.getContentResolver(), "tv_user_setup_complete", 0);
            mProgressDialog.show();
        }
        mUserApi.importChannelTable(usbPath, mActionCallback);
    }

    public void rebootSystemDelayed(long delayMillis) {
        mContext.getMainThreadHandler().postDelayed(() -> {
            PowerManager powerManager = mContext.getSystemService(PowerManager.class);
            powerManager.reboot("");
        }, delayMillis);
    }

    private final IActionCallback.Stub mActionCallback = new IActionCallback.Stub() {

        @Override
        public void onCompleted(int result) throws RemoteException {
            switch (result) {
                case TvFactoryManager.EXPORT_SETTINGS_RESULT_SUCCESS:
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mProgressDialog.isShowing() && mProgressDialog != null) {
                                mProgressDialog.dismiss();
                                Settings.Secure.putInt(mContext.getContentResolver(), "tv_user_setup_complete", 1);
                            }
                            AppToast.showToast(mContext, R.string.str_success, Toast.LENGTH_SHORT);
                        }
                    });
                    break;
                case TvFactoryManager.IMPORT_SETTINGS_RESULT_SUCCESS:
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mProgressDialog.isShowing() && mProgressDialog != null) {
                                mProgressDialog.dismiss();
                                Settings.Secure.putInt(mContext.getContentResolver(), "tv_user_setup_complete", 1);
                            }
                            AppToast.showToast(mContext, R.string.str_success, Toast.LENGTH_SHORT);
                            int tvInput = FactoryApplication.getInstance().getInputSource(TvInputUtils.getCurrentInput(mContext));
                            if(tvInput == INPUT_SOURCE_DVBS){
                                AppToast.showToast(mContext, R.string.import_reset, Toast.LENGTH_SHORT);
                                rebootSystemDelayed(2000);
                            }
                        }
                    });
                    break;
                default:
                    ((Activity)mContext).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressDialog.isShowing() && mProgressDialog != null) {
                                mProgressDialog.dismiss();
                                Settings.Secure.putInt(mContext.getContentResolver(), "tv_user_setup_complete", 1);
                            }
                            AppToast.showToast(mContext, R.string.str_fail, Toast.LENGTH_SHORT);
                        }
                    });
                    break;
            }
        }
    };
}
