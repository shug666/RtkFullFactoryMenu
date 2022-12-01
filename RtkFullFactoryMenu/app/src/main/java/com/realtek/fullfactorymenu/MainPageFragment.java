package com.realtek.fullfactorymenu;

import android.app.AlertDialog;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.realtek.fullfactorymenu.api.impl.FactoryMainApi;
import com.realtek.fullfactorymenu.api.manager.TvCommonManager;
import com.realtek.fullfactorymenu.chdefault.DefaultFragment;
import com.realtek.fullfactorymenu.debug.DeBugPageFragment;
import com.realtek.fullfactorymenu.oled.OledFragment;
import com.realtek.fullfactorymenu.others.OthersPageFragment;
import com.realtek.fullfactorymenu.picture.PicturePageFragment;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceContainer.PreferenceItemClickListener;
import com.realtek.fullfactorymenu.select.SelectFragment;
import com.realtek.fullfactorymenu.sound.SoundPageFragment;
import com.realtek.fullfactorymenu.ssc.SSCFragment;
import com.realtek.fullfactorymenu.swInfo.SwInfoFragment;
import com.realtek.fullfactorymenu.systemInfo.SystemInfoFragment;
import com.realtek.fullfactorymenu.user.UserPageFragment;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vendor.realtek.rtkconfigs.V1_0.IRtkProjectConfigs;
import vendor.realtek.rtkconfigs.V1_0.OptionalString;

public class MainPageFragment extends BaseFragment implements PreferenceItemClickListener {
    private PreferenceContainer mPreferenceContainer;
    public FactoryMenuActivity factoryMenuActivity;
    private TextView mPcbInfo;
    private TextView mSoftwareVersion;
    private TextView mPanel;
    private TextView mCompileTime;
    private TextView mPraName;
    private TextView mProName;
    private TextView mProTitle;
    private TextView mCusName;
    private TextView mPreVersion;
    private TextView mFirstConnectTime;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private SimpleDateFormat firstConnectFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private FactoryMainApi mFactoryMainApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.layout_main, null);
        if (view != null){
            mPreferenceContainer = (PreferenceContainer) view.findViewById(R.id.main_preference_container);
            mPreferenceContainer.setPreferenceItemClickListener(this);
            mPcbInfo = (TextView) view.findViewById(R.id.tv_pcb_info_label);
            Log.d("info", "mPcbInfo is null : " + (mPcbInfo == null));
            mSoftwareVersion = (TextView) view.findViewById(R.id.tv_software_version_label);
            mPanel = (TextView) view.findViewById(R.id.tv_panel_label);
            mCompileTime = (TextView) view.findViewById(R.id.tv_compile_time_label);
            mPraName = (TextView) view.findViewById(R.id.tv_praName_label);
            mProTitle = (TextView) view.findViewById(R.id.pro_title);
            mProName = (TextView) view.findViewById(R.id.tv_proName_label);
            mCusName = (TextView) view.findViewById(R.id.tv_cusName_label);
            mPreVersion = (TextView) view.findViewById(R.id.tv_preVersion_label);
            mFirstConnectTime = (TextView) view.findViewById(R.id.tv_connect_time);

            mFactoryMainApi = FactoryMainApi.getInstance();
            String projectName = getProjectName();
            mPcbInfo.setText(getProcessedProjectName(projectName));
            mPcbInfo.setSelected(true);
            mSoftwareVersion.setText(getProcessedVersion(projectName));
            mPanel.setText(mFactoryMainApi.getPanelType());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            long time = SystemProperties.getLong("ro.build.date.utc", 0) * 1000L;
            String compileTimeText = dateFormat.format(new Date(time));
            String versionTime = getVersionTime();
            mCompileTime.setText(versionTime != null ? versionTime : compileTimeText);
            String preName = SystemProperties.get("ro.build.version.incremental", "unknown");
            mPraName.setText(SystemProperties.get("ro.build.id", "unknown"));
            if (FactoryApplication.CUSTOMER_IS_KONKA) {
                mProTitle.setText(R.string.str_fingerprint);
                String fingerprint = SystemProperties.get("ro.build.fingerprint", "undefined");
                mProName.setText(fingerprint);
                mProName.setSelected(true);
                view.findViewById(R.id.container_ddr).setVisibility(View.VISIBLE);
            } else {
                mProName.setText(SystemProperties.get("ro.product.vendor.manufacturer", "unknown"));
            }
            mCusName.setText(SystemProperties.get("ro.product.model", "unknown"));
            if (preName.length() >= 11) {
                mPreVersion.setText(preName);
            } else {
                mPreVersion.setText(preName+compileTimeText.substring(0,4)+compileTimeText.substring(5,7)+compileTimeText.substring(8,10)+"."+compileTimeText.substring(11,13)+compileTimeText.substring(14,16)+compileTimeText.substring(17,19));
            }
        }
        factoryMenuActivity = (FactoryMenuActivity)getActivity();
        mFactoryMainApi = FactoryMainApi.getInstance();
        return view;
    }

    private String getVersionTime() {
        String version = SystemProperties.get("ro.build.version.incremental", null);
        if (TextUtils.isEmpty(version)) {
            return null;
        }
        String regex = "(?:\\w+[.])+(?<year>\\d{4})(?<month>\\d{2})(?<day>\\d{2})[.](?<hour>\\d{2})(?<minute>\\d{2})(?<second>\\d{2})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(version);
        if (matcher.matches()) {
            String part1 = String.join("-", matcher.group("year"), matcher.group("month"), matcher.group("day"));
            String part2 = String.join(":", matcher.group("hour"), matcher.group("minute"), matcher.group("second"));
            return String.join(" ", part1, part2);
        }
        return null;
    }

    public String getProjectName() {
        try {
            IRtkProjectConfigs configs = IRtkProjectConfigs.getService();
            OptionalString projectName = configs.getProjectName();
            Log.d("MainPage", "getProjectName:" + projectName);
            return projectName.value;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public String getProcessedProjectName(String pgn) {
        if (FactoryApplication.CUSTOMER_IS_BOE) {
            return pgn;
        }
        String[] items = pgn.split("_");
        int index = items.length - 3;
        if (index >= 0) {
            StringBuilder result = new StringBuilder();
            result.append(items[0]);
            for (int i = 1;i < items.length;i++) {
                if (i == index) {
                    continue;
                }
                result.append("_").append(items[i]);
            }
            return result.toString();
        }
        return pgn;
    }

    public String getProcessedVersion(String pgn) {
        if (FactoryApplication.CUSTOMER_IS_BOE) {
            return "V2.0.10";
        }
        String[] items = pgn.split("_");
        int index = items.length - 3;
        if (index >= 0) {
            return items[index];
        }
        return "unknown";
    }

    private String getVersionName(Context context) {
        String verName = "V1.0.3";
        try {
            verName = "V" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verName;
    }

    public int getTotalSize(Context mContext, String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = mContext.getSystemService(StorageStatsManager.class);
            Log.d("MainPageFragment", "size:" + stats.getTotalBytes(id));
            return (int) (stats.getTotalBytes(id) / 1000 / 1000 / 1000);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void showPage(Class<? extends BaseFragment> clazz, int title) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FactoryMenuFragment) {
            FactoryMenuFragment menuFragment = (FactoryMenuFragment) parentFragment;
            menuFragment.showPage(clazz, title);
        }
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
        switch (preference.getId()) {
            case R.id.page_sw_info:
                showPage(SwInfoFragment.class,R.string.str_sw_info);
                break;
            case R.id.select:
                showPage(SelectFragment.class,R.string.str_select_title);
                break;
            case R.id.system_info:
                showPage(SystemInfoFragment.class,R.string.str_system_info);
                break;
            case R.id.page_picture:
                showPage(PicturePageFragment.class, R.string.str_picture);
                break;
            case R.id.page_sound:
                showPage(SoundPageFragment.class, R.string.str_sound);
                break;
            case R.id.page_user:
                showPage(UserPageFragment.class, R.string.str_user);
                break;
            case R.id.page_debug:
                showPage(DeBugPageFragment.class,R.string.str_debug_setting);
                break;
            case R.id.page_others:
                showPage(OthersPageFragment.class, R.string.str_others);
                break;
            case R.id.reset:
                showResetCofirmDialog();
                break;
            case R.id.android_reset:
                showAndroidResetCofirmDialog();
                break;
            case R.id.page_ssc:
                showPage(SSCFragment.class, R.string.str_ssc_title);
                break;
            case R.id.page_ch_default:
                showPage(DefaultFragment.class, R.string.str_ch_default);
                break;
            case R.id.page_oled:
                showPage(OledFragment.class,R.string.str_OLED);
                break;
            default:
                break;
        }
    }


    private void showResetCofirmDialog(){
        AlertDialog dlg = new AlertDialog.Builder(factoryMenuActivity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
            .setTitle(factoryMenuActivity.getResources().getString(R.string.dlg_title_reset))
            .setMessage(factoryMenuActivity.getResources().getString(R.string.dlg_message_reset))
            .setPositiveButton(factoryMenuActivity.getResources().getString(R.string.btn_submit), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mFactoryMainApi.setIntegerValue(TvCommonManager.BACKGROUND_LIGHT, 0);
                    mFactoryMainApi.restoreToDefault();
                }
            })
        .setNegativeButton(factoryMenuActivity.getResources().getString(R.string.btn_cancel), null).create();
         //dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
         dlg.show();
         dlg.getButton(DialogInterface.BUTTON_NEGATIVE).requestFocus();
    }

    private void showAndroidResetCofirmDialog(){
        AlertDialog dlg = new AlertDialog.Builder(factoryMenuActivity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(factoryMenuActivity.getResources().getString(R.string.dlg_title_android_reset))
                .setMessage(factoryMenuActivity.getResources().getString(R.string.dlg_message_reset))
                .setPositiveButton(factoryMenuActivity.getResources().getString(R.string.btn_submit), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFactoryMainApi.androidRestoreToDefault();
                    }
                })
                .setNegativeButton(factoryMenuActivity.getResources().getString(R.string.btn_cancel), null).create();
        dlg.show();
        dlg.getButton(DialogInterface.BUTTON_NEGATIVE).requestFocus();
    }


}
