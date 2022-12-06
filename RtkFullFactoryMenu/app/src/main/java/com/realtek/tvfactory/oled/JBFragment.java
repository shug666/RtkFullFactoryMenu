package com.realtek.tvfactory.oled;

import android.app.Activity;
import android.os.Bundle;

import com.android.tv.common.TvCommonConstants;
import com.android.tv.common.TvCommonUtils;
import com.realtek.tvfactory.FactoryMenuActivity;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.SeekBarPreferenceFragment;

import java.util.ArrayList;


public class JBFragment extends PreferenceFragment {
    private FactoryMenuActivity mFactoryMenu;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFactoryMenu = (FactoryMenuActivity) activity;
    }

    @Override
    public void onDetach() {
        mFactoryMenu = null;
        super.onDetach();
    }

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_jb);
        return builder.create();
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
        switch (preference.getId()) {
            case R.id.Run_JB:
                TvCommonUtils.sendBroadCastToKeyIntercept(getContext(),
                        TvCommonConstants.ACTION_OLED_TCON_RUN_JB);
                break;

        }

        if (preference instanceof SeekBarPreference) {
            ArrayList<Preference> preferences = mPreferenceContainer.getPreferences();
            ArrayList<SeekBarPreference> list = new ArrayList<SeekBarPreference>();
            for (Preference child : preferences) {
                if (child instanceof SeekBarPreference) {
                    list.add((SeekBarPreference) child);
                }
            }
            SeekBarPreferenceFragment fragment = new SeekBarPreferenceFragment(list, list.indexOf(preference));
            fragment.setTargetFragment(getParentFragment(), 0);
            mFactoryMenu.showPage(fragment);
        }
    }
}
