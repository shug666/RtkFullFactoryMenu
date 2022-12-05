package com.realtek.fullfactorymenu.systemInfo;

import static com.realtek.fullfactorymenu.systemInfo.SystemInfoLogic.*;

import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;
import com.realtek.fullfactorymenu.utils.Tools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;

import java.util.List;

public class SystemInfoFragment extends PreferenceFragment {

    private SystemInfoLogic mSystemInfoLogic;

    private static final String PATH_MAC;
    private static final String PATH_OEM;
    private static final String PATH_NETFLIX_ESN;
    private static final String PATH_PLAYREADY;
    private static final String PATH_HDCP;
    private static final String PATH_HDCP22;
    private static final String PATH_WIDEVINE;
    private static final String PATH_CI_KEY;
    private static final String PATH_ATTESTATION_KEY;
    private static final String PATH_RMCA;

    static {
        if (FactoryApplication.CUSTOMER_IS_CH) {
            PATH_MAC = "/CH_SYSTEM_KEYS/MAC";
            PATH_OEM = "/CH_SYSTEM_KEYS/OEM_KEY";
            PATH_NETFLIX_ESN = "/CH_SYSTEM_KEYS/NETFLIX";
            PATH_PLAYREADY = "/CH_SYSTEM_KEYS/PLAYREADY";
            PATH_HDCP = "/CH_SYSTEM_KEYS/HDCP14";
            PATH_HDCP22 = "/CH_SYSTEM_KEYS/HDCP";
            PATH_WIDEVINE = "/CH_SYSTEM_KEYS/WIDEVINE";
            PATH_CI_KEY = "/CH_SYSTEM_KEYS/CIPLUS";
            PATH_ATTESTATION_KEY = "/CH_SYSTEM_KEYS/ATT";
            PATH_RMCA = "/CH_SYSTEM_KEYS/";
        } else {
            PATH_MAC = "/MAC";
            PATH_OEM = "/OEM_KEY";
            PATH_NETFLIX_ESN = "/ESN";
            PATH_PLAYREADY = "/PLAYREADY";
            PATH_HDCP = "/HDCP1.4";
            PATH_HDCP22 = "/HDCP2.2";
            PATH_WIDEVINE = "/WIDEVINE";
            PATH_CI_KEY = "/CIPLUS";
            PATH_ATTESTATION_KEY = "/ATT";
            PATH_RMCA = "/";
        }
    }

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_system_info);
        return builder.create();
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
        if (preference.getId() == R.id.check_network) {
            mSystemInfoLogic = (SystemInfoLogic) mPreferenceContainer.getPreferenceLogic(R.id.upgrede_mac);
            mSystemInfoLogic.checkNetwork(getContext());
            return;
        }
        if (preference.getId() == R.id.upgrede_mac_manual) {
            ComponentName name = new ComponentName("com.realtek.fullfactorymenu", "com.realtek.fullfactorymenu.systemInfo.InputMacActivity");
            Intent intent = new Intent();
            intent.setComponent(name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
            return;
        }

        String initUsb = initUsbList();
        if (initUsb == null) {
            Tools.showDialog(getActivity(), getString(R.string.str_error), getString(R.string.str_cannot_find));
            return;
        }

        switch (preference.getId()) {
        case R.id.upgrede_all_key:
            sendSyncCommand(CMD_UPGRADE_MAC, initUsb + PATH_MAC);
            sendSyncCommand(CMD_UPGRADE_OEM, initUsb + PATH_OEM);
            sendSyncCommand(CMD_UPGRADE_Netflix_ESN, initUsb + PATH_NETFLIX_ESN);
            sendSyncCommand(CMD_UPGRADE_PLAYREADY, initUsb + PATH_PLAYREADY);
            sendSyncCommand(CMD_UPGRADE_HDCP, initUsb + PATH_HDCP);
            sendSyncCommand(CMD_UPGRADE_HDCP22, initUsb + PATH_HDCP22);
            sendSyncCommand(CMD_UPGRADE_WIDEVINE, initUsb + PATH_WIDEVINE);
            sendSyncCommand(CMD_UPGRADE_CI_KEY, initUsb + PATH_CI_KEY);
            sendSyncCommand(CMD_UPGRADE_Attestation, initUsb + PATH_ATTESTATION_KEY);
            sendSyncCommand(CMD_UPGRADE_RMCA, initUsb + PATH_RMCA);
            break;
        case R.id.upgrede_mac:
            sendSyncCommand(CMD_UPGRADE_MAC, initUsb + PATH_MAC);
            break;
        case R.id.upgrade_oem:
            sendSyncCommand(CMD_UPGRADE_OEM, initUsb + PATH_OEM);
            break;
        case R.id.upgrade_netflix_esn:
            sendSyncCommand(CMD_UPGRADE_Netflix_ESN, initUsb + PATH_NETFLIX_ESN);
            break;
        case R.id.upgrade_playready:
            sendSyncCommand(CMD_UPGRADE_PLAYREADY, initUsb + PATH_PLAYREADY);
            break;
        case R.id.upgrade_hdcp:
            sendSyncCommand(CMD_UPGRADE_HDCP, initUsb + PATH_HDCP);
            break;
        case R.id.upgrade_hdcp2:
            sendSyncCommand(CMD_UPGRADE_HDCP22, initUsb + PATH_HDCP22);
            break;
        case R.id.upgrade_widevine:
            sendSyncCommand(CMD_UPGRADE_WIDEVINE, initUsb + PATH_WIDEVINE);
            break;
        case R.id.upgrade_ci:
            sendSyncCommand(CMD_UPGRADE_CI_KEY, initUsb + PATH_CI_KEY);
            break;
        case R.id.upgrade_attestation:
            sendSyncCommand(CMD_UPGRADE_Attestation, initUsb + PATH_ATTESTATION_KEY);
            break;
        case R.id.upgrade_rmca:
            sendSyncCommand(CMD_UPGRADE_RMCA, initUsb + PATH_RMCA);
            break;
        case R.id.upgrade_pq:
            sendSyncCommand(CMD_UPGRADE_PQ, initUsb);
            break;
        case R.id.upgrade_bootlogo:
            sendSyncCommand(CMD_UPGRADE_BOOTLOGO, initUsb);
            break;
        default:
            break;
        }
    }

    private String initUsbList() {
        StorageManager storageManager = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
        List<VolumeInfo> volumes = storageManager.getVolumes();
        int count = volumes == null ? 0 : volumes.size();
        VolumeInfo volume = null;
        for (int i = 0; i < count; i++) {
            volume = volumes.get(i);
            if (volume.disk != null && (volume.disk.isUsb() || volume.disk.isSd())) {
                if (volume.state != VolumeInfo.STATE_MOUNTED) {
                    continue;
                }
                return volume.path;
            }
        }
        return null;
    }

}
