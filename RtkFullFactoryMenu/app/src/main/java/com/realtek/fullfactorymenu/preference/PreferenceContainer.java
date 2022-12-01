package com.realtek.fullfactorymenu.preference;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Objects;

import org.xmlpull.v1.XmlPullParserException;

import com.realtek.fullfactorymenu.FactoryApplication;
import com.realtek.fullfactorymenu.R;
import com.realtek.fullfactorymenu.logic.LogicFactory;
import com.realtek.fullfactorymenu.logic.LogicInterface;
import com.realtek.fullfactorymenu.preference.Preference.PreferenceClickListener;
import com.realtek.fullfactorymenu.preference.PreferenceContainer.PreferenceContainerConfig.PreferenceConfig;
import com.realtek.fullfactorymenu.preference.PreferenceContainer.PreferenceContainerConfig.SeekBarPreferenceConfig;
import com.realtek.fullfactorymenu.preference.PreferenceContainer.PreferenceContainerConfig.StatePreferenceConfig;
import com.realtek.fullfactorymenu.preference.PreferenceContainer.PreferenceContainerConfig.StatePreferenceConfig.Entry;
import com.realtek.fullfactorymenu.preference.PreferenceContainer.PreferenceContainerConfig.SumaryPreferenceConfig;
import com.realtek.fullfactorymenu.utils.Constants;
import com.realtek.fullfactorymenu.utils.LogHelper;
import com.realtek.fullfactorymenu.utils.Utils;
import com.realtek.fullfactorymenu.utils.XmlUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class PreferenceContainer extends ScrollView implements PreferenceClickListener, OnKeyListener {

    private static final String TAG = "PreferenceContainer";

    private LinearLayout mContainer;

    private int mXml;

    private int mDivider;

    private LogicInterface mDefaultLogic;

    private final SparseArray<LogicInterface> mUserLogic = new SparseArray<LogicInterface>();

    private PreferenceItemClickListener mItemClickListener;
    private PreferenceItemKeyListener mItemKeyListener;

    private boolean mPrepared = false;

    private final ArrayList<Runnable> mActionsAfterPrepared = new ArrayList<Runnable>();

    public PreferenceContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        View.inflate(context, R.layout.preference_container, this);
        mContainer = (LinearLayout) findViewById(R.id.container);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PreferenceContainer, defStyleAttr, defStyleRes);
        mXml = a.getResourceId(R.styleable.PreferenceContainer_xml, 0);
        mDivider = a.getResourceId(R.styleable.PreferenceContainer_divider, 0);
        a.recycle();

        setDivider(mDivider);

        getViewTreeObserver().addOnPreDrawListener(mPreDrawListener);
    }

    public PreferenceContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceContainer(Context context) {
        this(context, null);

        inflatePreference();
    }

    private PreferenceContainer(Builder builder) {
        super(builder.context);
        View.inflate(builder.context, R.layout.preference_container, this);
        mContainer = (LinearLayout) findViewById(R.id.container);

        mXml = builder.xml;
        mDivider = builder.divider;

        setDivider(mDivider);

        getViewTreeObserver().addOnPreDrawListener(mPreDrawListener);

        inflatePreference();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        inflatePreference();
    }

    public void setDivider(int divider) {
        mDivider = divider;
        if (mDivider == 0) {
            mContainer.setDividerDrawable(new ColorDrawable(Color.TRANSPARENT));
            mContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        } else {
            mContainer.setDividerDrawable(getResources().getDrawable(mDivider, null));
            int dividerFlag = LinearLayout.SHOW_DIVIDER_MIDDLE;
            dividerFlag |= LinearLayout.SHOW_DIVIDER_BEGINNING | LinearLayout.SHOW_DIVIDER_END;
            mContainer.setShowDividers(dividerFlag);
        }
    }

    public Preference findPreferenceById(final int id) {
        View view = findViewById(id);
        if (view instanceof Preference) {
            return (Preference) view;
        }
        return null;
    }

    private void inflatePreference() {
        if (mXml != 0) {
            inflatePreference(mXml);
        }
    }

    public void inflatePreference(int xml) {
        long startTime = System.currentTimeMillis();
        clearAll();

        LogHelper.d(TAG, "inflatePreference -> xml: %s.", Utils.resourceNameOf(this.getContext(), xml));
        PreferenceContainerConfig containerConfig = parsePreference(xml);

        inflatePreference(containerConfig);

        long stopTime = System.currentTimeMillis();
        LogHelper.i(TAG, "inflatePreference -> cost: %d ms.", (stopTime - startTime));
    }

    private LogicInterface findLogicByName(String className) {
        LogicInterface logic = null;
        for (int i = 0, j = mUserLogic.size(); i < j; i++) {
            logic = mUserLogic.get(mUserLogic.keyAt(i));
            if (logic == null) {
                continue;
            }
            if (Objects.equals(logic.getClass().getName(), className)) {
                return logic;
            }
        }
        return null;
    }

    public void inflatePreference(PreferenceContainerConfig containerConfig) {
        mDefaultLogic = LogicFactory.createLogic(this, containerConfig.logicClassName);
        mUserLogic.clear();
        LogicInterface logic = null;
        Context context = getContext();
        ArrayList<Preference> preferences = new ArrayList<Preference>();
        LogHelper.d(TAG, "inflatePreference -> count: %d.", containerConfig.preferenceConfigs.size());
        for (PreferenceConfig config : containerConfig.preferenceConfigs) {
            if (config == null) {
                LogHelper.e(TAG, "config == null");
                continue;
            }
            if (config instanceof SeekBarPreferenceConfig) {
                SeekBarPreferenceConfig seekBarPreferenceConfig = (SeekBarPreferenceConfig) config;
                SeekBarPreference seekBarPreference = createSeekBarPreference(context, seekBarPreferenceConfig);
                if (Objects.equals(containerConfig.logicClassName, seekBarPreferenceConfig.logicClassName)) {
                    if (mDefaultLogic != null) {
                        seekBarPreference.setPreferenceProgressChangeListener(mDefaultLogic);
                    }
                } else {
                    logic = findLogicByName(seekBarPreferenceConfig.logicClassName);
                    if (logic == null) {
                        logic = LogicFactory.createLogic(this, seekBarPreferenceConfig.logicClassName);
                    }
                    if (logic != null) {
                        seekBarPreference.setPreferenceProgressChangeListener(logic);
                        if (mUserLogic.indexOfValue(logic) == -1) {
                            mUserLogic.put(seekBarPreferenceConfig.id, logic);
                        }
                    } else if (mDefaultLogic != null) {
                        seekBarPreference.setPreferenceProgressChangeListener(mDefaultLogic);
                    }
                }

                seekBarPreference.setId(seekBarPreferenceConfig.id);
                seekBarPreference.setPreferenceClickListener(this);
                seekBarPreference.setOnKeyListener(this);
                seekBarPreference.setVisibility(seekBarPreferenceConfig.visible ? VISIBLE : GONE);
                preferences.add(seekBarPreference);
            } else if (config instanceof StatePreferenceConfig) {
                StatePreferenceConfig statePreferenceConfig = (StatePreferenceConfig) config;
                StatePreference statePreference = createStatePreference(context, statePreferenceConfig);
                if (Objects.equals(containerConfig.logicClassName, statePreferenceConfig.logicClassName)) {
                    if (mDefaultLogic != null) {
                        statePreference.setPreferenceIndexChangeListener(mDefaultLogic);
                    }
                } else {
                    logic = findLogicByName(statePreferenceConfig.logicClassName);
                    if (logic == null) {
                        logic = LogicFactory.createLogic(this, statePreferenceConfig.logicClassName);
                    }
                    if (logic != null) {
                        statePreference.setPreferenceIndexChangeListener(logic);
                        if (mUserLogic.indexOfValue(logic) == -1) {
                            mUserLogic.put(statePreferenceConfig.id, logic);
                        }
                    } else if (mDefaultLogic != null) {
                        statePreference.setPreferenceIndexChangeListener(mDefaultLogic);
                    }
                }

                statePreference.setId(statePreferenceConfig.id);
                statePreference.setPreferenceClickListener(this);
                statePreference.setOnKeyListener(this);
                statePreference.setVisibility(statePreferenceConfig.visible ? VISIBLE : GONE);
                preferences.add(statePreference);
            } else if (config instanceof SumaryPreferenceConfig) {
                SumaryPreferenceConfig sumaryPreferenceConfig = (SumaryPreferenceConfig) config;
                SumaryPreference sumaryPreference = createSumaryPreference(context, sumaryPreferenceConfig);
                if (!Objects.equals(containerConfig.logicClassName, sumaryPreferenceConfig.logicClassName)) {
                    logic = findLogicByName(sumaryPreferenceConfig.logicClassName);
                    if (logic == null) {
                        logic = LogicFactory.createLogic(this, sumaryPreferenceConfig.logicClassName);
                    }
                    if (logic != null) {
                        if (mUserLogic.indexOfValue(logic) == -1) {
                            mUserLogic.put(sumaryPreferenceConfig.id, logic);
                        }
                    }
                }

                sumaryPreference.setId(sumaryPreferenceConfig.id);
                sumaryPreference.setOnKeyListener(this);
                sumaryPreference.setPreferenceClickListener(this);
                sumaryPreference.setVisibility(sumaryPreferenceConfig.visible ? VISIBLE : GONE);
                preferences.add(sumaryPreference);
            } else {
                Preference preference = createPreference(context, config);
                if (!Objects.equals(containerConfig.logicClassName, config.logicClassName)) {
                    logic = findLogicByName(config.logicClassName);
                    if (logic == null) {
                        logic = LogicFactory.createLogic(this, config.logicClassName);
                    }
                    if (logic != null) {
                        if (mUserLogic.indexOfValue(logic) == -1) {
                            mUserLogic.put(config.id, logic);
                        }
                    }
                }

                preference.setId(config.id);
                preference.setPreferenceClickListener(this);
                preference.setOnKeyListener(this);
                preference.setVisibility(config.visible ? VISIBLE : GONE);
                preferences.add(preference);
            }
        }

        for (Preference preference : preferences) {
            mContainer.addView(preference);

            runAfterPrepared(new NotifyAvailableAction(preference));
        }

        logicInit();

    }

    public void clearAll() {
        mContainer.removeAllViews();

        logicDeinit();
    }

    public ArrayList<Preference> getPreferences() {
        ArrayList<Preference> preferences = new ArrayList<Preference>();
        int count = mContainer.getChildCount();
        View child = null;
        for (int i = 0; i < count; i++) {
            child = mContainer.getChildAt(i);
            if (child instanceof Preference) {
                preferences.add((Preference) child);
            }
        }
        return preferences;
    }

    public LogicInterface getPreferenceLogic(int id) {
        LogicInterface logic = null;
        if ((logic = mUserLogic.get(id)) == null) {
            logic = mDefaultLogic;
        }
        return logic;
    }

    @SuppressWarnings("unchecked")
    public <T extends LogicInterface> T getPreferenceLogic(Class<T> clazz) {
        if (mDefaultLogic != null && Objects.equals(mDefaultLogic.getClass(), clazz)) {
            return (T) mDefaultLogic;
        }
        int count = mUserLogic.size();
        LogicInterface logic = null;
        for (int i = 0; i < count; i++) {
            logic = mUserLogic.valueAt(i);
            if (logic != null && Objects.equals(logic.getClass(), clazz)) {
                return (T) logic;
            }
        }
        return null;
    }

    private void logicInit() {
        if (mDefaultLogic != null) {
            mDefaultLogic.init();
        }
        LogicInterface logic = null;
        int count = mUserLogic.size();
        for (int i = 0; i < count; i++) {
            logic = mUserLogic.valueAt(i);
            if (logic != null) {
                logic.init();
            }
        }
    }

    private void logicDeinit() {
        if (mDefaultLogic != null) {
            mDefaultLogic.deinit();
            mDefaultLogic = null;
        }
        LogicInterface logic = null;
        int count = mUserLogic.size();
        for (int i = 0; i < count; i++) {
            logic = mUserLogic.valueAt(i);
            if (logic != null) {
                logic.deinit();
            }
            mUserLogic.removeAt(i);
            i--;
            count--;
        }
    }

    public void notifyRefresh() {
        scrollTo(0, 0);

        clearFocus();

        logicInit();

        int count = mContainer.getChildCount();
        View child = null;
        for (int i = 0; i < count; i++) {
            child = mContainer.getChildAt(i);
            if (child instanceof Preference) {
                Preference preference = (Preference) child;
                post(new NotifyAvailableAction(preference));
            }
        }
    }

    public void setPreferenceItemClickListener(PreferenceItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setPreferenceItemKeyListener(PreferenceItemKeyListener listener) {
        mItemKeyListener = listener;
    }

    @Override
    public void onPreferenceClick(Preference preference) {
        if (mItemClickListener != null) {
            mItemClickListener.onPreferenceItemClick(preference);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (mItemKeyListener == null || !(v instanceof Preference)) {
            return false;
        }
        if (mItemKeyListener.onKey((Preference) v, keyCode, event)) {
            return true;
        }
        return false;
    }

    private void runAfterPrepared(Runnable action) {
        if (action == null) {
            return;
        }
        if (mPrepared) {
            action.run();
        } else {
            mActionsAfterPrepared.add(action);
        }
    }

    private final OnPreDrawListener mPreDrawListener = new OnPreDrawListener() {

        @Override
        public boolean onPreDraw() {
            mPrepared = true;

            int count = mActionsAfterPrepared.size();
            for (int i = 0; i < count; i++) {
                mActionsAfterPrepared.get(i).run();
                mActionsAfterPrepared.remove(i);
                i--;
                count--;
            }

            getViewTreeObserver().removeOnPreDrawListener(this);
            return true;
        }
    };

    public static class PreferenceContainerConfig {

        public String logicClassName;

        public final ArrayList<PreferenceConfig> preferenceConfigs = new ArrayList<PreferenceConfig>();

        public void add(PreferenceConfig preferenceConfig) {
            if (preferenceConfig == null) {
                return;
            }
            if (TextUtils.isEmpty(preferenceConfig.logicClassName)) {
                preferenceConfig.logicClassName = logicClassName;
            }
            preferenceConfigs.add(preferenceConfig);
        }

        public void addAll(PreferenceContainerConfig preferenceContainerConfig) {
            if (preferenceContainerConfig == null) {
                return;
            }
            int count = preferenceContainerConfig.preferenceConfigs.size();
            PreferenceConfig preferenceConfig = null;
            for (int i = 0; i < count; i++) {
                preferenceConfig = preferenceContainerConfig.preferenceConfigs.get(i);
                if (TextUtils.isEmpty(preferenceConfig.logicClassName)) {
                    preferenceConfig.logicClassName = preferenceContainerConfig.logicClassName;
                }
                add(preferenceConfig);
            }
        }

        public static class PreferenceConfig {

            public String className;

            public String logicClassName;

            public int style;

            public int id;

            public String title;

            public boolean visible;

            public Object tag;

        }

        public static class SumaryPreferenceConfig extends PreferenceConfig {

            public CharSequence sumary;

        }

        public static class SeekBarPreferenceConfig extends PreferenceConfig {

            public int minValue;

            public int maxValue;

            public int progress;

            public int increment;

            public String displayFormat;

        }

        public static class StatePreferenceConfig extends PreferenceConfig {

            public int index;

            public boolean cycleEnabled;

            public final ArrayList<Entry> entries = new ArrayList<Entry>();

            public static class Entry {

                public String name;

                public int index;

                public boolean enabled;

                public Bundle value;

            }
        }
    }

    private PreferenceContainerConfig parsePreference(int xml) {
        long startTime = System.currentTimeMillis();

        PreferenceContainerConfig containerConfig = new PreferenceContainerConfig();
        containerConfig.preferenceConfigs.clear();

        String currentBrand = Constants.BRAND;
        String currentProduct = Build.HARDWARE;
        String currentModel = Build.ID;
        XmlResourceParser parser = getResources().getXml(xml);
        try {
            int eventType = parser.getEventType();
            String name = null;

            String condition = null;
            String brand = null;
            String product = null;
            String model = null;
            PreferenceConfig config = null;
            do {
                name = parser.getName();
                if (eventType == XmlResourceParser.START_TAG) {
                    if ("if".equals(name)) {
                        condition = parser.getAttributeValue(null, "condition");
                        brand = parser.getAttributeValue(null, "brand");
                        product = parser.getAttributeValue(null, "product");
                        model = parser.getAttributeValue(null,"model");
                        boolean matches = brandMatches(currentBrand, brand)
                            && productMatches(currentProduct, product)
                            && productMatches(currentModel, model);
                        if ("true".equals(condition)) {
                            if (!matches) {
                                XmlUtils.skipCurrentTag(parser);

                                eventType = parser.getEventType();
                                continue;
                            }
                        } else if ("false".equals(condition)) {
                            if (matches) {
                                XmlUtils.skipCurrentTag(parser);

                                eventType = parser.getEventType();
                                continue;
                            }
                        } else {
                            XmlUtils.skipCurrentTag(parser);

                            eventType = parser.getEventType();
                            continue;
                        }

                        eventType = parser.next();
                        continue;
                    }

                    if ("PreferenceContainer".equals(name)) {
                        containerConfig.logicClassName = parser.getAttributeValue(null, "logic");
                    } else if ("Preference".equals(name)) {
                        config = parsePreference(getContext(), parser);
                        containerConfig.add(config);
                    } else if ("SumaryPreference".equals(name)) {
                        config = parseSumaryPreference(getContext(), parser);
                        containerConfig.add(config);
                    } else if ("StatePreference".equals(name)) {
                        config = parseStatePreference(getContext(), parser);
                        containerConfig.add(config);
                    } else if ("SeekBarPreference".equals(name)) {
                        config = parseSeekBarPreference(getContext(), parser);
                        containerConfig.add(config);
                    } else if ("include".equals(name)) {
                        int includeXml = parser.getAttributeResourceValue(null, "xml", 0);
                        if (includeXml == 0) {

                        } else {
                            PreferenceContainerConfig childConfig = parsePreference(includeXml);
                            containerConfig.addAll(childConfig);
                        }
                    }
                } else if (eventType == XmlResourceParser.END_TAG) {
                    if ("PreferenceContainer".equals(name)) {
                        break;
                    }
                }
                eventType = parser.next();
            } while (eventType != XmlResourceParser.END_DOCUMENT);
        } catch (XmlPullParserException e) {
            LogHelper.w(TAG, e.getMessage(), e);
        } catch (IOException e) {
            LogHelper.w(TAG, e.getMessage(), e);
        } finally {
            parser.close();
            long stopTime = System.currentTimeMillis();
            LogHelper.d(TAG, "parsePreference -> cost: %d ms.", (stopTime - startTime));
        }
        return containerConfig;
    }

    private static boolean brandMatches(String currentBrand, String brands) {
        if (TextUtils.isEmpty(brands)) {
            return true;
        }
        String[] brandArray = brands.split("\\|");
        for (String brand : brandArray) {
            if (Objects.equals(currentBrand, brand)) {
                return true;
            }
        }
        return false;
    }

    private static boolean productMatches(String currentBrand, String brands) {
        if (TextUtils.isEmpty(brands)) {
            return true;
        }
        String[] brandArray = brands.split("\\|");
        for (String brand : brandArray) {
            if (Objects.equals(currentBrand, brand)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isStyleAttr(Context context, int style) {
        try {
            return "attr".equals(context.getResources().getResourceTypeName(style));
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isStyle(Context context, int style) {
        try {
            return "style".equals(context.getResources().getResourceTypeName(style));
        } catch (Exception e) {
            return false;
        }
    }

    private static Preference createPreference(Context context, PreferenceConfig config) {
        Preference preference = null;
        boolean isStyleAttr = isStyleAttr(context, config.style);
        boolean isStyle = isStyle(context, config.style);
        int styleAttr = isStyleAttr ? config.style : (isStyle ? 0 : R.attr.preferenceStyle);
        int style = isStyle ? config.style : 0;
        if (!TextUtils.isEmpty(config.className)) {
            try {
                Class<?> clazz = Class.forName(config.className);
                Constructor<?> c = clazz.getConstructor(Context.class, AttributeSet.class, int.class, int.class);
                preference = (Preference) c.newInstance(context, null, styleAttr, style);
            } catch (Exception e) {
                LogHelper.w(TAG, e.getMessage(), e);
            }
        }
        if (preference == null) {
            preference = new Preference(context, null, styleAttr, style);
        }
        preference.setId(config.id);
        preference.setTitle(config.title);
        preference.setTag(config.tag);
        return preference;
    }

    private static StatePreference createStatePreference(Context context, StatePreferenceConfig config) {
        StatePreference preference = null;
        boolean isStyleAttr = isStyleAttr(context, config.style);
        boolean isStyle = isStyle(context, config.style);
        int styleAttr = isStyleAttr ? config.style : (isStyle ? 0 : R.attr.preferenceStyle);
        int style = isStyle ? config.style : 0;
        if (!TextUtils.isEmpty(config.className)) {
            try {
                Class<?> clazz = Class.forName(config.className);
                Constructor<?> c = clazz.getConstructor(Context.class, AttributeSet.class, int.class, int.class);
                preference = (StatePreference) c.newInstance(context, null, styleAttr, style);
            } catch (Exception e) {
                LogHelper.w(TAG, e.getMessage(), e);
            }
        }
        if (preference == null) {
            preference = new StatePreference(context, null, styleAttr, style);
        }
        preference.setId(config.id);
        preference.setTitle(config.title);
        preference.setTag(config.tag);
        int count = config.entries.size();
        String[] names = new String[count];
        Bundle[] values = new Bundle[count];
        boolean[] state = new boolean[count];
        Entry entry = null;
        for (int i = 0; i < count; i++) {
            entry = config.entries.get(i);
            names[i] = entry.name;
            values[i] = entry.value;
            state[i] = entry.enabled;

            if (values[i] == null) {
                values[i] = new Bundle();
            }

            if (entry.index == -1) {
                values[i].putInt("index", i);
            } else {
                values[i].putInt("index", entry.index);
            }
        }
        preference.init(names, values, state, preference.entryIndexOf(config.index));
        preference.setCycleEnabled(config.cycleEnabled);
        return preference;
    }

    private static SumaryPreference createSumaryPreference(Context context, SumaryPreferenceConfig config) {
        SumaryPreference preference = null;
        boolean isStyleAttr = isStyleAttr(context, config.style);
        boolean isStyle = isStyle(context, config.style);
        int styleAttr = isStyleAttr ? config.style : (isStyle ? 0 : R.attr.preferenceStyle);
        int style = isStyle ? config.style : 0;
        if (!TextUtils.isEmpty(config.className)) {
            try {
                Class<?> clazz = Class.forName(config.className);
                Constructor<?> c = clazz.getConstructor(Context.class, AttributeSet.class, int.class, int.class);
                preference = (SumaryPreference) c.newInstance(context, null, styleAttr, style);
            } catch (Exception e) {
                LogHelper.w(TAG, e.getMessage(), e);
            }
        }
        if (preference == null) {
            preference = new SumaryPreference(context, null, styleAttr, style);
        }
        preference.setId(config.id);
        preference.setTitle(config.title);
        preference.setSumary(config.sumary);
        preference.setTag(config.tag);
        return preference;
    }

    private static SeekBarPreference createSeekBarPreference(Context context, SeekBarPreferenceConfig config) {
        SeekBarPreference preference = null;
        boolean isStyleAttr = isStyleAttr(context, config.style);
        boolean isStyle = isStyle(context, config.style);
        int styleAttr = isStyleAttr ? config.style : (isStyle ? 0 : R.attr.preferenceStyle);
        int style = isStyle ? config.style : 0;
        if (!TextUtils.isEmpty(config.className)) {
            try {
                Class<?> clazz = Class.forName(config.className);
                Constructor<?> c = clazz.getConstructor(Context.class, AttributeSet.class, int.class, int.class);
                preference = (SeekBarPreference) c.newInstance(context, null, styleAttr, style);
            } catch (Exception e) {
                LogHelper.w(TAG, e.getMessage(), e);
            }
        }
        if (preference == null) {
            preference = new SeekBarPreference(context, null, styleAttr, style);
        }
        preference.setId(config.id);
        preference.setTitle(config.title);
        preference.setTag(config.tag);
        preference.init(config.minValue, config.maxValue, config.progress, config.increment, config.displayFormat, null);
        return preference;
    }

    private static String getStringAttribute(Context context, XmlResourceParser parser, String attr) {
        int resourceId = parser.getAttributeResourceValue(null, attr, 0);
        if (resourceId == 0) {
            return parser.getAttributeValue(null, attr);
        } else {
            return context.getString(resourceId);
        }
    }

    private static PreferenceConfig parsePreference(Context context, XmlResourceParser parser) {
        PreferenceConfig config = new PreferenceConfig();
        try {
            config.className = parser.getAttributeValue(null, "class");
            config.logicClassName = parser.getAttributeValue(null, "logic");
            config.style = parser.getAttributeResourceValue(null, "style", 0);
            config.id = parser.getAttributeResourceValue(null, "id", View.NO_ID);
            config.title = getStringAttribute(context, parser, "title");
            config.visible = parser.getAttributeBooleanValue(null, "visible", true);
            config.tag = getStringAttribute(context, parser, "tag");
            return config;
        } catch (Exception e) {
            LogHelper.w(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static SumaryPreferenceConfig parseSumaryPreference(Context context, XmlResourceParser parser) {
        SumaryPreferenceConfig config = new SumaryPreferenceConfig();
        try {
            config.className = parser.getAttributeValue(null, "class");
            config.logicClassName = parser.getAttributeValue(null, "logic");
            config.style = parser.getAttributeResourceValue(null, "style", 0);
            config.id = parser.getAttributeResourceValue(null, "id", View.NO_ID);
            config.title = getStringAttribute(context, parser, "title");
            config.visible = parser.getAttributeBooleanValue(null, "visible", true);
            int sumaryId = parser.getAttributeResourceValue(null, "sumary", 0);
            String sumary = parser.getAttributeValue(null, "sumary");
            if (sumaryId != 0) {
                sumary = context.getString(sumaryId);
            }
            config.sumary = sumary;
            return config;
        } catch (Exception e) {
            LogHelper.w(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static SeekBarPreferenceConfig parseSeekBarPreference(Context context, XmlResourceParser parser) {
        SeekBarPreferenceConfig config = new SeekBarPreferenceConfig();
        try {
            config.className = parser.getAttributeValue(null, "class");
            config.logicClassName = parser.getAttributeValue(null, "logic");
            config.style = parser.getAttributeResourceValue(null, "style", 0);
            config.id = parser.getAttributeResourceValue(null, "id", View.NO_ID);
            config.title = getStringAttribute(context, parser, "title");
            config.visible = parser.getAttributeBooleanValue(null, "visible", true);
            config.minValue = parser.getAttributeIntValue(null, "minValue", 0);
            config.maxValue = parser.getAttributeIntValue(null, "maxValue", 100);
            config.progress = parser.getAttributeIntValue(null, "progress", 0);
            config.increment = parser.getAttributeIntValue(null, "increment", 1);
            config.displayFormat = parser.getAttributeValue(null, "displayFormat");
            return config;
        } catch (Exception e) {
            LogHelper.w(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static StatePreferenceConfig parseStatePreference(Context context, XmlResourceParser parser) {
        StatePreferenceConfig config = new StatePreferenceConfig();
        try {
            config.className = parser.getAttributeValue(null, "class");
            config.logicClassName = parser.getAttributeValue(null, "logic");
            config.style = parser.getAttributeResourceValue(null, "style", 0);
            config.id = parser.getAttributeResourceValue(null, "id", View.NO_ID);
            config.title = getStringAttribute(context, parser, "title");
            config.visible = parser.getAttributeBooleanValue(null, "visible", true);
            config.index = parser.getAttributeIntValue(null, "index", 0);
            config.cycleEnabled = parser.getAttributeBooleanValue(null, "cycleEnabled", true);
            config.entries.clear();

            String currentBrand = Constants.BRAND;
            String currentProduct = Build.HARDWARE;

            int eventType = parser.getEventType();

            String condition = null;
            String brand = null;
            String product = null;
            String name = null;
            Entry entry = null;
            boolean isParseEntry = false;
            do {
                name = parser.getName();
                if (eventType == XmlResourceParser.START_TAG) {
                    if ("if".equals(name)) {
                        condition = parser.getAttributeValue(null, "condition");
                        brand = parser.getAttributeValue(null, "brand");
                        product = parser.getAttributeValue(null, "product");
                        boolean matches = brandMatches(currentBrand, brand)
                            && productMatches(currentProduct, product);
                        if ("true".equals(condition)) {
                            if (!matches) {
                                XmlUtils.skipCurrentTag(parser);

                                eventType = parser.getEventType();
                                continue;
                            }
                        } else if ("false".equals(condition)) {
                            if (matches) {
                                XmlUtils.skipCurrentTag(parser);

                                eventType = parser.getEventType();
                                continue;
                            }
                        } else {
                            XmlUtils.skipCurrentTag(parser);

                            eventType = parser.getEventType();
                            continue;
                        }

                        eventType = parser.next();
                        continue;
                    }

                    if ("entry".equals(name)) {
                        isParseEntry = true;
                        entry = new Entry();
                        entry.name = getStringAttribute(context, parser, "name");
                        entry.index = parser.getAttributeIntValue(null, "index", -1);
                        entry.enabled = parser.getAttributeBooleanValue(null, "enabled", true);
                    } else if (isParseEntry) {
                        entry.value = XmlUtils.readThisBundleXml(parser, "entry", new String[1], null);
                        config.entries.add(entry);
                        isParseEntry = false;
                    }
                } else if (eventType == XmlResourceParser.END_TAG) {
                    if ("entry".equals(name)) {
                        config.entries.add(entry);
                    } else if ("StatePreference".equals(name)) {
                        break;
                    }
                }
                eventType = parser.next();
            } while (eventType != XmlResourceParser.END_DOCUMENT);
            return config;
        } catch (Exception e) {
            LogHelper.w(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static class Builder {

        private Context context;

        private int xml;

        private int divider;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setXml(int xml) {
            this.xml = xml;
            return this;
        }

        public Builder setDivider(int divider) {
            this.divider = divider;
            return this;
        }

        public PreferenceContainer create() {
            return new PreferenceContainer(this);
        }

    }

    private class NotifyAvailableAction implements Runnable {

        private final View child;

        public NotifyAvailableAction(View view) {
            child = view;
        }

        @Override
        public void run() {
            mContainer.focusableViewAvailable(child);
        }

    }

    public static interface PreferenceItemClickListener {

        void onPreferenceItemClick(Preference preference);

    }

    public static interface PreferenceItemKeyListener {

        boolean onKey(Preference preference, int keyCode, KeyEvent event);

    }

}
