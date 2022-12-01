package com.realtek.fullfactorymenu.utils;

import android.os.SystemProperties;

public class Constants {

    public static final String BRAND = SystemProperties.get("ro.product.vendor.brand", "");

    public static final String PACKAGE_M_MODE = "com.realtek.rtkfactoryirautotest/.mmode.service.MKeyEventService";
    public static final String ACTION_SERVICE_M_MODE = "android.intent.action.MKeyEventService";
    public static final String EXTRA_M_MODE_STATUS = "M_MODE_STATUS";
    public static final int MSG_ACTION_START_CH_MMODE = 0x01;
    public static final int MSG_ACTION_STOP_CH_MMODE = 0x02;

}
