package com.realtek.tvfactory.user;

import static com.realtek.tvfactory.utils.Constants.ACTIVITY_AGING;
import static com.realtek.tvfactory.utils.Constants.ACTIVITY_MMODE;
import static com.realtek.tvfactory.utils.Constants.PACKAGE_NAME_AUTO_TEST;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.realtek.tvfactory.BaseFragment;
import com.realtek.tvfactory.FactoryMenuFragment;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.UserApi;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;
import com.realtek.tvfactory.select.PanelPageFragment;
import com.realtek.tvfactory.tune.TuningSettingFragment;
import com.realtek.tvfactory.utils.ByteTransformUtils;

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
                ComponentName name = new ComponentName(PACKAGE_NAME_AUTO_TEST, PACKAGE_NAME_AUTO_TEST + "." + ACTIVITY_AGING);
                int state = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
                pm.setComponentEnabledSetting(name, state, PackageManager.DONT_KILL_APP);

                Intent intent = new Intent();
                intent.setComponent(name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                if (UserApi.getInstance().getBVTOnOff()) {
                    UserApi.getInstance().setBVTCmdOnOff(false, false);
                }
                getActivity().finish();
                break;
            case R.id.volume_curve:
                showPage(VolumeCurveFragment.class, R.string.str_volume_curve);
                break;
            case R.id.logcat_tools:
                showPage(LogPageFragment.class, R.string.str_log_tool);
                break;
            case R.id.m_mode: {
                Intent mMode = new Intent();
                ComponentName mModeComp = new ComponentName(PACKAGE_NAME_AUTO_TEST, PACKAGE_NAME_AUTO_TEST + "." + ACTIVITY_MMODE);
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
                ComponentName componentName = new ComponentName(PACKAGE_NAME_AUTO_TEST, PACKAGE_NAME_AUTO_TEST + ".dashboard.DashboardActivity");
                dashboard.setComponent(componentName);
                activity.startActivity(dashboard);
                activity.finish();
                break;
        }
    }
}
