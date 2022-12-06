package com.realtek.tvfactory.tune;

import static com.realtek.tvfactory.api.manager.TvCommonManager.INPUT_SOURCE_DVBS;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.api.impl.UserApi;
import com.realtek.tvfactory.api.listener.IActionCallback;
import com.realtek.tvfactory.api.manager.TvFactoryManager;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.preference.SumaryPreference;
import com.realtek.tvfactory.utils.AppToast;
import com.realtek.tvfactory.utils.Constants;
import com.realtek.tvfactory.utils.ProgressDialog;
import com.realtek.tvfactory.utils.TvInputUtils;
import com.realtek.tvfactory.utils.Utils;

import java.lang.ref.WeakReference;

public class TuningSettingLogic extends LogicInterface {

    private final String TAG = TuningSettingLogic.class.getSimpleName();

    private static final int MSG_ACTION_UPDATE_EXPORT_CHANNEL_UI = 0x01;
    private static final int MSG_ACTION_UPDATE_IMPORT_CHANNEL_UI = 0x02;

    private SumaryPreference mFactoryProgramReset;
    private SumaryPreference mExportChannel;
    private SumaryPreference mImportChannel;
    private SumaryPreference mExportPresetFileToUSB;
    private SumaryPreference mEnterCustomerFactoryMode;

    private UserApi mUserApi;
    private ProgressDialog mProgressDialog;
    private FactoryMainApi mFactoryMainApi;
    private UpdateViewHandler mUpdateViewHandler;
    private StatePreference mRecordAll;

    public TuningSettingLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mUserApi = UserApi.getInstance();
        mFactoryMainApi = FactoryMainApi.getInstance();
        mUpdateViewHandler = new UpdateViewHandler(this);

        StatePreference mMuteColor = (StatePreference) mContainer.findPreferenceById(R.id.mute_color);
        mFactoryProgramReset = (SumaryPreference) mContainer.findPreferenceById(R.id.factory_program_reset);
        mExportChannel = (SumaryPreference) mContainer.findPreferenceById(R.id.export_channel);
        mImportChannel = (SumaryPreference) mContainer.findPreferenceById(R.id.import_channel);
        mExportPresetFileToUSB = (SumaryPreference) mContainer.findPreferenceById(R.id.export_preset_file_to_usb);
        mEnterCustomerFactoryMode = (SumaryPreference) mContainer.findPreferenceById(R.id.enter_customer_factory_mode);
        StatePreference mPvrEnable = (StatePreference) mContainer.findPreferenceById(R.id.pvr_enable);
        mRecordAll = (StatePreference) mContainer.findPreferenceById(R.id.record_all);

        mMuteColor.init(mUserApi.getVideoMuteColor());
        mFactoryProgramReset.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mExportChannel.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mImportChannel.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mExportPresetFileToUSB.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mEnterCustomerFactoryMode.setSumary(mContext.getString(R.string.item_operation_text_fail));
        mPvrEnable.init(mFactoryMainApi.getBooleanValue("PVR_FUNCTION_ENABLED") ? 1 : 0);
        mRecordAll.init(Settings.Global.getInt(mContext.getContentResolver(), Constants.RECORD_ALL_ENABLE, 0));

        /*
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
        */
    }

    @Override
    public void deinit() {
        int recordAllEnable = Settings.Global.getInt(mContext.getContentResolver(), Constants.RECORD_ALL_ENABLE, 0);
        if (recordAllEnable == 1) {
            Log.d(TAG, "deinit stop record.");
            mUserApi.setPvrRecordAll(false, Utils.getUSBInternalPath(mContext));
            Settings.Global.putInt(mContext.getContentResolver(), Constants.RECORD_ALL_ENABLE, 0);
        }
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
                boolean isRecord = current == 1;
                String usbPath = Utils.getUSBInternalPath(mContext);
                if (usbPath == null || TextUtils.isEmpty(usbPath)) {
                    mRecordAll.init(0);
                    Toast.makeText(mContext, "No USB device Found.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean status = mUserApi.setPvrRecordAll(isRecord, Utils.getUSBInternalPath(mContext));
                    if (isRecord) {
                        Settings.Global.putInt(mContext.getContentResolver(), Constants.RECORD_ALL_ENABLE, status ? 1 : 0);
                    } else {
                        Settings.Global.putInt(mContext.getContentResolver(), Constants.RECORD_ALL_ENABLE, status ? 0 : 1);
                    }
                    Toast.makeText(mContext, String.format("%s record %s!", (isRecord ? "start" : "stop"), (status ? "OK" : "NG")), Toast.LENGTH_SHORT).show();
                }
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
                            mUpdateViewHandler.sendMessage(mUpdateViewHandler.obtainMessage(MSG_ACTION_UPDATE_EXPORT_CHANNEL_UI, R.string.item_operation_text_pass, -1));
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
                            mUpdateViewHandler.sendMessage(mUpdateViewHandler.obtainMessage(MSG_ACTION_UPDATE_IMPORT_CHANNEL_UI, R.string.item_operation_text_pass, -1));
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
                    if (result == TvFactoryManager.EXPORT_SETTINGS_RESULT_FAIL) {
                        mUpdateViewHandler.sendMessage(mUpdateViewHandler.obtainMessage(MSG_ACTION_UPDATE_EXPORT_CHANNEL_UI, R.string.item_operation_text_fail, -1));
                    } else if (result == TvFactoryManager.IMPORT_SETTINGS_RESULT_FAIL) {
                        mUpdateViewHandler.sendMessage(mUpdateViewHandler.obtainMessage(MSG_ACTION_UPDATE_IMPORT_CHANNEL_UI, R.string.item_operation_text_fail, -1));
                    }
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

    final static class UpdateViewHandler extends Handler {
        private final WeakReference<TuningSettingLogic> weakReference;

        public UpdateViewHandler(TuningSettingLogic logic) {
            weakReference = new WeakReference<>(logic);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            TuningSettingLogic logic = weakReference.get();
            if (logic == null)
                return;
            switch (msg.what) {
                case MSG_ACTION_UPDATE_EXPORT_CHANNEL_UI: {
                    int resId = msg.arg1;
                    if (resId > 0) {
                        logic.setBackupDBToUSBValue(logic.mContext.getString(resId));
                    }
                    break;
                }
                case MSG_ACTION_UPDATE_IMPORT_CHANNEL_UI: {
                    int resId = msg.arg1;
                    if (resId > 0) {
                        logic.setRestoreDBFromUSB(logic.mContext.getString(resId));
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
}
