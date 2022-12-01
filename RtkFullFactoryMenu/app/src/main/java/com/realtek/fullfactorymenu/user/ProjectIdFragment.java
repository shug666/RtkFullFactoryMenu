package com.realtek.fullfactorymenu.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.system.RtkProjectConfigs;
import com.realtek.tv.Factory;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.UserApi;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceContainer.PreferenceContainerConfig;
import com.realtek.fullfactorymenu.preference.PreferenceContainer.PreferenceContainerConfig.*;
import com.realtek.fullfactorymenu.preference.PreferenceContainer.PreferenceContainerConfig.StatePreferenceConfig.Entry;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;

import java.util.ArrayList;
import java.util.List;

public class ProjectIdFragment extends PreferenceFragment implements Handler.Callback {

    public static final String TAG = "ProjectIdFragment";

    private Handler mHandler;
    private UserApi mUserApi;
    private Factory mFactory;
    private ArrayList<ProjectId> mListItems;
    private static final int DIALOG_DELAY_MILLIS = 1000;
    private static final int CHANGE_MODEL_INI = 0;
    private static final String PKG_NAME = "android";
    private static final String SHUTDOWN_INTENT_EXTRA = "shutdown";

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {

        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_user_project_id);
        mHandler = new Handler(this);
        mUserApi = UserApi.getInstance();
        initData();
        PreferenceContainerConfig containerConfig = new PreferenceContainerConfig();
        containerConfig.logicClassName = ProjectIdLogic.class.getName();

        int index = 0;
        for (ProjectId mListItem : mListItems) {
            StatePreferenceConfig config = new StatePreferenceConfig();
            config.index = 0;
            config.cycleEnabled = false;
            // SumaryPreferenceConfig config = new SumaryPreferenceConfig();
            config.title = ++index + " " + mListItem.getProjectIdName();
            config.tag = mListItem;
            config.visible = true;
            Entry entry = new Entry();
            entry.index = 0;
            entry.enabled = true;
            if (mListItem.getProjectIdSelect()) {
                // config.sumary = "select";
                entry.name = "select";
            } else {
                entry.name = "";
            }
            config.entries.add(entry);
            containerConfig.preferenceConfigs.add(config);
        }
        PreferenceContainer preferenceContainer = builder.create();
        preferenceContainer.inflatePreference(containerConfig);
        return preferenceContainer;
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
        Object tag = preference.getTag();
        if (mHandler.hasMessages(CHANGE_MODEL_INI)) {
            mHandler.removeMessages(CHANGE_MODEL_INI);
        }
        Message msg = new Message();
        msg.what = CHANGE_MODEL_INI;
        msg.obj = tag;
        mHandler.sendMessageDelayed(msg, DIALOG_DELAY_MILLIS);
    }

    private void initData() {
        boolean isAddProject = false;
        int projectMaxId = mUserApi.getProjectMaxId();
        List<String> projectIniList = mUserApi.getProjectIniList();
        List<Integer> projectIdList = new ArrayList<>();
        int currentProjectId = mUserApi.getCurrentProjectId();
        Log.d(TAG, "currentProjectId: " + currentProjectId);
        for (int i = 0; i < projectMaxId; i++) {
            projectIdList.add(i + 1);
        }
       /* for (String s : projectIniList) {
            Log.d(TAG, "projectIniList: " + s);
        }
        for (Integer integer : projectIdList) {
            Log.d(TAG, "projectIdList: " + integer);
        }*/
        int minSize = Math.min(projectIniList.size(), projectIdList.size());
        mListItems = new ArrayList<>();

        if (!FactoryApplication.CUSTOMER_IS_BOE){
            for (int i = 0; i < minSize; i++) {
                if (i != (currentProjectId - 1)) {
                    mListItems.add(new ProjectId(projectIniList.get(i), projectIdList.get(i), false));
                } else {
                    mListItems.add(new ProjectId(projectIniList.get(i), projectIdList.get(i), true));
                }
            }
            return;
        }

        for (int i = 0; i < minSize; i++) {
            isAddProject = projectIniList.get(i).matches("^(?i)(.*)" + Build.BRAND + "(.*)$");
            if (isAddProject){
                if (i != (currentProjectId - 1)) {
                    mListItems.add(new ProjectId(projectIniList.get(i), projectIdList.get(i), false));
                } else {
                    mListItems.add(new ProjectId(projectIniList.get(i), projectIdList.get(i), true));
                }
            }
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case CHANGE_MODEL_INI:
                ProjectId projectId = (ProjectId) msg.obj;
                showSelectProjectConfirmDialog(projectId);
                break;
        }
        return false;
    }

    public static class ProjectId {

        private String projectIdName;

        private Integer projectIdNum;

        private Boolean projectIdSelect;


        public ProjectId(String projectIdName, Integer projectIdNum, Boolean projectIdSelect) {
            this.projectIdName = projectIdName;
            this.projectIdNum = projectIdNum;
            this.projectIdSelect = projectIdSelect;
        }

        public Boolean getProjectIdSelect() {
            return projectIdSelect;
        }

        public void setProjectIdSelect(Boolean projectIdSelect) {
            this.projectIdSelect = projectIdSelect;
        }

        public String getProjectIdName() {
            return projectIdName;
        }

        public void setProjectIdName(String projectIdName) {
            this.projectIdName = projectIdName;
        }

        public Integer getProjectIdNum() {
            return projectIdNum;
        }

        public void setProjectIdNum(Integer projectIdNum) {
            this.projectIdNum = projectIdNum;
        }

    }


    private void showSelectProjectConfirmDialog(ProjectId projectId) {
        final String dialogMsg = getResources().getString(R.string.select_project_id_dialog_msg) + projectId.getProjectIdNum();
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setMessage(dialogMsg)
                .setPositiveButton(R.string.btn_submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.d(TAG, "onClick: selectedItemPosition = " + projectId.getProjectIdNum());
                        final int projectID = projectId.getProjectIdNum();
                        boolean setSuccess = mUserApi.setProjectId(projectID);
                        Log.d(TAG, "setProjectId: " + projectID + " result: " + setSuccess);
                        if (setSuccess) {
                            applyPidConfig();
                            final String oemImageName = RtkProjectConfigs.getInstance().getOemImageName();
                            Intent resetIntent = getResetIntent(oemImageName);
                            getActivity().sendBroadcast(resetIntent);
                        } else {
                            Log.d(TAG, "setSuccess: " + setSuccess);
                        }
                    }
                }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.gray));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).requestFocus();
    }

    private void applyPidConfig() {
        if (mFactory == null) {
            mFactory = new Factory();
        }
        Factory.PidChgConfig mConfig = mFactory.new PidChgConfig();
        mConfig.applyPanelSetting = true;
        mConfig.applyIRSetting = true;
        mConfig.applyBootAnimationSetting = true;
        mConfig.applyBootlogoSetting = true;
        mConfig.applyAmpSetting = true;
        mConfig.applyTunerSelectSetting = true;
        mFactory.applyPidChg(mConfig);
    }

    private Intent getResetIntent(String oemImageName) {
        Log.d(TAG, "getResetIntent : oemImageName = " + oemImageName);
        Intent intent = new Intent(Intent.ACTION_FACTORY_RESET);
        intent.setPackage(PKG_NAME);
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra(Intent.EXTRA_REASON, oemImageName);
        if (getActivity().getIntent().getBooleanExtra(SHUTDOWN_INTENT_EXTRA, false)) {
            intent.putExtra(SHUTDOWN_INTENT_EXTRA, true);
        }
        return intent;
    }
}
