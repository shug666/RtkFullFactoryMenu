package com.realtek.tvfactory.select;

import android.app.ActivityManager;
import android.content.Context;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.app.LocalePicker;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.kk.Country;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.preference.SumaryPreference;

import java.util.List;
import java.util.Locale;

public class SelectLogic extends LogicInterface {

    private static final String TAG = SelectLogic.class.getSimpleName();
    private static final String SYSTEM_COUNTRY = "com.android.tv_current_country";

    private static final int INVALID_COUNTRY_INDEX = -1;

    public SelectLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        FactoryMainApi mFactoryMainApi = FactoryMainApi.getInstance();
        SumaryPreference panel_Sel = (SumaryPreference) mContainer.findPreferenceById(R.id.panel_Sel);
        panel_Sel.setSumary(mFactoryMainApi.getPanelType());
        SumaryPreference panel_name = (SumaryPreference) mContainer.findPreferenceById(R.id.panel_name);
        panel_name.setSumary(mFactoryMainApi.getPanelType());

        SumaryPreference boot_logo = (SumaryPreference) mContainer.findPreferenceById(R.id.boot_logo);
        boot_logo.setSumary(mFactoryMainApi.getBootLogo());

        String countryName;
        Country country = Country.getCountry(getCurCountryIndex(mContext));
        if (country != null) {
            countryName = mContext.getString(country.getName());
        } else {
            countryName = "unknown";
        }

        Locale currentLocale = null;
        try {
            currentLocale = ActivityManager.getService().getConfiguration()
                    .getLocales().get(0);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException:" + e);
        }

        final List<LocalePicker.LocaleInfo> localeInfoList =
                LocalePicker.getAllAssetLocales(mContainer.getContext(), false);
        LocalePicker.LocaleInfo currentLocaleInfo = null;
        for (final LocalePicker.LocaleInfo localeInfo : localeInfoList) {
            if (localeInfo.getLocale().equals(currentLocale)) {
                currentLocaleInfo = localeInfo;
                break;
            }
        }
        if (currentLocaleInfo != null) {
            ((SumaryPreference) mContainer.findPreferenceById(R.id.country_lang)).setSumary(countryName + " / " + currentLocaleInfo.getLabel());
        } else {
            ((SumaryPreference) mContainer.findPreferenceById(R.id.country_lang)).setSumary(countryName + " / " + SystemProperties.get("persist.sys.locale", ""));
        }
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {

    }

    private int getCurCountryIndex(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), SYSTEM_COUNTRY, INVALID_COUNTRY_INDEX);
    }
}
