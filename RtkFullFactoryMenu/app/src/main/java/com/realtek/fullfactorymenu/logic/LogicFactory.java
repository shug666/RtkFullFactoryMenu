package com.realtek.fullfactorymenu.logic;

import java.lang.reflect.Constructor;

import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.utils.LogHelper;

import android.text.TextUtils;

public class LogicFactory {

    private static final String TAG = "LogicFactory";

    private LogicFactory() {
    }

    public static LogicInterface createLogic(PreferenceContainer container, String logicClassName) {
        if (TextUtils.isEmpty(logicClassName)) {
            return null;
        }
        try {
            Class<?> clazz = Class.forName(logicClassName);
            Constructor<?> constructor = clazz.getConstructor(PreferenceContainer.class);
            return (LogicInterface) constructor.newInstance(container);
        } catch (Exception e) {
            LogHelper.e(TAG, e.getMessage(), e);
        }
        return null;
    }

}
