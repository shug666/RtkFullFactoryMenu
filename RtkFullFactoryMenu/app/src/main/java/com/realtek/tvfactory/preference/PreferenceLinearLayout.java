package com.realtek.tvfactory.preference;

import com.realtek.tvfactory.utils.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.widget.LinearLayout;

public class PreferenceLinearLayout extends LinearLayout {

    public PreferenceLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PreferenceLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceLinearLayout(Context context) {
        this(context, null);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event) || handleKeyEvent(event);
    }

    private boolean handleKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        boolean down = event.getAction() == KeyEvent.ACTION_DOWN;
        if (down) {
            switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (Utils.focusUp(this, true)) {
                    playSoundEffect(SoundEffectConstants.NAVIGATION_UP);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (Utils.focusDown(this, true)) {
                    playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN);
                    return true;
                }
                break;

            default:
                break;
            }
        }
        return false;
    }
}
