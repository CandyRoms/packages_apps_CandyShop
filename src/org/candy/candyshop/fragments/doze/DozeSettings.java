/*
 * Copyright (C) 2018-2019 crDroid Android Project
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

package org.candy.candyshop.fragments.doze;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import org.candy.candyshop.R;
import org.candy.candyshop.fragments.doze.Utils;
import org.candy.candyshop.preference.CustomSeekBarPreference;
import org.candy.candyshop.preference.SecureSettingSwitchPreference;
import org.candy.candyshop.preference.SecureSettingSeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.List;
import java.util.ArrayList;

@SearchIndexable
public class DozeSettings extends SettingsPreferenceFragment implements Indexable,
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "DozeSettings";

    private static final String CATEG_DOZE_SENSOR = "doze_sensor";

    private static final String KEY_DOZE_ENABLED = "doze_enabled";
    private static final String KEY_DOZE_ALWAYS_ON = "doze_always_on";
    private static final String KEY_DOZE_TILT_GESTURE = "doze_tilt_gesture";
    private static final String KEY_DOZE_PICK_UP_GESTURE = "doze_pick_up_gesture";
    private static final String KEY_DOZE_HANDWAVE_GESTURE = "doze_handwave_gesture";
    private static final String KEY_DOZE_POCKET_GESTURE = "doze_pocket_gesture";
    private static final String KEY_DOZE_GESTURE_VIBRATE = "doze_gesture_vibrate";

    private SecureSettingSwitchPreference mDozeEnabledPreference;
    private SecureSettingSwitchPreference mDozeAlwaysOnPreference;
    private SecureSettingSwitchPreference mTiltPreference;
    private SecureSettingSwitchPreference mPickUpPreference;
    private SecureSettingSwitchPreference mHandwavePreference;
    private SecureSettingSwitchPreference mPocketPreference;
    private SecureSettingSeekBarPreference mVibratePreference;

    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.doze_settings);

        Context context = getContext();

        PreferenceCategory dozeSensorCategory =
                (PreferenceCategory) getPreferenceScreen().findPreference(CATEG_DOZE_SENSOR);

        final ContentResolver resolver = getActivity().getContentResolver();

        mDozeEnabledPreference = (SecureSettingSwitchPreference) findPreference("doze_enabled");
        mDozeEnabledPreference.setOnPreferenceChangeListener(this);

        mDozeAlwaysOnPreference = (SecureSettingSwitchPreference) findPreference("doze_always_on");
        mDozeAlwaysOnPreference.setOnPreferenceChangeListener(this);

        mTiltPreference = (SecureSettingSwitchPreference) findPreference("doze_tilt_gesture");
        mTiltPreference.setOnPreferenceChangeListener(this);

        mPickUpPreference = (SecureSettingSwitchPreference) findPreference("doze_pick_up_gesture");
        mPickUpPreference.setOnPreferenceChangeListener(this);

        mHandwavePreference = (SecureSettingSwitchPreference) findPreference("doze_handwave_gesture");
        mHandwavePreference.setOnPreferenceChangeListener(this);

        mPocketPreference = (SecureSettingSwitchPreference) findPreference("doze_pocket_gesture");
        mPocketPreference.setOnPreferenceChangeListener(this);

        mVibratePreference = (SecureSettingSeekBarPreference) findPreference("doze_gesture_vibrate");
        mVibratePreference.setOnPreferenceChangeListener(this);


        // Hide sensor related features if the device doesn't support them
        if (!Utils.getTiltSensor(context) && !Utils.getPickupSensor(context) && !Utils.getProximitySensor(context)) {
            getPreferenceScreen().removePreference(dozeSensorCategory);
        } else {
            if (!Utils.getTiltSensor(context)) {
                getPreferenceScreen().removePreference(mTiltPreference);
            } else if (!Utils.getPickupSensor(context)) {
                getPreferenceScreen().removePreference(mPickUpPreference);
            } else if (!Utils.getProximitySensor(context)) {
                getPreferenceScreen().removePreference(mHandwavePreference);
                getPreferenceScreen().removePreference(mPocketPreference);
            }
        }

        // Hides always on toggle if device doesn't support it (based on config_dozeAlwaysOnDisplayAvailable overlay)
        boolean mAlwaysOnAvailable = getResources().getBoolean(com.android.internal.R.bool.config_dozeAlwaysOnDisplayAvailable);
        if (!mAlwaysOnAvailable) {
            getPreferenceScreen().removePreference(mDozeAlwaysOnPreference);
        }
        updatePrefs();
    }

    public void updatePrefs() {
        final ContentResolver resolver = getActivity().getContentResolver();
        // Disable sensor settings if doze and AOD are enabled
        boolean mAODEnabled = (Settings.Secure.getIntForUser(resolver,
                Settings.Secure.DOZE_ALWAYS_ON, 0, UserHandle.USER_CURRENT) == 1);
        boolean mDozeEnabled = (Settings.Secure.getIntForUser(resolver,
                Settings.Secure.DOZE_ENABLED, 0, UserHandle.USER_CURRENT) == 1);

        if (mAODEnabled) {
            if (mDozeEnabledPreference != null) {
                mDozeEnabledPreference.setEnabled(false);
            }
            if (mTiltPreference != null) {
                mTiltPreference.setEnabled(false);
            }
            if (mPickUpPreference != null) {
                mPickUpPreference.setEnabled(false);
            }
            if (mHandwavePreference != null) {
                mHandwavePreference.setEnabled(false);
            }
            if (mPocketPreference != null) {
                mPocketPreference.setEnabled(false);
            }
            if (mVibratePreference != null) {
                mVibratePreference.setEnabled(false);
            }
        } else {
            mDozeEnabledPreference.setEnabled(true);
            if (!mDozeEnabled){
                if (mTiltPreference != null) {
                    mTiltPreference.setEnabled(false);
                }
                if (mPickUpPreference != null) {
                    mPickUpPreference.setEnabled(false);
                }
                if (mHandwavePreference != null) {
                    mHandwavePreference.setEnabled(false);
                }
                if (mPocketPreference != null) {
                    mPocketPreference.setEnabled(false);
                }
                if (mVibratePreference != null) {
                    mVibratePreference.setEnabled(false);
                }
            } else {
                if (mTiltPreference != null) {
                    mTiltPreference.setEnabled(true);
                }
                if (mPickUpPreference != null) {
                    mPickUpPreference.setEnabled(true);
                }
                if (mHandwavePreference != null) {
                    mHandwavePreference.setEnabled(true);
                }
                if (mPocketPreference != null) {
                    mPocketPreference.setEnabled(true);
                }
                if (mVibratePreference != null) {
                    mVibratePreference.setEnabled(true);
                }
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Context context = getContext();
        ContentResolver resolver = context.getContentResolver();

        if ((preference == mTiltPreference) || (preference == mPickUpPreference) ||
                (preference == mHandwavePreference) || (preference == mPocketPreference)) {
            boolean value = (Boolean) newValue;
            Utils.enableService(context);
            if (newValue != null)
                sensorWarning(context);
            return true;
        } else if (preference == mDozeAlwaysOnPreference) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.DOZE_ALWAYS_ON, value ? 1 : 0);
            mDozeAlwaysOnPreference.setChecked(value);
            updatePrefs();
            return true;
        } else if (preference == mDozeEnabledPreference) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(resolver,
                    Settings.Secure.DOZE_ENABLED, value ? 1 : 0);
            mDozeEnabledPreference.setChecked(value);
            updatePrefs();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        updatePrefs();
        return super.onPreferenceTreeClick(preference);
    }

    private void sensorWarning(Context context) {
        mPreferences = context.getSharedPreferences("dozesettingsfragment", Activity.MODE_PRIVATE);
        if (mPreferences.getBoolean("sensor_warning_shown", false)) {
            return;
        }
        context.getSharedPreferences("dozesettingsfragment", Activity.MODE_PRIVATE)
                .edit()
                .putBoolean("sensor_warning_shown", true)
                .commit();

        new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.sensor_warning_title))
                .setMessage(getResources().getString(R.string.sensor_warning_message))
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                }).show();
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.DOZE_ENABLED, mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_doze_enabled_by_default) ? 1 : 0,
                UserHandle.USER_CURRENT);
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.DOZE_ALWAYS_ON, mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_dozeAlwaysOnEnabled) ? 1 : 0,
                UserHandle.USER_CURRENT);
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.DOZE_TILT_GESTURE, 0, UserHandle.USER_CURRENT);
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.DOZE_PICK_UP_GESTURE, 0, UserHandle.USER_CURRENT);
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.DOZE_HANDWAVE_GESTURE, 0, UserHandle.USER_CURRENT);
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.DOZE_POCKET_GESTURE, 0, UserHandle.USER_CURRENT);
        Settings.Secure.putIntForUser(resolver,
                Settings.Secure.DOZE_GESTURE_VIBRATE, 0, UserHandle.USER_CURRENT);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CANDYSHOP;
    }

    /**
     * For search
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.doze_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);

                    keys.add(KEY_DOZE_GESTURE_VIBRATE);
                    if (!Utils.getTiltSensor(context)) {
                        keys.add(KEY_DOZE_TILT_GESTURE);
                    }
                    if (!Utils.getPickupSensor(context)) {
                        keys.add(KEY_DOZE_PICK_UP_GESTURE);
                    }
                    if (!Utils.getProximitySensor(context)) {
                        keys.add(KEY_DOZE_HANDWAVE_GESTURE);
                        keys.add(KEY_DOZE_POCKET_GESTURE);
                    }

                    return keys;
                }
            };
}
