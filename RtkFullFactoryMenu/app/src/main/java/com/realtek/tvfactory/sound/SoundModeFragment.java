package com.realtek.tvfactory.sound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.realtek.tvfactory.FactoryMenuActivity;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

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
