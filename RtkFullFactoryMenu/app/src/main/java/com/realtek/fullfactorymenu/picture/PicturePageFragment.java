package com.realtek.fullfactorymenu.picture;

import com.realtek.fullfactorymenu.BaseFragment;
import com.realtek.fullfactorymenu.FactoryMenuFragment;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.Preference;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.PreferenceFragment;
import com.realtek.fullfactorymenu.utils.AppToast;
import com.realtek.fullfactorymenu.utils.TvInputUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.Toast;

public class PicturePageFragment extends PreferenceFragment {

    @Override
    public PreferenceContainer onCreatePreferenceContainer(Bundle savedInstanceState) {
        PreferenceContainer.Builder builder = new PreferenceContainer.Builder(getActivity());
        builder.setXml(R.xml.page_picture);
        return builder.create();
    }

    private boolean isTestPatternOff() {
        LogicInterface logic = mPreferenceContainer.getPreferenceLogic(R.id.test_pattern);
        if (logic instanceof PicturePageLogic) {
            PicturePageLogic picturePageLogic = (PicturePageLogic) logic;
            return !picturePageLogic.isTestPatternEnabled();
        }
        return true;
    }

    @Override
    public boolean onKey(Preference preference, int keyCode, KeyEvent event) {
        switch (preference.getId()) {
        case R.id.test_pattern: {
            switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            default:
                if (!isTestPatternOff()) {
                    return true;
                }
                break;
            }
            break;
        }
        default:
            break;
        }
        return super.onKey(preference, keyCode, event);
    }

    private void disableTestPattern() {
        LogicInterface logic = mPreferenceContainer.getPreferenceLogic(R.id.test_pattern);
        if (logic instanceof PicturePageLogic) {
            PicturePageLogic picturePageLogic = (PicturePageLogic) logic;
            picturePageLogic.disableTestPattern();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_MENU:
            break;
        case KeyEvent.KEYCODE_BACK:
            break;
        case KeyEvent.KEYCODE_ESCAPE:
            if (!isTestPatternOff()) {
                disableTestPattern();
                return true;
            }
            break;
        default:
            break;
        }
        return super.onKeyDown(keyCode, event);
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
        switch (preference.getId()) {
        case R.id.page_picture_mode:
            showPage(PictureModeFragment.class, R.string.str_picture_mode);
            break;
        case R.id.page_non_linear:
            showPage(NonLinearFragment.class, R.string.str_non_linear);
            break;
        case R.id.page_adc_adjust:
            String currentTvInputSource = TvInputUtils.getCurrentInput(getContext());
            if(TvInputUtils.isYpbpr(currentTvInputSource) || TvInputUtils.isVga(currentTvInputSource)){
                showPage(AdcAdjustFragment.class, R.string.str_adc_adjust);
            }else {
                AppToast.showToast(getActivity(), R.string.support_reminder, Toast.LENGTH_SHORT);
            }

            break;
        case R.id.page_white_balance:
            showPage(WhiteBalanceAdjustFragment.class, R.string.str_white_balance);
            break;
        case R.id.page_over_scan:
            showPage(OverScanFragment.class, R.string.str_over_scan);
            break;
        case R.id.page_ssc_adjust:
            showPage(SscAdjustFragment.class, R.string.str_ssc_adjust);
            break;
        case R.id.page_pq:
            showPage(PQFragment.class, R.string.picture_mode_pq);
            break;
        default:
            break;
        }
    }

}
