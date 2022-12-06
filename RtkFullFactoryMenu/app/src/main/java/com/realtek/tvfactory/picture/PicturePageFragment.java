package com.realtek.tvfactory.picture;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.Toast;

import com.realtek.tvfactory.BaseFragment;
import com.realtek.tvfactory.FactoryMenuFragment;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.PreferenceFragment;
import com.realtek.tvfactory.utils.AppToast;
import com.realtek.tvfactory.utils.TvInputUtils;

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
                String currentTvInputSource = TvInputUtils.getCurrentInput(preference.getContext());
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
            case R.id.page_pq:
                showPage(PQFragment.class, R.string.picture_mode_pq);
                break;
            default:
                break;
        }
    }

}
