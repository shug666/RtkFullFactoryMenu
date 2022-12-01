package com.realtek.fullfactorymenu.select;

import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;

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
