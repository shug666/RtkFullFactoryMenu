package com.realtek.tvfactory.designMode;

import static com.realtek.tvfactory.api.impl.UpgradeApi.KEYS_ATTESTATION_TYPE;
import static com.realtek.tvfactory.api.impl.UpgradeApi.KEYS_CIKEY_TYPE;
import static com.realtek.tvfactory.api.impl.UpgradeApi.KEYS_HDCP1_TYPE;
import static com.realtek.tvfactory.api.impl.UpgradeApi.KEYS_HDCP2_TYPE;
import static com.realtek.tvfactory.api.impl.UpgradeApi.KEYS_MAC_TYPE;
import static com.realtek.tvfactory.api.impl.UpgradeApi.KEYS_Netflix_ESN_TYPE;
import static com.realtek.tvfactory.api.impl.UpgradeApi.KEYS_OEM_KEY;
import static com.realtek.tvfactory.api.impl.UpgradeApi.KEYS_PLAYREADY_TYPE;
import static com.realtek.tvfactory.api.impl.UpgradeApi.KEYS_WIDEVINE_TYPE;
import static com.realtek.tvfactory.api.impl.UpgradeApi.NEED_TO_BURN_CKD_KEYS;

import android.app.Activity;
import android.app.usage.StorageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.realtek.system.RtkProjectConfigs;
import com.realtek.tv.Factory;
import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.UpgradeApi;
import com.realtek.tvfactory.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import vendor.realtek.rtkconfigs.V1_0.IRtkProjectConfigs;
import vendor.realtek.rtkconfigs.V1_0.OptionalString;

public class MModeActivity extends Activity implements OnItemClickListener {
    private static final String TAG = "MModeActivity";

    private final OptionAdapter optionAdapter = new OptionAdapter();
    private Handler handler;
    private ListView listView;
    private TextView macAddr;
    private TextView hdcpKey;
    private TextView hdcp22Key;
    private TextView esn;
    private TextView autoBurnState;
    private int ethMacIndex = -1;
    private int burnInIndex = -1;
    private boolean isBurning = false;
    private final Map<Integer, Boolean> allCheck = new HashMap<>();
    private UpgradeApi mUpgradeApi;
    private Factory mFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmode);
        handler = new Handler();
        mUpgradeApi = UpgradeApi.getInstance();
        mFactory = FactoryApplication.getInstance().getFactory();
        initUI();
        handler.post(()->doAutoBurn(false));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown keyCode:" + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    public static String getProjectName() {
        try {
            IRtkProjectConfigs configs = IRtkProjectConfigs.getService();
            OptionalString projectName = configs.getProjectName();
            return projectName.value;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public String getProcessedVersion() {
        String pgn = getProjectName();
        if (pgn == null) {
            return "unknown";
        }
        String[] items = pgn.split("_");
        int index = items.length - 3;
        if (index >= 0) {
            return items[index];
        }
        return "unknown";
    }

    private void initUI() {
        listView = findViewById(R.id.list);
        TextView mode = findViewById(R.id.mode);

        macAddr = findViewById(R.id.mac_addr);
        hdcpKey = findViewById(R.id.hdcp_key);
        hdcp22Key = findViewById(R.id.hdcp2_2_key);
        esn = findViewById(R.id.netflix_esn);
        autoBurnState = findViewById(R.id.state);

        listView.setAdapter(optionAdapter);
        listView.setOnItemClickListener(this);

        String modeName = SystemProperties.get("ro.product.model", "ENGINEERING");
        mode.setText(modeName);
        initData();
    }

    private void initData() {
        List<Options> options = new ArrayList<>();
        int startId = 0;
        boolean state;
        options.add(new Options(startId++, "SW Version", getProcessedVersion(), R.drawable.ic_check));
        String panel = getPanelType();
        options.add(new Options(startId++, "Panel Name", panel, R.drawable.ic_check));
        options.add(new Options(startId++, "Build Time", getCompileTime(), R.drawable.ic_check));
        options.add(new Options(startId++, "DDR/EMMC", getDDREMMC(MModeActivity.this), R.drawable.ic_check));
        String wifiMAC = Utils.getMACAddress("wlan0");
        state = !TextUtils.isEmpty(wifiMAC);
        options.add(new Options(startId++, "WIFI MAC Address", wifiMAC, stateToInteger(state)));
        String ethMAC = Utils.getMACAddress("eth0");
        state = !TextUtils.isEmpty(ethMAC);
        ethMacIndex = startId;
        options.add(new Options(startId++, "Ethernet MAC Address", ethMAC, stateToInteger(state)));
        String oem = mFactory.getOEMKey();
        state = !TextUtils.isEmpty(oem);
        options.add(new Options(startId++, "OEM Key", oem, stateToInteger(state)));

        burnInIndex = startId;
        String key = getKeyState(KEYS_MAC_TYPE);
        state = !"NG".equals(key);
        allCheck.put(startId, state);
        options.add(new Options(startId++, "MAC Key", state ? key : "NG", stateToInteger(state)));

        key = getKeyState(KEYS_HDCP1_TYPE);
        state = !"NG".equals(key);
        allCheck.put(startId, state);
        options.add(new Options(startId++, "HDCP1.4 Key", state ? key : "NG", stateToInteger(state)));

        key = getKeyState(KEYS_HDCP2_TYPE);
        state = !"NG".equals(key);
        allCheck.put(startId, state);
        options.add(new Options(startId++, "HDCP2.2 Key", state ? key : "NG", stateToInteger(state)));

        key = getKeyState(KEYS_CIKEY_TYPE);
        state = !"NG".equals(key);
        allCheck.put(startId, state);
        options.add(new Options(startId++, "CI+ Key", state ? key : "NG", stateToInteger(state)));

        key = getKeyState(KEYS_WIDEVINE_TYPE);
        state = !"NG".equals(key);
        allCheck.put(startId, state);
        options.add(new Options(startId++, "WideVine Key", state ? key : "NG", stateToInteger(state)));

        key = getKeyState(KEYS_ATTESTATION_TYPE);
        state = !"NG".equals(key);
        allCheck.put(startId, state);
        options.add(new Options(startId++, "Attestation Key", state ? key : "NG", stateToInteger(state)));

        key = getKeyState(KEYS_PLAYREADY_TYPE);
        state = !"NG".equals(key);
        allCheck.put(startId, state);
        options.add(new Options(startId++, "Playready Key", state ? key : "NG", stateToInteger(state)));

        key = getKeyState(KEYS_Netflix_ESN_TYPE);
        state = !"NG".equals(key);
        allCheck.put(startId, state);
        options.add(new Options(startId++, "Netflix ESN Key", state ? key : "NG", stateToInteger(state)));

        key = getKeyToString("mnt/vendor/factory/RMCA");
        state = !TextUtils.isEmpty(key);
        allCheck.put(startId, state);
        options.add(new Options(startId, "RMCA Key", state ? "RMCA" : "NG", stateToInteger(state)));

        optionAdapter.updateAll(options);
    }

    private void updateOptions(int id, boolean state) {
        allCheck.put(id, state);
        String current = autoBurnState.getText().toString();
        if (state && "OK".equals(current)) {
            return;
        }
        if (!state && "NG".equals(current)) {
            return;
        }
        verifyAllOptions();
    }

    private void verifyAllOptions() {
        Set<Integer> integers = allCheck.keySet();
        boolean result = true;
        for (int id : integers) {
            Boolean aBoolean = allCheck.get(id);
            Log.d(TAG, "check item " +id + " result is " + aBoolean);
            if (!aBoolean) {
                result = false;
                break;
            }
        }
        autoBurnState.setText(result ? "OK" : "NG");
        autoBurnState.setTextColor(result ? Color.GREEN : Color.RED);
    }

    private String getDDREMMC(Context context) {
        StorageStatsManager statsManager = context.getSystemService(StorageStatsManager.class);
        String totalSize = "-";
        try {
            totalSize = statsManager.getTotalBytes(StorageManager.UUID_DEFAULT) / 1000 / 1000 / 1000 + "GB";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getTotalRam(context) + "/" + totalSize;
    }

    public static String getTotalRam(Context context){
        String path = "/proc/meminfo";
        String firstLine = null;
        String totalRam = "0GB";
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader,8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(firstLine != null){
            int size = Integer.parseInt(firstLine);
            if (size <= 1024 * 1024) {
                return "1GB";
            } else if (size <= 1.5 * 1024 * 1024) {
                return "1.5GB";
            } else if (size <= 2 * 1024 * 1024) {
                return "2GB";
            } else {
                totalRam = String.format(Locale.getDefault(), "%.1fGB", Float.parseFloat(firstLine) / 1024 / 1024);
            }
        }
        return totalRam;
    }

    private String getCompileTime() {
        long time = SystemProperties.getLong("ro.build.date.utc", 0) * 1000L;
        if (time == 0) {
            return "unknown";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return dateFormat.format(new Date(time));
    }

    public String getPanelType() {
        String m_pPanelName = RtkProjectConfigs.getInstance().getConfig("[PANEL]", "m_pPanelName");
        Log.d(TAG, "getPanelType:" + m_pPanelName);
        File panelFile = new File(m_pPanelName.trim());
        return panelFile.getName();
    }

    private String getKeyState(int type) {
        String serialNumber = mUpgradeApi.getSerialNumber(type);
        if (serialNumber == null || serialNumber.isEmpty()) {
            return "NG";
        }

        Log.d(TAG,"serialNumber = "+serialNumber);
        return serialNumber;
    }

    private void initRight() {
        String mac = Utils.getMACAddress("eth0");
        macAddr.setText(mac);
        updateItemById(ethMacIndex, mac, stateToInteger(!TextUtils.isEmpty(mac)));
        hdcpKey.setText(getKeyToString("/mnt/vendor/factory/hdcp_key.bin"));
        hdcp22Key.setText(getKeyToString("/mnt/vendor/factory/hdcp_key2.0.bin"));
        loadNetflixEsn(MModeActivity.this);
    }

    private void doAutoBurn(final boolean cover) {
        if (isBurning) {
            if (cover) {
                Toast.makeText(MModeActivity.this, "Burning in, please operate later", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "doAutoBrun, is burning do return!");
            return;
        }
        if (mUpgradeApi.haveCKDKey() && !mUpgradeApi.isAllCkdKeyBurned()) {
            if (!mFactory.isCkdKeyBurned(mUpgradeApi.getCKDKeyIndex(KEYS_OEM_KEY))) {
                String serialNumber = mUpgradeApi.getSerialNumber(KEYS_OEM_KEY);
                updateItemById(burnInIndex - 1, serialNumber, stateToInteger(false));
            }
            isBurning = true;
            Log.d(TAG, "autoCKDKeyBurn start!");
            // initRight();
            new Thread(() -> {
                int position = burnInIndex;
                for (int type : NEED_TO_BURN_CKD_KEYS) {
                    boolean ret = mUpgradeApi.autoCKDKeyDetectAndBurn(type);
                    if (type == KEYS_OEM_KEY) {
                        final int position_tmp = burnInIndex - 1;
                        String serialNumber = FactoryApplication.getInstance().getFactory().getOEMKey();
                        runOnUiThread(()-> updateItemById(position_tmp, serialNumber, stateToInteger(ret)));
                    } else {
                        final int position_tmp = position;
                        runOnUiThread(() -> updateItemById(position_tmp, ret ? "OK" : "NG", stateToInteger(ret)));
                        position++;
                    }
                }
                runOnUiThread(this::initRight);
                isBurning = false;
                Log.d(TAG, "autoCKDKeyBurn compile!");
            }).start();
        } else {
            Log.e(TAG, "No CKD Key or All CKD Key have been burned do return!");
            runOnUiThread(this::initRight);
        }
    }

    private void loadNetflixEsn(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.netflix.ninja.intent.action.ESN_RESPONSE");
        context.registerReceiver(netflixESNReceiver, filter);

        Intent esnQueryIntent = new Intent("com.netflix.ninja.intent.action.ESN");
        esnQueryIntent.setPackage("com.netflix.ninja");
        esnQueryIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(esnQueryIntent);
    }

    final private BroadcastReceiver netflixESNReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("ESNValue")) {
                String esnValue = intent.getStringExtra("ESNValue");
                if (esnValue != null) {
                    Log.d(TAG, "ESN:" + esnValue);
                    esn.setText(esnValue);
                }
                context.unregisterReceiver(netflixESNReceiver);
            }
        }
    };

    private int stateToInteger(boolean state) {
        if (state) {
            return R.drawable.ic_check;
        }
        return R.drawable.ic_warming;
    }

    private String getKeyToString(String fullPath) {
        byte[] bytes = getBytesFromKey(fullPath, 100);
        if (bytes != null && bytes.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (byte aByte : bytes) {
                builder.append(String.format("%02X ", aByte));
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    private boolean updateItemById(int id, String value, int imgId) {
        updateOptions(id, imgId != R.drawable.ic_warming);
        int childCount = listView.getChildCount();
        int first = listView.getFirstVisiblePosition();
        optionAdapter.updateOptionById(id, value, imgId);
        if (first > 0) {
            id = id - first;
        }
        if (id < 0 || id > childCount + first) {
            Log.e(TAG, "childCount:" + childCount + " id:" + id);
            return true;
        }
        View childAt = listView.getChildAt(id);
        if (childAt == null) {
            Log.e(TAG, "getChildAt:" + id + " is null");
            return false;
        }
        Log.d(TAG, String.format("updateItemById first:%d id:%d value:%s imageId:%d", first, id, value, imgId));
        optionAdapter.updateStateById(childAt, value, imgId);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, String.format("onItemClick position:%d id:%d", position, id));
        int keypadIndex = -1;
        if (id == keypadIndex + 1) {
            updateItemById(keypadIndex, "NG", stateToInteger(false));
        }
        // int needBurnType = -1;
        // if (position == burnInIndex) {
        //     needBurnType = KEYS_MAC_TYPE;
        // } else if (position == burnInIndex + 1) {
        //     needBurnType = KEY_HDCP_KEY;
        // } else if (position == burnInIndex + 2) {
        //     needBurnType = KEY_HDCP2_2_KEY;
        // } else if (position == burnInIndex + 3) {
        //     needBurnType = KEYS_CIKEY_TYPE;
        // } else if (position == burnInIndex + 4) {
        //     needBurnType = KEYS_WIDEVINE_TYPE;
        // } else if (position == burnInIndex + 5) {
        //     needBurnType = KEYS_PLAYREADY_TYPE;
        // } else if (position == burnInIndex + 6) {
        //     needBurnType = KEYS_ATTESTATION_TYPE;
        // } else if (position == burnInIndex + 7) {
        //     needBurnType = KEYS_Netflix_ESN_TYPE;
        // } else if (position == burnInIndex + 8) {
        //     needBurnType = KEY_RMCA;
        // }
        // if (needBurnType > 0) {
        //     doManualBurn(needBurnType, position);
        // }
    }

    private class OptionAdapter extends BaseAdapter {
        private List<Options> options = new ArrayList<>();

        @Override
        public int getCount() {
            return options.size();
        }

        public void updateAll(List<Options> options) {
            this.options.clear();
            this.options.addAll(options);
            notifyDataSetChanged();
        }

        public boolean updateOptionById(int id, String value, int imgId) {
            if (id < 0 || id >= options.size()) {
                return false;
            }
            Options options = this.options.get(id);
            options.setState(value);
            if (imgId != -1) {
                options.setImg(imgId);
            }
            Log.d(TAG, String.format("updateOptionById id:%d name:%s value:%s imgId:%d",id, options.getTitle(), value, imgId));
            return true;
        }

        public void updateStateById(View view, String value, int imgId) {
            if (view == null)
                return;
            TextView title = get(view, R.id.title);
            TextView state = get(view, R.id.state);
            Log.d(TAG, String.format("updateStateById itemName:%s itemState:%s -> %s", title.getText(), state.getText(), value));
            state.setText(value);
            if (imgId != -1) {
                ImageView img = get(view, R.id.state_img);
                img.setImageResource(imgId);
            }
        }

        @Override
        public Options getItem(int position) {
            return options.get(position);
        }

        @Override
        public long getItemId(int position) {
            return options.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MModeActivity.this, R.layout.item_mmode_option, null);
            }
            Options option = options.get(position);
            TextView title = get(convertView, R.id.title);
            TextView state = get(convertView, R.id.state);
            ImageView img = get(convertView, R.id.state_img);

            title.setText(option.getTitle());
            state.setText(option.getState());
            int img1 = option.getImg();
            if (img1 != -1) {
                img.setImageResource(img1);
            } else {
                img.setImageResource(0);
            }
            // convertView.setTag(option);
            return convertView;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }


    private static class Options {
        private int id;
        private String title;
        private String state;
        private int img;

        public Options(int id, String title, String state, int img) {
            this.id = id;
            this.title = title;
            this.state = state;
            this.img = img;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }
    }

    public byte[] getBytesFromKey(String path, int size) {
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
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }
}