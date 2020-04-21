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

package org.candy.candyshop.fragments;

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
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.List;
import java.util.ArrayList;

@SearchIndexable
public class DozeSettings extends SettingsPreferenceFragment implements Indexable,
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "DozeSettings";

    private static final String KEY_DOZE = "doze_enabled";
    private static final String KEY_DOZE_ALWAYS_ON = "doze_always_on";

    private static final String PULSE_AMBIENT_LIGHT_COLOR = "pulse_ambient_light_color";

    private static final String CATEG_DOZE_SENSOR = "doze_sensor";

    private static final String KEY_DOZE_TILT_GESTURE = "doze_tilt_gesture";
    private static final String KEY_DOZE_PICK_UP_GESTURE = "doze_pick_up_gesture";
    private static final String KEY_DOZE_HANDWAVE_GESTURE = "doze_handwave_gesture";
    private static final String KEY_DOZE_POCKET_GESTURE = "doze_pocket_gesture";
    private static final String KEY_DOZE_GESTURE_VIBRATE = "doze_gesture_vibrate";

    private ColorPickerPreference mEdgeLightColorPreference;

    private SwitchPreference mDozePreference;
    private SwitchPreference mDozeAlwaysOnPreference;
    private SwitchPreference mTiltPreference;
    private SwitchPreference mPickUpPreference;
    private SwitchPreference mHandwavePreference;
    private SwitchPreference mPocketPreference;

    private SharedPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.doze_settings);

        Context context = getContext();

        mEdgeLightColorPreference = (ColorPickerPreference) findPreference(PULSE_AMBIENT_LIGHT_COLOR);
        int edgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.PULSE_AMBIENT_LIGHT_COLOR, 0xFF3980FF);
        mEdgeLightColorPreference.setNewPreviewColor(edgeLightColor);
        mEdgeLightColorPreference.setAlphaSliderEnabled(false);
        String edgeLightColorHex = String.format("#%08x", (0xFF3980FF & edgeLightColor));
        if (edgeLightColorHex.equals("#ff3980ff")) {
            mEdgeLightColorPreference.setSummary(R.string.default_string);
        } else {
            mEdgeLightColorPreference.setSummary(edgeLightColorHex);
        }
        mEdgeLightColorPreference.setOnPreferenceChangeListener(this);

        PreferenceCategory dozeSensorCategory =
                (PreferenceCategory) getPreferenceScreen().findPreference(CATEG_DOZE_SENSOR);

        mDozePreference = (SwitchPreference) findPreference(KEY_DOZE);

        mDozeAlwaysOnPreference = (SwitchPreference) findPreference(KEY_DOZE_ALWAYS_ON);
        mDozeAlwaysOnPreference.setOnPreferenceChangeListener(this);

        mTiltPreference = (SwitchPreference) findPreference(KEY_DOZE_TILT_GESTURE);
        mTiltPreference.setOnPreferenceChangeListener(this);

        mPickUpPreference = (SwitchPreference) findPreference(KEY_DOZE_PICK_UP_GESTURE);
        mPickUpPreference.setOnPreferenceChangeListener(this);

        mHandwavePreference = (SwitchPreference) findPreference(KEY_DOZE_HANDWAVE_GESTURE);
        mHandwavePreference.setOnPreferenceChangeListener(this);

        mPocketPreference = (SwitchPreference) findPreference(KEY_DOZE_POCKET_GESTURE);
        mPocketPreference.setOnPreferenceChangeListener(this);

        updateState();

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
            getPreferenceScreen().removePreference(findPreference(KEY_DOZE_ALWAYS_ON));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateState();
    }

    private void updateState() {
        Context context = getContext();

        if (mDozePreference != null) {
            mDozePreference.setChecked(Utils.isDozeEnabled(context));
        }
        if (mDozeAlwaysOnPreference != null) {
            mDozeAlwaysOnPreference.setChecked(Utils.isDozeAlwaysOnEnabled(context));
        }
        if (mTiltPreference != null) {
            mTiltPreference.setChecked(Utils.tiltEnabled(context));
        }
        if (mPickUpPreference != null) {
            mPickUpPreference.setChecked(Utils.pickUpEnabled(context));
        }
        if (mHandwavePreference != null) {
            mHandwavePreference.setChecked(Utils.handwaveGestureEnabled(context));
        }
        if (mPocketPreference != null) {
            mPocketPreference.setChecked(Utils.pocketGestureEnabled(context));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Context context = getContext();
        ContentResolver resolver = context.getContentResolver();

        if (preference == mDozePreference) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putIntForUser(resolver, Settings.Secure.DOZE_ENABLED,
                 value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff3980ff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putIntForUser(resolver,
                    Settings.System.PULSE_AMBIENT_LIGHT_COLOR, intHex, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mTiltPreference) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putIntForUser(resolver, Settings.Secure.DOZE_TILT_GESTURE,
                 value ? 1 : 0, UserHandle.USER_CURRENT);
            Utils.enableService(context);
            if (newValue != null)
                sensorWarning(context);
            return true;
        } else if (preference == mPickUpPreference) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putIntForUser(resolver, Settings.Secure.DOZE_PICK_UP_GESTURE,
                 value ? 1 : 0, UserHandle.USER_CURRENT);
            Utils.enableService(context);
            if (newValue != null)
                sensorWarning(context);
            return true;
        } else if (preference == mHandwavePreference) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putIntForUser(resolver, Settings.Secure.DOZE_HANDWAVE_GESTURE,
                 value ? 1 : 0, UserHandle.USER_CURRENT);
            Utils.enableService(context);
            if (newValue != null)
                sensorWarning(context);
            return true;
        } else if (preference == mPocketPreference) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putIntForUser(resolver, Settings.Secure.DOZE_POCKET_GESTURE,
                 value ? 1 : 0, UserHandle.USER_CURRENT);
            Utils.enableService(context);
            if (newValue != null)
                sensorWarning(context);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
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
        Settings.System.putIntForUser(resolver,
                Settings.System.PULSE_AMBIENT_LIGHT, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.PULSE_AMBIENT_LIGHT_COLOR, 0xFF3980FF, UserHandle.USER_CURRENT);
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
