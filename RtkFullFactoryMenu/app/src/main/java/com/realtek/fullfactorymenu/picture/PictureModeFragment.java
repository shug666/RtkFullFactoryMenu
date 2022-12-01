package com.realtek.fullfactorymenu.picture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.realtek.fullfactorymenu.FactoryMenuActivity;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.SeekBarPreferenceFragment;

import java.util.ArrayList;

public class PictureModeFragment extends PreferenceFragment {

    private FactoryMenuActivity mFactoryMenu;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFactoryMenu = (FactoryMenuActivity) activity;
    }

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_picture_mode);
        return builder.create();
    }

    @Override
    public void onDetach() {
        mFactoryMenu = null;
        super.onDetach();
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 && data != null) {
            mFocusId = data.getIntExtra("focusId", mFocusId);
        }
    }

}
