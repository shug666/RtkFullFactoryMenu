package com.realtek.tvfactory.sound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.realtek.tvfactory.BaseFragment;
import com.realtek.tvfactory.FactoryMenuFragment;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;
import com.realtek.tvfactory.user.VolumeCurveFragment;

/**
 * Created by Administrator on 2019/1/21.
 */

public class SoundPageFragment extends PreferenceFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_sound);
        mPreferenceContainer = builder.create();
        mPreferenceContainer.setPreferenceItemClickListener(this);
        return mPreferenceContainer;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
            case R.id.page_sound_mode:
                showPage(SoundModeFragment.class,R.string.str_sound_mode);
                break;
            case R.id.page_sound_adjust:
                showPage(SoundAdjustFragment.class,R.string.str_sound_adjust);
                break;
            case R.id.page_volume_curve:
                showPage(VolumeCurveFragment.class, R.string.str_volume_curve);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 && data != null) {
            mFocusId = data.getIntExtra("focusId", mFocusId);
        }
    }
}
