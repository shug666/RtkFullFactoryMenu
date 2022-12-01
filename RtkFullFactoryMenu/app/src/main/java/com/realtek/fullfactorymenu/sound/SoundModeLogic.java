package com.realtek.fullfactorymenu.sound;


import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.SoundApi;
import com.realtek.fullfactorymenu.api.manager.TvAudioManager;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;


public class SoundModeLogic extends LogicInterface {

    private final String TAG = "SoundModeLogic";

    private static Thread mThread;

    private volatile static int clock;

    private StatePreference mSoundMode = null;
    private SeekBarPreference mSoundEq0 = null;
    private SeekBarPreference mSoundEq1 = null;
    private SeekBarPreference mSoundEq2 = null;
    private SeekBarPreference mSoundEq3 = null;
    private SeekBarPreference mSoundEq4 = null;
    private SeekBarPreference mSoundEq5 = null;
    private SeekBarPreference mSoundEq6 = null;
    private SoundApi mSoundApi;

    public SoundModeLogic(PreferenceContainer container) {
        super(container);
        mSoundApi = SoundApi.getInstance();
    }

    @Override
    public void init() {
        mSoundMode = (StatePreference)mContainer.findPreferenceById(R.id.sound_mode);
        mSoundEq0 = (SeekBarPreference)mContainer.findPreferenceById(R.id.sound_eq_0);
        mSoundEq1 = (SeekBarPreference)mContainer.findPreferenceById(R.id.sound_eq_1);
        mSoundEq2 = (SeekBarPreference)mContainer.findPreferenceById(R.id.sound_eq_2);
        mSoundEq3 = (SeekBarPreference)mContainer.findPreferenceById(R.id.sound_eq_3);
        mSoundEq4 = (SeekBarPreference)mContainer.findPreferenceById(R.id.sound_eq_4);
        mSoundEq5 = (SeekBarPreference) mContainer.findPreferenceById(R.id.sound_eq_5);
        mSoundEq6 = (SeekBarPreference) mContainer.findPreferenceById(R.id.sound_eq_6);

        mSoundMode.init(mSoundApi.getAudioSoundMode());
        if (mSoundEq0 != null){
            mSoundEq0.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_0));
        }
        if (mSoundEq1 != null){
            mSoundEq1.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_1));
        }
        if (mSoundEq2 != null){
            mSoundEq2.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_2));
        }
        if (mSoundEq3 != null){
            mSoundEq3.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_3));
        }
        if (mSoundEq4 != null){
            mSoundEq4.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_4));
        }
        if (mSoundEq5 != null){
            mSoundEq5.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_5));
        }
        if (mSoundEq6 != null){
            mSoundEq6.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_6));
        }
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        switch (preference.getId()){
        case R.id.sound_eq_0:
            clock = progress;
            if (mThread == null) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_0, clock);
                        while (clock != mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_0)) {
                            mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_0, clock);
                        }
                        mThread = null;
                    }
                });
                mThread.start();
            }
            break;
        case R.id.sound_eq_1:
            clock = progress;
            if (mThread == null) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_1, clock);
                        while (clock != mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_1)) {
                            mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_1, clock);
                        }
                        mThread = null;
                    }
                });
                mThread.start();
            }
            break;
        case R.id.sound_eq_2:
            clock = progress;
            if (mThread == null) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_2, clock);
                        while (clock != mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_2)) {
                            mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_2, clock);
                        }
                        mThread = null;
                    }
                });
                mThread.start();
            }
            break;
        case R.id.sound_eq_3:
            clock = progress;
            if (mThread == null) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_3, clock);
                        while (clock != mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_3)) {
                            mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_3, clock);
                        }
                        mThread = null;
                    }
                });
                mThread.start();
            }
            break;
        case R.id.sound_eq_4:
            clock = progress;
            if (mThread == null) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_4, clock);
                        while (clock != mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_4)) {
                            mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_4, clock);
                        }
                        mThread = null;
                    }
                });
                mThread.start();
            }
            break;
        case R.id.sound_eq_5:
            clock = progress;
            if (mThread == null) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_5, clock);
                        while (clock != mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_5)) {
                            mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_5, clock);
                        }
                        mThread = null;
                    }
                });
                mThread.start();
            }
            break;
        case R.id.sound_eq_6:
            clock = progress;
            if (mThread == null) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_6, clock);
                        while (clock != mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_6)) {
                            mSoundApi.setEqBand(TvAudioManager.AUDIO_EQBAND_6, clock);
                        }
                        mThread = null;
                    }
                });
                mThread.start();
            }
            break;
        }
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()){
            case R.id.sound_mode:
                mSoundApi.setAudioSoundMode(current);
                if (mSoundEq0 != null){
                    mSoundEq0.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_0));
                }
                if (mSoundEq1 != null){
                    mSoundEq1.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_1));
                }
                if (mSoundEq2 != null){
                    mSoundEq2.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_2));
                }
                if (mSoundEq3 != null){
                    mSoundEq3.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_3));
                }
                if (mSoundEq4 != null){
                    mSoundEq4.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_4));
                }
                if (mSoundEq5 != null){
                    mSoundEq5.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_5));
                }
                if (mSoundEq6 != null){
                    mSoundEq6.init(mSoundApi.getEqBand(TvAudioManager.AUDIO_EQBAND_6));
                }
                break;
        }
    }
}
