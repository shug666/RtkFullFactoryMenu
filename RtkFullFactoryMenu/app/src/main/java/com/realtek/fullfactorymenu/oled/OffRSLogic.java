package com.realtek.fullfactorymenu.oled;


import android.provider.Settings;

import com.android.tv.common.TvCommonConstants;
import com.android.tv.common.TvCommonUtils;
import com.realtek.tv.Factory;
import com.realtek.tv.RtkSettingConstants;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;



public class OffRSLogic extends LogicInterface {

    private static final Factory mFactory = new Factory();

    private Preference mRumOffRs=null;
    private SeekBarPreference offRSInterval=null;
    private StatePreference moffRSEnable=null;

    public OffRSLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mRumOffRs = mContainer.findPreferenceById(R.id.RunOffRS);

        offRSInterval = (SeekBarPreference) mContainer.findPreferenceById(R.id.OffRS_Interval);
        moffRSEnable = (StatePreference) mContainer.findPreferenceById(R.id.OffRS_enable);


        int offrsValue = Settings.Global.getInt(mContext.getContentResolver(),
                RtkSettingConstants.OFFRS_INTERVAL,
                RtkSettingConstants.OFFRS_DEFAULT_INTERVAL);
        offRSInterval.init(offrsValue);
        int enableValue = TvCommonUtils.getGlobalSettings(mContext, RtkSettingConstants.OFFRS_ENABLE, 1);
        moffRSEnable.init(enableValue);
//        isEnable(enableValue);

    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        switch (preference.getId()) {
            case R.id.OffRS_Interval:
                Settings.Global.putInt(mContext.getContentResolver(),
                        RtkSettingConstants.OFFRS_INTERVAL, progress);
                TvCommonUtils.sendBroadCastToKeyIntercept(mContext,
                        TvCommonConstants.ACTION_OLED_TCON_INTERVAL);
                break;
        }
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()) {
            case R.id.OffRS_enable:
                TvCommonUtils.putGlobalSettings(mContext, RtkSettingConstants.OFFRS_ENABLE, current);
                break;
        }
    }

    private void isEnable(int enable){
        boolean b = enable == 1;
        mRumOffRs.setEnabled(b);
        offRSInterval.setEnabled(b);
    }
}
