package com.realtek.fullfactorymenu.preference;

import com.realtek.fullfactorymenu.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Preference extends LinearLayout implements OnClickListener, OnHoverListener {

    protected TextView tvTitle;
    protected CharSequence mTitle;

    protected PreferenceClickListener mClickListener;

    public Preference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Preference, defStyleAttr, defStyleRes);
        mTitle = a.getString(R.styleable.Preference_title);
        int layoutId = a.getResourceId(R.styleable.Preference_layout, 0);
        int backgroundId = a.getResourceId(R.styleable.Preference_background, 0);
        a.recycle();

        if (layoutId == 0) {
            inflateView();
        } else {
            View.inflate(context, layoutId, this);
        }

        if (backgroundId != 0) {
            setBackgroundResource(backgroundId);
        }

        setFocusable(true);
        setFocusableInTouchMode(true);

        super.setOnClickListener(this);
        super.setOnHoverListener(this);

        getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                initView();
                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    public Preference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.PreferenceStyle_Default);
    }

    public Preference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Preference(Context context) {
        this(context, null);
    }

    protected void inflateView() {
        View.inflate(getContext(), R.layout.widget_preference, this);
    }

    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.preference_title);

        setTitle(mTitle);

        tvTitle.setSelected(hasFocus());
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (tvTitle != null) {
            tvTitle.setSelected(gainFocus);
        }
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onPreferenceClick(this);
        }
    }

    @Override
    public boolean onHover(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_ENTER:
                requestFocus();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public final void setOnClickListener(OnClickListener l) {

    }

    @Override
    public final void setOnHoverListener(OnHoverListener l) {

    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        if (tvTitle != null) {
            tvTitle.setText(mTitle);
        }
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public void setPreferenceClickListener(PreferenceClickListener l) {
        mClickListener = l;
    }

    public interface PreferenceClickListener {

        void onPreferenceClick(Preference preference);

    }

}
