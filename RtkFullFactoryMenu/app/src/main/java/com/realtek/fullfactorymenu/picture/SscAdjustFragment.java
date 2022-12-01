package com.realtek.fullfactorymenu.picture;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;

import android.os.Bundle;

public class SscAdjustFragment extends PreferenceFragment {

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_ssc_adjust);
        return builder.create();
    }

}
