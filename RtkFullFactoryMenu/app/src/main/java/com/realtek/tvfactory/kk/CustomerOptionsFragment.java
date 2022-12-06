package com.realtek.tvfactory.kk;

import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

import android.os.Bundle;

public class CustomerOptionsFragment extends PreferenceFragment {

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_kk_customer_options);
        return builder.create();
    }
}
