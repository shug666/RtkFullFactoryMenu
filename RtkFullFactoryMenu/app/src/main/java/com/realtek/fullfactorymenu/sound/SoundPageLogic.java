package com.realtek.fullfactorymenu.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.session.MediaSessionLegacyHelper;
import android.media.tv.TvInputInfo;
import android.view.textclassifier.Log;

import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.PreferenceContainer;
import com.realtek.fullfactorymenu.preference.SeekBarPreference;
import com.realtek.fullfactorymenu.preference.StatePreference;
import com.realtek.fullfactorymenu.preference.SumaryPreference;
import com.realtek.tv.AQ;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Administrator on 2019/1/22.
 */

public class SoundPageLogic extends LogicInterface {

    private static final String TAG = SoundPageLogic.class.getSimpleName();

    private AQ mAq;
    private final int[] balance = {-50, 0, 50};
    private final int[] volume = {30, 0, 50, 100};
    private int default_volume;
    private SumaryPreference mBalanceCurr;
    private boolean isMute;

    private AQ getAq() {
        if (mAq == null) {
            mAq = new AQ();
        }
        return mAq;
    }

    public SoundPageLogic(PreferenceContainer container) {
        super(container);
    }

    @Override
    public void init() {
        StatePreference mInputSource = (StatePreference) mContainer.findPreferenceById(R.id.input_source);
        ArrayList<TvInputInfo> inputs = new ArrayList<TvInputInfo>();
        TvInputInfo current = FactoryApplication.getInstance().getInputList(inputs);
        int count = inputs.size();
        String[] inputNames = new String[count];
        TvInputInfo[] inputValues = new TvInputInfo[count];
        int currentIndex = 0;
        TvInputInfo item;
        for (int i = 0; i < count; i++) {
            item = inputs.get(i);
            inputNames[i] = item.loadLabel(mContext).toString();
            inputValues[i] = item;
            if (Objects.equals(current, item)) {
                currentIndex = i;
            }
        }
        mInputSource.init(inputNames, inputValues, currentIndex);
        mInputSource.setEnabled(false);
        mInputSource.setFocusable(false);
        isMute = isStreamMute(mContext);
        default_volume = getVolume(mContext);
        volume[0] = default_volume;

        StatePreference mVolume = (StatePreference) mContainer.findPreferenceById(R.id.volume);
        StatePreference mBalance = (StatePreference) mContainer.findPreferenceById(R.id.balance);
        mBalanceCurr = (SumaryPreference) mContainer.findPreferenceById(R.id.balance_default);
        SeekBarPreference mBalanceSeekbar = (SeekBarPreference) mContainer.findPreferenceById(R.id.balance_seekbar);

        mVolume.init(0);
        getAq().setBalanceLevel(0);
        mBalance.init(1);
        mBalanceCurr.setSumary(getAq().getBalanceLevel() + "");
        mBalanceSeekbar.init(getAq().getBalanceLevel());
    }

    @Override
    public void deinit() {
        if (!isMute) {
            setVolume(mContext, default_volume, false);
        } else {
            setStreamMute(mContext, true);
        }
        getAq().setBalanceLevel(0);
    }

    @Override
    public void onProgressChange(SeekBarPreference preference, int progress) {
        if (preference.getId() == R.id.balance_seekbar) {
            getAq().setBalanceLevel(progress);
        }
    }

    @Override
    public void onPreferenceIndexChange(StatePreference preference, int previous, int current) {
        switch (preference.getId()) {
            case R.id.volume:
                if (current >= volume.length) {
                    current = 0;
                }
                setVolume(mContext, volume[current], true);
                break;
            case R.id.balance:
                if (current >= balance.length) {
                    current = 0;
                }
                Log.d(TAG, "setBalanceLevel = " + balance[current]);
                getAq().setBalanceLevel(balance[current]);
                mBalanceCurr.setSumary(getAq().getBalanceLevel() + "");
                break;
            default:
                break;
        }
    }

    private void setVolume(Context context, int volume, boolean showUiEnable){
        Log.d(TAG, "setVolume = " + volume);
        if (volume < 0 || volume > 100)
            return;
        int flags = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_VIBRATE
                | AudioManager.FLAG_FROM_KEY;
        if (showUiEnable) {
            flags |= AudioManager.FLAG_SHOW_UI;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null){
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC ,volume,0);
        } else {
            return;
        }
        MediaSessionLegacyHelper.getHelper(context).sendAdjustVolumeBy(AudioManager.USE_DEFAULT_STREAM_TYPE, 0, flags);
    }

    private int getVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null)
            return 0;
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private boolean isStreamMute(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null)
            return false;
        return audioManager.isStreamMute(AudioManager.STREAM_MUSIC);
    }

    private void setStreamMute(Context context, boolean isMute) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null)
            return;
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, isMute);
    }
}
