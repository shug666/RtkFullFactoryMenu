package com.realtek.tvfactory.designMode;

import static com.realtek.tvfactory.utils.Constants.ACTIVITY_AGING;
import static com.realtek.tvfactory.utils.Constants.ACTIVITY_MMODE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.realtek.tvfactory.BaseFragment;
import com.realtek.tvfactory.FactoryMenuFragment;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.UserApi;
import com.realtek.tvfactory.picture.WhiteBalanceAdjustFragment;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;
import com.realtek.tvfactory.user.LogPageFragment;
import com.realtek.tvfactory.utils.PackageUtils;

import java.util.ArrayList;
import java.util.List;

public class DesignModeFragment extends PreferenceFragment {

    private final String TAG = "DesignModeFragment";
    private ProgressDialog progressDialog;

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_design_mode);
        return builder.create();
    }

    private void showPage(Class<? extends BaseFragment> clazz, int title) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FactoryMenuFragment) {
            FactoryMenuFragment menuFragment = (FactoryMenuFragment) parentFragment;
            menuFragment.showPage(clazz, title);
        }
    }

    @NonNull
    public List<String> getNotSystemApps(@NonNull Context context) {
        List<String> apps = new ArrayList<>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        @SuppressLint("QueryPermissionsNeeded") List<ApplicationInfo> packlist = pManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES );
        for (int i = 0, len = packlist.size(); i<len; i++) {
            ApplicationInfo pak = packlist.get(i);

            if (Process.SYSTEM_UID == pak.uid){
                continue;
            }
            if ((pak.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            apps.add(pak.packageName);
        }
        return apps;
    }

    private void uninstallApps(List<String> packages){
        PackageManager pkgManager = getContext().getPackageManager();
        PackageDeleteObserver observer = new PackageDeleteObserver(packages.size());
        for (String app : packages) {
            pkgManager.deletePackage(app, observer, 0);
        }
    }

    private void clearApplicationUserData(List<String> packages) {
        final List<String> tasks = new ArrayList<>(packages);
        IPackageDataObserver observer = new IPackageDataObserver.Stub() {

            @Override
            public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                Log.d(TAG, String.format("clear %s %b", packageName, succeeded));
                if (tasks.remove(packageName)) {
                    if (tasks.isEmpty()) {
                        uninstallApps(packages);
                    }
                }
            }
        };
        ActivityManager am = getContext().getSystemService(ActivityManager.class);
        for (String packageName : packages) {
            Log.d(TAG, "packagename :" + packageName);
            am.clearApplicationUserData(packageName, observer);
        }
    }

    private class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        private int size;
        private int position = 0;

        public PackageDeleteObserver(int size) {
            this.size = size;
        }

        @Override
        public void packageDeleted(String arg0, int arg1)
                throws RemoteException {
            // TODO Auto-generated method stub
            position ++;
            if (position == size) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(),"Cleaning Successful!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(getContext(),
                    title, message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }

        progressDialog.show();

    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
        switch (preference.getId()) {
            case R.id.aging_mode: {
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
            case R.id.wb_adjust:
                showPage(WhiteBalanceAdjustFragment.class, R.string.str_wb_adjust);
                break;
            case R.id.white_pattern:

                break;
            case R.id.clear_app:
                List<String> apps = getNotSystemApps(getContext());
                if (apps.isEmpty()){
                    Toast.makeText(getContext(),"Not Needs Cleaning!",Toast.LENGTH_SHORT).show();
                    break;
                }
                showProgressDialog("Clear APP","Processing Information,please wait...");
                clearApplicationUserData(apps);
                Log.d(TAG,"apps = "+ apps.toString());
                break;
            case R.id.logcat_tools:
                showPage(LogPageFragment.class, R.string.str_log_tool);
                break;
            default:
        }
    }
}
