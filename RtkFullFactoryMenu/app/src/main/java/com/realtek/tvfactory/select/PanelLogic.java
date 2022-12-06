package com.realtek.tvfactory.select;

import com.realtek.tvfactory.api.impl.PanelApi;
import com.realtek.tvfactory.api.manager.TvCommonManager;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.R;

public class PanelLogic extends LogicInterface {

    private static final String TAG = "PanelLogic";
    private StatePreference mTiMode = null;
    private StatePreference mBitMode = null;
    private StatePreference mSwapMode = null;
    private StatePreference mMirrorMode = null;
    private StatePreference mDualMode = null;
    private PanelApi mPanelApi;
    private static final String mGetPanelInfo = TvCommonManager.COMMAND_GET_PANEL_INFO;

    public PanelLogic(PreferenceContainer container) {
        super(container);
    }

    public void init() {

        mPanelApi = PanelApi.getInstance();
        mTiMode = (StatePreference)mContainer.findPreferenceById(R.id.ti_mode);
        mBitMode = (StatePreference)mContainer.findPreferenceById(R.id.bit_mode);
        mSwapMode = (StatePreference)mContainer.findPreferenceById(R.id.swap_mode);
        mMirrorMode = (StatePreference)mContainer.findPreferenceById(R.id.mirror_mode);
        mDualMode = (StatePreference)mContainer.findPreferenceById(R.id.dual_mode);
        int[] PanelInfo = mPanelApi.setTvosCommonCommand(mGetPanelInfo);

        if (mTiMode != null) {
            mTiMode.init(PanelInfo[0]);
        }
        if (mBitMode != null) {
            mBitMode.init(PanelInfo[1]);
        }
        if (mSwapMode != null) {
            mSwapMode.init(PanelInfo[2]);
        }

        mMirrorMode.init(PanelInfo[3]);
        if (mDualMode != null) {
            mDualMode.init(mPanelApi.getIntegerValue(TvCommonManager.DUAL_MODE));
        }
    }

    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {

        switch (preference.getId()){
            case R.id.ti_mode:
                if (mTiMode != null) {
                    mPanelApi.setIntegerValue(TvCommonManager.COMMAND_SET_TI_MODE, current);
                }
                break;
            case R.id.bit_mode:
                if (mBitMode != null) {
                    mPanelApi.setIntegerValue(TvCommonManager.BIT_MODE, current);
                }
                break;
            case R.id.swap_mode:
                if (mSwapMode != null) {
                    mPanelApi.setIntegerValue(TvCommonManager.COMMAND_SET_SWAP_MODE, current);
                }
                break;
            case R.id.mirror_mode:
                if (mMirrorMode != null) {
                    mPanelApi.setIntegerValue(TvCommonManager.MIRROR_MODE, current);
                }
                break;
            case R.id.dual_mode:
                if (mDualMode != null) {
                    mPanelApi.setIntegerValue(TvCommonManager.DUAL_MODE, current);
                }
                break;
        }

    }


    public void deinit() {

    }

    public void onProgressChange(SeekBarPreference preference, int progress) {

    }
}
