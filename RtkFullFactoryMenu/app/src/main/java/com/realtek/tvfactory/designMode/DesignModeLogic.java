package com.realtek.tvfactory.designMode;

import android.app.Activity;
import android.os.SystemProperties;
import android.provider.Settings;
import android.widget.Toast;

import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.api.impl.PictureApi;
import com.realtek.tvfactory.api.impl.UserApi;
import com.realtek.tvfactory.api.manager.TvFactoryManager;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.utils.Tools;

public class DesignModeLogic extends LogicInterface {

    private final String TAG = "DesignModeLogic";
    private FactoryMainApi mFactoryMainApi;
    private UserApi mUserApi;
    private PictureApi mPictureApi;
    private StatePreference mTestPattern;
    private StatePreference mPowerMode;
    private Preference mFactoryRemote;
    private StatePreference mFactoryTest;
    private StatePreference mDisplayLogo;
    private StatePreference mBoeCmdEnable;
    private StatePreference mkeyUpgradeForce;
    private StatePreference mUartLogcat;

    private static final String DEFAULT_SWITCH = "LogOnOff";
    private static final int SWITCH_ON = 0;
    private static final int SWITCH_OFF = 1;

    public DesignModeLogic(PreferenceContainer container) {
        super(container);
        mFactoryMainApi = FactoryMainApi.getInstance();
        mUserApi = UserApi.getInstance();
        mPictureApi = PictureApi.getInstance();
    }

    @Override
    public void init() {
        mPowerMode = (StatePreference) mContainer.findPreferenceById(R.id.item_power_mode);
        mTestPattern = (StatePreference) mContainer.findPreferenceById(R.id.test_pattern);
        mFactoryTest = (StatePreference) mContainer.findPreferenceById(R.id.factory_test);
        mDisplayLogo = (StatePreference) mContainer.findPreferenceById(R.id.display_logo);
        mBoeCmdEnable = (StatePreference) mContainer.findPreferenceById(R.id.BOE_cmd_enable);
        mFactoryRemote = mContainer.findPreferenceById(R.id.factory_remote);
        mkeyUpgradeForce = (StatePreference) mContainer.findPreferenceById(R.id.key_upgrade_force);
        mUartLogcat = (StatePreference) mContainer.findPreferenceById(R.id.uart_logcat);


        mPowerMode.init(mFactoryMainApi.getAcPowerOnMode());
        int factoryMode = Settings.Secure.getInt(FactoryApplication.getInstance().getContentResolver(), "factory_test", 0);
        mFactoryTest.init(factoryMode);
        initDisplayLogo();
        mBoeCmdEnable.init(mUserApi.getBVTOnOff() ? 1 : 0);
        mkeyUpgradeForce.init(Tools.isKeyUpgradeForce(mContext) ? 1 : 0);
        mUartLogcat.init(mUserApi.getUartOnOff() ? 1 : 0);
    }

    private void initDisplayLogo(){
        if (TvFactoryManager.STRING_ON.equals(mUserApi.getEnvironment(TvFactoryManager.DISPLAY_LOGO))) {
            mDisplayLogo.init(0);
        } else if (TvFactoryManager.STRING_OFF.equals(mUserApi.getEnvironment(TvFactoryManager.DISPLAY_LOGO))) {
            mDisplayLogo.init(1);
        }
    }

    @Override
    public void deinit() {
        if (!mContext.getResources().getString(R.string.str_off).equals(mTestPattern.getCurrentEntryName())) {
            mPictureApi.setVideoTestPattern(0);
        }
        if (Tools.getFisrtUsbStroagePath(mContext) == null) {
            Settings.Secure.putInt(mContext.getContentResolver(), DEFAULT_SWITCH, SWITCH_OFF);
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()) {
            case R.id.item_power_mode:
                if (Settings.System.getInt(mContext.getContentResolver(), "MMode", 0) == 0) {
                    mFactoryMainApi.setAcPowerOnModeAndSave(current);
                } else {
                    mPowerMode.init(mFactoryMainApi.getAcPowerOnMode());
                    Toast.makeText(mContext, R.string.str_close_mmode_first, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.test_pattern:
                mPictureApi.setVideoTestPattern(current);
                break;
            case R.id.factory_test:
                Settings.Secure.putInt(FactoryApplication.getInstance().getContentResolver(), "factory_test", current);
                SystemProperties.set("persist.sys.factory_boot_mode", (0 == current) ? "-1": "0");
                int anInt = Settings.Secure.getInt(FactoryApplication.getInstance().getContentResolver(), "default_power_on_mode", -1);
                int acPowerOnMode = mFactoryMainApi.getAcPowerOnMode();
                mFactoryMainApi.setAcPowerOnMode((0 == current) ? ((anInt != -1) ? anInt : acPowerOnMode) : 0);
                break;
            case R.id.display_logo:
                mUserApi.setEnvironment(TvFactoryManager.DISPLAY_LOGO, ((0 == current) ? "on" : "off"));
                break;
            case R.id.BOE_cmd_enable:
                mUserApi.setBVTCmdOnOff(0 != current, true);
                if (0 != current) {
                    ((Activity)mContext).finish();
                }
                break;
            case R.id.key_upgrade_force:
                SystemProperties.set("persist.sys.key_upgrade_force",String.valueOf(current));
                break;
            case R.id.uart_logcat:
                mUserApi.setUartOnOff((0 == current) ? false : true);
                break;
            default:
        }
    }
}
