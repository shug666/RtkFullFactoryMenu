package com.realtek.fullfactorymenu;

import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_ATSC;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_ATV;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_CVBS;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_DVBC;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_DVBS;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_DVBT;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_HDMI;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_HDMI2;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_HDMI3;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_ISDB;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_VGA;
import static com.realtek.fullfactorymenu.api.manager.TvCommonManager.INPUT_SOURCE_YPBPR;
import static com.realtek.fullfactorymenu.utils.Constants.MANUFACTURER_BVT;
import static com.realtek.fullfactorymenu.utils.Constants.MANUFACTURER_CH;
import static com.realtek.fullfactorymenu.utils.Constants.MANUFACTURER_KK;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.exttv.tv.ExtTv;
import com.realtek.fullfactorymenu.utils.ByteTransformUtils;
import com.realtek.fullfactorymenu.utils.TvInputUtils;
import com.realtek.fullfactorymenu.utils.Utils;
import com.realtek.tv.AQ;
import com.realtek.tv.DTVVideoAvailability;
import com.realtek.tv.Factory;
import com.realtek.tv.PQ;
import com.realtek.tv.SystemControl;
import com.realtek.tv.Tv;
import com.realtek.tv.VSC;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FactoryApplication extends Application {
    private static final String TAG = "FactoryApplication";

    private static FactoryApplication instance;
    public static final boolean CUSTOMER_IS_BVT;
    public static final boolean CUSTOMER_IS_CH;
    public static final boolean CUSTOMER_IS_KK;

    static {
        CUSTOMER_IS_BVT = MANUFACTURER_BVT.equals(ByteTransformUtils.parseAscii(
                SystemProperties.get("ro.product.manufacturer", "unknow").toUpperCase(Locale.ENGLISH)));
        CUSTOMER_IS_CH = MANUFACTURER_CH.equals(ByteTransformUtils.parseAscii(
                SystemProperties.get("ro.product.manufacturer", "unknow").toUpperCase(Locale.ENGLISH)));
        CUSTOMER_IS_KK = MANUFACTURER_KK.equals(ByteTransformUtils.parseAscii(
                SystemProperties.get("ro.product.manufacturer", "unknow").toUpperCase(Locale.ENGLISH)));
    }
    private Tv mTv;
    private AQ mAq;
    private PQ mPq;
    private VSC mVsc;
    private SystemControl mSysCtrl;
    private Factory mFactory;
    private ExtTv mExtTv;

    public static final String INPUT_ID_ATSC = "com.realtek.tv.atsc/.AtscTvInputService/HW33619969";
    public static final String INPUT_ID_ISDB = "com.realtek.tv.isdb/.IsdbTvInputService/HW33619969";
    public static final String INPUT_ID_ATV = "com.realtek.tv.atv/.atvinput.AtvInputService/HW33619968";
    public static final String INPUT_ID_DVBC = "com.realtek.dtv/.tvinput.DTVTvInputService/HW33685504";
    public static final String INPUT_ID_DVBT = "com.realtek.dtv/.tvinput.DTVTvInputService/HW33685505";
    public static final String INPUT_ID_DVBS = "com.realtek.dtv/.tvinput.DTVTvInputService/HW33685506";
    public static final String INPUT_ID_CVBS = "com.realtek.tv.passthrough/.avinput.AVTvInputService/HW50593792";
    public static final String INPUT_ID_YPBPR = "com.realtek.tv.passthrough/.yppinput.YPPTvInputService/HW101056512";
    public static final String INPUT_ID_VGA = "com.realtek.tv.passthrough/.vgainput.VGATvInputService/HW117899264";
    public static final String INPUT_ID_HDMI = "com.realtek.tv.passthrough/.hdmiinput.HDMITvInputService/HW151519232";
    public static final String INPUT_ID_HDMI2 = "com.realtek.tv.passthrough/.hdmiinput.HDMITvInputService/HW151519488";
    public static final String INPUT_ID_HDMI3 = "com.realtek.tv.passthrough/.hdmiinput.HDMITvInputService/HW151519744";

    // Channel Handler
    public final static int MSG_KILL_PROCESS = 0;
    public final static int MSG_PLAY = 1;
    private final static String[] PROCESS_NAMES = {"com.android.providers.tv"};
    private static final String ACTION_RELOAD_CHANNELS_FROM_RTK_TVPROVIDER = "com.realtek.tv.reload_channels_from_rtktvprovider";

    private List<TvInputInfo> tvInputs;
    private TvInputManager mTvInputManager;
    private Map<String, TvInputInfo> mSourceList = new HashMap<>();
    private SparseArray<String> mInputSourceList = new SparseArray<String>();


    @Override
    public void onCreate() {
        instance = this;
        initInputList();
        if (TextUtils.isEmpty(TvInputUtils.getCurrentInput(this))) {
            if (TvInputUtils.isATSC()) {
                TvInputUtils.setCurrentInput(this, INPUT_ID_ATSC);
            } else if (TvInputUtils.isISDB()) {
                TvInputUtils.setCurrentInput(this, INPUT_ID_ISDB);
            } else if (TvInputUtils.isDVB()) {
                TvInputUtils.setCurrentInput(this, INPUT_ID_DVBT);
            }
        }
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "on Terminate");
        Settings.Secure.putInt(getContentResolver(), "tv_user_setup_complete", 1);
        super.onTerminate();
    }

    public FactoryApplication() {
    }

    public static FactoryApplication getInstance() {
        return instance;
    }

    public Tv getTv() {
        if (mTv == null) {
            mTv = new Tv();
        }
        return mTv;
    }

    public AQ getAq() {
        if (mAq == null) {
            mAq = new AQ();
        }
        return mAq;
    }

    public PQ getPq() {
        if (mPq == null) {
            mPq = new PQ();
        }
        return mPq;
    }

    public VSC getVsc() {
        if (mVsc == null) {
            mVsc = new VSC();
        }
        return mVsc;
    }

    public SystemControl getSysCtrl() {
        if (mSysCtrl == null) {
            mSysCtrl = new SystemControl();
        }
        return mSysCtrl;
    }

    public Factory getFactory() {
        if (mFactory == null) {
            mFactory = new Factory();
        }
        return mFactory;
    }

    public ExtTv getExtTv() {
        if (mExtTv == null) {
            mExtTv = new ExtTv();
        }
        return mExtTv;
    }

    public int getInputSource(String inputId) {
        int index = mInputSourceList.indexOfValueByValue(inputId);
        Log.d(TAG, "inputId: " + inputId + " index : " + index);
        if (index == -1) {
            Log.d(TAG, String.format("inputId %s index is -1",inputId));
            TvInputManager inputManager = getTvInputManager();
            TvInputInfo tvInputInfo = inputManager.getTvInputInfo(inputId);
            String parentId = tvInputInfo.getParentId();
            if (parentId != null)
                index = mInputSourceList.indexOfValueByValue(parentId);
            Log.d(TAG, String.format("parentId %s index is %d",parentId, index));
        }
        if (index >= 0) {
            return mInputSourceList.keyAt(index);
        }
        return -1;
    }

    public String getInputId(int inputSource) {
        return mInputSourceList.get(inputSource);
    }

    private TvInputManager getTvInputManager() {
        if (mTvInputManager == null) {
            mTvInputManager = (TvInputManager) getSystemService(Context.TV_INPUT_SERVICE);
        }
        return mTvInputManager;
    }

    private void initInputList() {
        if (TvInputUtils.isATSC()) {
            mInputSourceList.put(INPUT_SOURCE_ATSC, INPUT_ID_ATSC);
        } else if (TvInputUtils.isISDB()) {
            mInputSourceList.put(INPUT_SOURCE_ISDB, INPUT_ID_ISDB);
        } else if (TvInputUtils.isDVB()) {
            mInputSourceList.put(INPUT_SOURCE_ATV, INPUT_ID_ATV);
            mInputSourceList.put(INPUT_SOURCE_DVBC, INPUT_ID_DVBC);
            mInputSourceList.put(INPUT_SOURCE_DVBT, INPUT_ID_DVBT);
            mInputSourceList.put(INPUT_SOURCE_DVBS, INPUT_ID_DVBS);
        }
        mInputSourceList.put(INPUT_SOURCE_CVBS, INPUT_ID_CVBS);
        mInputSourceList.put(INPUT_SOURCE_YPBPR, INPUT_ID_YPBPR);
        mInputSourceList.put(INPUT_SOURCE_VGA, INPUT_ID_VGA);
        mInputSourceList.put(INPUT_SOURCE_HDMI, INPUT_ID_HDMI);
        mInputSourceList.put(INPUT_SOURCE_HDMI2, INPUT_ID_HDMI2);
        mInputSourceList.put(INPUT_SOURCE_HDMI3, INPUT_ID_HDMI3);
        tvInputs = getTvInputManager().getTvInputList();
        for (int i = 0; i < tvInputs.size(); i++) {
            CharSequence label = tvInputs.get(i).loadLabel(this);
            TvInputInfo tvInputInfo = tvInputs.get(i);
            String tvInputId = tvInputs.get(i).getId();
            Log.d(TAG, "input source name :" + label + " input source id :" + tvInputId);
            switch (label.toString()) {
                case "TV":
                    if (TvInputUtils.isATSC()) {
                        mSourceList.put(INPUT_ID_ATSC, tvInputInfo);
                    } else if (TvInputUtils.isISDB()) {
                        mSourceList.put(INPUT_ID_ISDB, tvInputInfo);
                    }
                    break;
                case "DVBC":
                case "CABLE":
                    mSourceList.put(INPUT_ID_DVBC, tvInputInfo);
                    break;
                case "DVBT":
                case "DVBT2":
                case "ANTENNA":
                    mSourceList.put(INPUT_ID_DVBT, tvInputInfo);
                    break;
                case "DVBS":
                case "DVBS2":
                case "SATELLITE":
                    mSourceList.put(INPUT_ID_DVBS, tvInputInfo);
                    break;
                case "ATV":
                    mSourceList.put(INPUT_ID_ATV, tvInputInfo);
                    break;
                case "AV":
                    mSourceList.put(INPUT_ID_CVBS, tvInputInfo);
                    break;
                case "YPBPR":
                case "YPP":
                    mSourceList.put(INPUT_ID_YPBPR, tvInputInfo);
                    break;
                case "VGA":
                    mSourceList.put(INPUT_ID_VGA, tvInputInfo);
                    break;
                case "HDMI1":
                    mSourceList.put(INPUT_ID_HDMI, tvInputInfo);
                    break;
                case "HDMI2":
                    mSourceList.put(INPUT_ID_HDMI2, tvInputInfo);
                    break;
                case "HDMI3":
                    mSourceList.put(INPUT_ID_HDMI3, tvInputInfo);
                    break;
                default:
                    break;
            }
        }
    }

    public TvInputInfo getInputList(List<TvInputInfo> inputs) {
        inputs.addAll(tvInputs);
        String currentInput = TvInputUtils.getCurrentInput(this);
        TvInputInfo tvInputInfo = mSourceList.get(currentInput);
        if (tvInputInfo != null) {
            return tvInputInfo;
        }
        TvInputManager tvInputManager = getTvInputManager();
        tvInputInfo = tvInputManager.getTvInputInfo(currentInput);
        if (tvInputInfo.getParentId() != null) {
            return tvInputManager.getTvInputInfo(tvInputInfo.getParentId());
        }
        return tvInputInfo;
    }

    public void setInputSource(TvInputInfo tvInputInfo) {
        TvInputUtils.setCurrentInput(this, tvInputInfo.getId());
    }

    public void setInputSource(String inputInfoId) {
        TvInputUtils.setCurrentInput(this, inputInfoId);
    }

    private ChannelHandler mChannelHandler = new ChannelHandler();

    public ChannelHandler getChannelHandler() {
        if (mChannelHandler == null) {
            mChannelHandler = new ChannelHandler();
        }
        return mChannelHandler;
    }

    public class ChannelHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage msg.what = " + msg.what);
            switch (msg.what) {
                case MSG_KILL_PROCESS:
                    Utils.killProcesses(FactoryApplication.this, Arrays.asList(PROCESS_NAMES));
                    broadcastFinishDTV();
                    mChannelHandler.sendEmptyMessageDelayed(MSG_PLAY, 1000);
                    break;
                case MSG_PLAY:
//                    startTv();
                    break;
                default:
                    break;
            }
        }
    }

    private void broadcastFinishDTV() {
        Log.d(TAG, "broadcastFinishDTV : ***-*** ");
        Intent intent = new Intent(ACTION_RELOAD_CHANNELS_FROM_RTK_TVPROVIDER);
        //intent.setComponent(new ComponentName("com.android.tv", "com.android.tv.receiver.ReloadChannelsReceiver"));
        intent.setFlags((intent.getFlags() | 0x01000000));
        sendBroadcast(intent);
    }

    public int getAtvVideoStandard() {
        return -1;
    }




    public boolean isSignalStable() {
        boolean videoAvailable = DTVVideoAvailability.getInstance().isVideoAvailable();
        Log.d(TAG, "videoAvailable: " +videoAvailable);
        return true;
    }

}
