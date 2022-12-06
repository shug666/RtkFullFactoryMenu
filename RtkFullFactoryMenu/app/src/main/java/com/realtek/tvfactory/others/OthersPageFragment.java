package com.realtek.tvfactory.others;

import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

import android.os.Bundle;

public class OthersPageFragment extends PreferenceFragment {

	@Override
	public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_others);
		return builder.create();
	}

    
}
