package com.realtek.tvfactory.others;

import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.OthersApi;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;

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
