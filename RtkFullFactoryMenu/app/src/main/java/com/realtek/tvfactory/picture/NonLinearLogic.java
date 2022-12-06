package com.realtek.tvfactory.picture;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;


import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.PictureApi;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.utils.AppToast;
import com.realtek.tvfactory.utils.LogHelper;
import com.realtek.tvfactory.utils.Utils;

import android.media.tv.TvInputInfo;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class NonLinearLogic extends LogicInterface {

    private static final String TAG = "NonLinearLogic";

    private PictureApi mPictureApi;
    private StatePreference mInputSource;
    private StatePreference mCurveType;

    private SeekBarPreference mOsd0;
    private SeekBarPreference mOsd25;
    private SeekBarPreference mOsd50;
    private SeekBarPreference mOsd75;
    private SeekBarPreference mOsd100;

    private MyHandler mHandler;

    public NonLinearLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mHandler = new MyHandler(this);
        mPictureApi = PictureApi.getInstance();
        mInputSource = (StatePreference) mContainer.findPreferenceById(R.id.input_source);
        mCurveType = (StatePreference) mContainer.findPreferenceById(R.id.curve_type);
        mOsd0 = (SeekBarPreference) mContainer.findPreferenceById(R.id.osd_0);
        mOsd25 = (SeekBarPreference) mContainer.findPreferenceById(R.id.osd_25);
        mOsd50 = (SeekBarPreference) mContainer.findPreferenceById(R.id.osd_50);
        mOsd75 = (SeekBarPreference) mContainer.findPreferenceById(R.id.osd_75);
        mOsd100 = (SeekBarPreference) mContainer.findPreferenceById(R.id.osd_100);

        mInputSource.setVisibility(View.GONE);
        ArrayList<TvInputInfo> inputs = new ArrayList<TvInputInfo>();
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
        mInputSource.init(inputNames, inputValues, currentIndex);


        mCurveType.init(mPictureApi.getNlaCurveType());
        mOsd0.init(mPictureApi.getOsdV0Nonlinear());
        mOsd25.init(mPictureApi.getOsdV25Nonlinear());
        mOsd50.init(mPictureApi.getOsdV50Nonlinear());
        mOsd75.init(mPictureApi.getOsdV75Nonlinear());
        mOsd100.init(mPictureApi.getOsdV100Nonlinear());
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
        case R.id.curve_type:
            mPictureApi.setNlaCurveType(current);

            mOsd0.init(mPictureApi.getOsdV0Nonlinear());
            mOsd25.init(mPictureApi.getOsdV25Nonlinear());
            mOsd50.init(mPictureApi.getOsdV50Nonlinear());
            mOsd75.init(mPictureApi.getOsdV75Nonlinear());
            mOsd100.init(mPictureApi.getOsdV100Nonlinear());
            break;
        default:
            break;
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        LogHelper.d(TAG, "%s -> progress: %d.", Utils.resourceNameOf(mContext, preference.getId()), progress);
        switch (preference.getId()) {
        case R.id.osd_0:
            mPictureApi.setOsdV0Nonlinear((short) progress);
            break;
        case R.id.osd_25:
            mPictureApi.setOsdV25Nonlinear((short) progress);
            break;
        case R.id.osd_50:
            mPictureApi.setOsdV50Nonlinear((short) progress);
            break;
        case R.id.osd_75:
            mPictureApi.setOsdV75Nonlinear((short) progress);
            break;
        case R.id.osd_100:
            mPictureApi.setOsdV100Nonlinear((short) progress);
            break;
        default:
            break;
        }
        notifyProgress(preference,progress);
    }

    private void notifyProgress(SeekBarPreference preference, int progress) {
        switch (preference.getId()) {
        case R.id.osd_0:
            if (progress > mOsd25.getProgress()) {
                mOsd25.setProgress(progress);
                return;
            }
            break;
        case R.id.osd_25:
            if (progress < mOsd0.getProgress()) {
                mOsd0.setProgress(progress);
                return;
            }
            if (progress > mOsd50.getProgress()) {
                mOsd50.setProgress(progress);
                return;
            }
            break;
        case R.id.osd_50:
            if (progress < mOsd25.getProgress()) {
                mOsd25.setProgress(progress);
                return;
            }
            if (progress > mOsd75.getProgress()) {
                mOsd75.setProgress(progress);
                return;
            }
            break;
        case R.id.osd_75:
            if (progress < mOsd50.getProgress()) {
                mOsd50.setProgress(progress);
                return;
            }
            if (progress > mOsd100.getProgress()) {
                mOsd100.setProgress(progress);
                return;
            }
            break;
        case R.id.osd_100:
            if (progress < mOsd75.getProgress()) {
                mOsd75.setProgress(progress);
                return;
            }
            break;
        default:
            break;
        }
    }
    static class MyHandler extends Handler {

        private WeakReference<NonLinearLogic> reference;

        public MyHandler(NonLinearLogic logic) {
            reference = new WeakReference<NonLinearLogic>(logic);
        }

        @Override
        public void handleMessage(Message msg) {
            final NonLinearLogic logic = reference.get();
            if (logic == null) {
                return;
            }
            switch (msg.what) {
            case 0: {
                Object entryValue = logic.mInputSource.getCurrentEntryValue();
                if (entryValue instanceof TvInputInfo) {
                    TvInputInfo inputSource = (TvInputInfo) entryValue;
                    FactoryApplication.getInstance().setInputSource(inputSource);

                    logic.mCurveType.init(logic.mPictureApi.getNlaCurveType());
                    logic.mOsd0.init(logic.mPictureApi.getOsdV0Nonlinear());
                    logic.mOsd25.init(logic.mPictureApi.getOsdV25Nonlinear());
                    logic.mOsd50.init(logic.mPictureApi.getOsdV50Nonlinear());
                    logic.mOsd75.init(logic.mPictureApi.getOsdV75Nonlinear());
                    logic.mOsd100.init(logic.mPictureApi.getOsdV100Nonlinear());
                }
                break;
            }
            default:
                break;
            }
        }
    }

    public void catchInputNumber(SeekBarPreference seekBarPreference, int keyCode) {
        int number = keyCode - KeyEvent.KEYCODE_0;
        int progress = 0;
        if (seekBarPreference.hasTemporaryProgress()) {
            int temporaryProgress = seekBarPreference.getTemporaryProgress();
            progress = (temporaryProgress * 10) + number;
        } else {
            progress = number;
        }
        int minValue = seekBarPreference.getMinValue();
        int maxValue = seekBarPreference.getMaxValue();
        if (progress < minValue || progress > maxValue) {
            progress = number;
        }
        seekBarPreference.setProgressTemporarily(progress);
    }

    public boolean checkApplyProgress(SeekBarPreference seekBarPreference) {
        boolean hasTemporaryProgress = seekBarPreference.hasTemporaryProgress();
        if (hasTemporaryProgress) {
            int temporaryProgress = seekBarPreference.getTemporaryProgress();
            int minValue = seekBarPreference.getMinValue();
            int maxValue = seekBarPreference.getMaxValue();
            if (temporaryProgress >= minValue && temporaryProgress <= maxValue) {
                seekBarPreference.applyTemporaryProgress();
            } else {
                seekBarPreference.cancelTemporaryProgress();
                AppToast.showToast(mContext, R.string.str_fail, Toast.LENGTH_SHORT);
            }
            return true;
        }
        return false;
    }

}
