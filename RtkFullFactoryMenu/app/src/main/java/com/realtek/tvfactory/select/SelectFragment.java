package com.realtek.tvfactory.select;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.realtek.tvfactory.BaseFragment;
import com.realtek.tvfactory.FactoryMenuFragment;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.FactoryMainApi;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;

public class SelectFragment extends PreferenceFragment {


    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_select);
        return builder.create();
    }

    private void showPage(Class<? extends BaseFragment> clazz, int title) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FactoryMenuFragment) {
            FactoryMenuFragment menuFragment = (FactoryMenuFragment) parentFragment;
            menuFragment.showPage(clazz, title);
        }
    }


    @Override
    public void onPreferenceItemClick(Preference preference) {

        switch (preference.getId()){
            case R.id.page_panel_setting:
                showPage(PanelPageFragment.class, R.string.str_select_panel_setting);
                break;
            case R.id.page_Project:
                showPage(ProjectIdFragment.class, R.string.str_select_Project);
                break;
            case R.id.country_lang:
                openDialog(preference.getTitle().toString(), FactoryMainApi.getInstance().getCountryLang());
                break;
            default:
                break;
        }
    }

    private void openDialog(String title, String msg){
        AlertDialog dialog = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle(title).setMessage(msg).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setCancelable(false).create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).requestFocus();

    }
}
