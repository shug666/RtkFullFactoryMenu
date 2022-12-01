package com.realtek.fullfactorymenu.picture;


import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.PictureApi;
import com.realtek.fullfactorymenu.api.manager.TvCommonManager;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.utils.LogHelper;
import com.realtek.fullfactorymenu.utils.Utils;

public class PQLogic extends LogicInterface {

    private static final String TAG = "PQLogic";


    private PictureApi mPictureApi;
    private SeekBarPreference mCombBrightness;
    private SeekBarPreference mCombContrast;
    private SeekBarPreference mCombSaturation;

    public PQLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {

        mPictureApi = PictureApi.getInstance();
        mCombBrightness = (SeekBarPreference) mContainer.findPreferenceById(R.id.comb_brightness);
        mCombContrast = (SeekBarPreference) mContainer.findPreferenceById(R.id.comb_contrast);
        mCombSaturation = (SeekBarPreference) mContainer.findPreferenceById(R.id.comb_saturation);

        mCombBrightness.init(mPictureApi.getIntegerValue(TvCommonManager.COMB_BRIGHTNESS));
        mCombContrast.init(mPictureApi.getIntegerValue(TvCommonManager.COMB_CONTRAST));
        mCombSaturation.init(mPictureApi.getIntegerValue(TvCommonManager.COMB_SATURATION));
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        LogHelper.d(TAG, "%s -> progress: %d.", Utils.resourceNameOf(mContext, preference.getId()), progress);
        switch (preference.getId()) {
        case R.id.comb_brightness:
            mPictureApi.setIntegerValue(TvCommonManager.COMB_BRIGHTNESS, progress);
            break;
        case R.id.comb_contrast:
            mPictureApi.setIntegerValue(TvCommonManager.COMB_CONTRAST, progress);
            break;
        case R.id.comb_saturation:
            mPictureApi.setIntegerValue(TvCommonManager.COMB_SATURATION, progress);
            break;
        default:
            break;
        }
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        // TODO Auto-generated method stub

    }
}
