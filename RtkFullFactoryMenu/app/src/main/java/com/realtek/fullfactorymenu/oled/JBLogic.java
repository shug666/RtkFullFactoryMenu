package com.realtek.fullfactorymenu.oled;

import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_0;
import static android.view.KeyEvent.KEYCODE_9;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

import android.content.ContentResolver;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;

import com.android.tv.common.TvCommonConstants;
import com.android.tv.common.TvCommonUtils;
import com.realtek.tv.RtkSettingConstants;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;

public class JBLogic extends LogicInterface {

    private Preference mRunJB=null;
    private SeekBarPreference mUserMode=null;
    private StatePreference mJbEnable=null;
    private SeekBarPreference mRetailMode=null;
    private SeekBarPreference mCoolingInterval=null;

    private int inputNums;
    private SeekBarPreference seekBarPreference;
    private String contentKey;
    private ContentResolver contentResolver;


    public JBLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mRunJB = mContainer.findPreferenceById(R.id.Run_JB);
        mUserMode = (SeekBarPreference) mContainer.findPreferenceById(R.id.JB_User_Mode);
        mJbEnable = (StatePreference) mContainer.findPreferenceById(R.id.JB_enable);
        mRetailMode = (SeekBarPreference) mContainer.findPreferenceById(R.id.JB_Retail_Mode);
        mCoolingInterval = (SeekBarPreference) mContainer.findPreferenceById(R.id.Cooling_Interval);

        contentResolver = mContext.getContentResolver();
        int userModeValue = Settings.Global.getInt(contentResolver,
                RtkSettingConstants.JB_INTERVAL,
                RtkSettingConstants.JB_DEFAULT_INTERVAL);
        mUserMode.init(userModeValue);

        int retailModeValue = Settings.Global.getInt(contentResolver,
                RtkSettingConstants.JB_INTERVAL_RETAIL,
                RtkSettingConstants.JB_DEFAULT_INTERVAL_RETAIL);
        mRetailMode.init(retailModeValue);

        int coolingInterval = Settings.Global.getInt(contentResolver,
                RtkSettingConstants.JB_COOL_TIME,
                RtkSettingConstants.JB_DEFAULT_COOL_TIME);
        mCoolingInterval.init(coolingInterval);



        mUserMode.setOnKeyListener((view, keyCode, keyEvent) -> inputFlag(view, keyCode, keyEvent, 2000));

        mRetailMode.setOnKeyListener((view, keyCode, keyEvent) -> inputFlag(view, keyCode, keyEvent, 2000));

        mCoolingInterval.setOnKeyListener((view, keyCode, keyEvent) -> inputFlag(view,keyCode,keyEvent,120));

        int enableVale = TvCommonUtils.getGlobalSettings(mContext, RtkSettingConstants.JB_ENABLE, 1);
        mJbEnable.init(enableVale);
//        isEnable(enableVale);
    }

    @Override
    public void deinit() {

    }

    private boolean inputFlag(View view,int keyCode,KeyEvent keyEvent,int maxNum){

        switch (view.getId()){
            case R.id.JB_User_Mode:
                seekBarPreference = mUserMode;
                contentKey = RtkSettingConstants.JB_INTERVAL;
                break;
            case R.id.JB_Retail_Mode:
                seekBarPreference = mRetailMode;
                contentKey = RtkSettingConstants.JB_INTERVAL_RETAIL;
                break;
            case R.id.Cooling_Interval:
                seekBarPreference = mCoolingInterval;
                contentKey = RtkSettingConstants.JB_COOL_TIME;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }

        if (keyEvent.getAction() == ACTION_UP){
            if (keyCode == KEYCODE_DPAD_UP || keyCode == KEYCODE_DPAD_DOWN){
                inputNums = 0;
                return false;
            }else if (keyCode >= KEYCODE_0 && keyCode <= KEYCODE_9 ){
                inputNums = inputNums * 10 + (keyCode - 7);

                if (inputNums > maxNum){
                    seekBarPreference.init(maxNum);
                    Settings.Global.putInt(contentResolver,
                            contentKey, maxNum);
                    inputNums = 0;
                    return true;
                }
                Settings.Global.putInt(contentResolver,
                        contentKey, inputNums);
                seekBarPreference.init(inputNums);
                return true;
            }
        }
        return false;
    }


    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        switch (preference.getId()) {
            case R.id.JB_User_Mode:
                Settings.Global.putInt(contentResolver,
                        RtkSettingConstants.JB_INTERVAL, progress);
                TvCommonUtils.sendBroadCastToKeyIntercept(mContext,
                        TvCommonConstants.ACTION_OLED_TCON_INTERVAL);
                break;
            case R.id.JB_Retail_Mode:
                Settings.Global.putInt(contentResolver,
                        RtkSettingConstants.JB_INTERVAL_RETAIL, progress);
                break;
            case R.id.Cooling_Interval:
                Settings.Global.putInt(contentResolver,
                        RtkSettingConstants.JB_COOL_TIME, progress);
                break;
        }
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()) {
            case R.id.JB_enable:
                TvCommonUtils.putGlobalSettings(mContext, RtkSettingConstants.JB_ENABLE, current);
                break;
        }
    }

    private void isEnable(int enable){
        boolean b = enable == 1;
        mRunJB.setEnabled(b);
        mUserMode.setEnabled(b);
        mRetailMode.setEnabled(b);
        mCoolingInterval.setEnabled(b);
    }
}
