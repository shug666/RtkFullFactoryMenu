package com.realtek.fullfactorymenu.picture;

import java.util.ArrayList;

import com.realtek.fullfactorymenu.FactoryMenuActivity;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.api.impl.PictureApi;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.SeekBarPreferenceFragment;
import com.realtek.fullfactorymenu.utils.AppToast;
import com.realtek.fullfactorymenu.utils.ProgressDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class WhiteBalanceAdjustFragment extends PreferenceFragment {

    private FactoryMenuActivity mFactoryMenu;

    private WhiteBalanceAdjustLogic mWhiteBalanceLogic = null;
    private ProgressDialog mProgressDialog;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFactoryMenu = (FactoryMenuActivity) activity;
    }

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_white_balance);
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWhiteBalanceLogic = mPreferenceContainer.getPreferenceLogic(WhiteBalanceAdjustLogic.class);
    }

    @Override
    public void onDetach() {
        mFactoryMenu = null;
        super.onDetach();
    }

    @Override
    public void onPreferenceItemClick(Preference preference) {
        if (preference instanceof SeekBarPreference) {
            ArrayList<Preference> preferences = mPreferenceContainer.getPreferences();
            ArrayList<SeekBarPreference> list = new ArrayList<SeekBarPreference>();
            for (Preference child : preferences) {
                if (child instanceof SeekBarPreference) {
                    list.add((SeekBarPreference) child);
                }
            }
            SeekBarPreferenceFragment fragment = new SeekBarPreferenceFragment(list, list.indexOf(preference));
            fragment.setTargetFragment(getParentFragment(), 0);
            mFactoryMenu.showPage(fragment);
        }
        switch (preference.getId()) {
            case R.id.copy_data_to_allsource:
                new CopyTask().execute();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(Preference preference, int keyCode, KeyEvent event) {
        if (preference instanceof SeekBarPreference) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                SeekBarPreference seekBarPreference = (SeekBarPreference) preference;
                switch (keyCode) {
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                    mWhiteBalanceLogic.catchInputNumber(seekBarPreference, keyCode);
                    return true;
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (mWhiteBalanceLogic.checkApplyProgress(seekBarPreference)) {
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (seekBarPreference.hasTemporaryProgress()) {
                        seekBarPreference.cancelTemporaryProgress();
                    }
                    break;
                default:
                    break;
                }
            }
        }
        return super.onKey(preference, keyCode, event);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 && data != null) {
            mFocusId = data.getIntExtra("focusId", mFocusId);
        }
    }

    public class CopyTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            int[] isFail = PictureApi.getInstance().setTvosCommonCommand("copyDataToAllSource");
            return isFail[0] == 1 ? true : false;
        }

        @Override
        protected void onPreExecute() {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(mFactoryMenu);
                mProgressDialog.setMessage(getString(R.string.str_wait));
            }
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();
            if (aBoolean) {
                AppToast.showToast(mFactoryMenu, R.string.str_success, Toast.LENGTH_SHORT);
            } else {
                AppToast.showToast(mFactoryMenu, R.string.str_fail, Toast.LENGTH_SHORT);
            }
        }
    }
}
