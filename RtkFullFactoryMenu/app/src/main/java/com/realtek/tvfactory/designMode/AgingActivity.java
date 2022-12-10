package com.realtek.tvfactory.designMode;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.realtek.tvfactory.BootCompletedReceiver.BOOT_COMPILE_TO_FACTORY_AGING;
import static com.realtek.tvfactory.BootCompletedReceiver.BootMode;
import static com.realtek.tvfactory.utils.Constants.SHARE_PREFERENCE_FILE;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;

import com.exttv.tv.ExtTv;
import com.realtek.system.KeyEventManager;
import com.realtek.tv.PQ;
import com.realtek.tv.RtkSettingConstants;
import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.utils.RtkKeyEvent;
import com.realtek.tvfactory.utils.TvUtils;

import java.lang.ref.WeakReference;

public class AgingActivity extends Activity {
    private static final String TAG = "AgingActivity";
    public static final String LED_RED_GREEN = "Misc_LED_LEDTest";
    private static final int MSG_LED_LIGHT_FLASH_ON = 0;
    private static final int MSG_LED_LIGHT_FLASH_OFF = 1;
    public static final int LED_LIGHT_RED = 0;
    public static final int LED_LIGHT_GREEN = 1;
    private boolean redOrGreenIsRed = false;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Handler handler;
    private PQ mPq;
    private ExtTv extTv;
    private KeyEventManager mKeyEventManager;
    private int agingTime;
    private boolean flag = false;
    private long mCurrentTime = 0;
    private FactoryMainApi mFactoryMainApi;

    private KeyEventManager getKeyEventManager() {
        if (mKeyEventManager == null)
            mKeyEventManager = KeyEventManager.getInstance();
        return mKeyEventManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aging);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        handler = new BackgroundHandler(this);
        handler.sendEmptyMessage(MSG_LED_LIGHT_FLASH_ON);
        mPq = new PQ();
        extTv = new ExtTv();
        mFactoryMainApi = FactoryMainApi.getInstance();
        extTv.extTv_tv001_SetValueString("Misc_AgingMod_AgingModTest", "ENTER");
        if ("false".equals(SystemProperties.get("persist.sys.aging", "false"))) {
            SystemProperties.set("persist.sys.aging", "true");
        }
        SystemProperties.set(BootMode, BOOT_COMPILE_TO_FACTORY_AGING + "");
        mFactoryMainApi.setAcPowerOnMode(0);
        int currentBacklight = mPq.getBacklight();
        Log.d(TAG, "currentBacklight: " + currentBacklight);
        sp = getSharedPreferences(SHARE_PREFERENCE_FILE, MODE_PRIVATE);
        editor = sp.edit();
        if (sp.getInt("currentBacklight", -1) == -1) {
            editor.putInt("currentBacklight", currentBacklight);
            editor.commit();
        } else {
            currentBacklight = sp.getInt("currentBacklight", -1);
        }
        mPq.setBacklight(100);
        mPq.setBrightnessBoosterEnable(true);
        handleKey(true);
        mCurrentTime = System.currentTimeMillis();
        agingTime = TvUtils.getCurrentAgingTime(this);
        flag = true;
        saveAndDisableIdle(editor);
        handler.post(new Runnable() {
            @Override
            public void run() {
                agingTime++;
                TvUtils.setCurrentAgingTime(AgingActivity.this, agingTime);
                if (flag) {
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void saveAndDisableIdle(SharedPreferences.Editor editor) {
        if (editor == null) {
            return;
        }
        long idleTime = Global.getLong(getContentResolver(), RtkSettingConstants.TV_IDLE_STANDBY_NOTIFY_TIME, 0);
        Log.d(TAG, "saveAndDisableIdle:" + idleTime);
        editor.putLong(RtkSettingConstants.TV_IDLE_STANDBY_NOTIFY_TIME, idleTime);
        editor.commit();
        Global.putLong(getContentResolver(), RtkSettingConstants.TV_IDLE_STANDBY_NOTIFY_TIME, -1);
    }

    private void restoreIdle(SharedPreferences sp) {
        if (sp == null) {
            return;
        }
        long idleTime = sp.getLong(RtkSettingConstants.TV_IDLE_STANDBY_NOTIFY_TIME, 0);
        Log.d(TAG, "restoreIdle:" + idleTime);
        Global.putLong(getContentResolver(), RtkSettingConstants.TV_IDLE_STANDBY_NOTIFY_TIME, idleTime);
    }

    private void handleKey(boolean isPassToUser) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.realtek.keyintercept",
                "com.realtek.keyintercept.RtkKeyInterceptService"));
        if (isPassToUser) {
            getApplicationContext().stopService(intent);
            getKeyEventManager().registerAllKey(KeyEventManager.KEY_EVENT_ACTION_DOWN_UP);
            getKeyEventManager().setKeyEventListener(new KeyEventManager.OnKeyListener() {
                @Override
                public int onKeyDown(int i, String s) {
                    Log.d(TAG, String.format("KeyEventManager onKeyDown %d %s", i, s));
                    if (FactoryApplication.CUSTOMER_IS_CH) {
                        if (i == KeyEvent.KEYCODE_POWER || i == RtkKeyEvent.KEYCODE_CH_FACTORY_AGING_MODE) {
                            return 0;
                        }
                    }
                    return 1;
                }

                @Override
                public int onKeyUp(int i, String s) {
                    Log.d(TAG, String.format("KeyEventManager onKeyUp %d %s", i, s));
                    if (FactoryApplication.CUSTOMER_IS_KK) {
                        if (i == KeyEvent.KEYCODE_VOLUME_MUTE || i == RtkKeyEvent.KEYCODE_KK_BURNTV) {
                            ExitAgingMode();
                        }
                    } else {
                        if (FactoryApplication.CUSTOMER_IS_CH) {
                            if (i == KeyEvent.KEYCODE_POWER || i == RtkKeyEvent.KEYCODE_CH_FACTORY_AGING_MODE) {
                                ExitAgingMode();
                                return 0;
                            }
                        } else if (("rtk_keypad".equals(s) && i == KeyEvent.KEYCODE_POWER) || i == RtkKeyEvent.KEYCODE_EXIT_AGING) {
                            ExitAgingMode();
                        }
                    }
                    return 1;
                }
            });
        } else {
            getKeyEventManager().unregisterAllKey(KeyEventManager.KEY_EVENT_ACTION_DOWN_UP);
            getKeyEventManager().setKeyEventListener(null);
            getApplicationContext().startService(intent);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        String DEVICE_NAME = InputDevice.getDevice(event.getDeviceId()).getName();
        boolean isRtkKeypad = DEVICE_NAME.equals("rtk_keypad");
        boolean isDown = event.getAction() == KeyEvent.ACTION_DOWN;
        int keyCode = event.getKeyCode();

        Log.d(TAG, "aging dispatchKeyEvent: device-name:" + DEVICE_NAME + ", keycode:" + keyCode + " isDown:" + isDown);
        if (isRtkKeypad && keyCode == KeyEvent.KEYCODE_POWER) {
            if (isDown)
                ExitAgingMode();
            return true;
        }
        if (DEVICE_NAME.equals("Virtual") && keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            ExitAgingMode();
            return true;
        }
        return true;
    }

    public void ExitAgingMode() {
        Log.d(TAG, "The second press time is " + (System.currentTimeMillis() - mCurrentTime));
        if ((System.currentTimeMillis() - mCurrentTime) <= 1000 || mCurrentTime <= 0) {
            Log.d(TAG, "The second press time is too short!");
            return;
        }
        handler.sendEmptyMessage(MSG_LED_LIGHT_FLASH_OFF);
        extTv.extTv_tv001_SetValueString("Misc_AgingMod_AgingModTest", "EXIT");
        mFactoryMainApi.resetAcPowerOnMode();

        sp = getSharedPreferences(SHARE_PREFERENCE_FILE, MODE_PRIVATE);
        editor = sp.edit();
        int Backlight = sp.getInt("currentBacklight", -1);
        mPq.setBacklight(Backlight);
        mPq.setBrightnessBoosterEnable(false);
        editor.putInt("currentBacklight", -1);
        editor.commit();

        SystemProperties.set("persist.sys.aging", "false");
        int anInt = SystemProperties.getInt(BootMode, -1);
        if (anInt == BOOT_COMPILE_TO_FACTORY_AGING) {
            SystemProperties.set(BootMode, "-1");
        }
        this.finish();
    }

    public static class BackgroundHandler extends Handler {
        private final WeakReference<AgingActivity> weakReference;

        public BackgroundHandler(AgingActivity activity) {
            weakReference = new WeakReference<AgingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AgingActivity activity = weakReference.get();
            if (activity == null)
                return;
            switch (msg.what) {
                case MSG_LED_LIGHT_FLASH_ON:
                    activity.handler.removeMessages(MSG_LED_LIGHT_FLASH_ON);
                    activity.extTv.extTv_tv001_SetValueInt(LED_RED_GREEN, activity.redOrGreenIsRed ? LED_LIGHT_RED : LED_LIGHT_GREEN);
                    activity.redOrGreenIsRed = !activity.redOrGreenIsRed;
                    activity.handler.sendEmptyMessageDelayed(MSG_LED_LIGHT_FLASH_ON, 1000);
                    break;
                case MSG_LED_LIGHT_FLASH_OFF:
                    activity.handler.removeMessages(MSG_LED_LIGHT_FLASH_ON);
                    activity.extTv.extTv_tv001_SetValueInt(LED_RED_GREEN, LED_LIGHT_GREEN);
                    activity.redOrGreenIsRed = false;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        restoreIdle(sp);
        handleKey(false);
    }
}