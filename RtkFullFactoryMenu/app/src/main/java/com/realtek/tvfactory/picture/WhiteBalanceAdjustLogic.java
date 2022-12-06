package com.realtek.tvfactory.picture;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.PictureApi;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.Preference;
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

public class WhiteBalanceAdjustLogic extends LogicInterface {

    private static final String TAG = "WhiteBalanceAdjustLogic";


    private PictureApi mPictureApi;
    private StatePreference mInputSource;
    private StatePreference mColorTemp;

    private SeekBarPreference mRGain;
    private SeekBarPreference mGGain;
    private SeekBarPreference mBGain;
    private SeekBarPreference mROffset;
    private SeekBarPreference mGOffset;
    private SeekBarPreference mBOffset;

    private MyHandler mHandler;

    private int rGain;

    private int gGain;

    private int bGain;

    private int rOffset;

    private int gOffset;

    private int bOffset;

    private Preference mCopyToSource;

    public WhiteBalanceAdjustLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mHandler = new MyHandler(this);

        mPictureApi = PictureApi.getInstance();
        mInputSource = (StatePreference) mContainer.findPreferenceById(R.id.input_source);
        mColorTemp = (StatePreference) mContainer.findPreferenceById(R.id.color_temp);
        mRGain = (SeekBarPreference) mContainer.findPreferenceById(R.id.r_gain);
        mGGain = (SeekBarPreference) mContainer.findPreferenceById(R.id.g_gain);
        mBGain = (SeekBarPreference) mContainer.findPreferenceById(R.id.b_gain);
        mROffset = (SeekBarPreference) mContainer.findPreferenceById(R.id.r_offset);
        mGOffset = (SeekBarPreference) mContainer.findPreferenceById(R.id.g_offset);
        mBOffset = (SeekBarPreference) mContainer.findPreferenceById(R.id.b_offset);
        mCopyToSource = mContainer.findPreferenceById(R.id.copy_data_to_allsource);
        mInputSource.setVisibility(View.GONE);
        List<TvInputInfo> inputs = new ArrayList<TvInputInfo>();
        TvInputInfo current = FactoryApplication.getInstance().getInputList(inputs);
        int count = inputs.size();
        String[] inputNames = new String[count];
        TvInputInfo[] inputValues = new TvInputInfo[count];
        int currentIndex = 0;
        TvInputInfo item;
        for (int i = 0; i < count; i++) {
            item = inputs.get(i);
            inputNames[i] = item.loadLabel(mContext).toString();;
            inputValues[i] = item;
            if (Objects.equals(current, item)) {
                currentIndex = i;
            }
        }
        mInputSource.init(inputNames, inputValues, currentIndex);
        initColorTempData();

    }
    private void initColorTempData() {
        int colorTempIdx = mPictureApi.getWbColorTempIdx();
        if (colorTempIdx == 0) {
            mColorTemp.init(1);
        } else {
            mColorTemp.init(mPictureApi.getWbColorTempIdx());
        }
        LogHelper.d(TAG, "colorTempIdx: %d.", colorTempIdx);
        rGain = mPictureApi.getWbRedGain();
        gGain = mPictureApi.getWbGreenGain();
        bGain = mPictureApi.getWbBlueGain();
        rOffset = mPictureApi.getWbRedOffset();
        gOffset = mPictureApi.getWbGreenOffset();
        bOffset = mPictureApi.getWbBlueOffset();
        mRGain.init(rGain);
        mGGain.init(gGain);
        mBGain.init(bGain);
        mROffset.init(rOffset);
        mGOffset.init(gOffset);
        mBOffset.init(bOffset);
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
            mHandler.sendEmptyMessageDelayed(0, 250);
            break;
        case R.id.color_temp:
            mPictureApi.setWbColorTempIdx(current);
            initColorTempData();
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
            mPictureApi.setWbRedGain((short) progress);
            break;
        case R.id.g_gain:
            mPictureApi.setWbGreenGain((short) progress);
            break;
        case R.id.b_gain:
            mPictureApi.setWbBlueGain((short) progress);
            break;
        case R.id.r_offset:
            mPictureApi.setWbRedOffset((short) progress);
            break;
        case R.id.g_offset:
            mPictureApi.setWbGreenOffset((short) progress);
            break;
        case R.id.b_offset:
            mPictureApi.setWbBlueOffset((short) progress);
            break;
        default:
            break;
        }
    }
    static class MyHandler extends Handler {

        private WeakReference<WhiteBalanceAdjustLogic> reference;

        public MyHandler(WhiteBalanceAdjustLogic logic) {
            reference = new WeakReference<WhiteBalanceAdjustLogic>(logic);
        }

        @Override
        public void handleMessage(Message msg) {
            final WhiteBalanceAdjustLogic logic = reference.get();
            if (logic == null) {
                return;
            }
            switch (msg.what) {
            case 0: {
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

    public void changeInputSource(TvInputInfo inputSource) {
        if (inputSource == null) {
            return;
        }
        FactoryApplication.getInstance().setInputSource(inputSource);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initColorTempData();
            }
        },1000);
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
