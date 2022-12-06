package com.realtek.tvfactory.select;

import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;

public class ProjectIdLogic extends LogicInterface{

    public static final String TAG = "ProjectIdLogic";

    public ProjectIdLogic(PreferenceContainer container) {
        super(container);

    }

    @Override
    public void init() {

    }

    @Override
    public void deinit() {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
    }

}
