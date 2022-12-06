package com.realtek.tvfactory.picture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.realtek.tvfactory.FactoryMenuActivity;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.SeekBarPreferenceFragment;

import java.util.ArrayList;

public class AdcAdjustFragment extends PreferenceFragment {

    private FactoryMenuActivity mFactoryMenu;

    private AdcAdjustLogic mAdcAdjustLogic;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFactoryMenu = (FactoryMenuActivity) activity;
    }

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_adc_adjust);
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdcAdjustLogic = mPreferenceContainer.getPreferenceLogic(AdcAdjustLogic.class);
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
            return;
        }
        switch (preference.getId()) {
        case R.id.auto_tune:
            AdcAdjustLogic adcAdjustLogic = (AdcAdjustLogic) mPreferenceContainer.getPreferenceLogic(R.id.r_gain);
            adcAdjustLogic.exceAutoAdc();
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onKey(Preference preference, int keyCode, KeyEvent event) {
        if (preference instanceof SeekBarPreference) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                SeekBarPreference seekBarPreference = (SeekBarPreference) preference;
                switch (keyCode) {
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                    mAdcAdjustLogic.catchInputNumber(seekBarPreference, keyCode);
                    return true;
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (mAdcAdjustLogic.checkApplyProgress(seekBarPreference)) {
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (seekBarPreference.hasTemporaryProgress()) {
                        seekBarPreference.cancelTemporaryProgress();
                    }
                    break;
                default:
                    break;
                }
            }
        }
        return super.onKey(preference, keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 && data != null) {
            mFocusId = data.getIntExtra("focusId", mFocusId);
        }
    }

}
