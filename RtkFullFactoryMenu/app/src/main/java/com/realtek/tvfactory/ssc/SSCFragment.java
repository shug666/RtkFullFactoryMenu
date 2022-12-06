package com.realtek.tvfactory.ssc;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.realtek.tvfactory.BaseFragment;
import com.realtek.tvfactory.FactoryMenuFragment;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

public class SSCFragment extends PreferenceFragment {
    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_ssc);
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
            case R.id.page_ssc_adjust:
                showPage(SscAdjustFragment.class,R.string.str_ssc_adjust);
                break;
            default:
                break;
        }
    }
}
