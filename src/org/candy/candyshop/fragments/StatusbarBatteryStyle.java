/*
 *  Copyright (C) 2016 Dirty Unicorns
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
*/
package org.candy.candyshop.fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.Utils;

import org.candy.candyshop.preference.SystemSettingSwitchPreference;

public class StatusbarBatteryStyle extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "StatusbarBatteryStyle";

    private static final String BATTERY_STYLE = "battery_style";
    private static final String KEY_BATTERY_PERCENTAGE = "battery_percentage";

    private ListPreference mBatteryIconStyle;
    private SystemSettingSwitchPreference mShowBatteryPercent;

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CANDYSHOP;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.statusbar_battery_style);

        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        int batteryStyle = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_BATTERY_STYLE, 0);
        mBatteryIconStyle = (ListPreference) findPreference(BATTERY_STYLE);
        mBatteryIconStyle.setValue(Integer.toString(batteryStyle));
        int valueIndex = mBatteryIconStyle.findIndexOfValue(String.valueOf(batteryStyle));
        mBatteryIconStyle.setSummary(mBatteryIconStyle.getEntries()[valueIndex]);
        mBatteryIconStyle.setOnPreferenceChangeListener(this);

        int showPercent = Settings.System.getInt(resolver,
                Settings.System.SHOW_BATTERY_PERCENT, 1);
        mShowBatteryPercent = (SystemSettingSwitchPreference) findPreference(KEY_BATTERY_PERCENTAGE);
        mShowBatteryPercent.setOnPreferenceChangeListener(this);
        boolean hideForcePercent = batteryStyle == 6 || batteryStyle == 7; /*text or hidden style*/
        mShowBatteryPercent.setEnabled(!hideForcePercent);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        // If we didn't handle it, let preferences handle it.
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryIconStyle) {
            int value = Integer.valueOf((String) newValue);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.STATUS_BAR_BATTERY_STYLE, value);
            int valueIndex = mBatteryIconStyle
                    .findIndexOfValue((String) newValue);
            mBatteryIconStyle
                    .setSummary(mBatteryIconStyle.getEntries()[valueIndex]);
            boolean hideForcePercent = value == 6 || value == 7;/*text or hidden style*/
            return true;
        } else if (preference == mShowBatteryPercent) {
            boolean showPercentage = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SHOW_BATTERY_PERCENT, showPercentage ? 1 : 0);
            return true;
        }
        return false;
    }

}
