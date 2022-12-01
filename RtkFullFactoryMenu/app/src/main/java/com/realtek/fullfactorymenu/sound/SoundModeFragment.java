package com.realtek.fullfactorymenu.sound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.realtek.fullfactorymenu.FactoryMenuActivity;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;

/**
 * Created by Administrator on 2019/1/21.
 */

public class SoundModeFragment extends PreferenceFragment {

    private FactoryMenuActivity mFactoryMenu;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFactoryMenu = (FactoryMenuActivity) activity;
    }

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_sound_mode);
        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFactoryMenu = null;
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 && data != null){
            mFocusId = data.getIntExtra("focusId", mFocusId);
        }
    }
}
