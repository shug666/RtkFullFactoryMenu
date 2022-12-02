package com.realtek.fullfactorymenu;

import static com.realtek.fullfactorymenu.utils.Constants.SHOW_FRAGMENT_ADC_ADJUST;
import static com.realtek.fullfactorymenu.utils.Constants.SHOW_FRAGMENT_PAGE;
import static com.realtek.fullfactorymenu.utils.Constants.SHOW_FRAGMENT_SW_INFO;
import static com.realtek.fullfactorymenu.utils.Constants.SHOW_FRAGMENT_SYSTEM_INFO;
import static com.realtek.fullfactorymenu.utils.Constants.SHOW_FRAGMENT_WHITE_BALANCE;
import static com.realtek.fullfactorymenu.utils.Constants.SHOW_FRAGMENT_WHITE_PATTERN;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.realtek.fullfactorymenu.api.impl.PictureApi;
import com.realtek.fullfactorymenu.picture.AdcAdjustFragment;
import com.realtek.fullfactorymenu.picture.WhiteBalanceAdjustFragment;
import com.realtek.fullfactorymenu.preference.SeekBarPreferenceFragment;
import com.realtek.fullfactorymenu.swInfo.SwInfoFragment;
import com.realtek.fullfactorymenu.systemInfo.SystemInfoFragment;
import com.realtek.fullfactorymenu.utils.AppToast;
import com.realtek.fullfactorymenu.utils.TvInputUtils;
import com.realtek.fullfactorymenu.utils.TvUtils;

import java.util.Stack;

public class FactoryMenuActivity extends FragmentActivity {
    private static final String TAG = FactoryMenuActivity.class.getSimpleName();

    private final Stack<BaseFragment> mFragments = new Stack<BaseFragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory_menu);
        showMenuPage(MainPageFragment.class, R.string.app_name);

        IntentFilter intentFilter =new IntentFilter();
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mBroadcastReceiver,intentFilter);

        Log.d(TAG, "onCreate getIntent:" + getIntent());
        handleIntent(getIntent());
   }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "onNewIntent:" + intent);
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void handleIntent(Intent intent) {
        if (intent == null)
            return;
        showFragmentPageByExtras(intent);
    }

    private void showFragmentPageByExtras(Intent intent) {
        int page_index = intent.getIntExtra(SHOW_FRAGMENT_PAGE, -1);
        if (page_index == SHOW_FRAGMENT_SW_INFO) {
            showMenuPage(SwInfoFragment.class, R.string.str_sw_info);
        } else if (page_index == SHOW_FRAGMENT_SYSTEM_INFO) {
            showMenuPage(SystemInfoFragment.class, R.string.str_system_info);
        } else if (page_index == SHOW_FRAGMENT_WHITE_BALANCE) {
            showMenuPage(WhiteBalanceAdjustFragment.class, R.string.str_white_balance);
        } else if (page_index == SHOW_FRAGMENT_WHITE_PATTERN) {
            PictureApi.getInstance().setVideoTestPattern(1);
        } else if (page_index == SHOW_FRAGMENT_ADC_ADJUST) {
            int currentTvInputSource = FactoryApplication.getInstance().getInputSource(TvInputUtils.getCurrentInput(this));
            if(TvUtils.isYpbpr(currentTvInputSource) || TvUtils.isVga(currentTvInputSource)){
                showMenuPage(AdcAdjustFragment.class,R.string.str_adc_adjust);
            }else {
                this.finish();
                AppToast.showToast(getApplicationContext(), R.string.support_reminder, Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        BaseFragment current = currentFragment();
        if (current != null && current.onKeyDown(keyCode, event)) {
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_ESCAPE:
                finish();
                return true;
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        BaseFragment current = currentFragment();
        if (current != null && current.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (!popCurrentPage()) {
            finish();
        }
    }

    protected BaseFragment currentFragment() {
        if (mFragments.size() > 0) {
            return mFragments.peek();
        }
        return null;
    }

    private void showMenuPage(Class<? extends BaseFragment> clazz, int title) {
        Bundle arguments = new Bundle();
        arguments.putInt("title", title);
        arguments.putString("fragment", clazz.getName());
        showPage(FactoryMenuFragment.class, arguments);
    }

    public void showPage(Class<? extends BaseFragment> clazz) {
        BaseFragment fragment = (BaseFragment) Fragment.instantiate(this, clazz.getName());
        showPage(fragment);
    }

    public void showPage(Class<? extends BaseFragment> clazz, Bundle arguments) {
        BaseFragment fragment = (BaseFragment) Fragment.instantiate(this, clazz.getName());
        fragment.setArguments(arguments);
        showPage(fragment);
    }

    public void showPage(BaseFragment fragment) {
        if (fragment == null) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment instanceof SeekBarPreferenceFragment) {
            if (!mFragments.isEmpty()) {
                BaseFragment current = mFragments.peek();
                transaction.hide(current);
            }
            transaction.add(R.id.content_container, fragment);
            transaction.commitAllowingStateLoss();
        } else {
            transaction.replace(R.id.content_container, fragment);
            transaction.commitAllowingStateLoss();
        }
        if (!mFragments.contains(fragment)) {
            mFragments.add(fragment);
        }
    }

    public boolean popCurrentPage() {
        if (mFragments.size() > 1) {
            BaseFragment current = mFragments.pop();
            BaseFragment next = mFragments.peek();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (current instanceof SeekBarPreferenceFragment) {
                transaction.remove(current);
                transaction.show(next);
                transaction.commitAllowingStateLoss();
            } else {
                transaction.replace(R.id.content_container, next);
                transaction.commitAllowingStateLoss();
            }
            return true;
        }
        return false;
    }

    public void showBackground() {
        View container = findViewById(R.id.content_container);
        Drawable background = container.getBackground();
        if (background != null) {
            background.setAlpha(255);
        }
    }

    public void hideBackground() {
        View container = findViewById(R.id.content_container);
        Drawable background = container.getBackground();
        if (background != null) {
            background.setAlpha(0);
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_EXTRA_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            String action = intent.getAction();

            switch (action) {
                case Intent.ACTION_CLOSE_SYSTEM_DIALOGS: {
                    if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(intent.getStringExtra(SYSTEM_DIALOG_EXTRA_KEY))
                            && (Settings.Secure.getInt(getContentResolver(),"tv_user_setup_complete", 1) == 1)) {
                        FactoryMenuActivity.this.finish();
                    }
                    break;
                }
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
