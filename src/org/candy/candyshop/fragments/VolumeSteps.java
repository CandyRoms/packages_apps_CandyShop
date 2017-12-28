/*
 * Copyright (C) 2015 The Dirty Unicorns Project
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.util.candy.DeviceUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class VolumeSteps extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "VolumeSteps";

    // base map of all preference keys and the associated stream
    private static final Map<String, Integer> volume_map = new HashMap<String, Integer>();
    static {
        volume_map.put("volume_steps_alarm", new Integer(AudioManager.STREAM_ALARM));
        volume_map.put("volume_steps_music", new Integer(AudioManager.STREAM_MUSIC));
        volume_map.put("volume_steps_notification", new Integer(AudioManager.STREAM_NOTIFICATION));
        volume_map.put("volume_steps_ring", new Integer(AudioManager.STREAM_RING));
        volume_map.put("volume_steps_system", new Integer(AudioManager.STREAM_SYSTEM));
        volume_map.put("volume_steps_voice_call", new Integer(AudioManager.STREAM_VOICE_CALL));
    }

    // entries to remove on non-telephony devices
    private static final Set<String> telephony_set = new HashSet<String>();
    static {
        telephony_set.add("volume_steps_ring");
        telephony_set.add("volume_steps_voice_call");
    }

    // set of available pref keys after device configuration filter
    private Set<String> mAvailableKeys = new HashSet<String>();
    private AudioManager mAudioManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.volume_steps);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final PreferenceScreen prefScreen = getPreferenceScreen();
        mAvailableKeys = volume_map.keySet();

        // remove invalid audio stream prefs
        boolean isPhone = DeviceUtils.isPhone(getActivity());

        if (!isPhone) {
            // remove telephony keys from available set
            mAvailableKeys.removeAll(telephony_set);
            for (String key : telephony_set) {
                Preference toRemove = prefScreen.findPreference(key);
                if (toRemove != null) {
                    prefScreen.removePreference(toRemove);
                }
            }
        }

        // initialize prefs: set defaults if first run, set listeners and update values
        for (String key : mAvailableKeys) {
            Preference pref = prefScreen.findPreference(key);
            if (pref == null || !(pref instanceof ListPreference)) {
                continue;
            }
            final ListPreference listPref = (ListPreference) pref;
            int steps = mAudioManager.getStreamMaxVolume(volume_map.get(key));
            updateVolumeStepPrefs(listPref, steps);
            listPref.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference.hasKey() && mAvailableKeys.contains(preference.getKey())) {
            commitVolumeSteps(preference, Integer.parseInt(objValue.toString()));
        }
        return true;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CANDYSHOP;
    }
    
        private int getDefaultSteps(Preference pref) {
        if (pref == null || !(pref instanceof ListPreference)) {
            // unlikely
            return -1;
        }
        String key = pref.getKey();
        return mAudioManager.getDefaultStreamMaxVolume(volume_map.get(key));
    }


    private void updateVolumeStepPrefs(Preference pref, int steps) {
        if (pref == null || !(pref instanceof ListPreference)) {
            return;
        }
        final ListPreference listPref = (ListPreference) pref;
        listPref.setValue(String.valueOf(steps));
        listPref.setSummary(listPref.getEntry());
    }

    private void commitVolumeSteps(Preference pref, int steps) {
        Settings.Global.putInt(getContentResolver(), pref.getKey(), steps);
        mAudioManager.setStreamMaxVolume(volume_map.get(pref.getKey()), steps);
        updateVolumeStepPrefs(pref, steps);
    }
}

