package com.realtek.tvfactory;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import com.realtek.tvfactory.designMode.AgingActivity;
import com.realtek.tvfactory.utils.PackageUtils;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";

    public static final String BootMode = "persist.sys.factory_boot_mode";
    public static final int BOOT_COMPILE_TO_GTV_MAIN = 0;
    public static final int BOOT_COMPILE_TO_GTV_AGING = 1;
    public static final int BOOT_COMPILE_TO_BVT_MAIN = 2;
    public static final int BOOT_COMPILE_TO_BVT_AGING = 3;
    public static final int BOOT_COMPILE_TO_BVT_MAIN_AGING = 4;
    public static final int BOOT_COMPILE_TO_KK_MMODE = 5;
    public static final int BOOT_COMPILE_TO_FACTORY_AGING = 6;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive intent = " + intent);
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_LOCKED_BOOT_COMPLETED:
                    int anInt = SystemProperties.getInt(BootMode, -1);
                    Log.d(TAG, String.format("onReceive %s bootMode:%d, aging:%s", intent.getAction(), anInt, SystemProperties.getBoolean("persist.sys.aging", false)));
                    if (SystemProperties.getBoolean("persist.sys.aging", false) && anInt == BOOT_COMPILE_TO_FACTORY_AGING) {
                        Log.d(TAG, "start AgingActivity");
                        ComponentName name = new ComponentName(context.getPackageName(), AgingActivity.class.getName());
                        intent = PackageUtils.getActivityIntentByComponentName(context, name);
                        if (intent != null) {
                            Log.i(TAG, String.format("start %s !", name.getClassName()));
                            context.startActivity(intent);
                            return;
                        } else {
                            Log.e(TAG, String.format("start %s fail, because not exist!", name.getClassName()));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
