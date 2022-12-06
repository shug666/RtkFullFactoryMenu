package com.realtek.tvfactory.select;

import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.preference.SumaryPreference;

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
