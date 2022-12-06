package com.realtek.tvfactory.chdefault;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.api.manager.TvCommonManager;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

public class DefaultFragment extends PreferenceFragment {
    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_ch_default);
        return builder.create();
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
        if (preference.getId() == R.id.ch_default_factory_setting) {
            showResetConfirmDialog();
        }
    }

    private void showResetConfirmDialog(){
        Context context = getContext();
        if (context == null) {
            context = getActivity();
        }
        if (context == null)
            return;
        AlertDialog dlg = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(context.getResources().getString(R.string.dlg_title_reset))
                .setMessage(context.getResources().getString(R.string.dlg_message_reset))
                .setPositiveButton(context.getResources().getString(R.string.btn_submit), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FactoryMainApi.getInstance().setIntegerValue(TvCommonManager.BACKGROUND_LIGHT, 0);
                        FactoryMainApi.getInstance().restoreToDefault();
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.btn_cancel), null).create();
        //dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dlg.show();
        dlg.getButton(DialogInterface.BUTTON_NEGATIVE).requestFocus();
    }
}
