package com.realtek.fullfactorymenu.panel;

import android.os.Bundle;

import com.realtek.fullfactorymenu.R;

import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;

public class PanelPageFragment extends PreferenceFragment{

    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_panel);
        return builder.create();
    }


}
