package com.realtek.tvfactory.select;

import android.os.Bundle;

import com.realtek.tvfactory.R;

import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

public class PanelPageFragment extends PreferenceFragment{

    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_panel);
        return builder.create();
    }


}
