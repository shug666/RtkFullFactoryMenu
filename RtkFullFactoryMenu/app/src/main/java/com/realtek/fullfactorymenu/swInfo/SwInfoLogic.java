package com.realtek.fullfactorymenu.swInfo;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.FactoryMainApi;
import com.realtek.fullfactorymenu.api.impl.UpgradeApi;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.preference.SumaryPreference;
import com.realtek.fullfactorymenu.utils.TvInputUtils;
import com.realtek.fullfactorymenu.utils.Utils;
import com.realtek.system.RtkProjectConfigs;
import com.realtek.tv.Factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vendor.realtek.rtkconfigs.V1_0.IRtkProjectConfigs;
import vendor.realtek.rtkconfigs.V1_0.OptionalString;

public class SwInfoLogic extends LogicInterface {


    private final String TAG = "SwInfoLogic";
    private UpgradeApi mUpgradeApi;
    private Factory mFactory;
    private SumaryPreference mPqMd5;
    private SumaryPreference mPqHdrMd5;
    private SumaryPreference mPqOsdMd5;
    private SumaryPreference mDvMd5;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private RtkProjectConfigs mRtkProjectConfigs;

    public SwInfoLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        SumaryPreference project = (SumaryPreference) mContainer.findPreferenceById(R.id.display_project_id);
        SumaryPreference panel = (SumaryPreference) mContainer.findPreferenceById(R.id.display_panel);
        SumaryPreference macAddress = (SumaryPreference) mContainer.findPreferenceById(R.id.display_mac_address);
        SumaryPreference deviceId = (SumaryPreference) mContainer.findPreferenceById(R.id.display_device_id);
        SumaryPreference hdcpKey1 = (SumaryPreference) mContainer.findPreferenceById(R.id.display_hdcp_key1);
        SumaryPreference hdcpKey2 = (SumaryPreference) mContainer.findPreferenceById(R.id.display_hdcp_key2);
        SumaryPreference widevineKey = (SumaryPreference) mContainer.findPreferenceById(R.id.display_widevine_key);
        SumaryPreference attKey = (SumaryPreference) mContainer.findPreferenceById(R.id.display_att_key);
        SumaryPreference mgkidKey = (SumaryPreference) mContainer.findPreferenceById(R.id.display_mgkid_key);
        SumaryPreference playreadyKey = (SumaryPreference) mContainer.findPreferenceById(R.id.display_playready_key);
        SumaryPreference cikey = (SumaryPreference) mContainer.findPreferenceById(R.id.display_ci_key);
        SumaryPreference oemKey = (SumaryPreference) mContainer.findPreferenceById(R.id.display_oem_key);
        SumaryPreference rmcaKey = (SumaryPreference) mContainer.findPreferenceById(R.id.display_rmca_key);
        SumaryPreference googleSn = (SumaryPreference) mContainer.findPreferenceById(R.id.display_google_sn);
        SumaryPreference Ip = (SumaryPreference) mContainer.findPreferenceById(R.id.display_ip);
        mPqMd5 = (SumaryPreference) mContainer.findPreferenceById(R.id.display_pq_md5);
        mPqHdrMd5 = (SumaryPreference) mContainer.findPreferenceById(R.id.display_pq_hdr_md5);
        mPqOsdMd5 = (SumaryPreference) mContainer.findPreferenceById(R.id.display_pq_osd_md5);
        mDvMd5 = (SumaryPreference) mContainer.findPreferenceById(R.id.display_dv_md5);
        SumaryPreference pqVersion = (SumaryPreference) mContainer.findPreferenceById(R.id.display_pq_version);
        SumaryPreference aqVersion = (SumaryPreference) mContainer.findPreferenceById(R.id.display_aq_version);
        SumaryPreference swVersion = (SumaryPreference) mContainer.findPreferenceById(R.id.display_sw_version);
        SumaryPreference builtTime = (SumaryPreference) mContainer.findPreferenceById(R.id.display_built_time);
        SumaryPreference tvConfigVersion = (SumaryPreference) mContainer.findPreferenceById(R.id.display_tvConfig_version);
        SumaryPreference tvWorkTime = (SumaryPreference) mContainer.findPreferenceById(R.id.display_tv_work_time);

        mRtkProjectConfigs = RtkProjectConfigs.getInstance();
        mUpgradeApi = UpgradeApi.getInstance();
        mFactory = FactoryApplication.getInstance().getFactory();

        project.setSumary(getProjectId());
        panel.setSumary(getPanelType());
//        panel.setSumary(getPanel());
        String eth0Address = Utils.getMACAddress("eth0");
        macAddress.setSumary(eth0Address.equals("") ? "NG" : eth0Address);
        hdcpKey1.setSumary(getKeyState(UpgradeApi.KEYS_HDCP1_TYPE));
        hdcpKey2.setSumary(getKeyState(UpgradeApi.KEYS_HDCP2_TYPE));
        widevineKey.setSumary(getKeyState(UpgradeApi.KEYS_WIDEVINE_TYPE));
        attKey.setSumary(getKeyState(UpgradeApi.KEYS_ATTESTATION_TYPE));
        mgkidKey.setSumary(getKeyState(UpgradeApi.KEYS_Netflix_ESN_TYPE));
        playreadyKey.setSumary(getKeyState(UpgradeApi.KEYS_PLAYREADY_TYPE));
        cikey.setSumary(getKeyState(UpgradeApi.KEYS_CIKEY_TYPE));
        oemKey.setSumary(FactoryApplication.getInstance().getFactory().getOEMKey());
        rmcaKey.setSumary(getKeyToString("mnt/vendor/factory/RMCA"));
        googleSn.setSumary(getGoogleSn().trim());
        String localIp = Utils.getLocalIp(mContext);
        Ip.setSumary(localIp.equals("F") ? mContext.getResources().getString(R.string.str_system_info_IP) : localIp);
        initMD5();

        swVersion.setSumary(getSwVersion());
        builtTime.setSumary(getBuiltTime());
        tvConfigVersion.setSumary(getTvConfigVersion());


        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            if (TvInputUtils.isISDB()){
                cikey.setVisibility(View.GONE);
            }
            String resolutionSupport = SystemProperties.get("ro.hdmi.resolution");
            if (!resolutionSupport.equals("2K")) return;
            if ((TvInputUtils.isDVB() || TvInputUtils.isISDB())){
                widevineKey.setVisibility(View.GONE);
            }
        }
    }

    public String getPanelType() {
        String m_pPanelName = mRtkProjectConfigs.getConfig("[PANEL]", "m_pPanelName");
        File panelFile = new File(m_pPanelName.trim());
        return panelFile.getName();
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

    private String getTvConfigVersion() {
        String pgn = getProjectName();

        if (FactoryApplication.CUSTOMER_IS_BVT) {
            return pgn;
        }
        String[] items = pgn.split("_");
        int length = items.length;
        if ((length -3) >= 0) {
            StringBuilder day = new StringBuilder(items[length-2]).insert(4,"-").insert(7,"-").append(" ");
            StringBuilder time = new StringBuilder(items[length-1]).insert(2,":").insert(5,":");
            return String.valueOf(day.append(time));
        }
        return pgn;
    }

    private String getBuiltTime(){
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        long time = SystemProperties.getLong("ro.build.date.utc", 0) * 1000l;
        String compileTimeText = dateFormat.format(new Date(time));
        String versionTime = getVersionTime();
        return versionTime != null ? versionTime : compileTimeText;
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

    private String getSwVersion(){
        String proName = getProjectName();

        if (FactoryApplication.CUSTOMER_IS_BVT) {
            return "V2.0.10";
        }
        String[] items = proName.split("_");
        int index = items.length - 3;
        if (index >= 0) {
            return items[index];
        }
        return "unknown";
    }

    private void initMD5() {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (messageDigest == null) {
            return;
        }
        String pq = mRtkProjectConfigs.getConfig("[MISC_PQ_MAP_CFG]", "PQ");
        String pq_hdr = mRtkProjectConfigs.getConfig("[MISC_PQ_MAP_CFG]", "PQ_HDR");
        String pq_osd = mRtkProjectConfigs.getConfig("[MISC_PQ_MAP_CFG]", "PQ_OSD");
        String dv = mRtkProjectConfigs.getConfig("[MISC_PQ_MAP_CFG]", "DV");
        StringBuilder builder = new StringBuilder();
        mPqMd5.setSumary(byteToString(builder, getMd5(messageDigest, pq)));
        mPqHdrMd5.setSumary(byteToString(builder, getMd5(messageDigest, pq_hdr)));
        mPqOsdMd5.setSumary(byteToString(builder, getMd5(messageDigest, pq_osd)));
        mDvMd5.setSumary(byteToString(builder, getMd5(messageDigest, dv)));
    }

    private String byteToString(StringBuilder builder, byte[] data) {
        if (data == null) {
            return "File not exist or can not read.";
        }
        builder.setLength(0);
        for (byte aByte : data) {
            builder.append(String.format("%02X", aByte));
        }
        return builder.toString();
    }

    private byte[] getMd5(MessageDigest md5, String path) {
        File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            return null;
        }
        byte[] bytesFromKey = null;
        try {
            bytesFromKey = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bytesFromKey == null || bytesFromKey.length == 0) {
            return null;
        }
        md5.update(bytesFromKey);
        return md5.digest();
    }

//    public String getNetworkIP(Context context) {
//        if (context != null) {
//            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
//            if (mNetworkInfo != null) {
//                return Utils.getLocalIp(mContext);
//            }
//        }
//        return "NG";
//    }

    private String getGoogleSn(){
        String serialKey = null;
        try {
            serialKey = mFactory.getSerialKey();
        } catch (NoSuchMethodError | NullPointerException e) {
            e.printStackTrace();
            return "NG";
        }
        if (serialKey == null || serialKey.isEmpty()){
            return "NG";
        }
        return serialKey;
    }

    private String getKeyState(int type) {
        String serialNumber = mUpgradeApi.getSerialNumber(type);
        if (serialNumber == null || serialNumber.isEmpty()) {
            return "NG";
        }

        Log.d(TAG,"serialNumber = "+serialNumber);
        return "OK";
    }

    private String getKeyToString(String fullPath) {
        byte[] bytes = getBytesFromKey(fullPath, 0);
        if (bytes == null || bytes.length == 0) {
            return "NG";
        }
        return "OK";
    }

    private byte[] getBytesFromKey(String path, int size) {
        File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            return null;
        }
        byte[] bytes;
        if (size <= 0) {
            int length = (int) file.length();
            bytes = new byte[length];
        } else {
            bytes = new byte[size];
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private String getPanel(){
        byte[] bytes = mFactory.readConfigFile("/mnt/vendor/factory/env.txt");

        if (bytes == null || bytes.length == 0){
            return null;
        }
        Matcher matcher = Pattern.compile("panel_file_name=(.*).h").matcher(new String(bytes));
        if (!matcher.find()){
            return null;
        }
        String[] splitStr = matcher.group().split("=");
        if (splitStr.length != 2 ){
            return null;
        }
       return splitStr[1];
    }

    private String getProjectId(){
        int projectId = mRtkProjectConfigs.getProjectId();
        List<String> projectIniList = mRtkProjectConfigs.getProjectIniList();
        for (int i = 0; i < projectIniList.size(); i++) {
            if (i == (projectId - 1)){
                return projectIniList.get(i);
            }
        }
        return "unknow";
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {

    }
}
