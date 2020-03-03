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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import android.provider.Settings;
import com.android.settings.Utils;

public class System extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String DEVICE_CATEGORY = "device_extras_category";
    private static final String MISC_CATEGORY = "system_category";
	private static final String TAG = "System";
    private static final String NOTIFICATIONS = "notifications_category";
    private static final String CHARGING_LIGHT = "charging_light";

    private PreferenceCategory mNotifications = null;
    private Preference mChargingLight = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system);

        // This should remove the batter light preference if config_intrusiveBatteryLed is not set
        mNotifications = (PreferenceCategory)findPreference(NOTIFICATIONS);
        mChargingLight = (Preference) findPreference(CHARGING_LIGHT);

        if (!(getResources().getBoolean(com.android.internal.R.bool.config_intrusiveNotificationLed))) {
            mNotifications.removePreference(mChargingLight);
        }
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
        final String key = preference.getKey();
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CANDYSHOP;
    }
}

