package com.realtek.tvfactory.user;

import android.os.Bundle;

import com.realtek.tvfactory.R;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

/**
 * Created by Administrator on 2019/3/19.
 */

public class VolumeCurveFragment extends PreferenceFragment {
    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_volume_curve);
        return builder.create();
    }
}
