package com.realtek.fullfactorymenu.konka;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;

import android.os.Bundle;

public class CustomerOptionsFragment extends PreferenceFragment {

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_konka_customer_options);
        return builder.create();
    }
}
