package com.realtek.fullfactorymenu.preference;

import java.util.ArrayList;

import com.realtek.fullfactorymenu.BaseFragment;
import com.realtek.fullfactorymenu.FactoryMenuActivity;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.utils.Predicate;
import com.realtek.fullfactorymenu.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreferenceFragment extends BaseFragment implements OnSeekBarChangeListener {

    private final ArrayList<SeekBarPreference> mPreferences = new ArrayList<SeekBarPreference>();

    private SeekBarPreference mPreference;

    private TextView tvTitle;
    private SeekBar seekBar;
    private TextView tvValue;

    public SeekBarPreferenceFragment() {
        mPreferences.clear();
    }

    public SeekBarPreferenceFragment(ArrayList<SeekBarPreference> preferences, int index) {
        if (preferences == null || preferences.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (index < 0 || index >= preferences.size()) {
            throw new IllegalArgumentException();
        }
        mPreferences.clear();
        mPreferences.addAll(preferences);
        mPreference = preferences.get(index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = View.inflate(getActivity(), R.layout.fragment_seekbar_preview, null);
        tvTitle = (TextView) root.findViewById(R.id.seekbar_title);
        seekBar = (SeekBar) root.findViewById(R.id.seekbar_progress_bar);
        tvValue = (TextView) root.findViewById(R.id.seekbar_progress_value);

        if (isSeekBarPreferenceEnabled(mPreference)) {
            selectPreference(mPreference);
        } else {
            selectPreference(Utils.next(mPreferences, mPreference, true, mPredicate));
        }
        return root;
    }

    private void hideBackground() {
        Activity activity = getActivity();
        if (activity instanceof FactoryMenuActivity) {
            FactoryMenuActivity mainMenuActivity = (FactoryMenuActivity) activity;
            mainMenuActivity.hideBackground();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        hideBackground();
    }

    private void showBackground() {
        Activity activity = getActivity();
        if (activity instanceof FactoryMenuActivity) {
            FactoryMenuActivity mainMenuActivity = (FactoryMenuActivity) activity;
            mainMenuActivity.showBackground();
        }
    }

    @Override
    public void onStop() {
        showBackground();
        super.onStop();
    }

    private boolean isSeekBarPreferenceEnabled(SeekBarPreference preference) {
        return preference != null && preference.isEnabled();
    }

    public void selectPreference(SeekBarPreference preference) {
        if (preference == null) {
            return;
        }
        mPreference = preference;

        CharSequence title = preference.getTitle();
        int minValue = preference.getMinValue();
        int maxValue = preference.getMaxValue();
        int progress = preference.getProgress();
        int increment = preference.getIncrement();

        tvTitle.setText(title);
        seekBar.setMax(maxValue - minValue);
        seekBar.setProgress(progress - minValue);
        seekBar.setKeyProgressIncrement(increment);
        tvValue.setText(String.valueOf(progress));

        seekBar.setOnSeekBarChangeListener(this);
    }

    private void setResult(int resultCode, Intent data) {
        Fragment fragment = getTargetFragment();
        if (fragment == null) {
            return;
        }
        fragment.onActivityResult(getTargetRequestCode(), resultCode, data);
    }

    private void onBackPressed() {
        Activity activity = getActivity();
        if (activity instanceof FactoryMenuActivity) {
            FactoryMenuActivity mainMenuActivity = (FactoryMenuActivity) activity;
            if (mPreference != null) {
                Intent intent = new Intent();
                intent.putExtra("focusId", mPreference.getId());
                setResult(0, intent);
            }
            mainMenuActivity.popCurrentPage();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_MENU:
        case KeyEvent.KEYCODE_BACK:
            onBackPressed();
            return true;
        default:
            break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            selectPreference(Utils.previous(mPreferences, mPreference, true, mPredicate));
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            selectPreference(Utils.next(mPreferences, mPreference, true, mPredicate));
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mPreference == null) {
            return;
        }
        int minValue = mPreference.getMinValue();
        tvValue.setText(String.valueOf(progress + minValue));
        if (fromUser) {
            mPreference.setProgress(progress + minValue);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private final Predicate<SeekBarPreference> mPredicate = new Predicate<SeekBarPreference>() {

        @Override
        public boolean apply(SeekBarPreference preference) {
            return isSeekBarPreferenceEnabled(preference);
        }
    };

}
