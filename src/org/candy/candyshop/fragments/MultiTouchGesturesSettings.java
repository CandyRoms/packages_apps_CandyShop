/*
 * Copyright (C) 2018 CarbonROM
 * Copyright (C) 2019 CandyRoms
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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

import org.candy.candyshop.preference.CustomSeekBarPreference;
import org.candy.candyshop.fragments.hideappfromrecents.HAFRAppChooserAdapter.AppItem;
import org.candy.candyshop.fragments.hideappfromrecents.HAFRAppChooserDialog;

public class MultiTouchGesturesSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "MultiTouchGestures";
    private CustomSeekBarPreference mMultiTouchGestureFingers;
    private ListPreference mMultiTouchGestureRight;
    private ListPreference mMultiTouchGestureLeft;
    private ListPreference mMultiTouchGestureUp;
    private ListPreference mMultiTouchGestureDown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.multi_touch_gestures);

        ContentResolver resolver = getActivity().getContentResolver();

        mMultiTouchGestureFingers = (CustomSeekBarPreference) findPreference("multi_touch_gestures_fingers");
        mMultiTouchGestureFingers.setOnPreferenceChangeListener(this);
        int multiTouchGestureFingers = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_FINGERS,
                2, UserHandle.USER_CURRENT);
        mMultiTouchGestureFingers.setValue(multiTouchGestureFingers);

        mMultiTouchGestureRight = (ListPreference) findPreference("multi_touch_gestures_right");
        mMultiTouchGestureRight.setOnPreferenceChangeListener(this);
        int multiTouchGestureRight = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_RIGHT,
                0, UserHandle.USER_CURRENT);
        mMultiTouchGestureRight.setValue(String.valueOf(multiTouchGestureRight));
        if (multiTouchGestureRight == 1001) {
            setApplicationNamePreferenceSummary(Settings.System.getStringForUser(getContentResolver(),
                Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_PACKAGE_RIGHT,
                UserHandle.USER_CURRENT), mMultiTouchGestureRight);
        } else {
            mMultiTouchGestureRight.setSummary(mMultiTouchGestureRight.getEntry());
        }

        mMultiTouchGestureLeft = (ListPreference) findPreference("multi_touch_gestures_left");
        mMultiTouchGestureLeft.setOnPreferenceChangeListener(this);
        int multiTouchGestureLeft = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_LEFT,
                0, UserHandle.USER_CURRENT);
        mMultiTouchGestureLeft.setValue(String.valueOf(multiTouchGestureLeft));
        if (multiTouchGestureLeft == 1001) {
            setApplicationNamePreferenceSummary(Settings.System.getStringForUser(getContentResolver(),
                Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_PACKAGE_LEFT,
                UserHandle.USER_CURRENT), mMultiTouchGestureLeft);
        } else {
            mMultiTouchGestureLeft.setSummary(mMultiTouchGestureLeft.getEntry());
        }

        mMultiTouchGestureUp = (ListPreference) findPreference("multi_touch_gestures_up");
        mMultiTouchGestureUp.setOnPreferenceChangeListener(this);
        int multiTouchGestureUp = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_UP,
                0, UserHandle.USER_CURRENT);
        mMultiTouchGestureUp.setValue(String.valueOf(multiTouchGestureUp));
        if (multiTouchGestureUp == 1001) {
            setApplicationNamePreferenceSummary(Settings.System.getStringForUser(getContentResolver(),
                Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_PACKAGE_UP,
                UserHandle.USER_CURRENT), mMultiTouchGestureUp);
        } else {
            mMultiTouchGestureUp.setSummary(mMultiTouchGestureUp.getEntry());
        }

        mMultiTouchGestureDown = (ListPreference) findPreference("multi_touch_gestures_down");
        mMultiTouchGestureDown.setOnPreferenceChangeListener(this);
        int multiTouchGestureDown = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_DOWN,
                0, UserHandle.USER_CURRENT);
        mMultiTouchGestureDown.setValue(String.valueOf(multiTouchGestureDown));
        if (multiTouchGestureDown == 1001) {
            setApplicationNamePreferenceSummary(Settings.System.getStringForUser(getContentResolver(),
                Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_PACKAGE_DOWN,
                UserHandle.USER_CURRENT), mMultiTouchGestureDown);
        } else {
            mMultiTouchGestureDown.setSummary(mMultiTouchGestureDown.getEntry());
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CANDYSHOP;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void launchAppChooseDialog(String setting, ListPreference pref) {
        HAFRAppChooserDialog dDialog = new HAFRAppChooserDialog(getActivity()) {
            @Override
            public void onListViewItemClick(AppItem info, int id) {
                Settings.System.putStringForUser(getContentResolver(),
                    setting, info.packageName, UserHandle.USER_CURRENT);
                setApplicationNamePreferenceSummary(info.packageName, pref);
            }
        };
        dDialog.setCancelable(false);
        dDialog.setLauncherFilter(true);
        dDialog.show(1);
    }

    private void setApplicationNamePreferenceSummary(String pkg, ListPreference pref) {
        PackageManager packageManager = getActivity().getApplicationContext().getPackageManager();
        String packageInfo = "";
        try {
            packageInfo = packageManager.getApplicationLabel(packageManager.getApplicationInfo(pkg, PackageManager.GET_META_DATA)).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        pref.setSummary(getActivity().getString(R.string.multi_touch_gesture_launch) + " " + packageInfo);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference.equals(mMultiTouchGestureFingers)) {
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_FINGERS, (Integer) objValue, UserHandle.USER_CURRENT);
            return true;
        }

        if (preference.equals(mMultiTouchGestureRight)) {
            int multiTouchGestureRight = Integer.parseInt(((String) objValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_RIGHT, multiTouchGestureRight, UserHandle.USER_CURRENT);
            int index = mMultiTouchGestureRight.findIndexOfValue((String) objValue);
            if (multiTouchGestureRight == 1001) {
                launchAppChooseDialog(Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_PACKAGE_RIGHT, mMultiTouchGestureRight);
            } else {
                mMultiTouchGestureRight.setSummary(
                        mMultiTouchGestureRight.getEntries()[index]);
            }
            return true;
        }

        if (preference.equals(mMultiTouchGestureLeft)) {
            int multiTouchGestureLeft = Integer.parseInt(((String) objValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_LEFT, multiTouchGestureLeft, UserHandle.USER_CURRENT);
            int index = mMultiTouchGestureLeft.findIndexOfValue((String) objValue);
            if (multiTouchGestureLeft == 1001) {
                launchAppChooseDialog(Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_PACKAGE_LEFT, mMultiTouchGestureLeft);
            } else {
                mMultiTouchGestureLeft.setSummary(
                        mMultiTouchGestureLeft.getEntries()[index]);
            }
            return true;
        }

        if (preference.equals(mMultiTouchGestureUp)) {
            int multiTouchGestureUp = Integer.parseInt(((String) objValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_UP, multiTouchGestureUp, UserHandle.USER_CURRENT);
            int index = mMultiTouchGestureUp.findIndexOfValue((String) objValue);
            if (multiTouchGestureUp == 1001) {
                launchAppChooseDialog(Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_PACKAGE_UP, mMultiTouchGestureUp);
            } else {
                mMultiTouchGestureUp.setSummary(
                        mMultiTouchGestureUp.getEntries()[index]);
            }
            return true;
        }

        if (preference.equals(mMultiTouchGestureDown)) {
            int multiTouchGestureDown = Integer.parseInt(((String) objValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_DOWN, multiTouchGestureDown, UserHandle.USER_CURRENT);
            int index = mMultiTouchGestureDown.findIndexOfValue((String) objValue);
            if (multiTouchGestureDown == 1001) {
                launchAppChooseDialog(Settings.System.MULTI_TOUCH_CUSTOM_GESTURE_PACKAGE_DOWN, mMultiTouchGestureDown);
            } else {
                mMultiTouchGestureDown.setSummary(
                        mMultiTouchGestureDown.getEntries()[index]);
            }
            return true;
        }

        return false;
    }
} 
