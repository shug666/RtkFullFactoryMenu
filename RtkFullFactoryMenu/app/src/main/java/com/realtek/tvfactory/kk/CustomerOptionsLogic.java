package com.realtek.tvfactory.kk;

import java.util.Locale;

import com.realtek.tvfactory.utils.ByteTransformUtils;
import com.realtek.tvfactory.utils.Constants;
import com.realtek.system.RtkConfigs.TvConfigs;
import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.preference.SumaryPreference;

import android.os.Handler;
import android.os.LocaleList;
import android.os.Message;
import android.os.SystemProperties;

public class CustomerOptionsLogic extends LogicInterface implements Handler.Callback {
    private FactoryApplication mApplication;
    private FactoryMainApi mFactoryMainApi;
    private Handler mHandler;

    public CustomerOptionsLogic(PreferenceContainer container) {
        super(container);
        mApplication = FactoryApplication.getInstance();
        mFactoryMainApi = FactoryMainApi.getInstance();
        mHandler = new Handler(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void init() {
        SumaryPreference customerID = (SumaryPreference) mContainer.findViewById(R.id.item_id);
        SumaryPreference osdLanguage = (SumaryPreference) mContainer.findViewById(R.id.item_osd_language);
        SumaryPreference country = (SumaryPreference) mContainer.findViewById(R.id.item_country);
        SumaryPreference teletext = (SumaryPreference) mContainer.findViewById(R.id.item_teletext);
        Preference logoUpdate = (Preference) mContainer.findViewById(R.id.item_logo_update);
        Preference logoUpdate2nd = (Preference) mContainer.findViewById(R.id.item_2nd_logo_update);
        StatePreference powerMode = (StatePreference) mContainer.findViewById(R.id.item_power_mode);

        customerID.setSumary(SystemProperties.get("ro.product.manufacturer", ByteTransformUtils.asciiToString(Constants.MANUFACTURER_KK)));

        Locale aDefault = LocaleList.getDefault().get(0);
        String language = String.format("%s-%s", aDefault.getLanguage(), aDefault.getCountry());
        osdLanguage.setSumary(language);

        country.setSumary(getScanCountry());

        teletext.setSumary(TvConfigs.SUPPORTED_TELTEX ? "On" : "Off");

        powerMode.init(getPowerOnMode());
    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()) {
        case R.id.item_power_mode:
            mFactoryMainApi.setAcPowerOnMode(current);
            break;
        }
    }

    private String getScanCountry() {
        int index = mApplication.getTv().getCountry();
        Country country = Country.getCountry(index);
        if (country == null) {
            return "unknown";
        }
        return mContext.getResources().getString(country.getName());
    }

    private int getPowerOnMode() {
        int powerOnMode = mApplication.getFactory().getPowerOnMode();
        if (powerOnMode >= 0 && powerOnMode <= 2) {
            return powerOnMode;
        }
        return 0;
    }
}
