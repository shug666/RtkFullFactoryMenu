package com.realtek.fullfactorymenu.systemInfo;

import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;
import static com.realtek.fullfactorymenu.api.impl.UpgradeApi.KEYS_MAC_TYPE;
import static com.realtek.fullfactorymenu.systemInfo.SystemInfoLogic.CMD_UPGRADE_MAC_MANUAL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.utils.AppToast;
import com.realtek.fullfactorymenu.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class InputMacActivity extends FragmentActivity {

    private static final int MAC_GROUP_LENGTH = 12;

    private static final int[] MAC_INPUT_ID = {
            R.id.mac_input_1,
            R.id.mac_input_2,
            R.id.mac_input_3,
            R.id.mac_input_4,
            R.id.mac_input_5,
            R.id.mac_input_6,
            R.id.mac_input_7,
            R.id.mac_input_8,
            R.id.mac_input_9,
            R.id.mac_input_10,
            R.id.mac_input_11,
            R.id.mac_input_12
    };

    private final TextView[] macInputText = new TextView[MAC_GROUP_LENGTH];

    private LinearLayout inputMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_mac);

        inputMac = findViewById(R.id.input_mac);

        String macAddress = FactoryApplication.getInstance().getExtTv().extTv_tv001_GetTrustZoneKeysSerialNumber(KEYS_MAC_TYPE);

        List<String> list = new ArrayList<>();
        for (String s : macAddress.split("")) {
            if (!s.equals(":")) {
                list.add(s);
            }
        }

        String[] mac = list.toArray(new String[MAC_GROUP_LENGTH]);

        for (int i = 0; i < MAC_GROUP_LENGTH; i++) {
            macInputText[i] = findViewById(MAC_INPUT_ID[i]);

            macInputText[i].setText(mac[i]);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event == null) {
            return false;
        }

        if (event.getAction() == KeyEvent.ACTION_UP) {
            return false;
        }

        TextView textView = (TextView) getCurrentFocus();
        String text = textView.getText().toString();
        boolean isUp = text.matches("[A-F]");
        boolean isDown = text.matches("[0-9]");

        switch (event.getKeyCode()) {
        case KEYCODE_DPAD_UP:
            if (!isUp || text.equals("F")) {
                textView.setText("A");
            } else {
                int i = Integer.parseInt(text, 16);
                textView.setText(Integer.toHexString(i + 1).toUpperCase());
            }
            break;
        case KEYCODE_DPAD_DOWN:
            if (!isDown || text.equals("0")) {
                textView.setText("9");
            } else {
                int i = Integer.parseInt(text, 16);
                textView.setText(Integer.toHexString(i - 1));
            }
            break;
        case KEYCODE_DPAD_LEFT:
            if (Utils.focusUp(inputMac, true)) {
                return true;
            }
            break;
        case KEYCODE_DPAD_RIGHT:
            if (Utils.focusDown(inputMac, true)) {
                return true;
            }
            break;
        case KEYCODE_DPAD_CENTER:
            String macAddress = getInputMacAddress();
            if (TextUtils.isEmpty(macAddress)) {
                AppToast.showToast(this, R.string.input_mac_invalid, Toast.LENGTH_SHORT);
                break;
            }
            SystemInfoLogic.sendSyncCommand(CMD_UPGRADE_MAC_MANUAL, macAddress);
            onBackPressed();
            break;
        case KeyEvent.KEYCODE_BACK:
            onBackPressed();
            break;
        default:
            break;
        }
        return super.dispatchKeyEvent(event);
    }

    private String getInputMacAddress() {
        StringBuilder macAddress = new StringBuilder();
        for (int i = 0; i < MAC_GROUP_LENGTH; i++) {
            macAddress.append(macInputText[i].getText().toString());
        }
        return macAddress.toString();
    }

}