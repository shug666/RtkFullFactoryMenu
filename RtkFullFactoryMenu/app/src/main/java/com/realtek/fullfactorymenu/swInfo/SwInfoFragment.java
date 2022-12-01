package com.realtek.fullfactorymenu.swInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.realtek.fullfactorymenu.BaseFragment;
import com.realtek.fullfactorymenu.FactoryMenuFragment;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.oled.JBFragment;
import com.realtek.fullfactorymenu.oled.OffRSFragment;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;

public class SwInfoFragment extends PreferenceFragment {
    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_sw_info);
        return builder.create();
    }

    private void showPage(Class<? extends BaseFragment> clazz, int title) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FactoryMenuFragment) {
            FactoryMenuFragment menuFragment = (FactoryMenuFragment) parentFragment;
            menuFragment.showPage(clazz, title);
        }
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {

        switch (preference.getId()){
        }
    }
}
