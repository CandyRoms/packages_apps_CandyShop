/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.candy.candyshop.fragments.batterylight;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import org.candy.candyshop.preference.SystemSettingSwitchPreference;

import java.util.List;
import java.util.ArrayList;

public class BatteryLightSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "BatteryLightSettings";

    private static final String LOW_COLOR_PREF = "low_color";
    private static final String MEDIUM_COLOR_PREF = "medium_color";
    private static final String FULL_COLOR_PREF = "full_color";
    private static final String REALLY_FULL_COLOR_PREF = "really_full_color";
    private static final String KEY_CATEGORY_GENERAL = "general_section";
    private static final String FAST_COLOR_PREF = "fast_color";
    private static final String FAST_CHARGING_LED_PREF = "fast_charging_led_enabled";
    private static final String BATTERY_LIGHT_PREF = "battery_light_enabled";
    private static final String BATTERY_PULSE_PREF = "battery_light_pulse";
    private static final String BATTERY_LIGHT_ONLY_FULL_PREF = "battery_light_only_fully_charged";
    private static final String KEY_CATEGORY_FAST_CHARGE = "fast_color_cat";
    private static final String KEY_CATEGORY_CHARGE_COLORS = "colors_list";

    private boolean mMultiColorLed;
    private SystemSettingSwitchPreference mEnabledPref;
    private SystemSettingSwitchPreference mPulsePref;
    private SystemSettingSwitchPreference mOnlyFullPref;
    private SystemSettingSwitchPreference mFastBatteryLightEnabledPref;
    private PreferenceGroup mColorPrefs;
    private BatteryLightPreference mLowColorPref;
    private BatteryLightPreference mMediumColorPref;
    private BatteryLightPreference mFullColorPref;
    private BatteryLightPreference mReallyFullColorPref;
    private BatteryLightPreference mFastColorPref;
    private static final int MENU_RESET = Menu.FIRST;
    private int mLowBatteryWarningLevel;
    private boolean mBatteryLightEnabled;
    private boolean mFastBatteryLightEnabled;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CONFIGURE_NOTIFICATION;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.battery_light_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getContentResolver();
        mLowBatteryWarningLevel = getResources().getInteger(
                com.android.internal.R.integer.config_lowBatteryWarningLevel);
        mBatteryLightEnabled = getResources().getBoolean(
                com.android.internal.R.bool.config_intrusiveBatteryLed);
        mFastBatteryLightEnabled = getResources().getBoolean(
                com.android.internal.R.bool.config_FastChargingLedSupported);

        mEnabledPref = (SystemSettingSwitchPreference)prefSet.findPreference(BATTERY_LIGHT_PREF);
        if (mEnabledPref != null) {
            mEnabledPref.setChecked(Settings.System.getInt(resolver,
                        Settings.System.BATTERY_LIGHT_ENABLED, mBatteryLightEnabled ? 1 : 0) != 0);
            mEnabledPref.setOnPreferenceChangeListener(this);
        }

        mPulsePref = (SystemSettingSwitchPreference)prefSet.findPreference(BATTERY_PULSE_PREF);
        if (mPulsePref != null) {
            mPulsePref.setChecked(Settings.System.getInt(resolver,
                        Settings.System.BATTERY_LIGHT_PULSE, mBatteryLightEnabled ? 1 : 0) != 0);
            mPulsePref.setOnPreferenceChangeListener(this);
        }

        mOnlyFullPref = (SystemSettingSwitchPreference)prefSet.findPreference(BATTERY_LIGHT_ONLY_FULL_PREF);
        mOnlyFullPref.setOnPreferenceChangeListener(this);

        // Does the Device support changing battery LED colors?
        if (getResources().getBoolean(com.android.internal.R.bool.config_multiColorBatteryLed)) {
            setHasOptionsMenu(true);

            // Low, Medium and full color preferences
            mLowColorPref = (BatteryLightPreference) prefSet.findPreference(LOW_COLOR_PREF);
            mLowColorPref.setOnPreferenceChangeListener(this);

            mMediumColorPref = (BatteryLightPreference) prefSet.findPreference(MEDIUM_COLOR_PREF);
            mMediumColorPref.setOnPreferenceChangeListener(this);

            mFullColorPref = (BatteryLightPreference) prefSet.findPreference(FULL_COLOR_PREF);
            mFullColorPref.setOnPreferenceChangeListener(this);

            mReallyFullColorPref = (BatteryLightPreference) prefSet.findPreference(REALLY_FULL_COLOR_PREF);
            mReallyFullColorPref.setOnPreferenceChangeListener(this);

            mFastBatteryLightEnabledPref = (SystemSettingSwitchPreference) prefSet.findPreference(FAST_CHARGING_LED_PREF);
            if (mFastBatteryLightEnabledPref != null) {
                mFastBatteryLightEnabledPref.setChecked(Settings.System.getInt(resolver,
                            Settings.System.FAST_CHARGING_LED_ENABLED, mFastBatteryLightEnabled ? 1 : 0) != 0);
                mFastBatteryLightEnabledPref.setOnPreferenceChangeListener(this);
            }

            mFastColorPref = (BatteryLightPreference) prefSet.findPreference(FAST_COLOR_PREF);
            mFastColorPref.setOnPreferenceChangeListener(this);

            // Does the Device support fast charge ?
            if (!getResources().getBoolean(com.android.internal.R.bool.config_FastChargingLedSupported)) {
                prefSet.removePreference(prefSet.findPreference(KEY_CATEGORY_FAST_CHARGE));
            }
        } else {
            prefSet.removePreference(prefSet.findPreference(KEY_CATEGORY_CHARGE_COLORS));
            // not multi color cant have fast charge
            prefSet.removePreference(prefSet.findPreference(KEY_CATEGORY_FAST_CHARGE));
        }
        boolean showOnlyWhenFull = Settings.System.getInt(resolver,
                Settings.System.BATTERY_LIGHT_ONLY_FULLY_CHARGED, 0) != 0;
        updateEnablement(showOnlyWhenFull);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDefault();
    }

    private void refreshDefault() {
        ContentResolver resolver = getContentResolver();
        Resources res = getResources();

        if (mLowColorPref != null) {
            int lowColor = Settings.System.getInt(resolver, Settings.System.BATTERY_LIGHT_LOW_COLOR,
                    res.getInteger(com.android.internal.R.integer.config_notificationsBatteryLowARGB));
            mLowColorPref.setColor(lowColor);
        }

        if (mMediumColorPref != null) {
            int mediumColor = Settings.System.getInt(resolver, Settings.System.BATTERY_LIGHT_MEDIUM_COLOR,
                    res.getInteger(com.android.internal.R.integer.config_notificationsBatteryMediumARGB));
            mMediumColorPref.setColor(mediumColor);
        }

        if (mFullColorPref != null) {
            int fullColor = Settings.System.getInt(resolver, Settings.System.BATTERY_LIGHT_FULL_COLOR,
                    res.getInteger(com.android.internal.R.integer.config_notificationsBatteryFullARGB));
            mFullColorPref.setColor(fullColor);
        }

        if (mReallyFullColorPref != null) {
            int reallyFullColor = Settings.System.getInt(resolver, Settings.System.BATTERY_LIGHT_REALLY_FULL_COLOR,
                    res.getInteger(com.android.internal.R.integer.config_notificationsBatteryFullARGB));
            mReallyFullColorPref.setColor(reallyFullColor);
        }

        if (mFastColorPref != null) {
            int fastColor = Settings.System.getInt(resolver, Settings.System.FAST_BATTERY_LIGHT_COLOR,
                    res.getInteger(com.android.internal.R.integer.config_notificationsFastBatteryARGB));
            mFastColorPref.setColor(fastColor);
        }
    }

    /**
     * Updates the default or application specific notification settings.
     *
     * @param key of the specific setting to update
     * @param color
     */
    protected void updateValues(String key, Integer color) {
        ContentResolver resolver = getContentResolver();

        if (key.equals(LOW_COLOR_PREF)) {
            Settings.System.putInt(resolver, Settings.System.BATTERY_LIGHT_LOW_COLOR, color);
        } else if (key.equals(MEDIUM_COLOR_PREF)) {
            Settings.System.putInt(resolver, Settings.System.BATTERY_LIGHT_MEDIUM_COLOR, color);
        } else if (key.equals(FULL_COLOR_PREF)) {
            Settings.System.putInt(resolver, Settings.System.BATTERY_LIGHT_FULL_COLOR, color);
        } else if (key.equals(REALLY_FULL_COLOR_PREF)) {
            Settings.System.putInt(resolver, Settings.System.BATTERY_LIGHT_REALLY_FULL_COLOR, color);
        } else if (key.equals(FAST_COLOR_PREF)) {
            Settings.System.putInt(resolver, Settings.System.FAST_BATTERY_LIGHT_COLOR, color);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getResources().getBoolean(
                com.android.internal.R.bool.config_multiColorBatteryLed)) {
            menu.add(0, MENU_RESET, 0, R.string.reset)
                    .setIcon(R.drawable.ic_settings_backup_restore)
                    .setAlphabeticShortcut('r')
                    .setShowAsActionFlags(
                            MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefaults();
                return true;
        }
        return false;
    }

    protected void resetColors() {
        ContentResolver resolver = getActivity().getContentResolver();
        Resources res = getResources();

        // Reset to the framework default colors
        Settings.System.putInt(resolver, Settings.System.BATTERY_LIGHT_LOW_COLOR,
                res.getInteger(com.android.internal.R.integer.config_notificationsBatteryLowARGB));
        Settings.System.putInt(resolver, Settings.System.BATTERY_LIGHT_MEDIUM_COLOR,
                res.getInteger(com.android.internal.R.integer.config_notificationsBatteryMediumARGB));
        Settings.System.putInt(resolver, Settings.System.BATTERY_LIGHT_FULL_COLOR,
                res.getInteger(com.android.internal.R.integer.config_notificationsBatteryFullARGB));
        Settings.System.putInt(resolver, Settings.System.BATTERY_LIGHT_REALLY_FULL_COLOR,
                res.getInteger(com.android.internal.R.integer.config_notificationsBatteryFullARGB));
        Settings.System.putInt(resolver, Settings.System.FAST_BATTERY_LIGHT_COLOR,
                res.getInteger(com.android.internal.R.integer.config_notificationsFastBatteryARGB));
        refreshDefault();
    }

    protected void resetToDefaults() {
        if (mEnabledPref != null) mEnabledPref.setChecked(true);
        if (mOnlyFullPref != null) mOnlyFullPref.setChecked(false);
        if (mFastBatteryLightEnabledPref != null) mFastBatteryLightEnabledPref.setChecked(false);
        resetColors();

        if (mLowColorPref != null) {
            mLowColorPref.setEnabled(true);
        }
        if (mMediumColorPref != null) {
            mMediumColorPref.setEnabled(true);
        }
        if (mFullColorPref != null) {
            mFullColorPref.setEnabled(true);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mEnabledPref) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.BATTERY_LIGHT_ENABLED, value ? 1:0);
        } else if (preference == mPulsePref) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.BATTERY_LIGHT_PULSE, value ? 1:0);
        } else if (preference == mOnlyFullPref) {
            boolean value = (Boolean) objValue;
            // If enabled, disable all but really full color preference.
            updateEnablement(value);
        } else if (preference == mFastBatteryLightEnabledPref) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FAST_CHARGING_LED_ENABLED, value ? 1:0);
        } else {
            BatteryLightPreference lightPref = (BatteryLightPreference) preference;
            updateValues(lightPref.getKey(), lightPref.getColor());
        }
        return true;
    }

    private void updateEnablement(boolean showOnlyWhenFull) {
        // If enabled, disable all but really full color preference.
        if (mLowColorPref != null) {
            mLowColorPref.setEnabled(!showOnlyWhenFull);
        }
        if (mMediumColorPref != null) {
            mMediumColorPref.setEnabled(!showOnlyWhenFull);
        }
        if (mFullColorPref != null) {
            mFullColorPref.setEnabled(!showOnlyWhenFull);
        }
        if (mFastColorPref != null) {
            mFastColorPref.setEnabled(!showOnlyWhenFull);
        }
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.battery_light_settings;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    final Resources res = context.getResources();
                    if (!res.getBoolean(com.android.internal.R.bool.config_intrusiveBatteryLed)) {
                        result.add(BATTERY_LIGHT_PREF);
                        result.add(BATTERY_PULSE_PREF);
                        result.add(BATTERY_LIGHT_ONLY_FULL_PREF);
                    }
                    if (!res.getBoolean(com.android.internal.R.bool.config_multiColorBatteryLed)) {
                        result.add(LOW_COLOR_PREF);
                        result.add(MEDIUM_COLOR_PREF);
                        result.add(FULL_COLOR_PREF);
                        result.add(REALLY_FULL_COLOR_PREF);
                    }
                    if (!res.getBoolean(com.android.internal.R.bool.config_FastChargingLedSupported)) {
                        result.add(FAST_CHARGING_LED_PREF);
                        result.add(FAST_COLOR_PREF);
                    }
                    return result;
                }
            };
}
