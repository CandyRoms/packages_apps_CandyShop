/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
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

package org.candy.candyshop.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.util.Log;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.hwkeys.ActionUtils;
import com.android.internal.util.hwkeys.Config.ButtonConfig;
import com.android.settings.Utils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.candy.candyshop.preference.SecureSettingSwitchPreference;
import org.candy.candyshop.preference.SystemSettingSwitchPreference;

public class StockNavbar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String MISC_CATEGORY = "navigation_category";
    private static final String TAG = "Navigation";
    private static final String GESTURE_SWIPE_UP = "gesture_swipe_up";
    private static final String FULL_GESTURE_MODE = "full_gesture_mode";
    private static final String FULL_GESTURE_MODE_DT2S = "full_gesture_mode_dt2s";

    private SecureSettingSwitchPreference mGestureSwipeUp;
    private SystemSettingSwitchPreference mFullGestureMode;
    private SystemSettingSwitchPreference mFullGestureModeDt2s;

    private boolean mIsNavSwitchingMode = false;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.stock_navbar);

        mGestureSwipeUp = (SecureSettingSwitchPreference) findPreference(GESTURE_SWIPE_UP);
        boolean hasSwipe = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED,
                ActionUtils.hasNavbarByDefault(getActivity()) ? 1 : 0) != 0;
        updateSwipeUpGestureNav(hasSwipe);
        mGestureSwipeUp.setOnPreferenceChangeListener(this);

        mFullGestureMode = (SystemSettingSwitchPreference) findPreference(FULL_GESTURE_MODE);
        boolean fullGestureMode = Settings.System.getInt(getContentResolver(),
                Settings.System.FULL_GESTURE_NAVBAR,
                ActionUtils.hasNavbarByDefault(getActivity()) ? 1 : 0) != 0;
        updateSwipeUpGestureNav(fullGestureMode);
        mFullGestureMode.setOnPreferenceChangeListener(this);

        mFullGestureModeDt2s = (SystemSettingSwitchPreference) findPreference(FULL_GESTURE_MODE_DT2S);
        boolean fullGestureModeDt2s = Settings.System.getInt(getContentResolver(),
                Settings.System.FULL_GESTURE_NAVBAR_DT2S,
                ActionUtils.hasNavbarByDefault(getActivity()) ? 1 : 0) != 0;
        updateSwipeUpGestureNav(fullGestureModeDt2s);
        mFullGestureModeDt2s.setOnPreferenceChangeListener(this);

        mHandler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference.equals(mGestureSwipeUp)) {
            boolean swipeUpNavEnabled = ((Boolean)objValue);
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED,
                    swipeUpNavEnabled ? 1 : 0);
            updateSwipeUpGestureNav(swipeUpNavEnabled);
            return true;
        } else if (preference.equals(mFullGestureMode)) {
            boolean fullGestureMode = ((Boolean)objValue);
            Settings.System.putInt(getContentResolver(), Settings.System.FULL_GESTURE_NAVBAR,
                    fullGestureMode ? 1 : 0);
            updateFullGestureNav(fullGestureMode);
            return true;
        } else if (preference.equals(mFullGestureModeDt2s)) {
            boolean fullGestureModeDt2s = ((Boolean)objValue);
            Settings.System.putInt(getContentResolver(), Settings.System.FULL_GESTURE_NAVBAR_DT2S,
                    fullGestureModeDt2s ? 1 : 0);
            updateFullGestureNavD2ts(fullGestureModeDt2s);
            return true;
        }
        return false;
    }

    private void updateSwipeUpGestureNav(boolean swipeUpNavEnabled) {
        mGestureSwipeUp.setChecked(swipeUpNavEnabled);
    }

    private void updateFullGestureNav(boolean fullGestureMode) {
        mFullGestureMode.setChecked(fullGestureMode);
    }

    private void updateFullGestureNavD2ts(boolean fullGestureModeDt2s) {
        mFullGestureModeDt2s.setChecked(fullGestureModeDt2s);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CANDYSHOP;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.navigation;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
            };
}

