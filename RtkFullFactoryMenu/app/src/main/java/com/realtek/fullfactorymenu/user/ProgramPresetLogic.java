package com.realtek.fullfactorymenu.user;

import android.app.Activity;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.UserApi;
import com.realtek.fullfactorymenu.api.listener.IActionCallback;
import com.realtek.fullfactorymenu.api.manager.TvFactoryManager;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.preference.SumaryPreference;
import com.realtek.fullfactorymenu.utils.AppToast;
import com.realtek.fullfactorymenu.utils.ProgressDialog;
import com.realtek.fullfactorymenu.utils.TvInputUtils;

import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_DVBS;

public class ProgramPresetLogic extends LogicInterface {

    private final String TAG = "ProgramePresetLogic";
    private SumaryPreference mFactoryProgramReset;
    private SumaryPreference mBackupDBToUSB;
    private SumaryPreference mRestoreDBFromUSB;
    private SumaryPreference mExportPresetFileToUSB;
    private SumaryPreference mEnterCustomerFactoryMode;

    private UserApi mUserApi;
    private ProgressDialog mProgressDialog;

    public ProgramPresetLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mUserApi = UserApi.getInstance();
        mFactoryProgramReset = (SumaryPreference) mContainer.findPreferenceById(R.id.factory_program_reset);
        mBackupDBToUSB = (SumaryPreference) mContainer.findPreferenceById(R.id.backup_db_to_usb);
        mRestoreDBFromUSB = (SumaryPreference) mContainer.findPreferenceById(R.id.restore_db_from_usb);
        mExportPresetFileToUSB = (SumaryPreference) mContainer.findPreferenceById(R.id.export_preset_file_to_usb);
        mEnterCustomerFactoryMode = (SumaryPreference) mContainer.findPreferenceById(R.id.enter_customer_factory_mode);

        mFactoryProgramReset.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mBackupDBToUSB.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mRestoreDBFromUSB.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mExportPresetFileToUSB.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mEnterCustomerFactoryMode.setSumary(mContext.getString(R.string.item_operation_text_fail));

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

    }

    public void setFactoryProgramResetValue(String value) {
        mFactoryProgramReset.setSumary(value);
    }

    public void setBackupDBToUSBValue(String value) {
        mBackupDBToUSB.setSumary(value);
    }

    public void setRestoreDBFromUSB(String value) {
        mRestoreDBFromUSB.setSumary(value);
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
