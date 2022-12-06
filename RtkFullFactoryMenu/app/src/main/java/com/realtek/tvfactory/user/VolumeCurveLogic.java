package com.realtek.tvfactory.user;

import android.content.Context;
import android.media.tv.TvInputInfo;
import android.os.Handler;
import android.os.Message;
import android.view.View;


import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.api.manager.TvCommonManager;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VolumeCurveLogic extends LogicInterface {

    private static final String[] SOURCE = new String[]{"ATV", "DTV", "AV", "USB", "YPbPr", "VGA", "HDMI"};

    private StatePreference mSource;
    private SeekBarPreference mPreScale;
    private SeekBarPreference mVolume0;
    private SeekBarPreference mVolume10;
    private SeekBarPreference mVolume20;
    private SeekBarPreference mVolume30;
    private SeekBarPreference mVolume40;
    private SeekBarPreference mVolume50;
    private SeekBarPreference mVolume60;
    private SeekBarPreference mVolume70;
    private SeekBarPreference mVolume80;
    private SeekBarPreference mVolume90;
    private SeekBarPreference mVolume100;

    private MyHandler mHandler;

    private FactoryMainApi mFactoryMainApi;

    private static Context mInputContext;

    public VolumeCurveLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mHandler = new MyHandler(this);
        mFactoryMainApi = FactoryMainApi.getInstance();
        mSource = (StatePreference) mContainer.findPreferenceById(R.id.volume_source);
        mPreScale = (SeekBarPreference) mContainer.findPreferenceById(R.id.preScale);
        mVolume0 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_0);
        mVolume10 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_10);
        mVolume20 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_20);
        mVolume30 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_30);
        mVolume40 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_40);
        mVolume50 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_50);
        mVolume60 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_60);
        mVolume70 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_70);
        mVolume80 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_80);
        mVolume90 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_90);
        mVolume100 = (SeekBarPreference) mContainer.findPreferenceById(R.id.volume_100);
        mInputContext = mContext;
        mSource.setVisibility(View.GONE);
        getAndLoadData();
    }

    private void getAndLoadData(){
        List<TvInputInfo> inputs = new ArrayList<TvInputInfo>();
        TvInputInfo current = FactoryApplication.getInstance().getInputList(inputs);
        int count = inputs.size();
        String[] inputNames = new String[count];
        TvInputInfo[] inputValues = new TvInputInfo[count];
        int currentIndex = 0;
        TvInputInfo item;
        for (int i = 0; i < count; i++) {
            item = inputs.get(i);
            inputNames[i] = item.loadLabel(mContext).toString();
            inputValues[i] = item;
            if (Objects.equals(current, item)) {
                currentIndex = i;
            }
        }
        String inputSource = getInputSource(inputValues[currentIndex].loadLabel(mContext).toString());
        mFactoryMainApi.setStringValue(TvCommonManager.USER_VOLUME_SOURCE, inputSource);
        mSource.init(inputNames, inputValues, currentIndex);
        mPreScale.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_PRESCALE));
        mVolume0.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_0));
        mVolume10.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_10));
        mVolume20.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_20));
        mVolume30.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_30));
        mVolume40.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_40));
        mVolume50.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_50));
        mVolume60.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_60));
        mVolume70.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_70));
        mVolume80.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_80));
        mVolume90.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_90));
        mVolume100.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_100));
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        switch (preference.getId()){
        case R.id.preScale:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_PRESCALE, progress);
            break;
        case R.id.volume_0:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_0, progress);
            break;
        case R.id.volume_10:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_10, progress);
            break;
        case R.id.volume_20:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_20, progress);
            break;
        case R.id.volume_30:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_30, progress);
            break;
        case R.id.volume_40:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_40, progress);
            break;
        case R.id.volume_50:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_50, progress);
            break;
        case R.id.volume_60:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_60, progress);
            break;
        case R.id.volume_70:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_70, progress);
            break;
        case R.id.volume_80:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_80, progress);
            break;
        case R.id.volume_90:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_90, progress);
            break;
        case R.id.volume_100:
            mFactoryMainApi.setIntegerValue(TvCommonManager.USER_VOLUME_CURVE_100, progress);
            break;
        default:
            break;
        }
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()){
        case R.id.volume_source:
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 150);
            break;
        default:
            break;
        }
    }

    private String getInputSource(String value) {
        if (value == null || "".equals(value)) {
            return null;
        }
        for (int i = 0; i < SOURCE.length; i++) {
            if (value.equals(SOURCE[i])) {
                return SOURCE[i];
            }
        }
        if (value.equals("YPP") || value.equals("YPBPR")){
            return SOURCE[4];
        } else if (value.equals("SATELLITE") || value.equals("ANTENNA") || value.equals("CABLE") || value.equals("DVBT") || value.equals("DVBT2")
                || value.equals("DVBC") || value.equals("DVBS") || value.equals("DVBS2")) {
            return SOURCE[1];
        } else if (value.contains(SOURCE[6])) {
            return SOURCE[6];
        }
        return null;
    }

    static class MyHandler extends Handler {

        private WeakReference<VolumeCurveLogic> reference;
        private FactoryMainApi mFactoryMainApi;

        public MyHandler(VolumeCurveLogic logic) {
            reference = new WeakReference<VolumeCurveLogic>(logic);
            mFactoryMainApi = FactoryMainApi.getInstance();
        }

        @Override
        public void handleMessage(Message msg) {
            final VolumeCurveLogic logic = reference.get();
            if (logic == null) {
                return;
            }
            switch (msg.what) {
                case 0: {
                    Object entryValue = logic.mSource.getCurrentEntryValue();
                    if (entryValue instanceof TvInputInfo) {
                        TvInputInfo inputSource = (TvInputInfo) entryValue;
                        FactoryApplication.getInstance().setInputSource(inputSource);
                        String input = logic.getInputSource(inputSource.loadLabel(mInputContext).toString());
                        mFactoryMainApi.setStringValue(TvCommonManager.USER_VOLUME_SOURCE,input);
                        logic.mVolume0.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_0));
                        logic.mVolume10.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_10));
                        logic.mVolume20.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_20));
                        logic.mVolume30.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_30));
                        logic.mVolume40.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_40));
                        logic.mVolume50.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_50));
                        logic.mVolume60.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_60));
                        logic.mVolume70.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_70));
                        logic.mVolume80.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_80));
                        logic.mVolume90.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_90));
                        logic.mVolume100.init(mFactoryMainApi.getIntegerValue(TvCommonManager.USER_VOLUME_CURVE_100));
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
}
