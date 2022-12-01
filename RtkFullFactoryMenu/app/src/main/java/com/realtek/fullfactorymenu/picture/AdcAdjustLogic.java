package com.realtek.fullfactorymenu.picture;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.PictureApi;
import com.realtek.fullfactorymenu.api.listener.IActionCallback;
import com.realtek.fullfactorymenu.api.manager.TvCommonManager;
import com.realtek.fullfactorymenu.api.manager.TvFactoryManager;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.utils.AppToast;
import com.realtek.fullfactorymenu.utils.LogHelper;
import com.realtek.fullfactorymenu.utils.Utils;

import android.app.Activity;
import android.media.tv.TvInputInfo;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class AdcAdjustLogic extends LogicInterface {

    private static final String TAG = "AdcAdjustLogic";

    private static final SparseIntArray sAdcSource = new SparseIntArray();

    static {
        sAdcSource.put(TvFactoryManager.ADC_SET_VGA, TvCommonManager.INPUT_SOURCE_VGA);
        sAdcSource.put(TvFactoryManager.ADC_SET_YPBPR_HD, TvCommonManager.INPUT_SOURCE_YPBPR);
    }


    private PictureApi mPictureApi;
    private StatePreference mInputSource;

    private SeekBarPreference mRGain;
    private SeekBarPreference mGGain;
    private SeekBarPreference mBGain;
    private SeekBarPreference mROffset;
    private SeekBarPreference mGOffset;
    private SeekBarPreference mBOffset;
    private SeekBarPreference mPhase;

    private MyHandler mHandler;

    public AdcAdjustLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mHandler = new MyHandler(this);

        mPictureApi = PictureApi.getInstance();
        mInputSource = (StatePreference) mContainer.findPreferenceById(R.id.input_source);
        mRGain = (SeekBarPreference) mContainer.findPreferenceById(R.id.r_gain);
        mGGain = (SeekBarPreference) mContainer.findPreferenceById(R.id.g_gain);
        mBGain = (SeekBarPreference) mContainer.findPreferenceById(R.id.b_gain);
        mROffset = (SeekBarPreference) mContainer.findPreferenceById(R.id.r_offset);
        mGOffset = (SeekBarPreference) mContainer.findPreferenceById(R.id.g_offset);
        mBOffset = (SeekBarPreference) mContainer.findPreferenceById(R.id.b_offset);
        mPhase = (SeekBarPreference) mContainer.findPreferenceById(R.id.phase);

        mInputSource.setVisibility(View.GONE);
        List<TvInputInfo> inputs = new ArrayList<TvInputInfo>();
        FactoryApplication.getInstance().getInputList(inputs);
        SparseArray<TvInputInfo> inputsMap = new SparseArray<TvInputInfo>();
        int count = inputs.size();
        TvInputInfo item ;
        for (int i = 0; i < count; i++) {
            item = inputs.get(i);
            String inputId = item.getId();
            int inputSource = FactoryApplication.getInstance().getInputSource(inputId);
            if (indexOfValue(sAdcSource, inputSource) >= 0) {
                inputsMap.put(inputSource , item);
            }
        }

        TvInputInfo[] inputValues = new TvInputInfo[8];
        boolean[] state = new boolean[8];
        int sourceCount = sAdcSource.size();
        int adcIndex = mPictureApi.getFactoryAdcIndex();
        TvInputInfo targetSource = null;

        for (int i = 0; i < sourceCount; i++) {
            int inputId = sAdcSource.valueAt(i);
            if (inputsMap.indexOfKey(inputId) >= 0) {
                inputValues[sAdcSource.keyAt(i)] = inputsMap.get(inputId);
                state[sAdcSource.keyAt(i)] = true;
            } else {
                inputValues[sAdcSource.keyAt(i)] = null;
                state[sAdcSource.keyAt(i)] = false;
            }
            if (adcIndex == sAdcSource.valueAt(i)) {
                targetSource = inputValues[sAdcSource.keyAt(i)];
            }
        }
        int index = Utils.indexOf(inputValues, targetSource);
        mInputSource.init(mInputSource.getEntryNames(), inputValues, state, index);

//        mPhase.setVisibility(TvInputUtils.isVga(current.getId()) ? View.VISIBLE :View.GONE);
        mPhase.setVisibility(View.GONE);
        initData();
    }

    public void initData() {
        mRGain.init(mPictureApi.getAdcRedGain());
        mRGain.setEnabled(false);
        mGGain.init(mPictureApi.getAdcGreenGain());
        mGGain.setEnabled(false);
        mBGain.init(mPictureApi.getAdcBlueGain());
        mBGain.setEnabled(false);
        mROffset.init(mPictureApi.getAdcRedOffset());
        mROffset.setEnabled(false);
        mGOffset.init(mPictureApi.getAdcGreenOffset());
        mGOffset.setEnabled(false);
        mBOffset.init(mPictureApi.getAdcBlueOffset());
        mBOffset.setEnabled(false);
        mPhase.init(mPictureApi.getAdcPhase());
    }

    @Override
    public void deinit() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void changeInputSource(TvInputInfo source) {
        if (source == null) {
            return;
        }

        int index = indexOfValue(sAdcSource, FactoryApplication.getInstance().getInputSource(source.getId()));
        mPictureApi.setFactoryAdcIndex(sAdcSource.keyAt(index));
//        mPhase.setVisibility(TvInputUtils.isVga(source.getId()) ? View.VISIBLE :View.GONE);
        mPhase.setVisibility(View.GONE);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                initData();
            }
        }, 200);
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        LogHelper.d(TAG, "%s -> previous: %d, current: %d.", Utils.resourceNameOf(mContext, preference.getId()), previous, current);
        switch (preference.getId()) {
        case R.id.input_source:
            mHandler.removeMessages(MSG_CHANGE_INPUT_SOURCE);
            mHandler.sendEmptyMessageDelayed(MSG_CHANGE_INPUT_SOURCE, 250);
            break;
        default:
            break;
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        LogHelper.d(TAG, "%s -> progress: %d.", Utils.resourceNameOf(mContext, preference.getId()), progress);
        switch (preference.getId()) {
        case R.id.r_gain:
            mPictureApi.setAdcRedGain(progress);
            break;
        case R.id.g_gain:
            mPictureApi.setAdcGreenGain(progress);
            break;
        case R.id.b_gain:
            mPictureApi.setAdcBlueGain(progress);
            break;
        case R.id.r_offset:
            mPictureApi.setAdcRedOffset(progress);
            break;
        case R.id.g_offset:
            mPictureApi.setAdcGreenOffset(progress);
            break;
        case R.id.b_offset:
            mPictureApi.setAdcBlueOffset(progress);
            break;
        case R.id.phase:
            mPictureApi.setAdcPhase(progress);
            break;
        default:
            break;
        }
    }

    private static int indexOfValue(SparseIntArray array, int value) {
        int size = array == null ? 0 : array.size();
        for (int i = 0; i < size; i++) {
            if (array.valueAt(i) == value) {
                return i;
            }
        }
        return -1;
    }

    public void exceAutoAdc() {
        mPictureApi.execAutoAdc(mActionCallback);
    }

    private static final int MSG_CHANGE_INPUT_SOURCE = 0;

    static class MyHandler extends Handler {

        private WeakReference<AdcAdjustLogic> reference;

        public MyHandler(AdcAdjustLogic logic) {
            reference = new WeakReference<AdcAdjustLogic>(logic);
        }

        @Override
        public void handleMessage(Message msg) {
            final AdcAdjustLogic logic = reference.get();
            if (logic == null) {
                return;
            }
            switch (msg.what) {
            case MSG_CHANGE_INPUT_SOURCE: {
                Object entryValue = logic.mInputSource.getCurrentEntryValue();
                if (entryValue instanceof TvInputInfo) {
                    TvInputInfo inputSource = (TvInputInfo) entryValue;

                    logic.changeInputSource(inputSource);
                }
                break;
            }
            default:
                break;
            }
        }
    }

    private final IActionCallback.Stub mActionCallback = new IActionCallback.Stub() {

        @Override
        public void onCompleted(int result) throws RemoteException {
            switch (result) {
            case TvFactoryManager.ADC_AUTO_TUNE_RESULT_SUCCESS:
                ((Activity)mContext).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        AppToast.showToast(mContext, R.string.str_success, Toast.LENGTH_SHORT);
                        initData();
                    }
                });
                break;
            default:
                ((Activity)mContext).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        AppToast.showToast(mContext, R.string.str_fail, Toast.LENGTH_SHORT);
                    }
                });
                break;
            }
        }
    };

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
