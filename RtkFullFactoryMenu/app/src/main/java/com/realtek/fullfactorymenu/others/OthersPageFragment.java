package com.realtek.fullfactorymenu.others;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;

import android.os.Bundle;

public class OthersPageFragment extends PreferenceFragment {

	@Override
	public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_others);
		return builder.create();
	}

    
}
