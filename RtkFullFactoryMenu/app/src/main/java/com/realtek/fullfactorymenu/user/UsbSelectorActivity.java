package com.realtek.fullfactorymenu.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.realtek.fullfactorymenu.R;

import java.util.ArrayList;
import java.util.List;

public class UsbSelectorActivity extends Activity {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_PATH = "path";
    public static final String EXTRA_TYPE = "type";

    public static final String EXTRA_RETURN_RESULT_WHEN_SINGLE = "flag";

    private final ArrayList<UsbItem> mUsbItems = new ArrayList<UsbItem>();

    private ListView mListView;

    private final UsbAdapter mUsbAdapter = new UsbAdapter();
    private UsbBroadcast mUsbBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUsbList();
        initUsbBroadcast();

        int diskCount = mUsbItems.size();
        if (diskCount == 0) {
            setResult(RESULT_OK);
            finish();
            return;
        } else if (diskCount == 1) {
            if (getIntent().getBooleanExtra(EXTRA_RETURN_RESULT_WHEN_SINGLE, false)) {
                Intent intent = getIntent();
                UsbItem item = mUsbItems.get(0);
                intent.putExtra(EXTRA_NAME, item.name);
                intent.putExtra(EXTRA_PATH, item.path);
                intent.putExtra(EXTRA_TYPE, item.type);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_usb_list);

        mListView = (ListView) findViewById(R.id.usb_list);
        mListView.setAdapter(mUsbAdapter);
        mListView.setOnItemClickListener(mItemClickListener);

        mListView.requestFocus();
    }

    @Override
    protected void onDestroy() {
        if (mUsbBroadcast != null) {
            unregisterReceiver(mUsbBroadcast);
            mUsbBroadcast = null;
        }
        super.onDestroy();
    }

    private void initUsbList() {
        mUsbItems.clear();

        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);

        List<VolumeInfo> volumes = storageManager.getVolumes();
        int count = volumes == null ? 0 : volumes.size();
        VolumeInfo volume = null;
        UsbItem usb = null;
        for (int i = 0; i < count; i++) {
            volume = volumes.get(i);
            if (volume.disk != null && (volume.disk.isUsb() || volume.disk.isSd())) {
                if (volume.state != VolumeInfo.STATE_MOUNTED) {
                    continue;
                }
                usb = new UsbItem();
                usb.path = volume.path;
                usb.name = volume.fsLabel;
                usb.type = volume.fsType;
                mUsbItems.add(usb);
            }
        }
    }

    private void backFragment(){
        setResult(RESULT_OK);
        finish();
    }

    private void initUsbBroadcast() {
        mUsbBroadcast = new UsbBroadcast();
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        usbFilter.addDataScheme("file");
        registerReceiver(mUsbBroadcast, usbFilter);
    }

    private final OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UsbItem item = mUsbAdapter.getItem(position);
            Intent data = getIntent();
            data.putExtra(EXTRA_NAME, item.name);
            data.putExtra(EXTRA_PATH, item.path);
            data.putExtra(EXTRA_TYPE, item.type);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode || KeyEvent.KEYCODE_MENU  == keyCode) {
            backFragment();
        }
        return super.onKeyUp(keyCode, event);
    }

    private class UsbItem {

        private String path;

        private String name;

        private String type;

    }

    private class UsbAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUsbItems.size();
        }

        @Override
        public UsbItem getItem(int position) {
            return mUsbItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(UsbSelectorActivity.this, R.layout.widget_preference, null);
            }

            TextView name = (TextView) convertView.findViewById(R.id.preference_title);
            UsbItem item = getItem(position);
            if (TextUtils.isEmpty(item.name)) {
                name.setText(item.path);
            } else {
                name.setText(String.format("%s(%s)", item.path, item.name));
            }
            return convertView;
        }

    }
    private class UsbBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)){
                initUsbList();
                if (mUsbItems.size() == 0){
                    backFragment();
                }
                mUsbAdapter.notifyDataSetChanged();
            }
            if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
                initUsbList();
                mUsbAdapter.notifyDataSetChanged();
            }
        }
    }

}
