package com.realtek.fullfactorymenu.sound;

import android.util.Log;
import android.view.View;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.SoundApi;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.tv.SoundEqualList;

import java.util.List;

public class SoundModeLogic extends LogicInterface {

    private final String TAG = "SoundModeLogic";
    private final SoundApi mSoundApi;
    private final int[] ids = {R.id.sound_eq_0, R.id.sound_eq_1, R.id.sound_eq_2, R.id.sound_eq_3, R.id.sound_eq_4, R.id.sound_eq_5, R.id.sound_eq_6};

    private StatePreference mSoundMode = null;
    private SeekBarPreference mSoundTreble = null, mSoundBass = null;
    private List<Integer> mFreqList;

    public SoundModeLogic(PreferenceContainer container) {
        super(container);
        mSoundApi = SoundApi.getInstance();
    }

    @Override
    public void init() {
        mSoundMode = (StatePreference) mContainer.findPreferenceById(R.id.sound_mode);
        mSoundTreble = (SeekBarPreference) mContainer.findPreferenceById(R.id.sound_treble);
        mSoundBass = (SeekBarPreference) mContainer.findPreferenceById(R.id.sound_bass);

        initSoundModeData(mSoundApi.getAudioSoundMode());
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        switch (preference.getId()){
            case R.id.sound_treble:
                mSoundApi.setTrebleLevel(progress);
                break;
            case R.id.sound_bass:
                mSoundApi.setBassLevel(progress);
                break;
            default:
                for (int index = 0; index < ids.length; index++) {
                    if (preference.getId() == ids[index]) {
                        Log.e(TAG, "index:" + index + ", Freq:" + mFreqList.get(index) + ", Gain:" + progress);
                        mSoundApi.setEqualLoudValue(mFreqList.get(index), progress);
                        return;
                    }
                }
                Log.e(TAG, "preference.getId():" + preference.getId());
                break;
        }
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        if (preference.getId() == R.id.sound_mode) {
            if (mSoundApi.setAudioSoundMode(current)) {
                initSoundModeData(current);
            } else {
                Log.e(TAG, String.format("setAudioSoundMode(%d) failed!", current));
            }
        }
    }

    private void initSoundModeData(int soundMode) {
        mSoundMode.init(soundMode);
        if (mSoundTreble != null){
            mSoundTreble.init(mSoundApi.getTrebleLevel());
        }
        if (mSoundBass != null){
            mSoundBass.init(mSoundApi.getBassLevel());
        }
        SoundEqualList soundEqualList = mSoundApi.getEqualList();
        if (soundEqualList != null) {
            List<Integer> mGainList = soundEqualList.getGainList();
            mFreqList = soundEqualList.getFreqList();
            int mEqCount = soundEqualList.getCount();
            Log.d(TAG, "mGainList=" + mGainList);
            Log.d(TAG, "mFreqList=" + mFreqList);
            if (mEqCount > ids.length) {
                Log.e(TAG, String.format("mEqCount(%d) is too large", mEqCount));
                mEqCount = ids.length;
            }
            for (int index = 0; index < mEqCount; index++) {
                SeekBarPreference seekBarPreference = (SeekBarPreference) mContainer.findPreferenceById(ids[index]);
                seekBarPreference.setProgress(mGainList.get(index));
                seekBarPreference.setTitle(mFreqList.get(index) + "Hz");
                seekBarPreference.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e(TAG, "soundEqualList=null");
        }
    }
}
