package com.realtek.fullfactorymenu.sound;

import android.util.Log;

import java.util.Locale;

import com.realtek.tv.AQ;
import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.FactoryMainApi;
import com.realtek.fullfactorymenu.api.impl.SoundApi;
import com.realtek.fullfactorymenu.api.manager.TvCommonManager;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.SeekBarPreference.DisplayHelper;
import com.realtek.fullfactorymenu.preference.StatePreference;


/**
 * Created by Administrator on 2019/1/22.
 */

public class SoundAdjustLogic extends LogicInterface {

    private final String TAG = "SoundAdjustLogic";
    private SeekBarPreference mAudioOutVolume = null;
    private StatePreference mAvcEnable = null;
    private SeekBarPreference mAvcThl = null;
    private SeekBarPreference mAvcThlMax = null;
    private StatePreference mDrcEnable = null;
    private SeekBarPreference mDrcThl = null;
    private SeekBarPreference mDrcThlMax = null;
    private SeekBarPreference mDigitalAudio = null;
    private StatePreference mEffect = null;
    private SeekBarPreference mAudioDelay = null;
    private StatePreference mAudioNR = null;
    private StatePreference mSurround = null;
    private SoundApi mSoundApi;
    private FactoryMainApi mFactoryMainApi;
    private static final String mGetSoundAdjustInfo = TvCommonManager.COMMAND_GET_SOUND_ADJUST_INFO;

    public static final int TYPE_VALUE_PCM = 1;

    private AQ mAq = null;
    private StatePreference audioOutChannel;

    private AQ getAq() {
        if (mAq == null) {
            mAq = new AQ();
        }
        return mAq;
    }

    public SoundAdjustLogic(PreferenceContainer container) {
        super(container);
        mSoundApi = SoundApi.getInstance();
        mFactoryMainApi = FactoryMainApi.getInstance();
    }

    @Override
    public void init() {
        mAudioOutVolume = (SeekBarPreference)mContainer.findPreferenceById(R.id.audio_out_volume);
        mAvcEnable = (StatePreference)mContainer.findPreferenceById(R.id.avc_enable);
        mAvcThl = (SeekBarPreference)mContainer.findPreferenceById(R.id.avc_thl);
        mAvcThlMax = (SeekBarPreference)mContainer.findPreferenceById(R.id.avc_thl_max);
        mDrcEnable = (StatePreference)mContainer.findPreferenceById(R.id.drc_enable);
        mDrcThl = (SeekBarPreference)mContainer.findPreferenceById(R.id.drc_thl);
        mDrcThlMax = (SeekBarPreference)mContainer.findPreferenceById(R.id.drc_thl_max);
        mDigitalAudio = (SeekBarPreference)mContainer.findPreferenceById(R.id.digital_audio);
        mEffect = (StatePreference)mContainer.findPreferenceById(R.id.effect);
        mAudioDelay = (SeekBarPreference)mContainer.findPreferenceById(R.id.audio_delay);
        mAudioNR = (StatePreference)mContainer.findPreferenceById(R.id.audio_nr);
        mSurround = (StatePreference)mContainer.findPreferenceById(R.id.surround);
        audioOutChannel = (StatePreference) mContainer.findPreferenceById(R.id.audio_out_channel);
        int speaker = getAq().getSpeakerOutput();
        if (getSpeakerOutput(speaker) > 0){
            audioOutChannel.init(speaker);
        }

        int[] soundAdjustInfo = mFactoryMainApi.setTvosCommonCommand(mGetSoundAdjustInfo);

        mAudioOutVolume.init(soundAdjustInfo[0]);
        mAvcEnable.init(soundAdjustInfo[1]);
        mAvcThl.init(soundAdjustInfo[2]);
        mAvcThlMax.init(soundAdjustInfo[3]);
        mDrcEnable.init(soundAdjustInfo[4]);
        mDrcThl.init(soundAdjustInfo[5]);
        mDrcThlMax.init(soundAdjustInfo[6]);
        mEffect.init(soundAdjustInfo[7]);
        mAudioDelay.init(soundAdjustInfo[8]);
        mAudioNR.init(soundAdjustInfo[9]);
        mDigitalAudio.init(soundAdjustInfo[10]);
        mSurround.init(mSoundApi.getAudioSurroundMode());
        int spdifValue = FactoryApplication.getInstance().getAq().getSPDIFOutput();
        Log.d(TAG, "init: spdifvalue" + spdifValue);
        if (spdifValue == TYPE_VALUE_PCM) {
            mDigitalAudio.setEnabled(true);
        } else {
            mDigitalAudio.setEnabled(false);
        }
    }

    private int getSpeakerOutput(int speaker) {
        if (speaker == 0){
            return 0;
        }
        if (speaker == 1){
            return 1;
        }
        return -1;
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        switch (preference.getId()){
            case R.id.audio_out_volume:
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_AUDIO_OUT_VOLUME, progress);
                break;
            case R.id.avc_thl:
                if (progress >= mAvcThlMax.getProgress()){
                    progress = mAvcThlMax.getProgress() - 1;
                    mAvcThl.setProgress(progress);
                    return;
                }
                if (progress == 0) {
                    mAvcThl.setProgress(1);
                    return;
                }
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_AVC_THL, progress);
                break;
            case R.id.avc_thl_max:
                if (progress <= mAvcThl.getProgress()){
                    progress = mAvcThl.getProgress() + 1;
                    mAvcThlMax.setProgress(progress);
                    return;
                }
                if (progress == 0) {
                    mAvcThlMax.setProgress(1);
                    return;
                }
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_AVC_THL_MAX, progress);
                break;
            case R.id.drc_thl:
                if (progress >= mDrcThlMax.getProgress()){
                    progress = mDrcThlMax.getProgress() - 1;
                    mDrcThl.setProgress(progress);
                    return;
                }
                if (progress == 0) {
                    mDrcThl.setProgress(1);
                    return;
                }
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_DRC_THL, progress);
                break;
            case R.id.drc_thl_max:
                if (progress <= mDrcThl.getProgress()){
                    progress = mDrcThl.getProgress() + 1;
                    mDrcThlMax.setProgress(progress);
                    return;
                }
                if (progress == 0) {
                    mDrcThlMax.setProgress(1);
                    return;
                }
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_DRC_THL_MAX, progress);
                break;
            case R.id.digital_audio:
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_DIGITALAUDIO_OUT_VOLUME, progress);
                break;
            case R.id.audio_delay:
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_AUDIO_DELAY, progress);
                break;
        }
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()){
            case R.id.avc_enable:
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_AVC_ENABLE, current);
                break;
            case R.id.drc_enable:
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_DRC_ENABLE, current);
                break;
            case R.id.effect:
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_EFFECT, current);
                mSurround.init(current);
                break;
            case R.id.audio_nr:
                mFactoryMainApi.setIntegerValue(TvCommonManager.COMMAND_SET_AUDIO_NR, current);
                break;
            case R.id.surround:
                mSoundApi.setAudioSurroundMode(current);
                mEffect.init(current);
                break;
            case R.id.audio_out_channel:
                mAq.setSpeakerOutput(current);
                break;
        }
    }

    private final DisplayHelper mDisplayHelper = new DisplayHelper() {

        @Override
        public String display(int progress) {
            if (progress / 10 == 0) {
                return String.format(Locale.ROOT, "-%d.%d", progress/10, -(progress%10));
            } else {
                return String.format(Locale.ROOT, "%d.%d", progress/10, -(progress%10));
            }
        }

    };

}
