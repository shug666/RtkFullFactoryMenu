package com.realtek.tvfactory.ssc;

import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;

public class SSCLogic extends LogicInterface {

    public SSCLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {

    }
}
