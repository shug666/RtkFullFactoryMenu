package com.realtek.fullfactorymenu.ssc;

import android.os.Bundle;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;

public class SscAdjustFragment extends PreferenceFragment {

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_ssc_adjust);
        return builder.create();
    }

}
