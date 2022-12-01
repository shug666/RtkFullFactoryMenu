package com.realtek.fullfactorymenu.api.impl;

import android.content.Context;


import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.api.manager.TvAudioManager;

public class SoundApi {
    private static final String TAG = "SoundApi";

    private static SoundApi mInstance = null;
    private final FactoryApplication mFactoryApplication;
    private final Context mContext;

    public SoundApi(){
        mInstance = this;
        mFactoryApplication = FactoryApplication.getInstance();
        mContext = mFactoryApplication.getApplicationContext();
    }

    public static SoundApi getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (SoundApi.class) {
                if (mInstance == null) {
                    mInstance = new SoundApi();
                }
            }
        }
        return mInstance;
    }

    public int getAudioSurroundMode() {
        return mFactoryApplication.getAq().getSurroundMode() ? 1 : 0;
    }

    public boolean setAudioSurroundMode(int surroundMode) {
        if (surroundMode == 0) {
            mFactoryApplication.getAq().setSurroundMode(false);
        } else {
            mFactoryApplication.getAq().setClearAudio(false);
            mFactoryApplication.getAq().setSurroundMode(true);
        }
        return true;
    }

    public int getAudioSoundMode() {
        return mFactoryApplication.getAq().getAudioMode();
    }

    public boolean setAudioSoundMode(int soundMode) {
        mFactoryApplication.getAq().setAudioMode(soundMode);
        return true;
    }

    public int getEqBand(int index) {
        if (TvAudioManager.AUDIO_EQBAND_0 == index) {
            return mFactoryApplication.getAq().getEqualLoudValue(125);
        } else if (TvAudioManager.AUDIO_EQBAND_1 == index) {
            return mFactoryApplication.getAq().getEqualLoudValue(250);
        } else if (TvAudioManager.AUDIO_EQBAND_2 == index) {
            return mFactoryApplication.getAq().getEqualLoudValue(500);
        } else if (TvAudioManager.AUDIO_EQBAND_3 == index) {
            return mFactoryApplication.getAq().getEqualLoudValue(1000);
        } else if (TvAudioManager.AUDIO_EQBAND_4 == index) {
            return mFactoryApplication.getAq().getEqualLoudValue(2000);
        } else if (TvAudioManager.AUDIO_EQBAND_5 == index) {
            return mFactoryApplication.getAq().getEqualLoudValue(4000);
        } else if (TvAudioManager.AUDIO_EQBAND_6 == index) {
            return mFactoryApplication.getAq().getEqualLoudValue(12000);
        }
        return 0;
    }

    public boolean setEqBand(int index, int eqValue) {
        if (TvAudioManager.AUDIO_EQBAND_0 == index) {
            mFactoryApplication.getAq().setEqualLoudValue(125, eqValue);
        } else if (TvAudioManager.AUDIO_EQBAND_1 == index) {
            mFactoryApplication.getAq().setEqualLoudValue(250, eqValue);
        } else if (TvAudioManager.AUDIO_EQBAND_2 == index) {
            mFactoryApplication.getAq().setEqualLoudValue(500, eqValue);
        } else if (TvAudioManager.AUDIO_EQBAND_3 == index) {
            mFactoryApplication.getAq().setEqualLoudValue(1000, eqValue);
        } else if (TvAudioManager.AUDIO_EQBAND_4 == index) {
            mFactoryApplication.getAq().setEqualLoudValue(2000, eqValue);
        } else if (TvAudioManager.AUDIO_EQBAND_5 == index) {
            mFactoryApplication.getAq().setEqualLoudValue(4000, eqValue);
        } else if (TvAudioManager.AUDIO_EQBAND_6 == index) {
            mFactoryApplication.getAq().setEqualLoudValue(12000, eqValue);
        }
        return true;
    }

    public int getTrebleLevel() {
        return mFactoryApplication.getAq().getTrebleLevel();
    }

    public void setTrebleLevel(int bLevel) {
        mFactoryApplication.getAq().setTrebleLevel(bLevel);
    }

    public int getBassLevel() {
        return mFactoryApplication.getAq().getBassLevel();
    }

    public void setBassLevel(int bLevel) {
        mFactoryApplication.getAq().setBassLevel(bLevel);
    }

}
