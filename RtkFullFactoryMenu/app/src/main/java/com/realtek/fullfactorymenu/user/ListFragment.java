package com.realtek.fullfactorymenu.user;

import java.util.ArrayList;
import java.util.List;

import com.realtek.fullfactorymenu.BaseFragment;
import com.realtek.fullfactorymenu.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListFragment extends BaseFragment {
    private ArrayList<Pair<String, Integer>> mListItems = new ArrayList<>();
    private ListAdapter listAdapter = new ListAdapter();
    private boolean registeredBLE = false;
    private boolean registeredWIFI = false;
    private Handler mHandler;
    private ListView list;

    private static final int MINOR_DEVICE_CLASS_POINTING =
            Integer.parseInt("0000010000000", 2);
    private static final int MINOR_DEVICE_CLASS_JOYSTICK =
            Integer.parseInt("0000000000100", 2);
    private static final int MINOR_DEVICE_CLASS_GAMEPAD =
            Integer.parseInt("0000000001000", 2);
    private static final int MINOR_DEVICE_CLASS_KEYBOARD =
            Integer.parseInt("0000001000000", 2);
    private static final int MINOR_DEVICE_CLASS_REMOTE =
            Integer.parseInt("0000000001100", 2);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mHandler = new Handler();
        String type = null;
        if (arguments != null) {
            type = arguments.getString("type");
        }
        if ("WIFI".equals(type)) {
            initWifiData(getContext());
        } else if ("BLUETOOTH".equals(type)) {
            initBluetoothData(getContext());
        }
    }

    private void initBluetoothData(Context context) {
        if (context == null) {
            return;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(bluetoothScanReceiver, filter);
        registeredBLE = true;
        //adapter.cancelDiscovery();
        mListItems.clear();
        boolean start = adapter.startDiscovery();
        if (!start) {
            Log.e("TAG", "bluetoothCheck start scan fail.");
            context.unregisterReceiver(bluetoothScanReceiver);
            registeredBLE = false;
        }
    }

    final private BroadcastReceiver bluetoothScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", String.format("bluetoothScanReceiver %s", intent.getAction()));
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) return;
                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                Log.d("TAG", String.format("onReceiveDevice Name:%s type:%d address:%s state:%d rssi:%d", device.getName(), device.getType(), device.getAddress(), device.getBondState(), rssi));

                if (!isMatchingDevice(device)) return;
                boolean match = mListItems.stream().anyMatch(pair -> pair.first.equals(device.getAddress()));
                if (match) {
                    return;
                }
                mListItems.add(new Pair<>(device.getAddress(), rssi));
                listAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                context.unregisterReceiver(this);
                registeredBLE = false;
                listAdapter.notifyDataSetChanged();
            }
        }
    };

    private boolean isMatchingDevice(BluetoothDevice device) {
        if (device.getAddress() == null || device.getName() == null) {
            return false;
        }

        int majorDeviceClass = device.getBluetoothClass().getMajorDeviceClass();
        int majorMinorClass = device.getBluetoothClass().getDeviceClass();
        if (isInputDevice(majorDeviceClass,majorMinorClass)){
            return true;
        }
        if (majorDeviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO && a2dpDeviceClass(majorMinorClass)) {
            return true;
        }
        return false;
    }

    private boolean a2dpDeviceClass(int majorMinorClass) {
        return (majorMinorClass == BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED ||
                majorMinorClass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET ||
                majorMinorClass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES ||
                majorMinorClass == BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER ||
                majorMinorClass == BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO ||
                majorMinorClass == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE ||
                majorMinorClass == BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO
        );
    }

    private boolean inputDeviceClass(int majorMinorClass) {
        int acceptableDevicesMask = MINOR_DEVICE_CLASS_POINTING | MINOR_DEVICE_CLASS_JOYSTICK |
                MINOR_DEVICE_CLASS_GAMEPAD | MINOR_DEVICE_CLASS_KEYBOARD |
                MINOR_DEVICE_CLASS_REMOTE;

        return (acceptableDevicesMask & majorMinorClass) != 0;
    }

    public boolean inputMajorDeviceClass(int majorDeviceClass) {
        return majorDeviceClass == BluetoothClass.Device.Major.PERIPHERAL;
    }

    public boolean isInputDevice(int majorDeviceClass,int majorMinorClass) {
        return inputMajorDeviceClass(majorDeviceClass) &&
                inputDeviceClass(majorMinorClass);
    }

    private void initWifiData(Context context) {
        if (context == null) {
            return;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();
        List<String> filter = new ArrayList<>();
        if (scanResults != null && scanResults.size() > 0) {
            for (ScanResult scanResult : scanResults) {
                if (TextUtils.isEmpty(scanResult.SSID))
                    continue;
                if (filter.contains(scanResult.SSID)) {
                    continue;
                }
                filter.add(scanResult.SSID);
                mListItems.add(new Pair<>(scanResult.SSID, scanResult.level));
            }
            return;
        }
        boolean start = wifiManager.startScan();
        Log.d("TAG", "initWifiData null, startScan:" + start);
        if (start && !registeredWIFI) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(wifiScanReceiver, intentFilter);
            registeredWIFI = true;
        }
    }

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                mHandler.post(()-> initWifiData(c));
            }
            c.unregisterReceiver(this);
            registeredWIFI = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.list_fragment, null);
        list = view.findViewById(R.id.list);
        list.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        list.requestFocus();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Context context = getContext();
        if (registeredBLE && context != null) {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            context.unregisterReceiver(bluetoothScanReceiver);
        }
        if (registeredWIFI && context != null) {
            context.unregisterReceiver(wifiScanReceiver);
        }
    }

    private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.widget_state_preference, null);
            }
            Pair<String, Integer> pair = mListItems.get(position);
            TextView title = convertView.findViewById(R.id.preference_title);
            TextView value = convertView.findViewById(R.id.preference_value);
            title.setText(pair.first);
            value.setText(String.valueOf(pair.second));
            return convertView;
        }
    }
}
