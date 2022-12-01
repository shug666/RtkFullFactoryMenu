package com.realtek.fullfactorymenu.debug;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.OthersApi;
import com.realtek.fullfactorymenu.api.impl.UserApi;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.utils.Utils;

public class DebugLogic extends  LogicInterface {
    private OthersApi mOthersApi;
    private UserApi mUserApi;
    private StatePreference mWatchDog;
    private StatePreference mUartEnable;
    private StatePreference mPvrRecordAll;

    public DebugLogic(PreferenceContainer container) {
        super(container);
        mUserApi = UserApi.getInstance();
        mOthersApi = OthersApi.getInstance();
    }

    @Override
    public void init() {
        mWatchDog = (StatePreference) mContainer.findPreferenceById(R.id.others_watchDog);
        mUartEnable = (StatePreference) mContainer.findPreferenceById(R.id.uart_enable);
        mPvrRecordAll = (StatePreference) mContainer.findPreferenceById(R.id.pvr_record_all);
        mWatchDog.init(mOthersApi.getWatchDogMode()? 1:0);
        mUartEnable.init(mUserApi.getUartOnOff() ? 1 : 0);
        mPvrRecordAll.init(0);

    }

    @Override
    public void deinit() {
        mUserApi.setPvrRecordAll(false, Utils.getUSBInternalPath(mContext));
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()) {
            case R.id.others_watchDog:
                mOthersApi.setWatchDogMode(current != 0);
                break;
            case R.id.uart_enable:
                mUserApi.setUartOnOff((0 == current) ? false : true);
                break;
            case R.id.pvr_record_all:
                boolean status = (0 == current) ? false : true;
                mUserApi.setPvrRecordAll(status, Utils.getUSBInternalPath(mContext));
                break;
            default:
                break;
        }

    }
}
