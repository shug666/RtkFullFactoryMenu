package com.realtek.tvfactory.user;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.api.impl.PictureApi;
import com.realtek.tvfactory.api.impl.UserApi;
import com.realtek.tvfactory.api.manager.TvCommonManager;
import com.realtek.tvfactory.api.manager.TvFactoryManager;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.preference.SumaryPreference;
import com.realtek.tvfactory.utils.AppToast;
import com.realtek.tvfactory.utils.TvInputUtils;
import com.realtek.tvfactory.utils.TvUtils;

public class UserLogic extends LogicInterface {

    private final String TAG = "UserLogic";
    private StatePreference mTestPattern = null;
    private Preference mPanel = null;
    private StatePreference mPowerMode = null;
    private StatePreference mDisplayLogo = null;
    private StatePreference mFactoryTest = null;
    private StatePreference mUartEnable = null;
    private StatePreference mBvtCmdEnable = null;
    private StatePreference mFactoryRemoteControlEnable = null;
    private StatePreference mTeletextEnable = null;
    private SumaryPreference mTotalRunTime = null;
    private FactoryMainApi mFactoryMainApi;
    private UserApi mUserApi;
    private PictureApi mPictureApi;

    public UserLogic(PreferenceContainer container) {
        super(container);
        mFactoryMainApi = FactoryMainApi.getInstance();
        mUserApi = UserApi.getInstance();
        mPictureApi = PictureApi.getInstance();
    }

    public void init() {
        if (FactoryApplication.CUSTOMER_IS_KK) {
            mTestPattern = (StatePreference) mContainer.findPreferenceById(R.id.test_pattern);
            mTestPattern.setVisibility(View.VISIBLE);
            mContainer.findPreferenceById(R.id.user_panel).setVisibility(View.VISIBLE);
            mContainer.findPreferenceById(R.id.uart_enable).setVisibility(View.GONE);
            mContainer.findPreferenceById(R.id.remote_control_enable).setVisibility(View.GONE);
            mContainer.findPreferenceById(R.id.logcat_tools).setVisibility(View.GONE);
            mContainer.findPreferenceById(R.id.wifi_list).setVisibility(View.GONE);
            mContainer.findPreferenceById(R.id.bluetooth_list).setVisibility(View.GONE);
            mContainer.findPreferenceById(R.id.bash_board).setVisibility(View.GONE);
        } else {
            mUartEnable = (StatePreference) mContainer.findPreferenceById(R.id.uart_enable);
            mUartEnable.init(mUserApi.getUartOnOff() ? 1 : 0);
            mFactoryRemoteControlEnable = (StatePreference) mContainer.findPreferenceById(R.id.remote_control_enable);
            mFactoryRemoteControlEnable.init(mUserApi.getFactoryRemoteControlOnOff() ? 1 : 0);
        }
        mBvtCmdEnable = (StatePreference) mContainer.findPreferenceById(R.id.BVT_cmd_enable);
        mBvtCmdEnable.init(mUserApi.getBVTOnOff() ? 1 : 0);
        mPowerMode = (StatePreference) mContainer.findPreferenceById(R.id.power_mode);
        mFactoryTest = (StatePreference) mContainer.findPreferenceById(R.id.factory_test);
        mDisplayLogo = (StatePreference) mContainer.findPreferenceById(R.id.display_logo);
        mTeletextEnable = (StatePreference) mContainer.findPreferenceById(R.id.teletext_enable);
        mTotalRunTime = (SumaryPreference) mContainer.findPreferenceById(R.id.TotalRunTimt);
        Log.d(TAG, "uart : " + mUserApi.getUartOnOff());
        mPowerMode.init(mFactoryMainApi.getAcPowerOnMode());
        int mainInputSource = FactoryApplication.getInstance().getInputSource(TvInputUtils.getCurrentInput(mContext));
        if (TvUtils.isHdmi(mainInputSource) || TvUtils.isVga(mainInputSource) || TvUtils.isYpbpr(mainInputSource) || mainInputSource == -1) {
            mTeletextEnable.setVisibility(View.GONE);
        }
        if (TvUtils.isDtv(mainInputSource) && (mFactoryMainApi.getBooleanValue("SYSTEM_ISDB")
                || mFactoryMainApi.getBooleanValue("SYSTEM_ATSC")
                || mFactoryMainApi.getBooleanValue("SYSTEM_DTMB"))) {
            mTeletextEnable.setVisibility(View.GONE);
        }
        if (TvUtils.isAtv(mainInputSource) && mFactoryMainApi.getBooleanValue("SYSTEM_NTSC")) {
            mTeletextEnable.setVisibility(View.GONE);
        }
        if (TvFactoryManager.STRING_ON.equals(mUserApi.getEnvironment(TvFactoryManager.DISPLAY_LOGO))) {
            mDisplayLogo.init(0);
        } else if (TvFactoryManager.STRING_OFF.equals(mUserApi.getEnvironment(TvFactoryManager.DISPLAY_LOGO))) {
            mDisplayLogo.init(1);
        }
        int factoryMode = Settings.Secure.getInt(FactoryApplication.getInstance().getContentResolver(), "factory_test", 0);
        mFactoryTest.init(factoryMode);

        mTeletextEnable.init(mFactoryMainApi.getBooleanValue(TvCommonManager.TELETEXT_ENABLED) ? 1 : 0);

        String readOutStr = mFactoryMainApi.getStringValue(TvCommonManager.TOTAL_RUN_TIME);
//        readOutStr = String.valueOf(Float.parseFloat(readOutStr) / 2f);
        mTotalRunTime.setSumary(readOutStr + " ");
    }

    public void deinit() {
        if (FactoryApplication.CUSTOMER_IS_KK) {
            Log.d(TAG, String.format("name:%s value:%s", mTestPattern.getCurrentEntryName(), mTestPattern.getCurrentEntryValue()));
            if (!mContext.getResources().getString(R.string.str_off).equals(mTestPattern.getCurrentEntryName())) {
                mPictureApi.setVideoTestPattern(0);
            }
        }
    }

    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    private Handler mHander = new Handler() {
        public void handleMessage(Message msg) {
            PowerManager pManager = (PowerManager) mContext.getSystemService(mContext.POWER_SERVICE);
            pManager.reboot("");
        }
    };

    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {

        switch (preference.getId()) {
        case R.id.test_pattern:
            mPictureApi.setVideoTestPattern(current);
            break;
        case R.id.power_mode:
            mFactoryMainApi.setAcPowerOnMode(current);
            break;
        case R.id.uart_enable:
            mUserApi.setUartOnOff(0 != current);
            break;
        case R.id.BVT_cmd_enable:
            mUserApi.setBVTCmdOnOff(0 != current, true);
            if (0 != current) {
                ((Activity)mContext).finish();
            }
            break;
        case R.id.remote_control_enable:
            mUserApi.setFactoryRemoteControlOnOff(0 != current);
            break;
        case R.id.teletext_enable:
            mFactoryMainApi.setBooleanValue(TvCommonManager.TELETEXT_ENABLED,
                    0 != current);
            AppToast.showToast(mContext, R.string.teletext_reset, Toast.LENGTH_SHORT);
            mHander.sendEmptyMessageDelayed(10000, 2000);
            break;
        case R.id.display_logo:
            Log.d(TAG, "current : " + current);
            mUserApi.setEnvironment(TvFactoryManager.DISPLAY_LOGO, ((0 == current) ? "on" : "off"));
            break;
        case R.id.factory_test:
            Settings.Secure.putInt(FactoryApplication.getInstance().getContentResolver(), "factory_test", current);
            SystemProperties.set("persist.sys.factory_boot_mode", (0 == current) ? "-1": "0");
            int anInt = Settings.Secure.getInt(FactoryApplication.getInstance().getContentResolver(), "default_power_on_mode", -1);
            int acPowerOnMode = mFactoryMainApi.getAcPowerOnMode();
            mFactoryMainApi.setAcPowerOnMode((0 == current) ? ((anInt != -1) ? anInt : acPowerOnMode) : 0);
            break;
        }

    }

}
