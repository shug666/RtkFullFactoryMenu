package com.realtek.tvfactory.utils;

import android.content.Context;
import android.widget.Toast;

public class AppToast {
    
    private static Toast mToast = null;

    public static void showToast(Context context, int msgId, int duration) {
        showToast(context, context.getString(msgId), duration);
    }

    public static void showToast(Context context, String msg, int duration) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }

        mToast = Toast.makeText(context, msg, duration);
        mToast.show();
    }

}
