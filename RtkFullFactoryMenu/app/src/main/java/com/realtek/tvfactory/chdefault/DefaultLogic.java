package com.realtek.tvfactory.chdefault;

import static com.realtek.tvfactory.utils.Constants.EXTRA_M_MODE_STATUS;
import static com.realtek.tvfactory.utils.Constants.MSG_ACTION_START_CH_MMODE;
import static com.realtek.tvfactory.utils.Constants.MSG_ACTION_STOP_CH_MMODE;
import static com.realtek.tvfactory.utils.Constants.SERVICE_MKEY_EVENT;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.utils.PackageUtils;

public class DefaultLogic extends LogicInterface {

    private static final String TAG = DefaultLogic.class.getSimpleName();

    public DefaultLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        StatePreference mMode = (StatePreference) mContainer.findPreferenceById(R.id.ch_default_m_mode);
        mMode.init(Settings.System.getInt(mContext.getContentResolver(), "MMode", 0));
        if (!FactoryApplication.CUSTOMER_IS_CH) {
            mMode.setEnabled(false);
            mMode.setFocusable(false);
        }
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        if (preference.getId() == R.id.ch_default_m_mode) {
            Settings.System.putInt(mContext.getContentResolver(), "MMode", current);

            ComponentName componentName = ComponentName.unflattenFromString(SERVICE_MKEY_EVENT);
            Intent service = PackageUtils.getServiceIntentByComponentName(preference.getContext(), componentName);
            if (service != null) {
                service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                service.putExtra(EXTRA_M_MODE_STATUS, current == 1 ? MSG_ACTION_START_CH_MMODE : MSG_ACTION_STOP_CH_MMODE);
                preference.getContext().startService(service);
            } else {
                Log.e(TAG, String.format("start %s fail, because not exist!",componentName.getClassName()));
            }
        }
    }
}
