package com.realtek.tvfactory.picture;

import android.content.res.XmlResourceParser;
import android.media.tv.TvInputInfo;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.PictureApi;
import com.realtek.tvfactory.api.manager.TvPictureManager;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.utils.LogHelper;
import com.realtek.tvfactory.utils.TvInputUtils;
import com.realtek.tvfactory.utils.TvUtils;
import com.realtek.tvfactory.utils.Utils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class PictureModeLogic extends LogicInterface {

    private static final String TAG = "PictureModeLogic";

    private PictureApi mPictureApi;
    private StatePreference mInputSource;
    private StatePreference mPictureMode;

    private SeekBarPreference mBrightness;
    private SeekBarPreference mContrast;
    private SeekBarPreference mColor;
    private SeekBarPreference mSharpness;
    private SeekBarPreference mHue;
    private SeekBarPreference mBackLight;

    private String[] mColorFormatArray;

    private MyHandler mHandler;

    public PictureModeLogic(PreferenceContainer container) {
        super(container);
    }

    private boolean isHueEnabled() {
        int inputSource = FactoryApplication.getInstance().getInputSource(TvInputUtils.getCurrentInput(mContext));
        if (TvUtils.isAtv(inputSource) || TvUtils.isAv(inputSource)) {
            int colorSystem = FactoryApplication.getInstance().getAtvVideoStandard();
            return isNtscColorSystem(colorSystem);
        }
        return false;
    }

    private boolean isNtscColorSystem(int colorFormat) {
        String ntsc = mContext.getString(R.string.colorSystem_ntsc);
        return Objects.equals(ntsc, colorSystemAsString(colorFormat));
    }

    private String colorSystemAsString(int colorFormat) {
        if (mColorFormatArray == null) {
            XmlResourceParser parser = mContext.getResources().getXml(R.xml.color_system);
            try {
                mColorFormatArray = Utils.readStringArray(mContext, parser, "color_system");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Utils.valueInArray(mColorFormatArray, colorFormat, null);
    }

    @Override
    public void init() {
        mHandler = new MyHandler(this);

        mPictureApi = PictureApi.getInstance();
        mInputSource = (StatePreference) mContainer.findPreferenceById(R.id.input_source);
        mPictureMode = (StatePreference) mContainer.findPreferenceById(R.id.picture_mode);
        mBrightness = (SeekBarPreference) mContainer.findPreferenceById(R.id.pm_brightness);
        mContrast = (SeekBarPreference) mContainer.findPreferenceById(R.id.pm_contrast);
        mColor = (SeekBarPreference) mContainer.findPreferenceById(R.id.pm_color);
        mSharpness = (SeekBarPreference) mContainer.findPreferenceById(R.id.pm_sharpness);
        mHue = (SeekBarPreference) mContainer.findPreferenceById(R.id.pm_hue);
        mBackLight = (SeekBarPreference) mContainer.findPreferenceById(R.id.pm_backlight);

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
        mInputSource.setVisibility(View.GONE);

        mPictureMode.init(mPictureApi.getPictureMode());
        mBrightness.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_BRIGHTNESS));
        mContrast.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_CONTRAST));
        mColor.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_SATURATION));
        mSharpness.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_SHARPNESS));
        mHue.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_HUE));
        mBackLight.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_BACKLIGHT));

        mHue.setEnabled(isHueEnabled());
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
        case R.id.picture_mode:
            mPictureApi.setPictureMode(current);

            mBrightness.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_BRIGHTNESS));
            mContrast.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_CONTRAST));
            mColor.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_SATURATION));
            mSharpness.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_SHARPNESS));
            mHue.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_HUE));
            mBackLight.init(mPictureApi.getVideoItem(TvPictureManager.PICTURE_BACKLIGHT));
            break;
        default:
            break;
        }
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        LogHelper.d(TAG, "%s -> progress: %d.", Utils.resourceNameOf(mContext, preference.getId()), progress);
        switch (preference.getId()) {
        case R.id.pm_brightness:
            mPictureApi.setVideoItem(TvPictureManager.PICTURE_BRIGHTNESS, progress);
            break;
        case R.id.pm_contrast:
            mPictureApi.setVideoItem(TvPictureManager.PICTURE_CONTRAST, progress);
            break;
        case R.id.pm_color:
            mPictureApi.setVideoItem(TvPictureManager.PICTURE_SATURATION, progress);
            break;
        case R.id.pm_sharpness:
            mPictureApi.setVideoItem(TvPictureManager.PICTURE_SHARPNESS, progress);
            break;
        case R.id.pm_hue:
            mPictureApi.setVideoItem(TvPictureManager.PICTURE_HUE, progress);
            break;
        case R.id.pm_backlight:
            mPictureApi.setVideoItem(TvPictureManager.PICTURE_BACKLIGHT, progress);
            break;
        default:
            break;
        }
    }

    static class MyHandler extends Handler {

        private WeakReference<PictureModeLogic> reference;

        public MyHandler(PictureModeLogic logic) {
            reference = new WeakReference<PictureModeLogic>(logic);
        }

        @Override
        public void handleMessage(Message msg) {
            final PictureModeLogic logic = reference.get();
            if (logic == null) {
                return;
            }
            switch (msg.what) {
            case 0: {
                Object entryValue = logic.mInputSource.getCurrentEntryValue();
                if (entryValue instanceof TvInputInfo) {
                    TvInputInfo inputSource = (TvInputInfo) entryValue;
                    FactoryApplication.getInstance().setInputSource(inputSource);

                    logic.mPictureMode.init(logic.mPictureApi.getPictureMode());
                    logic.mBrightness.init(logic.mPictureApi.getVideoItem(TvPictureManager.PICTURE_BRIGHTNESS));
                    logic.mContrast.init(logic.mPictureApi.getVideoItem(TvPictureManager.PICTURE_CONTRAST));
                    logic.mColor.init(logic.mPictureApi.getVideoItem(TvPictureManager.PICTURE_SATURATION));
                    logic.mSharpness.init(logic.mPictureApi.getVideoItem(TvPictureManager.PICTURE_SHARPNESS));
                    logic.mHue.init(logic.mPictureApi.getVideoItem(TvPictureManager.PICTURE_HUE));
                    logic.mBackLight.init(logic.mPictureApi.getVideoItem(TvPictureManager.PICTURE_BACKLIGHT));

                    logic.mHue.setEnabled(logic.isHueEnabled());
                }
                break;
            }
            default:
                break;
            }
        }
    }

}
