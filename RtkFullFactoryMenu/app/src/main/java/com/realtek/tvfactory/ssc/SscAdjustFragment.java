package com.realtek.tvfactory.ssc;

import android.os.Bundle;

import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

public class SscAdjustFragment extends PreferenceFragment {

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_ssc_adjust);
        return builder.create();
    }

}
