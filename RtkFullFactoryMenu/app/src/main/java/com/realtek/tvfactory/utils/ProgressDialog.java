package com.realtek.tvfactory.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.realtek.tvfactory.R;

import static com.android.internal.R.attr.alertDialogStyle;
import static com.android.internal.R.layout.progress_dialog;
import static com.android.internal.R.styleable.AlertDialog;
import static com.android.internal.R.styleable.AlertDialog_progressLayout;

public class ProgressDialog extends AlertDialog {

    private ProgressBar mProgress;
    private TextView mMessageView;

    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private boolean mIndeterminate;
    private Toast mToast;

    public ProgressDialog(Context context) {
        super(context);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message) {
        return show(context, title, message, false);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message,
            boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate,
            boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate,
            boolean cancelable, OnCancelListener cancelListener) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        TypedArray a = context.obtainStyledAttributes(null, AlertDialog, alertDialogStyle, 0);
        View view = inflater.inflate(a.getResourceId(AlertDialog_progressLayout, progress_dialog), null);
        mProgress = (ProgressBar) view.findViewById(com.android.internal.R.id.progress);
        mMessageView = (TextView) view.findViewById(com.android.internal.R.id.message);
        setView(view);

        if (mMessage != null) {
            setMessage(mMessage);
        }
        if (mIndeterminateDrawable != null) {
            setIndeterminateDrawable(mIndeterminateDrawable);
        }
        setIndeterminate(mIndeterminate);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void setMessage(CharSequence message) {
        if (mMessageView == null) {
            mMessage = message;
        } else {
            mMessageView.setText(message);
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        if (mProgress == null) {
            mIndeterminate = indeterminate;
        } else {
            mProgress.setIndeterminate(indeterminate);
        }
    }

    public void setIndeterminateDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(d);
        } else {
            mIndeterminateDrawable = d;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_POWER:
//        case KeyEvent.KEYCODE_BROWSER:
        case KeyEvent.KEYCODE_HOME:
//        case KeyEvent.KEYCODE_TV_INPUT_USB:
            showToast(getContext().getString(R.string.illegal_operation));
            return true;

        default:
            break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void showToast(String str) {
        if (str != null) {
            if (mToast == null) {
                mToast = Toast.makeText(getContext(), str, Toast.LENGTH_SHORT);
            }
            mToast.setText(str);
            mToast.show();
        }
    }

    public void setSpecialKeyEvent(boolean intercept) {
        TvUtils.interceptSpecialKeyEvent(getWindow(), intercept);
    }

    @Override
    public void dismiss() {
        setSpecialKeyEvent(false);
        super.dismiss();
    }

}