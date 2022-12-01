package com.realtek.fullfactorymenu.preference;

import java.util.Arrays;

import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.utils.Predicate;
import com.realtek.fullfactorymenu.utils.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StatePreference extends Preference {

    protected static final int INVALID_INDEX = -1;

    protected ImageView ivPrevious, ivNext;

    protected TextView tvValue;

    protected String[] mEntryNames;

    protected Object[] mEntryValues;

    protected boolean[] mEntryState;

    protected int mIndex = INVALID_INDEX;

    protected boolean mCycleEnabled;

    protected int mIconVisible;

    public static final int NEVER = 0;
    public static final int ALWAYS = 1;
    public static final int FOCUSED = 2;

    protected final ClickListener mClickListener = new ClickListener();

    protected PreferenceIndexChangeListener mPreferenceIndexChangeListener;

    public StatePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatePreference, defStyleAttr, defStyleRes);
        mCycleEnabled = a.getBoolean(R.styleable.StatePreference_cycleEnabled, false);
        mIconVisible = a.getInt(R.styleable.StatePreference_iconVisible, NEVER);
        a.recycle();
    }

    public StatePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.PreferenceStyle_Default);
    }

    public StatePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatePreference(Context context) {
        this(context, null);
    }

    @Override
    protected void inflateView() {
        View.inflate(getContext(), R.layout.widget_state_preference, this);
    }

    @Override
    protected void initView() {
        super.initView();

        ivPrevious = (ImageView) findViewById(R.id.previous_state);
        ivNext = (ImageView) findViewById(R.id.next_state);

        tvValue = (TextView) findViewById(R.id.preference_value);
        init(mEntryNames, mEntryValues, mEntryState, mIndex);

        updateIconState();

        ivPrevious.setOnClickListener(mClickListener);
        ivNext.setOnClickListener(mClickListener);

        post(new Runnable() {

            @Override
            public void run() {
                if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                    ivPrevious.setImageResource(R.drawable.preference_arrow_right);
                    ivNext.setImageResource(R.drawable.preference_arrow_left);
                } else {
                    ivPrevious.setImageResource(R.drawable.preference_arrow_left);
                    ivNext.setImageResource(R.drawable.preference_arrow_right);
                }
            }
        });
    }

    private void updateIconState() {
        if (ivPrevious == null || ivNext == null) {
            return;
        }
        if (mIconVisible == NEVER) {
            ivPrevious.setVisibility(View.GONE);
            ivNext.setVisibility(View.GONE);
        } else if (mIconVisible == ALWAYS) {
            ivPrevious.setVisibility(View.VISIBLE);
            ivNext.setVisibility(View.VISIBLE);
        } else {
            ivPrevious.setVisibility(hasFocus() ? View.VISIBLE : View.GONE);
            ivNext.setVisibility(hasFocus() ? View.VISIBLE : View.GONE);
        }
    }

    private void handleIndexChange() {
        if (mIndex == INVALID_INDEX || mEntryNames == null) {
            if (tvValue != null) {
                tvValue.setText(null);
            }
        } else {
            if (tvValue != null) {
                tvValue.setText(mEntryNames[mIndex]);
            }
        }
        if (mIndex == INVALID_INDEX) {
            if (ivPrevious != null) {
                ivPrevious.setAlpha(0.35f);
            }
            if (ivNext != null) {
                ivNext.setAlpha(0.35f);
            }
        } else {
            if (ivPrevious != null) {
                int previousIndex = previousIndex();
                ivPrevious.setAlpha(previousIndex == INVALID_INDEX || previousIndex == mIndex ? 0.35f : 1f);
            }
            if (ivNext != null) {
                int nextIndex = nextIndex();
                ivNext.setAlpha(nextIndex == INVALID_INDEX || nextIndex == mIndex ? 0.35f : 1f);
            }
        }
    }

    public void init(String[] names, Object[] values, boolean[] state, int index) {
        if (names == null) {
            throw new IllegalArgumentException("The entries of StatePreference can not be null.");
        }
        mEntryNames = names;
        if (values == null || names.length != values.length) {
            mEntryValues = null;
        } else {
            mEntryValues = values;
        }
        if (state == null || names.length != state.length) {
            boolean[] defaultState = new boolean[names.length];
            Arrays.fill(defaultState, true);
            mEntryState = defaultState;
        } else {
            mEntryState = state;
        }
        if (index >= 0 && index < names.length) {
            mIndex = index;
        } else {
            mIndex = INVALID_INDEX;
        }
        handleIndexChange();
    }

    public void init(String[] names, Object[] values, int index) {
        init(names, values, mEntryState, index);
    }

    public void init(String[] names, int index) {
        init(names, mEntryValues, index);
    }

    public void init(boolean[] state, int index) {
        init(mEntryNames, mEntryValues, state, index);
    }

    public void init(int index) {
        init(mEntryNames, index);
    }

    public String[] getEntryNames() {
        return mEntryNames;
    }

    public Object[] getEntryValues() {
        return mEntryValues;
    }

    public boolean[] getEntryState() {
        return mEntryState;
    }

    public String getCurrentEntryName() {
        return getEntryName(mIndex);
    }

    public Object getCurrentEntryValue() {
        return getEntryValue(mIndex);
    }

    public String getEntryName(int index) {
        return mIndex == INVALID_INDEX || mEntryNames == null ? null : mEntryNames[index];
    }

    public Object getEntryValue(int index) {
        return mIndex == INVALID_INDEX || mEntryValues == null ? null : mEntryValues[index];
    }

    public boolean isEntryEnabled(int index) {
        return mIndex == INVALID_INDEX || mEntryState == null ? false : mEntryState[index];
    }

    public int entryIndexOf(final int realIndex) {
        if (mEntryValues instanceof Bundle[]) {
            Bundle[] values = (Bundle[]) mEntryValues;
            return Utils.findItemIndexByPredicate(values, new Predicate<Bundle>() {

                @Override
                public boolean apply(Bundle item) {
                    if (item == null || !item.containsKey("index")) {
                        return false;
                    }
                    return realIndex == item.getInt("index");
                }
            });
        }
        return realIndex;
    }

    public int realIndexOf(int entryIndex) {
        if (mEntryValues instanceof Bundle[]) {
            Bundle[] values = (Bundle[]) mEntryValues;
            Bundle entry = Utils.valueInArray(values, entryIndex, null);
            if (entry == null|| !entry.containsKey("index")) {
                return entryIndex;
            }
            return entry.getInt("index");
        }
        return entryIndex;
    }

    private void notifyIndexChange(int previous, int current) {
        if (mPreferenceIndexChangeListener != null) {
            mPreferenceIndexChangeListener.onPreferenceIndexChange(this, previous, current);
        }
    }

    public void setEntryIndex(int index) {
        int previous = mIndex;
        if (index >= 0 && mEntryNames != null && index < mEntryNames.length) {
            mIndex = index;
        } else {
            mIndex = INVALID_INDEX;
        }
        handleIndexChange();
        if (mIndex != INVALID_INDEX) {
            notifyIndexChange(previous, mIndex);
        }
    }

    private int previousIndex() {
        if (mIndex == INVALID_INDEX || mEntryNames == null) {
            return INVALID_INDEX;
        }
        boolean loop = mCycleEnabled;
        int current = mIndex;
        int count = mEntryNames.length;
        if (!loop && current == 0) {
            return current;
        }
        int previous = (loop && current == 0) ? count - 1 : current - 1;
        for (int i = previous; i != current; i = (loop && i == 0) ? count - 1 : i - 1) {
            if (mEntryState[i]) {
                return i;
            }
            if (!loop && i == 0) {
                return current;
            }
        }
        return current;
    }

    public void previousState() {
        int previousIndex = previousIndex();
        setEntryIndex(previousIndex);
    }

    private int nextIndex() {
        if (mIndex == INVALID_INDEX || mEntryNames == null) {
            return INVALID_INDEX;
        }
        boolean loop = mCycleEnabled;
        int current = mIndex;
        int count = mEntryNames.length;
        if (!loop && current == count - 1) {
            return current;
        }
        int next = (loop && current == count - 1) ? 0 : current + 1;
        for (int i = next; i != current; i = (loop && i == count - 1) ? 0 : i + 1) {
            if (mEntryState[i]) {
                return i;
            }
            if (!loop && i == count - 1) {
                return current;
            }
        }
        return current;
    }

    public void nextState() {
        int nextIndex = nextIndex();
        setEntryIndex(nextIndex);
    }

    public boolean isCycleEnabled() {
        return mCycleEnabled;
    }

    public void setCycleEnabled(boolean cycleEnabled) {
        mCycleEnabled = cycleEnabled;
        handleIndexChange();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (tvValue != null) {
            tvValue.setSelected(gainFocus);
        }
        updateIconState();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_LEFT:
            if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                ivNext.performClick();
            } else {
                ivPrevious.performClick();
            }
            return true;
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                ivPrevious.performClick();
            } else {
                ivNext.performClick();
            }
            return true;
        default:
            break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setPreferenceIndexChangeListener(PreferenceIndexChangeListener l) {
        mPreferenceIndexChangeListener = l;
    }

    private class ClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.previous_state:
                previousState();
                break;
            case R.id.next_state:
                nextState();
                break;
            default:
                break;
            }
        }

    }

    public interface PreferenceIndexChangeListener {

        void onPreferenceIndexChange(StatePreference preference, int previous, int current);

    }

}
