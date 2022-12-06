package com.realtek.tvfactory.sound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

public class SoundAdjustFragment extends PreferenceFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_sound_adjust);
        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 && data != null) {
            mFocusId = data.getIntExtra("focusId", mFocusId);
        }
    }

}
