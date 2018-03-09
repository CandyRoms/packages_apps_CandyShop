/*
 * Copyright (C) 2016 Ground Zero Roms
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
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.widget.Toast;

import com.android.settings.R;
import com.android.internal.logging.nano.MetricsProto;
import org.candy.candyshop.SystemTheme;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class SystemTheme extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String SYSTEMUI_THEME_STYLE = "systemui_theme_style";

    private ListPreference mSystemUIThemeStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_theme);

        final ContentResolver resolver = getActivity().getContentResolver();

        mSystemUIThemeStyle = (ListPreference) findPreference(SYSTEMUI_THEME_STYLE);
        int systemUIThemeStyle = Settings.System.getInt(resolver,
                Settings.System.SYSTEM_UI_THEME, 0);
        mSystemUIThemeStyle.setValue(String.valueOf(systemUIThemeStyle));
        mSystemUIThemeStyle.setSummary(mSystemUIThemeStyle.getEntry());
        mSystemUIThemeStyle.setOnPreferenceChangeListener(this);
     }

     @Override
     public boolean onPreferenceChange(Preference preference, Object newValue) {
         ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSystemUIThemeStyle) {
            String value = (String) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.SYSTEM_UI_THEME, Integer.valueOf(value));
            int valueIndex = mSystemUIThemeStyle.findIndexOfValue(value);
            mSystemUIThemeStyle.setSummary(mSystemUIThemeStyle.getEntries()[valueIndex]);
            if (valueIndex == 2) {
                Intent intent2 = new Intent(Intent.ACTION_MAIN);
                intent2.addCategory(Intent.CATEGORY_HOME);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                Toast.makeText(getContext(), R.string.system_dark_theme_toast_before,
                    Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                      @Override
                      public void run() {
                          Intent intent = new Intent(Intent.ACTION_MAIN);
                          intent.setClassName("com.android.settings",
                                "com.android.settings.Settings$MiscellaneousSettingsActivity");
                          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          startActivity(intent);
                          Toast.makeText(getContext(), R.string.system_dark_theme_toast_after,
                              Toast.LENGTH_SHORT).show();
                      }
                }, 2000);
            }
            return true;
         }
         return false;
     }

     @Override
     public int getMetricsCategory() {
         return MetricsProto.MetricsEvent.CANDYSHOP;
     }
 }
