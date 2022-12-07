package com.realtek.tvfactory.user;

import static com.realtek.tvfactory.utils.Constants.ACTIVITY_AGING;
import static com.realtek.tvfactory.utils.Constants.ACTIVITY_MMODE;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.realtek.tvfactory.BaseFragment;
import com.realtek.tvfactory.FactoryMenuFragment;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.UserApi;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;
import com.realtek.tvfactory.select.PanelPageFragment;
import com.realtek.tvfactory.utils.PackageUtils;

public class UserPageFragment extends PreferenceFragment{
    private final String TAG = "UserPageFragment";

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
            case R.id.AgingMode: {
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    Log.e(TAG, "getActivity() = null!");
                    return;
                }
                Intent agingIntent = PackageUtils.getIntentByActivityName(activity, ACTIVITY_AGING);
                if (agingIntent != null) {
                    if (UserApi.getInstance().getBVTOnOff()) {
                        UserApi.getInstance().setBVTCmdOnOff(false, false);
                    }
                    PackageManager pm = getActivity().getPackageManager();
                    int state = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
                    pm.setComponentEnabledSetting(agingIntent.getComponent(), state, PackageManager.DONT_KILL_APP);
                    activity.startActivity(agingIntent);
                    getActivity().finish();
                } else {
                    Toast.makeText(activity, R.string.ch_function_not_support, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.volume_curve:
                showPage(VolumeCurveFragment.class, R.string.str_volume_curve);
                break;
            case R.id.logcat_tools:
                showPage(LogPageFragment.class, R.string.str_log_tool);
                break;
            case R.id.m_mode: {
                Context context = getActivity();
                if (context == null) {
                    context = getContext();
                    if (context == null) {
                        Log.e(TAG, "getActivity() and getContext() are null!");
                        return;
                    }
                }
                Intent mMode = PackageUtils.getIntentByActivityName(context, ACTIVITY_MMODE);
                if (mMode != null) {
                    context.startActivity(mMode);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                } else {
                    Toast.makeText(context, R.string.ch_function_not_support, Toast.LENGTH_SHORT).show();
                }
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
            case R.id.bash_board: {
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    Log.e(TAG, "getActivity() = null!");
                    return;
                }
                Intent dashboardIntent = PackageUtils.getIntentByActivityName(activity, "dashboard.DashboardActivity");
                if (dashboardIntent != null) {
                    activity.startActivity(dashboardIntent);
                    getActivity().finish();
                } else {
                    Toast.makeText(activity, R.string.ch_function_not_support, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
    }
}
