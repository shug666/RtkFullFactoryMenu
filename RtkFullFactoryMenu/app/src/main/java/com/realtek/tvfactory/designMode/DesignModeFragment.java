package com.realtek.tvfactory.designMode;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.realtek.tvfactory.BaseFragment;
import com.realtek.tvfactory.FactoryMenuFragment;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.api.impl.UserApi;
import com.realtek.tvfactory.picture.WhiteBalanceAdjustFragment;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;
import com.realtek.tvfactory.user.LogPageFragment;
import com.realtek.tvfactory.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DesignModeFragment extends PreferenceFragment {

    private final String TAG = "DesignModeFragment";

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
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((pak.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // 添加自己已经安装的应用程序
                continue;
            }
            apps.add(pak.packageName);
            // Log.d(TAG,pak.packageName);
        }
        return apps;
    }

    private void clearApplicationUserData(List<String> packages, final Runnable callback) {
        final List<String> tasks = new ArrayList<>(packages);
        IPackageDataObserver observer = new IPackageDataObserver.Stub() {

            @Override
            public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                Log.d(TAG, String.format("clear %s %b", packageName, succeeded));
                if (tasks.remove(packageName)) {
                    if (tasks.isEmpty()) {
                        callback.run();
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

    public static boolean deleteAllFile(String path, boolean isIncludeRoot) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }

        if(file.isFile()) {
            return file.delete();
        }

        File[] fileList = file.listFiles();
        boolean res = true;
        for (File f : fileList) {
            if(f.isFile()) {
                res = res && f.delete();
            } else if(f.isDirectory()) {
                res = res && deleteAllFile(f.getAbsolutePath(), true);
            }
        }

        if(isIncludeRoot) {
            res = res && file.delete();
        }

        return res;
    }



    @Override
    public void onPreferenceItemClick(Preference preference) {
        switch (preference.getId()) {
            case R.id.aging_mode:
                PackageManager pm = getActivity().getPackageManager();
                ComponentName name = new ComponentName("com.toptech.factorytoolsgtv", "com.toptech.factorytoolsgtv.AgingActivity");
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
            case R.id.m_mode:
                Intent mMode = new Intent();
                ComponentName mModeComp = new ComponentName("com.toptech.factorytoolsboe", "com.toptech.factorytoolsboe.MModeActivity");
                mMode.setComponent(mModeComp);
                mMode.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(mMode);
                getActivity().finish();
                break;
            case R.id.wb_adjust:
                showPage(WhiteBalanceAdjustFragment.class, R.string.str_wb_adjust);
                break;
            case R.id.white_pattern:

                break;
            case R.id.cancel_pattern:

                break;
            case R.id.clear_app:
                List<String> apps = getNotSystemApps(getContext());

                clearApplicationUserData(apps,() -> {
                    Thread thr = new Thread("ResetActivity") {
                        @Override
                        public void run() {
                            Log.d(TAG, "clear data and reboot");
                            try {
                                Thread.sleep(300);
                            } catch (Exception e) {
                            }
                            try {
                                Runtime.getRuntime().exec("reboot");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thr.start();
                    // Wait for us to tell the power manager to shutdown.
                    try {
                        thr.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                Log.d("shugan","apps = "+ apps.toString());
                break;
            case R.id.logcat_tools:
                showPage(LogPageFragment.class, R.string.str_log_tool);
                break;
            default:
        }
    }
}
