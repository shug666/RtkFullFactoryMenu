package com.realtek.fullfactorymenu.picture;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.realtek.system.RtkProjectConfigs;
import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.FactoryMainApi;
import com.realtek.fullfactorymenu.api.impl.PictureApi;
import com.realtek.fullfactorymenu.api.manager.TvCommonManager;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.preference.SumaryPreference;
import com.realtek.fullfactorymenu.utils.LogHelper;
import com.realtek.fullfactorymenu.utils.Predicate;
import com.realtek.fullfactorymenu.utils.TvInputUtils;
import com.realtek.fullfactorymenu.utils.Utils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class PicturePageLogic extends LogicInterface {

    private static final String TAG = "PicturePageLogic";

    private PictureApi mPictureApi;
    private FactoryMainApi mFactoryMainApi;
    private StatePreference mTestPattern;
    private Preference mOverScan;
    private int mCurrentInputSource;
    private boolean mIsSignalStable;
    private SumaryPreference md5PQ;
    private SumaryPreference md5PQHDR;
    private SumaryPreference md5PQOSD;
    private SumaryPreference md5DV;

    public PicturePageLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {

        mPictureApi = PictureApi.getInstance();
        mFactoryMainApi = FactoryMainApi.getInstance();
        mTestPattern = (StatePreference) mContainer.findPreferenceById(R.id.test_pattern);
        if (FactoryApplication.CUSTOMER_IS_KONKA) {
            mTestPattern.setVisibility(View.GONE);
        }
        mOverScan = (Preference) mContainer.findPreferenceById(R.id.page_over_scan);
        mCurrentInputSource = FactoryApplication.getInstance().getInputSource(TvInputUtils.getCurrentInput(mContext));
        mIsSignalStable = FactoryApplication.getInstance().isSignalStable();
        if (mCurrentInputSource == TvCommonManager.INPUT_SOURCE_DVI ||
                mCurrentInputSource == TvCommonManager.INPUT_SOURCE_VGA ||
                !mIsSignalStable) {
            mOverScan.setEnabled(false);
        }
        md5PQ = (SumaryPreference) mContainer.findPreferenceById(R.id.md5_pq);
        md5PQHDR = (SumaryPreference) mContainer.findPreferenceById(R.id.md5_pq_hdr);
        md5PQOSD = (SumaryPreference) mContainer.findPreferenceById(R.id.md5_pq_osd);
        md5DV = (SumaryPreference) mContainer.findPreferenceById(R.id.md5_dv);
        initMD5();
    }

    private void initMD5() {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (messageDigest == null) {
            return;
        }
        RtkProjectConfigs instance = RtkProjectConfigs.getInstance();
        String pq = instance.getConfig("[MISC_PQ_MAP_CFG]", "PQ");
        String pq_hdr = instance.getConfig("[MISC_PQ_MAP_CFG]", "PQ_HDR");
        String pq_osd = instance.getConfig("[MISC_PQ_MAP_CFG]", "PQ_OSD");
        String dv = instance.getConfig("[MISC_PQ_MAP_CFG]", "DV");
        StringBuilder builder = new StringBuilder();
        md5PQ.setSumary(byteToString(builder, getMd5(messageDigest, pq)));
        md5PQHDR.setSumary(byteToString(builder, getMd5(messageDigest, pq_hdr)));
        md5PQOSD.setSumary(byteToString(builder, getMd5(messageDigest, pq_osd)));
        md5DV.setSumary(byteToString(builder, getMd5(messageDigest, dv)));
    }

    private String byteToString(StringBuilder builder, byte[] data) {
        if (data == null) {
            return "File not exist or can not read.";
        }
        builder.setLength(0);
        for (byte aByte : data) {
            builder.append(String.format("%02X", aByte));
        }
        return builder.toString();
    }

    private byte[] getMd5(MessageDigest md5, String path) {
        File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            return null;
        }
        byte[] bytesFromKey = null;
        try {
            bytesFromKey = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bytesFromKey == null || bytesFromKey.length == 0) {
            return null;
        }
        md5.update(bytesFromKey);
        return md5.digest();
    }

    @Override
    public void deinit() {
        if (mTestPattern.getVisibility() == View.VISIBLE) {
            Log.d(TAG, String.format("name:%s value:%s", mTestPattern.getCurrentEntryName(), mTestPattern.getCurrentEntryValue()));
            if (!mContext.getResources().getString(R.string.str_off).equals(mTestPattern.getCurrentEntryName())) {
                mPictureApi.setVideoTestPattern(0);
            }
        }
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
        default:
            break;
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

}
