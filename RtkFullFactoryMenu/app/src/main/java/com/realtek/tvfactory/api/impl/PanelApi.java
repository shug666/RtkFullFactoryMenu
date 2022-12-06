package com.realtek.tvfactory.api.impl;

import android.content.Context;
import android.text.TextUtils;


import com.realtek.tv.PQ;
import com.realtek.tvfactory.FactoryApplication;
import com.realtek.tvfactory.api.manager.TvCommonManager;

public class PanelApi {
    private static final String TAG = "PanelApi";

    private static PanelApi mInstance = null;
    private final FactoryApplication mFactoryApplication;
    private final Context mContext;
    private final PQ mPQ = new PQ();

    //panel type
    private static final int TI_MODE = 0;
    private static final int BIT_MODE = 1;
    private static final int SWAP_MODE = 2;
    private static final int DUAL_MODE = 3;

    public PanelApi(){
        mInstance = this;
        mFactoryApplication = FactoryApplication.getInstance();
        mContext = mFactoryApplication.getApplicationContext();
    }

    public static PanelApi getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (PanelApi.class) {
                if (mInstance == null) {
                    mInstance = new PanelApi();
                }
            }
        }
        return mInstance;
    }

    public void setIntegerValue(String key, int value) {
        switch (key) {
            case TvCommonManager.COMMAND_SET_TI_MODE:
                mFactoryApplication.getExtTv().extTv_tv001_SetPanelConfig(TI_MODE, value);
                break;
            case TvCommonManager.BIT_MODE:
                mFactoryApplication.getExtTv().extTv_tv001_SetPanelConfig(BIT_MODE, value);
                break;
            case TvCommonManager.COMMAND_SET_SWAP_MODE:
                mFactoryApplication.getExtTv().extTv_tv001_SetPanelConfig(SWAP_MODE, value);
                break;
            case TvCommonManager.MIRROR_MODE:
                mFactoryApplication.getExtTv().extTv_tv001_SetValueInt("Misc_Panel_VideoMirrorV", value);
                break;
            case TvCommonManager.DUAL_MODE:
                mFactoryApplication.getExtTv().extTv_tv001_SetPanelConfig(DUAL_MODE, value);
                break;
            case TvCommonManager.TPC:
                mPQ.setOledTconTPC(value == 1);
                break;
            case TvCommonManager.CPC:
                mPQ.setOledTconCPC(value == 1);
                break;
            case TvCommonManager.LEA:
                mPQ.setOledTconLEA(value == 1);
                break;
            case TvCommonManager.Orbit:
                mPQ.setOledTconOrbit(value == 1);
                break;
            case TvCommonManager.PLC:
                try{
                    mPQ.setOledTconPLC(value == 1);
                }catch (NoSuchMethodError e){
                }
                break;
        }
    }

    public int getIntegerValue(String key) {
        switch (key) {
            case TvCommonManager.DUAL_MODE:
                return mFactoryApplication.getExtTv().extTv_tv001_GetPanelConfig(DUAL_MODE);
        }
        return 0;
    }

    public int[] setTvosCommonCommand(String command)  {
        if (command == null) {
            return null;
        }
        if (TextUtils.isEmpty(command)) {
            return null;
        }
        if (TvCommonManager.COMMAND_GET_PANEL_INFO.equals(command)) {

            int[] panelInfo = new int[9];
            panelInfo[0] = mFactoryApplication.getExtTv().extTv_tv001_GetPanelConfig(TI_MODE);
            panelInfo[1] = mFactoryApplication.getExtTv().extTv_tv001_GetPanelConfig(BIT_MODE);
            panelInfo[2] = mFactoryApplication.getExtTv().extTv_tv001_GetPanelConfig(SWAP_MODE);
//            int videoMirrorH = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Misc_Panel_VideoMirrorH");
            int videoMirrorV = mFactoryApplication.getExtTv().extTv_tv001_GetValueInt("Misc_Panel_VideoMirrorV");
            panelInfo[3] = videoMirrorV;
            panelInfo[4] = mPQ.getOledTconTPC() ? 1 : 0;
            panelInfo[5] = mPQ.getOledTconCPC() ? 1 : 0;
            panelInfo[6] = mPQ.getOledTconLEA() ? 1 : 0;
            panelInfo[7] = mPQ.getOledTconOrbit() ? 1 : 0;

            try{
                panelInfo[8] = mPQ.getOledTconPLC() ? 1 : 0;
            }catch (NoSuchMethodError e){
                panelInfo[8] = 1;
            }

            return panelInfo;
        }
        return null;
    }
}
