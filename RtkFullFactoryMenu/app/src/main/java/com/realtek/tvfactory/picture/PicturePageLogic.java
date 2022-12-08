package com.realtek.tvfactory.picture;


import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.PictureApi;
import com.realtek.tvfactory.api.manager.TvCommonManager;
import com.realtek.tvfactory.api.manager.TvPictureManager;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.preference.SumaryPreference;
import com.realtek.tvfactory.utils.Constants;
import com.realtek.tvfactory.utils.LogHelper;
import com.realtek.tvfactory.utils.Predicate;
import com.realtek.tvfactory.utils.TvInputUtils;
import com.realtek.tvfactory.utils.Utils;

public class PicturePageLogic extends LogicInterface {

    private static final String TAG = "PicturePageLogic";

    private PictureApi mPictureApi;
    private StatePreference mTestPattern;
    private SumaryPreference pic_backlight;

    private int backlight_default;

    public PicturePageLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {

        mPictureApi = PictureApi.getInstance();
        mTestPattern = (StatePreference) mContainer.findPreferenceById(R.id.test_pattern);
        if (FactoryApplication.CUSTOMER_IS_KK) {
            mTestPattern.setVisibility(View.GONE);
        }
        Preference mOverScan = (Preference) mContainer.findPreferenceById(R.id.page_over_scan);
        int mCurrentInputSource = FactoryApplication.getInstance().getInputSource(TvInputUtils.getCurrentInput(mContext));
        boolean mIsSignalStable = FactoryApplication.getInstance().isSignalStable();
        if (mCurrentInputSource == TvCommonManager.INPUT_SOURCE_DVI ||
                mCurrentInputSource == TvCommonManager.INPUT_SOURCE_VGA ||
                !mIsSignalStable) {
            mOverScan.setEnabled(false);
        }
        SumaryPreference dream_time = (SumaryPreference) mContainer.findPreferenceById(R.id.dream_time);
        pic_backlight = (SumaryPreference) mContainer.findPreferenceById(R.id.pic_backlight);

        backlight_default = mPictureApi.getVideoItem(TvPictureManager.PICTURE_BACKLIGHT);
        int rtkDreamTime = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Constants.DEFAULT_DREAM_TIME_MS);
        dream_time.setSumary(rtkDreamTime + " ms");
        pic_backlight.setSumary(mPictureApi.getVideoItem(TvPictureManager.PICTURE_BACKLIGHT)+"");
    }

    @Override
    public void deinit() {
        if (mTestPattern.getVisibility() == View.VISIBLE) {
            Log.d(TAG, String.format("name:%s value:%s", mTestPattern.getCurrentEntryName(), mTestPattern.getCurrentEntryValue()));
            if (!mContext.getResources().getString(R.string.str_off).equals(mTestPattern.getCurrentEntryName())) {
                mPictureApi.setVideoTestPattern(0);
            }
        }
        mPictureApi.setVideoItem(TvPictureManager.PICTURE_BACKLIGHT, backlight_default);
    }

    public boolean isTestPatternEnabled() {
        Object entryValue = mTestPattern.getCurrentEntryValue();
        if (entryValue instanceof Bundle) {
            Bundle bundle = (Bundle) entryValue;
            return !isTestPatternOff(bundle);
        }
        return true;
    }

    private boolean isTestPatternOff(Bundle bundle) {
        return bundle != null && bundle.getBoolean("off", false);
    }

    public void disableTestPattern() {
        Object[] entryValues = mTestPattern.getEntryValues();
        if (entryValues instanceof Bundle[]) {
            Bundle[] values = (Bundle[]) entryValues;
            int index = Utils.firstIndexOf(values, new Predicate<Bundle>() {

                @Override
                public boolean apply(Bundle bundle) {
                    return isTestPatternOff(bundle);
                }
            });
            if (index != -1 && mTestPattern.isEntryEnabled(index)) {
                mTestPattern.setEntryIndex(index);
            }
        }
    }

    private void handleTestPatternChanged() {
        /*if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            LayoutParams attrs = activity.getWindow().getAttributes();
            int keyFeatures = attrs.keyFeatures;
            if (isTestPatternEnabled()) {
                keyFeatures |= LayoutParams.KEY_FEATURE_POWER_PASS_TO_USER;
                keyFeatures |= LayoutParams.KEY_FEATURE_HOME_PASS_TO_USER;
            } else {
                keyFeatures &= ~LayoutParams.KEY_FEATURE_POWER_PASS_TO_USER;
                keyFeatures &= ~LayoutParams.KEY_FEATURE_HOME_PASS_TO_USER;
            }
            if (keyFeatures != attrs.keyFeatures) {
                attrs.keyFeatures = keyFeatures;
                activity.getWindow().setAttributes(attrs);
            }
        }*/
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        LogHelper.d(TAG, "%s -> index: %d.", Utils.resourceNameOf(mContext, preference.getId()), current);
        switch (preference.getId()) {
            case R.id.test_pattern:
                mPictureApi.setVideoTestPattern(current);
                handleTestPatternChanged();
                break;
            case R.id.backlight_mode:
                setBacklightMode(current);
                pic_backlight.setSumary(mPictureApi.getVideoItem(TvPictureManager.PICTURE_BACKLIGHT)+"");
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    private void setBacklightMode(int current){
        switch (current){
            case 0:
                mPictureApi.setVideoItem(TvPictureManager.PICTURE_BACKLIGHT, backlight_default);
                break;
            case 1:
                mPictureApi.setVideoItem(TvPictureManager.PICTURE_BACKLIGHT,0);
                break;
            case 2:
                mPictureApi.setVideoItem(TvPictureManager.PICTURE_BACKLIGHT,50);
                break;
            case 3:
                mPictureApi.setVideoItem(TvPictureManager.PICTURE_BACKLIGHT,100);
                break;
            default:
                break;
        }
    }
}
