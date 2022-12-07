package com.realtek.tvfactory.utils;

import static com.realtek.tvfactory.utils.Constants.PACKAGE_NAME_AUTO_TEST;
import static com.realtek.tvfactory.utils.Constants.PACKAGE_NAME_TT_TOOL_BVT;
import static com.realtek.tvfactory.utils.Constants.PACKAGE_NAME_TT_TOOL_GTV;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;

public class PackageUtils {

    private static final String TAG = "PackageUtils";

    public static Intent getServiceIntentByComponentName(Context context, ComponentName component) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(component);
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentServices(intent, 0);
        if (resolveInfoList.size() > 0) {
            return intent;
        } else {
            return null;
        }
    }

    public static Intent getActivityIntentByComponentName(Context context, ComponentName component) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(component);
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
        if (resolveInfoList.size() > 0) {
            return intent;
        } else {
            return null;
        }
    }

    public static Intent getIntentByActivityName(Context context, String activity_name) {
        ComponentName name = ComponentName.unflattenFromString(ByteTransformUtils.asciiToString(PACKAGE_NAME_TT_TOOL_BVT) + "/." + activity_name);
        Intent intent = getActivityIntentByComponentName(context, name);
        if (intent != null) {
            return intent;
        } else {
            Log.e(TAG, String.format("start %s fail, because not exist!", name.getClassName()));
            name = ComponentName.unflattenFromString(ByteTransformUtils.asciiToString(PACKAGE_NAME_TT_TOOL_GTV) + "/." + activity_name);
            intent = getActivityIntentByComponentName(context, name);
            if (intent != null) {
                return intent;
            } else {
                Log.e(TAG, String.format("start %s fail, because not exist!", name.getClassName()));
                name = ComponentName.unflattenFromString(PACKAGE_NAME_AUTO_TEST + "/." + activity_name);
                intent = getActivityIntentByComponentName(context, name);
                if (intent != null) {
                    return intent;
                } else {
                    Log.e(TAG, String.format("start %s fail, because not exist!", name.getClassName()));
                    return null;
                }
            }
        }
    }

    public static String getTopActivityPackageName(Context context) {
        String topPackageName = getTopPackageName(context);
        Log.d(TAG, "getTopPackageName:" + topPackageName);
        if (topPackageName !=null && !TextUtils.isEmpty(topPackageName)) {
            return topPackageName;
        }
        final int PROCESS_STATE_TOP = 2;
        try {
            Field processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            List<ActivityManager.RunningAppProcessInfo> processes = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    int state = processStateField.getInt(process);
                    if (state == PROCESS_STATE_TOP) {
                        String[] packageName = process.pkgList;
                        Log.d(TAG, "packageName[0]:" + packageName[0]);
                        return packageName[0];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTopPackageName(Context mContext) {
        try {
            ActivityManager am = (ActivityManager) mContext
                    .getSystemService(Activity.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
