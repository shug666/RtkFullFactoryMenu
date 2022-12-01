package com.realtek.fullfactorymenu.utils;

import android.os.SystemProperties;

public class Constants {

    public static final String BRAND = SystemProperties.get("ro.product.vendor.brand", "");

    private Constants() {
    }

}
