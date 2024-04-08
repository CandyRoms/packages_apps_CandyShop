/*
 * Copyright (C) 2018 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.candy.candyshop.fragments;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.format.DateFormat;
import androidx.preference.*;
import androidx.preference.ListPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;
import com.android.internal.logging.nano.MetricsProto;

import org.candy.candyshop.preferences.SecureSettingListPreference;
import org.candy.candyshop.preferences.SystemSettingListPreference;
import org.candy.candyshop.colorpicker.ColorPickerPreference;
import com.android.settingslib.development.SystemPropPoker;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
public class MiscBatterySettings extends SettingsPreferenceFragment implements
    Preference.OnPreferenceChangeListener {

    private static final String EVL_BATTERY_CHARGING_COLOR = "evl_battery_charging_color";
    private static final String EVL_BATTERY_FILL_COLOR = "evl_battery_fill_color";
    private static final String EVL_BATTERY_POWERSAVE_COLOR = "evl_battery_powersave_color";
    private static final String EVL_BATTERY_POWERSAVEFILL_COLOR = "evl_battery_powersavefill_color";
    private static final String EVL_BATTERY_FILL_GRADIENT_COLOR = "evl_battery_fill_gradient_color";

    private ColorPickerPreference mEvlCustomChargingColor;
    private ColorPickerPreference mEvlCustomFillColor;
    private ColorPickerPreference mEvlCustomPowerSaveColor;
    private ColorPickerPreference mEvlCustomPowerSaveFillColor;
    private ColorPickerPreference mEvlCustomGradientFillColor;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.misc_battery_settings);
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();

        mEvlCustomChargingColor = (ColorPickerPreference) findPreference(EVL_BATTERY_CHARGING_COLOR);
        int mgradL = Settings.System.getInt(getContentResolver(),
                "evl_battery_charging_color", 0xff3ab74e);
        mEvlCustomChargingColor.setNewPreviewColor(mgradL);
        mEvlCustomChargingColor.setAlphaSliderEnabled(true);
        String mgradHexaL = String.format("#%08x", (0xff3ab74e & mgradL));
        if (mgradHexaL.equals("#ff3ab74e")) {
            mEvlCustomChargingColor.setSummary(R.string.color_default);
        } else {
            mEvlCustomChargingColor.setSummary(mgradHexaL);
        }
        mEvlCustomChargingColor.setOnPreferenceChangeListener(this);

        mEvlCustomFillColor = (ColorPickerPreference) findPreference(EVL_BATTERY_FILL_COLOR);
        int mgradL2 = Settings.System.getInt(getContentResolver(),
                "evl_battery_fill_color", 0xffde57ce);
        mEvlCustomFillColor.setNewPreviewColor(mgradL2);
        mEvlCustomFillColor.setAlphaSliderEnabled(true);
        String mgradHexaL2 = String.format("#%08x", (0xffde57ce & mgradL2));
        if (mgradHexaL2.equals("#ffde57ce")) {
            mEvlCustomFillColor.setSummary(R.string.color_default);
        } else {
            mEvlCustomFillColor.setSummary(mgradHexaL2);
        }
        mEvlCustomFillColor.setOnPreferenceChangeListener(this);

        mEvlCustomPowerSaveColor = (ColorPickerPreference) findPreference(EVL_BATTERY_POWERSAVE_COLOR);
        int mgradL11 = Settings.System.getInt(getContentResolver(),
                "evl_battery_powersave_color", 0xffff5722);
        mEvlCustomPowerSaveColor.setNewPreviewColor(mgradL11);
        mEvlCustomPowerSaveColor.setAlphaSliderEnabled(true);
        String mgradHexaL11 = String.format("#%08x", (0xffff5722 & mgradL11));
        if (mgradHexaL11.equals("#ffff5722")) {
            mEvlCustomPowerSaveColor.setSummary(R.string.color_default);
        } else {
            mEvlCustomPowerSaveColor.setSummary(mgradHexaL11);
        }
        mEvlCustomPowerSaveColor.setOnPreferenceChangeListener(this);

        mEvlCustomPowerSaveFillColor = (ColorPickerPreference) findPreference(EVL_BATTERY_POWERSAVEFILL_COLOR);
        int mgradL22 = Settings.System.getInt(getContentResolver(),
                "evl_battery_powersavefill_color", 0xfffdd015);
        mEvlCustomPowerSaveFillColor.setNewPreviewColor(mgradL22);
        mEvlCustomPowerSaveFillColor.setAlphaSliderEnabled(true);
        String mgradHexaL22 = String.format("#%08x", (0xfffdd015 & mgradL22));
        if (mgradHexaL22.equals("#fffdd015")) {
            mEvlCustomPowerSaveFillColor.setSummary(R.string.color_default);
        } else {
            mEvlCustomPowerSaveFillColor.setSummary(mgradHexaL22);
        }
        mEvlCustomPowerSaveFillColor.setOnPreferenceChangeListener(this);

        mEvlCustomGradientFillColor = (ColorPickerPreference) findPreference(EVL_BATTERY_FILL_GRADIENT_COLOR);
        int mgradLi = Settings.System.getInt(getContentResolver(),
                "evl_battery_fill_gradient_color", Color.BLACK);
        mEvlCustomGradientFillColor.setNewPreviewColor(mgradLi);
        mEvlCustomGradientFillColor.setAlphaSliderEnabled(true);
        String mgradHexaLi = String.format("#%08x", (Color.BLACK & mgradLi));
        if (mgradHexaLi.equals("#ff000000")) {
            mEvlCustomGradientFillColor.setSummary(R.string.custom_battery_fill_grad_color_summary);
        } else {
            mEvlCustomGradientFillColor.setSummary(mgradHexaLi);
        }
        mEvlCustomGradientFillColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mEvlCustomChargingColor) {
            String hexagradi = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradi.equals("#ff3ab74e")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexagradi);
            }
            int intHexaGradi = ColorPickerPreference.convertToColorInt(hexagradi);
            Settings.System.putInt(getContentResolver(),
                    "evl_battery_charging_color", intHexaGradi);
            return true;
        } else if (preference == mEvlCustomFillColor) {
            String hexagradie = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradie.equals("#ffde57ce")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexagradie);
            }
            int intHexaGradie = ColorPickerPreference.convertToColorInt(hexagradie);
            Settings.System.putInt(getContentResolver(),
                    "evl_battery_fill_color", intHexaGradie);
            return true;
        } else if (preference == mEvlCustomPowerSaveColor) {
            String hexagradien = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradien.equals("#ffff5722")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexagradien);
            }
            int intHexaGradien = ColorPickerPreference.convertToColorInt(hexagradien);
            Settings.System.putInt(getContentResolver(),
                    "evl_battery_powersave_color", intHexaGradien);
            return true;
        } else if (preference == mEvlCustomPowerSaveFillColor) {
            String hexagradiena = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradiena.equals("#fffdd015")) {
                preference.setSummary(R.string.color_default);
            } else {
                preference.setSummary(hexagradiena);
            }
            int intHexaGradiena = ColorPickerPreference.convertToColorInt(hexagradiena);
            Settings.System.putInt(getContentResolver(),
                    "evl_battery_powersavefill_color", intHexaGradiena);
            return true;
        } else if (preference == mEvlCustomGradientFillColor) {
            String hexagradienai = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hexagradienai.equals("#ff000000")) {
                preference.setSummary(R.string.custom_battery_fill_grad_color_summary);
            } else {
                preference.setSummary(hexagradienai);
            }
            int intHexaGradienai = ColorPickerPreference.convertToColorInt(hexagradienai);
            Settings.System.putInt(getContentResolver(),
                    "evl_battery_fill_gradient_color", intHexaGradienai);
            return true;
        }

        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CANDYSHOP;
    }
}
