package com.realtek.tvfactory.logic;

import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference.PreferenceProgressChangeListener;
import com.realtek.tvfactory.preference.StatePreference.PreferenceIndexChangeListener;

import android.content.Context;

public abstract class LogicInterface implements PreferenceIndexChangeListener, PreferenceProgressChangeListener {

    protected Context mContext;
    protected PreferenceContainer mContainer;

    public LogicInterface(PreferenceContainer container) {
        mContext = container.getContext();
        mContainer = container;
    }

    public abstract void init();

    public abstract void deinit();

}
