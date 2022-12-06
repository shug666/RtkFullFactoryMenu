package com.realtek.tvfactory;

import android.icu.text.SimpleDateFormat;

import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;

import java.util.Locale;

/**
 * Created by Administrator on 2019/3/9.
 */

public class MainLogic extends LogicInterface {

    private static final String TAG = "MainLogic";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private StatePreference mCustomerPowerOnMode = null;
    private Preference mAndroidReset =null;
    private Preference mReset =null;
    private Preference mSystemInfo =null;
    private FactoryMainApi mFactoryMainApi;
    public MainLogic(PreferenceContainer container) {
        super(container);
        mFactoryMainApi = FactoryMainApi.getInstance();
    }

    @Override
    public void init() {
        if (FactoryApplication.CUSTOMER_IS_KK) {
            return;
        }
        mCustomerPowerOnMode = (StatePreference) mContainer.findPreferenceById(R.id.CustomerPowerOnMode);
        mAndroidReset = (Preference) mContainer.findPreferenceById(R.id.android_reset);
        mReset = (Preference) mContainer.findPreferenceById(R.id.reset);
        mSystemInfo = (Preference) mContainer.findPreferenceById(R.id.system_info);
        //mCustomerPowerOnMode.init(mTvFactoryManager.getAcPowerOnMode());
        if (mFactoryMainApi.getAcPowerOnMode() == 0) {
            mCustomerPowerOnMode.init(0);
        } else if (mFactoryMainApi.getAcPowerOnMode() == 1) {
            mCustomerPowerOnMode.init(1);
        } else if (mFactoryMainApi.getAcPowerOnMode() == 2) {
            mCustomerPowerOnMode.init(2);
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
          case R.id.CustomerPowerOnMode:{
              mFactoryMainApi.setAcPowerOnMode(current);
           break;
          }
       }
    }

}
