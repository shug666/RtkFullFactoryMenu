package com.realtek.fullfactorymenu.ssc;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.PictureApi;
import com.realtek.fullfactorymenu.api.manager.TvCommonManager;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.utils.LogHelper;
import com.realtek.fullfactorymenu.utils.Utils;

public class SscAdjustLogic extends LogicInterface {

    private static final String TAG = "SscAdjustLogic";

    private PictureApi mPictureApi;
    private StatePreference mLvdsEnable;
    private SeekBarPreference mLvdsPercentage;
    private SeekBarPreference mLvdsPeriod;
    private StatePreference mDDRPercentage;
    private String[] DDRSpreadName;

    public SscAdjustLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mPictureApi = PictureApi.getInstance();
        mLvdsEnable = (StatePreference) mContainer.findPreferenceById(R.id.ssc_lvds_enable);
        mLvdsPercentage = (SeekBarPreference) mContainer.findPreferenceById(R.id.ssc_lvds_percentage);
        mLvdsPeriod = (SeekBarPreference) mContainer.findPreferenceById(R.id.ssc_lvds_period);
        mDDRPercentage = (StatePreference) mContainer.findPreferenceById(R.id.ssc_ddr_percentage);
        mLvdsEnable.init(mPictureApi.getLvdsenable() ? 1 : 0);
        mLvdsPercentage.init(mPictureApi.getLvdsPercentage());
        mLvdsPeriod.init(mPictureApi.getLvdsPeriod() / 10);
//        DDRSpreadName = new String[]{"0.00%", "0.25%", "0.50%", "0.75%", "1.00%", "1.25%", "1.50%", "1.75%","-0.25%", "-0.50%", "-0.75%", "-1.00%", "-1.25%", "-1.50%", "-1.75%"};
        DDRSpreadName = new String[]{"0.00%",
                "-0.25%", "-0.50%", "-0.75%", "-1.00%",
                "-1.25%", "-1.50%", "-1.75%", "-2.00%",
                "-2.25%", "-2.50%", "-2.75%", "-3.00%",
                "-3.25%", "-3.50%", "-3.75%", "-4.00%",
                "-4.25%", "-4.50%", "-4.75%", "-5.00%",
                "-5.25%", "-5.50%", "-5.75%", "-6.00%",
                "0.25%", "0.50%", "0.75%", "1.00%",
                "1.25%", "1.50%", "1.75%", "2.00%",
                "2.25%", "2.50%", "2.75%", "3.00%",
                "3.25%", "3.50%", "3.75%", "4.00%",
                "4.25%", "4.50%", "4.75%", "5.00%",
                "5.25%", "5.50%", "5.75%", "6.00%"};
        int misc_ddrSpread_ddrSpreadRatio_index = mPictureApi.getIntegerValue(TvCommonManager.DDRSPREAD_RATIO);
        mDDRPercentage.init(DDRSpreadName, DDRSpreadName, misc_ddrSpread_ddrSpreadRatio_index);
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        LogHelper.d(TAG, "%s -> index: %d.", Utils.resourceNameOf(mContext, preference.getId()), current);
        switch (preference.getId()) {
            case R.id.ssc_lvds_enable:
                int progress = mLvdsPercentage.getProgress();
                mPictureApi.setLvdsEnable(current != 0, progress);
                break;
            case R.id.ssc_ddr_percentage:
                mPictureApi.setIntegerValue(TvCommonManager.DDRSPREAD_RATIO, current);
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

        switch (preference.getId()) {
            case R.id.ssc_lvds_percentage:
                String currentEntryName = mLvdsEnable.getCurrentEntryName();
                boolean isEnable = currentEntryName.equals("On");
                mPictureApi.setLvdsPercentage(progress, isEnable);
                break;
            case R.id.ssc_lvds_period:
                int period = progress * 10;
                mPictureApi.setLvdsPeriod(period);
                break;
            default:
                break;
        }
    }

}
