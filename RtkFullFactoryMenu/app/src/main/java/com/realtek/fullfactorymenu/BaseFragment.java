package com.realtek.fullfactorymenu;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

public class BaseFragment extends Fragment {

    protected int mFocusId = View.NO_ID;

    private void doRequestFocus() {
        View view = getView();
        View focus = null;
        if (view != null && (focus = view.findViewById(mFocusId)) != null) {
            focus.requestFocus();
        }
        mFocusId = View.NO_ID;
    }

    private void restoreFocus() {
        if (mFocusId != View.NO_ID) {
            View view = getView();
            if (view != null) {
                doRequestFocus();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        restoreFocus();
    }

    private void saveFocus() {
        View view = getView();
        if (view != null) {
            View focus = view.findFocus();
            if (focus != null) {
                mFocusId = focus.getId();
            }
        }
    }

    @Override
    public void onStop() {
        saveFocus();
        super.onStop();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            saveFocus();
        } else {
            restoreFocus();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

}
