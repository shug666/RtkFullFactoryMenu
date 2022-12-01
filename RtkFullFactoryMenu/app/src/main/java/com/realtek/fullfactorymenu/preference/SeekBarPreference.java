package com.realtek.fullfactorymenu.preference;

import java.util.Locale;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.utils.LogHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekBarPreference extends Preference {

    protected static final String TAG = "SeekBarPreference";

    protected SeekBar seekBar;

    protected TextView tvValue;

    protected int mMinValue;

    protected int mMaxValue;

    protected int mProgress;

    protected boolean mHasTemporaryProgress;
    protected int mTemporaryProgress;

    protected int mIncrement;

    protected String mDisplayFormat;

    protected DisplayHelper mDisplayHelper;

    protected final SeekBarChangeListener mSeekBarChangeListener = new SeekBarChangeListener();

    private PreferenceProgressChangeListener mProgressChangeListener;

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference, defStyleAttr, defStyleRes);
        mMinValue = a.getInt(R.styleable.SeekBarPreference_minValue, 0);
        mMaxValue = a.getInt(R.styleable.SeekBarPreference_maxValue, 100);
        mProgress = a.getInt(R.styleable.SeekBarPreference_progress, 0);
        mIncrement = a.getInt(R.styleable.SeekBarPreference_increment, 1);
        mDisplayFormat = a.getString(R.styleable.SeekBarPreference_displayFormat);
        a.recycle();
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.PreferenceStyle_Default);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void inflateView() {
        View.inflate(getContext(), R.layout.widget_seekbar_preference, this);
    }

    @Override
    protected void initView() {
        super.initView();

        seekBar = (SeekBar) findViewById(R.id.progress_bar);
        tvValue = (TextView) findViewById(R.id.preference_value);

        init(mMinValue, mMaxValue, mProgress, mIncrement);

        seekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event) || seekBar.dispatchKeyEvent(event);
    }

    private void updateProgressValue(int progress) {
        if (tvValue != null) {
            if (mDisplayHelper == null) {
                String format = mDisplayFormat == null ? "%d" : mDisplayFormat;
                tvValue.setText(String.format(Locale.ROOT, format, mProgress));
            } else {
                tvValue.setText(mDisplayHelper.display(mProgress));
            }
        }
    }

    private void updateProgress(int progress) {
        mProgress = progress;
        mHasTemporaryProgress = false;
        mTemporaryProgress = progress;

        if (seekBar != null) {
            seekBar.setProgress(mProgress - mMinValue);
        }
        updateProgressValue(mProgress);
    }

    private void updateProgressTemporarily(int progress) {
        mHasTemporaryProgress = true;
        mTemporaryProgress = progress;

        updateProgressValue(mTemporaryProgress);
    }

    public void init(int min, int max, int progress, int increment, String displayFormat, DisplayHelper displayHelper) {
        if (!(progress >= min && progress <= max && max > min && increment > 0)) {
            LogHelper.w(TAG, "min: %x, max: %x, progress: %x, increment: %x.", min, max, progress, increment);
            return;
        }
        mMinValue = min;
        mMaxValue = max;
        mIncrement = increment;
        mDisplayFormat = displayFormat;
        mDisplayHelper = displayHelper;

        if (seekBar != null) {
            seekBar.setMax(mMaxValue - mMinValue);
            seekBar.setKeyProgressIncrement(mIncrement);
        }
        updateProgress(progress);
    }

    public void init(int min, int max, int progress, int increment, DisplayHelper displayHelper) {
        init(min, max, progress, increment, mDisplayFormat, displayHelper);
    }

    public void init(int min, int max, int progress, int increment) {
        init(min, max, progress, increment, mDisplayFormat, mDisplayHelper);
    }

    public void init(int min, int max, int progress, DisplayHelper displayHelper) {
        init(min, max, progress, mIncrement, mDisplayFormat, displayHelper);
    }

    public void init(int min, int max, int progress) {
        init(min, max, progress, mIncrement, mDisplayFormat, mDisplayHelper);
    }

    public void init(int progress, DisplayHelper displayHelper) {
        init(mMinValue, mMaxValue, progress, mIncrement, mDisplayFormat, displayHelper);
    }

    public void init(int progress) {
        init(mMinValue, mMaxValue, progress, mIncrement, mDisplayFormat, mDisplayHelper);
    }

    public int getMinValue() {
        return mMinValue;
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public int getProgress() {
        return mProgress;
    }

    public int getTemporaryProgress() {
        return mTemporaryProgress;
    }

    public int getIncrement() {
        return mIncrement;
    }

    public void setProgress(int progress) {
        if (!(progress >= mMinValue && progress <= mMaxValue)) {
            return;
        }

        updateProgress(progress);

        if (mProgressChangeListener != null) {
            mProgressChangeListener.onProgressChange(this, progress);
        }
    }

    public boolean hasTemporaryProgress() {
        return mHasTemporaryProgress;
    }

    public void setProgressTemporarily(int progress) {
        if (!(progress >= mMinValue && progress <= mMaxValue)) {
            return;
        }

        updateProgressTemporarily(progress);
    }

    public void applyTemporaryProgress() {
        if (mHasTemporaryProgress) {
            if (mProgress == mTemporaryProgress) {
                return;
            }
            setProgress(mTemporaryProgress);
        }
    }

    public void cancelTemporaryProgress() {
        if (mHasTemporaryProgress) {
            mHasTemporaryProgress = false;
            mTemporaryProgress = mProgress;
            updateProgressValue(mProgress);
        }
    }

    public void setPreferenceProgressChangeListener(PreferenceProgressChangeListener listener) {
        mProgressChangeListener = listener;
    }

    private class SeekBarChangeListener implements OnSeekBarChangeListener {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                setProgress(progress + mMinValue);
            } else {
                updateProgress(progress + mMinValue);
            }
        }

    }

    public static interface PreferenceProgressChangeListener {

        void onProgressChange(SeekBarPreference preference, int progress);

    }

    public static interface DisplayHelper {

        public String display(int progress);

    }

}
