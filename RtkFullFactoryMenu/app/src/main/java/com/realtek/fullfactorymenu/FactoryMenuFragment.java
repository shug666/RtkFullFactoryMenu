package com.realtek.fullfactorymenu;

import java.util.Objects;
import java.util.Stack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FactoryMenuFragment extends BaseFragment {

    private TextView mPageTitle;
    private FrameLayout mFactoryFrameLayout;
    private int mTitleId;
    public int scrollx;// this must little than zero
    private boolean isHide;

    private final Stack<Record> mFragments = new Stack<Record>();
    private LinearLayout extraInfo;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = View.inflate(getActivity(), R.layout.fragment_factory_menu, null);
        mPageTitle = (TextView) rootView.findViewById(R.id.pageTitle);
        mFactoryFrameLayout = (FrameLayout) rootView.findViewById(R.id.factory_frame_layout);
        scrollx = 0;
        isHide = false;

        if (mFragments.isEmpty()) {
            Bundle arguments = getArguments();
            int title = arguments.getInt("title");
            String clazzName = arguments.getString("fragment");
            showPage(clazzName, title);
        } else {
            showPage(mFragments.peek());
        }
        return rootView;
    }

    private BaseFragment currentFragment() {
        if (mFragments.isEmpty()) {
            return null;
        }
        return mFragments.peek().fragment;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        BaseFragment current = currentFragment();
        if (current != null && current.onKeyDown(keyCode, event)) {
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
                if (popCurrentPage()) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_PROG_GREEN:
                float density = (float) (SystemProperties.getInt("ro.sf.lcd_density", 0) / 160.0);
                if (scrollx == 0)
                    scrollx = -100;// -Math.round(50*density);
                else if (scrollx == -100)
                    scrollx = -200;// -Math.round(100*density);
                else
                    scrollx = 0;
                mFactoryFrameLayout.scrollTo(Math.round(scrollx * density), 0);
                break;
            case KeyEvent.KEYCODE_PROG_RED:
                if (isHide){
                    mFactoryFrameLayout.setVisibility(View.VISIBLE);
                    isHide = false;
                } else {
                    mFactoryFrameLayout.setVisibility(View.GONE);
                    isHide = true;
                }
                break;
            /*case KeyEvent.KEYCODE_INFO:
                if(extraInfo == null){
                    extraInfo = (LinearLayout)rootView.findViewById(R.id.factory_extra_info);
                }
                if(extraInfo.getVisibility() == View.VISIBLE){
                    extraInfo.setVisibility(View.INVISIBLE);
                }else{
                    extraInfo.setVisibility(View.VISIBLE);
                    initInfoView();
                }
                break;*/
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void initInfoView(){
            TextView item1_name = (TextView) rootView.findViewById(R.id.extrainfo_item1_name);
            TextView item1_value = (TextView) rootView.findViewById(R.id.extrainfo_item1_value);
            TextView item2_name = (TextView) rootView.findViewById(R.id.extrainfo_item2_name);
            TextView item2_value = (TextView) rootView.findViewById(R.id.extrainfo_item2_value);
            TextView item3_name = (TextView) rootView.findViewById(R.id.extrainfo_item3_name);
            TextView item3_value = (TextView) rootView.findViewById(R.id.extrainfo_item3_value);
            TextView item4_name = (TextView) rootView.findViewById(R.id.extrainfo_item4_name);
            TextView item4_value = (TextView) rootView.findViewById(R.id.extrainfo_item4_value);
            TextView item5_name = (TextView) rootView.findViewById(R.id.extrainfo_item5_name);
            TextView item5_value = (TextView) rootView.findViewById(R.id.extrainfo_item5_value);
            TextView item6_name = (TextView) rootView.findViewById(R.id.extrainfo_item6_name);
            TextView item6_value = (TextView) rootView.findViewById(R.id.extrainfo_item6_value);

            item1_name.setText("PQ BIN");
            item2_name.setText("PQ MD5");
            item3_name.setText("PQ SHA1");
            String testPqBin = "tmp/factory/bin_panel/PanelParam/vip_test_pq.bin";
            File testpqFile = new File(testPqBin);
            if (testpqFile.exists()){
                File pqFile = new File(testPqBin);
                item1_value.setText(testPqBin);
                item2_value.setText(getFileCheckSum(pqFile, "MD5"));
                item3_value.setText(getFileCheckSum(pqFile, "SHA1"));
            }else{
                File pqFile = new File("tmp/factory/bin_panel/PanelParam/vip_default_pq.bin");
                item1_value.setText("vendor/pq/vip_default_pq.bin");
                item2_value.setText(getFileCheckSum(pqFile, "MD5"));
                item3_value.setText(getFileCheckSum(pqFile, "SHA1"));
            }
            item4_name.setText("OSD BIN");
            item5_name.setText("OSD MD5");
            item6_name.setText("OSD SHA1");
            String testOsdBin = "tmp/factory/bin_panel/PanelParam/vip_test_osd.bin";
            File testOsdFile = new File(testOsdBin);
            if (testOsdFile.exists()){
                File osdFile = new File(testOsdBin);
                item4_value.setText(testOsdBin);
                item5_value.setText(getFileCheckSum(osdFile, "MD5"));
                item6_value.setText(getFileCheckSum(osdFile, "SHA1"));
            }else {
                File osdFile = new File("tmp/factory/bin_panel/PanelParam/vip_default_pq.bin");
                item4_value.setText("tmp/factory/bin_panel/PanelParam/vip_default_pq.bin");
                item5_value.setText(getFileCheckSum(osdFile, "MD5"));
                item6_value.setText(getFileCheckSum(osdFile, "SHA1"));
            }
    }
    private String getFileCheckSum(File file, String algorithm) {

        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[8192];
        int len;

        try {
            digest = MessageDigest.getInstance(algorithm);
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
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
    public void onHiddenChanged(boolean hidden) {
        BaseFragment current = currentFragment();
        if (current != null) {
            current.onHiddenChanged(hidden);
        }
    }

    public void showPage(Class<? extends BaseFragment> clazz, int title) {
        showPage(clazz.getName(), title);
    }

    public void showPage(String clazzName, int title) {
        showPage(clazzName, title, null);
    }

    public void showPage(String clazzName, int title, Bundle arguments) {
        BaseFragment fragment = (BaseFragment) Fragment.instantiate(getActivity(), clazzName);
        fragment.setArguments(arguments);
        Record record = findRecordByTag(clazzName);
        if (record == null) {
            record = new Record(clazzName, title, fragment);

            if (title == 0) {
                record.title = mTitleId;
            }
        }
        showPage(record);
    }

    public Record findRecordByTag(String tag) {
        if (mFragments.isEmpty()) {
            return null;
        }
        for (Record record : mFragments) {
            if (Objects.equals(tag, record.tag)) {
                return record;
            }
        }
        return null;
    }

    public boolean popCurrentPage() {
        if (mFragments.size() > 1) {
            mFragments.pop();

            Record next = mFragments.peek();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            updateTitle(next.title);
            transaction.replace(R.id.menu_container, next.fragment);
            transaction.commitAllowingStateLoss();
            return true;
        }
        return false;
    }

    private void updateTitle(int title) {
        mTitleId = title;
        mPageTitle.setText(mTitleId);
    }

    private void showPage(Record record) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        updateTitle(record.title);
        transaction.replace(R.id.menu_container, record.fragment);
        transaction.commitAllowingStateLoss();
        if (!mFragments.contains(record)) {
            mFragments.add(record);
        }
    }

    static class Record {

        String tag;

        int title;

        BaseFragment fragment;

        public Record() {
        }

        public Record(String tag, int title, BaseFragment fragment) {
            this.tag = tag;
            this.title = title;
            this.fragment = fragment;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Record)) {
                return false;
            }
            return Objects.equals(tag, ((Record) obj).tag);
        }

    }

}
