package com.realtek.fullfactorymenu.picture;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;


import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;

import com.realtek.fullfactorymenu.api.impl.PictureApi;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.utils.LogHelper;
import com.realtek.fullfactorymenu.utils.Predicate;
import com.realtek.fullfactorymenu.utils.Utils;

import android.media.tv.TvInputInfo;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class OverScanLogic extends LogicInterface {

    private static final String TAG = "OverScanLogic";


    private PictureApi mPictureApi;
    private StatePreference mInputSource;
    private SeekBarPreference mHPosition;
    private SeekBarPreference mHSize;
    private SeekBarPreference mVPosition;
    private SeekBarPreference mVSize;

    private MyHandler mHandler;

    public OverScanLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mHandler = new MyHandler(this);
        mPictureApi = PictureApi.getInstance();
        mInputSource = (StatePreference) mContainer.findPreferenceById(R.id.input_source);
        mHPosition = (SeekBarPreference) mContainer.findPreferenceById(R.id.h_position);
        mHSize = (SeekBarPreference) mContainer.findPreferenceById(R.id.h_size);
        mVPosition = (SeekBarPreference) mContainer.findPreferenceById(R.id.v_position);
        mVSize = (SeekBarPreference) mContainer.findPreferenceById(R.id.v_size);

        mInputSource.setVisibility(View.GONE);

        ArrayList<TvInputInfo> inputs = new ArrayList<TvInputInfo>();
        TvInputInfo current = FactoryApplication.getInstance().getInputList(inputs);
        int count = inputs.size();
        String[] inputNames = new String[count];
        boolean[] inputStates = new boolean[count];
        TvInputInfo[] inputValues = new TvInputInfo[count];
        int currentIndex = 0;
        TvInputInfo item;
        for (int i = 0; i < count; i++) {
            item = inputs.get(i);
            inputNames[i] = item.loadLabel(mContext).toString();
            inputStates[i] = !FactoryApplication.INPUT_ID_VGA.equals(item.getId());
            inputValues[i] = item;
            if (Objects.equals(current, item)) {
                currentIndex = i;
            }
        }

        if (FactoryApplication.INPUT_ID_VGA.equals(current.getId())) {
            TvInputInfo source = Utils.next(inputs, current, true, new Predicate<TvInputInfo>() {

                @Override
                public boolean apply(TvInputInfo item) {
                    return !FactoryApplication.INPUT_ID_VGA.equals(item.getId());
                }
            });
            if (source != null) {
                currentIndex = inputs.indexOf(source);
                FactoryApplication.getInstance().setInputSource(source);
            }
        }

        mInputSource.init(inputNames, inputValues, currentIndex);

        mHPosition.init(mPictureApi.getOverScanHPosition());
        mHSize.init(mPictureApi.getOverScanHSize());
        mVPosition.init(mPictureApi.getOverScanVPosition());
        mVSize.init(mPictureApi.getOverScanVSize());
    }

    @Override
    public void deinit() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()) {
        case R.id.input_source:
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 150);
            break;
        default:
            break;
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        LogHelper.d(TAG, "%s -> progress: %d.", Utils.resourceNameOf(mContext, preference.getId()), progress);
        switch (preference.getId()) {
        case R.id.h_position:
            mPictureApi.setOverScanHPosition((short) progress);
            break;
        case R.id.h_size:
            mPictureApi.setOverScanHSize((short) progress);
            break;
        case R.id.v_position:
            mPictureApi.setOverScanVPosition((short) progress);
            break;
        case R.id.v_size:
            mPictureApi.setOverScanVSize((short) progress);
            break;
        default:
            break;
        }
    }
    static class MyHandler extends Handler {

        private WeakReference<OverScanLogic> reference;

        public MyHandler(OverScanLogic logic) {
            reference = new WeakReference<OverScanLogic>(logic);
        }

        @Override
        public void handleMessage(Message msg) {
            final OverScanLogic logic = reference.get();
            if (logic == null) {
                return;
            }
            switch (msg.what) {
            case 0: {
                Object entryValue = logic.mInputSource.getCurrentEntryValue();
                if (entryValue instanceof TvInputInfo) {
                    TvInputInfo inputSource = (TvInputInfo) entryValue;
                    FactoryApplication.getInstance().setInputSource(inputSource);

                    logic.mHPosition.init(logic.mPictureApi.getOverScanHPosition());
                    logic.mHSize.init(logic.mPictureApi.getOverScanHSize());
                    logic.mVPosition.init(logic.mPictureApi.getOverScanVPosition());
                    logic.mVSize.init(logic.mPictureApi.getOverScanVSize());
                }
                break;
            }
            default:
                break;
            }
        }
    }

}
