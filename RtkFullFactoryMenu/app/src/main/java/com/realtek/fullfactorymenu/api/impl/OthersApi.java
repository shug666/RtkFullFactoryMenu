package com.realtek.fullfactorymenu.api.impl;

import android.content.Context;
import android.text.TextUtils;

import com.realtek.fullfactorymenu.FactoryApplication;

public class OthersApi {

    private static final String TAG = "OthersApi";

    private static OthersApi mInstance = null;
    private final FactoryApplication mFactoryApplication;
    private final Context mContext;

    public OthersApi(){
        mInstance = this;
        mFactoryApplication = FactoryApplication.getInstance();
        mContext = mFactoryApplication.getApplicationContext();
    }

    public static OthersApi getInstance() {
        if (mInstance == null) {
            synchronized (OthersApi.class) {
                if (mInstance == null) {
                    mInstance = new OthersApi();
                }
            }
        }
        return mInstance;
    }

    public boolean getWatchDogMode() {
        int watchDog = mFactoryApplication.getSysCtrl().getValueInt("Misc_Register_WatchDog");
        return watchDog == 0 ? false : true;
    }

    public boolean setWatchDogMode(boolean isEnable) {
        mFactoryApplication.getSysCtrl().setValueInt("Misc_Register_WatchDog", isEnable ? 1 : 0);
        return true;
    }

    public int[] setTvosCommonCommand(String command) {
        if (command == null) {
            return null;
        }
        if (TextUtils.isEmpty(command)) {
            return null;
        }

        if (command.matches("GET_STR_ENABLE")) {
            int index = mFactoryApplication.getSysCtrl().getValueInt("Misc_STR_STR");
            return new int[] { index };
        }

        if (command.matches("SET_STR_ENABLE#\\d+")) {
            String[] parts = command.split("#");
            int index = Integer.parseInt(parts[1]);
            boolean result = mFactoryApplication.getSysCtrl().setValueInt("Misc_STR_STR", index);
            return new int[] { result ? 0 : -1 };
        }
        return null;
    }
}
