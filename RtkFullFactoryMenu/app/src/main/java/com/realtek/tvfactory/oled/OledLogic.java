package com.realtek.tvfactory.oled;

import android.annotation.SuppressLint;
import android.view.View;

import com.android.tv.common.TvCommonConstants;
import com.android.tv.common.TvCommonUtils;
import com.realtek.tv.Factory;
import com.realtek.tv.RtkSettingConstants;
import com.realtek.tvfactory.R;
import com.realtek.tvfactory.api.impl.PanelApi;
import com.realtek.tvfactory.api.manager.TvCommonManager;
import com.realtek.tvfactory.logic.LogicInterface;
import com.realtek.tvfactory.preference.Preference;
import com.realtek.tvfactory.preference.PreferenceContainer;
import com.realtek.tvfactory.preference.SeekBarPreference;
import com.realtek.tvfactory.preference.StatePreference;
import com.realtek.tvfactory.preference.SumaryPreference;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class OledLogic extends LogicInterface {

    private final double ONE_HOUR = 60 * 60 * 1000;
    private StatePreference moffrsOrJbEnable = null;
    private StatePreference mTPC = null;
    private StatePreference mCPC = null;
    private StatePreference mLEA = null;
    private StatePreference mOrbit = null;
    private StatePreference mPLC = null;
    private StatePreference mPqBypass = null;
    private SumaryPreference mTotalTIme = null;
    private SumaryPreference mOffRSExecuted = null;
    private SumaryPreference mOffRSLastExecution = null;
    private SumaryPreference mJBExecuted = null;
    private SumaryPreference mJBLastExecution = null;
    private SumaryPreference mPanelMatching = null;

    private Preference mOffRSMode = null;
    private Preference mJbMode = null;
    private PanelApi mPanelApi;
    private static final String mGetPanelInfo = TvCommonManager.COMMAND_GET_PANEL_INFO;

    public OledLogic(PreferenceContainer container) {
        super(container);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void init() {
        mPanelApi = PanelApi.getInstance();

        moffrsOrJbEnable = (StatePreference)mContainer.findPreferenceById(R.id.str_OffRSOrJb_enable);
        mOffRSMode = (Preference) mContainer.findPreferenceById(R.id.page_offrs);
        mJbMode = (Preference) mContainer.findPreferenceById(R.id.page_JB);
        mTPC = (StatePreference)mContainer.findPreferenceById(R.id.TPC);
        mCPC = (StatePreference)mContainer.findPreferenceById(R.id.CPC);
        mLEA = (StatePreference)mContainer.findPreferenceById(R.id.LEA);
        mOrbit = (StatePreference)mContainer.findPreferenceById(R.id.Orbit);
        mPLC = (StatePreference)mContainer.findPreferenceById(R.id.PLC);
        mPqBypass = (StatePreference)mContainer.findPreferenceById(R.id.PQ_Bypass);
        mTotalTIme = (SumaryPreference)mContainer.findPreferenceById(R.id.Total_Running_Time);
        mOffRSExecuted = (SumaryPreference)mContainer.findPreferenceById(R.id.OffRS_Executed);
        mOffRSLastExecution = (SumaryPreference)mContainer.findPreferenceById(R.id.OffRS_Last_Execution);
        mJBExecuted = (SumaryPreference)mContainer.findPreferenceById(R.id.JB_Executed);
        mJBLastExecution = (SumaryPreference)mContainer.findPreferenceById(R.id.JB_Last_Execution);
        mPanelMatching = (SumaryPreference)mContainer.findPreferenceById(R.id.Panel_Matching);


        int[] PanelInfo = mPanelApi.setTvosCommonCommand(mGetPanelInfo);

        moffrsOrJbEnable.init(TvCommonUtils.getGlobalSettings(mContext, RtkSettingConstants.OLED_MENU_ENABLE, 1));

        mTPC.init(PanelInfo[4]);
        mCPC.init(PanelInfo[5]);
        mLEA.init(PanelInfo[6]);
        mOrbit.init(PanelInfo[7]);
        mPLC.init(PanelInfo[8]);
        mPqBypass.init(0);
        mPqBypass.setVisibility(View.GONE);


        DecimalFormat df = new DecimalFormat("###.#");
        df.setRoundingMode(RoundingMode.DOWN);

        mTotalTIme.setEnabled(false);
        Factory factory = new Factory();
        String systemRunningTime = factory.getSystemRunningTime();
        long totalTime = Long.parseLong(systemRunningTime.split("\\|")[0]);
        mTotalTIme.setSumary(df.format(totalTime / ONE_HOUR)+"(Hours)");

        int runOffRSCounts = TvCommonUtils.
                getExecuteCount(TvCommonConstants.OFFRS_COUNT_IDX);
        mOffRSExecuted.setSumary(String.valueOf(runOffRSCounts));
        mOffRSExecuted.setEnabled(false);
        long offrsuptime = TvCommonUtils.getUptime(TvCommonConstants.OFFRS_UPTIME_IDX);
        mOffRSLastExecution.setSumary(df.format(offrsuptime/ONE_HOUR)+"(Hours)");
        mOffRSLastExecution.setEnabled(false);

        int runJBCounts = TvCommonUtils
                .getExecuteCount(TvCommonConstants.JB_COUNT_IDX);
        mJBExecuted.setSumary(String.valueOf(runJBCounts));
        mJBExecuted.setEnabled(false);
        long jbUptime = TvCommonUtils.getUptime(TvCommonConstants.JB_UPTIME_IDX);
        mJBLastExecution.setSumary(df.format(jbUptime/ONE_HOUR)+"(Hours)");
        mJBLastExecution.setEnabled(false);


        mPanelMatching.setSumary("NG");
        mPanelMatching.setVisibility(View.GONE);

    }

    @Override
    public void deinit() {

    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {

    }

    @SuppressLint("WrongConstant")
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {

        switch (preference.getId()){
            case R.id.str_OffRSOrJb_enable:
                TvCommonUtils.putGlobalSettings(mContext, RtkSettingConstants.OLED_MENU_ENABLE, current);
                break;
            case R.id.TPC:
                mPanelApi.setIntegerValue(TvCommonManager.TPC,current);
                break;
            case R.id.CPC:
                mPanelApi.setIntegerValue(TvCommonManager.CPC,current);
                break;
            case R.id.LEA:
                mPanelApi.setIntegerValue(TvCommonManager.LEA,current);
                break;
            case R.id.Orbit:
                mPanelApi.setIntegerValue(TvCommonManager.Orbit,current);
                break;
            case R.id.PLC:
                mPanelApi.setIntegerValue(TvCommonManager.PLC,current);
                break;
            case R.id.PQ_Bypass:
                break;
        }


    }
}
