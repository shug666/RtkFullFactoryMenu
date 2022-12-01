package com.realtek.fullfactorymenu.others;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.OthersApi;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;

public class OthersLogic extends LogicInterface {

    private StatePreference mWatchDog;
    private StatePreference mEnableSTR;

    private OthersApi mOthersApi;

    
    
    public OthersLogic(PreferenceContainer containter) {
        super(containter);
        mOthersApi = OthersApi.getInstance();
    }

    @Override
    public void init() {

        mWatchDog = (StatePreference) mContainer.findPreferenceById(R.id.others_watchDog);
        mEnableSTR = (StatePreference) mContainer.findPreferenceById(R.id.others_enableSTR);

        mWatchDog.init(mOthersApi.getWatchDogMode()? 1:0);
        int [] arr = mOthersApi.setTvosCommonCommand("GET_STR_ENABLE");
        mEnableSTR.init(arr[0]);
    }

    @Override
    public void deinit() {
        
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()) {
        case R.id.others_watchDog:
            mOthersApi.setWatchDogMode(current != 0);
            break;
        case R.id.others_enableSTR:
            mOthersApi.setTvosCommonCommand("SET_STR_ENABLE#"+current);
            break;
        default:
            break;
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

}
