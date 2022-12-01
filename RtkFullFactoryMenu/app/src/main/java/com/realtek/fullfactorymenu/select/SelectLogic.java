package com.realtek.fullfactorymenu.select;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.FactoryMainApi;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.preference.SumaryPreference;

public class SelectLogic extends LogicInterface {

    private SumaryPreference panel_Sel;
    private FactoryMainApi mFactoryMainApi;

    public SelectLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        mFactoryMainApi = FactoryMainApi.getInstance();
        panel_Sel = (SumaryPreference) mContainer.findPreferenceById(R.id.panel_Sel);
        panel_Sel.setSumary(mFactoryMainApi.getPanelType());
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
