package com.realtek.fullfactorymenu.user;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.realtek.fullfactorymenu.BaseFragment;
import com.realtek.fullfactorymenu.FactoryMenuFragment;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.UserApi;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;
import com.realtek.fullfactorymenu.select.PanelPageFragment;

public class UserPageFragment extends PreferenceFragment{

    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {

        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_user);
        return builder.create();
    }

    private void showPage(Class<? extends BaseFragment> clazz, int title) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FactoryMenuFragment) {
            FactoryMenuFragment menuFragment = (FactoryMenuFragment) parentFragment;
            menuFragment.showPage(clazz, title);
        }
    }

    private void showPage(Class<? extends BaseFragment> clazz, int title, Bundle arguments) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FactoryMenuFragment) {
            FactoryMenuFragment menuFragment = (FactoryMenuFragment) parentFragment;
            menuFragment.showPage(clazz.getName(), title, arguments);
        }
    }

    public void onPreferenceItemClick(Preference preference) {
       switch (preference.getId()){
       case R.id.AgingMode:
            PackageManager pm = getActivity().getPackageManager();
            ComponentName name = new ComponentName("com.toptech.factorytoolsgtv", "com.toptech.factorytoolsgtv.AgingActivity");
            int state = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            pm.setComponentEnabledSetting(name, state, PackageManager.DONT_KILL_APP);

            Intent intent = new Intent();
            intent.setComponent(name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
            if (UserApi.getInstance().getBOEOnOff()) {
                UserApi.getInstance().setBOECmdOnOff(false, false);
            }
            getActivity().finish();
            break;
       case R.id.program_preset:
           showPage(ProgramPresetFragment.class, R.string.str_program_preset);
           break;
       case R.id.volume_curve:
           showPage(VolumeCurveFragment.class, R.string.str_volume_curve);
           break;
       case R.id.logcat_tools:
           showPage(LogPageFragment.class, R.string.str_log_tool);
           break;
       case R.id.m_mode: {
           Intent mMode = new Intent();
           ComponentName mModeComp = new ComponentName("com.toptech.factorytoolsboe", "com.toptech.factorytoolsboe.MModeActivity");
           mMode.setComponent(mModeComp);
           mMode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           getActivity().startActivity(mMode);
           getActivity().finish();
           break;
       }
       case R.id.user_panel:{
           showPage(PanelPageFragment.class, R.string.str_panel_setting);
           break;
       }
       case R.id.wifi_list: {
           Bundle bundle = new Bundle();
           bundle.putString("type", "WIFI");
           showPage(ListFragment.class, R.string.str_wifi_list, bundle);
           break;
       }
       case R.id.bluetooth_list: {
           Bundle bundle = new Bundle();
           bundle.putString("type", "BLUETOOTH");
           showPage(ListFragment.class, R.string.str_bluetooth_list, bundle);
           break;
       }
       case R.id.bash_board:
           FragmentActivity activity = getActivity();
           if (activity == null) {
               return;
           }
           Intent dashboard = new Intent();
           ComponentName componentName = new ComponentName("com.toptech.factorytoolsboe", "com.toptech.factorytoolsboe.dashboard.DashboardActivity");
           dashboard.setComponent(componentName);
           activity.startActivity(dashboard);
           activity.finish();
           break;
       }
    }
}
