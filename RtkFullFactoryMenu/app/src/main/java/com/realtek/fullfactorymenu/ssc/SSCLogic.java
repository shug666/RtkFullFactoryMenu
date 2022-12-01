package com.realtek.fullfactorymenu.ssc;

import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;

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
