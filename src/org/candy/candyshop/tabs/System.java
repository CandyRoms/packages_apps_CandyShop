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

package org.candy.candyshop.tabs;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.Utils;

public class System extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "System";

    private ListPreference mAnnoyingNotification;
    private ListPreference mNoisyNotification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system);

        ContentResolver resolver = getActivity().getContentResolver();

        mAnnoyingNotification = (ListPreference) findPreference("less_notification_sounds");
        mAnnoyingNotification.setOnPreferenceChangeListener(this);
        int threshold = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD,
                30000, UserHandle.USER_CURRENT);
        mAnnoyingNotification.setValue(String.valueOf(threshold));

        mNoisyNotification = (ListPreference) findPreference("notification_sound_vib_screen_on");
        mNoisyNotification.setOnPreferenceChangeListener(this);
        int mode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.NOTIFICATION_SOUND_VIB_SCREEN_ON,
                1, UserHandle.USER_CURRENT);
        mNoisyNotification.setValue(String.valueOf(mode));
        mNoisyNotification.setSummary(mNoisyNotification.getEntry());
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CANDYSHOP;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        if (preference.equals(mAnnoyingNotification)) {
            int mode = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, mode, UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mNoisyNotification)) {
            int mode = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.NOTIFICATION_SOUND_VIB_SCREEN_ON, mode, UserHandle.USER_CURRENT);
            int index = mNoisyNotification.findIndexOfValue((String) newValue);
            mNoisyNotification.setSummary(
                    mNoisyNotification.getEntries()[index]);
            return true;
        }
        return false;
    }

}

