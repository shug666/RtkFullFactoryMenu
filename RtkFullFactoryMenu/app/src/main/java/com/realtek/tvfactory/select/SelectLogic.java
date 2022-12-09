package com.realtek.tvfactory.select;

import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.preference.SumaryPreference;

public class SelectLogic extends LogicInterface {

    public SelectLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        FactoryMainApi mFactoryMainApi = FactoryMainApi.getInstance();
        SumaryPreference panel_Sel = (SumaryPreference) mContainer.findPreferenceById(R.id.panel_Sel);
        panel_Sel.setSumary(mFactoryMainApi.getPanelType());
        SumaryPreference panel_name = (SumaryPreference) mContainer.findPreferenceById(R.id.panel_name);
        panel_name.setSumary(mFactoryMainApi.getPanelType());

        SumaryPreference boot_logo = (SumaryPreference) mContainer.findPreferenceById(R.id.boot_logo);
        boot_logo.setSumary(mFactoryMainApi.getBootLogo());

        SumaryPreference boot_music = (SumaryPreference) mContainer.findPreferenceById(R.id.boot_music);
        boot_music.setSumary(mFactoryMainApi.getPanelType());

        SumaryPreference boot_animation = (SumaryPreference) mContainer.findPreferenceById(R.id.boot_animation);
        boot_animation.setSumary(mFactoryMainApi.getPanelType());

        ((SumaryPreference) mContainer.findPreferenceById(R.id.country_lang)).setSumary(mContext.getString(R.string.str_click_to_view));
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
