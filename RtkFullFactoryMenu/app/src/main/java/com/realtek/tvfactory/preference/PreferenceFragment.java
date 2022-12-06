package com.realtek.tvfactory.preference;

import com.realtek.tvfactory.BaseFragment;
import com.realtek.tvfactory.preference.PreferenceContainer.PreferenceItemClickListener;
import com.realtek.tvfactory.preference.PreferenceContainer.PreferenceItemKeyListener;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class PreferenceFragment extends BaseFragment
        implements PreferenceItemClickListener, PreferenceItemKeyListener {

    protected PreferenceContainer mPreferenceContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPreferenceContainer = onCreatePreferenceContainer(savedInstanceState);
        if (mPreferenceContainer != null) {
            mPreferenceContainer.setPreferenceItemClickListener(this);
            mPreferenceContainer.setPreferenceItemKeyListener(this);
        }
        return mPreferenceContainer;
    }

    public abstract PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        if (mPreferenceContainer != null) {
            mPreferenceContainer.clearAll();
            mPreferenceContainer = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {

    }

    @Override
    public boolean onKey(Preference preference, int keyCode, KeyEvent event) {
        return false;
    }

}
