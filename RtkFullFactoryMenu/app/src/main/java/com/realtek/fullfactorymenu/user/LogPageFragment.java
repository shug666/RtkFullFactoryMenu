package com.realtek.fullfactorymenu.user;

import android.os.IBinder;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.View;

import android.os.Bundle;
import android.content.Context;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;

public class LogPageFragment extends PreferenceFragment {

    private static final String TAG = "LogPageFragment";
    private LogLogic mLogLogic = null;

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_log);
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        mLogLogic = (LogLogic) mPreferenceContainer.getPreferenceLogic(LogLogic.class);
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
        if (preference.getId() == R.id.item_log_reboot) {
            IBinder binder = ServiceManager.getService(Context.POWER_SERVICE);
            IPowerManager pm = IPowerManager.Stub.asInterface(binder);
            try {
                pm.reboot(false, null, false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


}