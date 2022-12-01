package com.realtek.fullfactorymenu.preference;

import com.realtek.fullfactorymenu.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Rect;
import android.widget.TextView;

public class SumaryPreference extends Preference {

    protected TextView tvValue;
    protected CharSequence mSumary;

    public SumaryPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SumaryPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.PreferenceStyle_Default);
    }

    public SumaryPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SumaryPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void inflateView() {
        View.inflate(getContext(), R.layout.widget_sumary_preference, this);

        tvValue = (TextView) findViewById(R.id.preference_value);

        tvValue.setText(mSumary);

        tvValue.setSelected(hasFocus());
    }

    public void setSumary(CharSequence sumary) {
        mSumary = sumary;

        if (tvValue != null) {
            tvValue.setText(mSumary);
        }
    }

    public CharSequence getSumary() {
        return mSumary;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if (tvValue != null) {
            tvValue.setSelected(gainFocus);
        }
    }
}
