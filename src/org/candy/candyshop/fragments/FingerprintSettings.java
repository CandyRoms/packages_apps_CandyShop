/*
 *  Copyright (C) 2019 The OmniROM Project
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v14.preference.SwitchPreference;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.io.FileDescriptor;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FingerprintSettings extends SettingsPreferenceFragment implements
    OnPreferenceChangeListener, Indexable {
    private static final String TAG = "FingerprintSettings";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String FINGERPRINT_CUSTOM_ICON = "custom_fingerprint_icon";
    private static final String FINGERPRINT_CATEGORY = "category_fingerprint_custom_icon";
    private static final int GET_CUSTOM_FP_ICON = 69;
    private Preference mFilePicker;
    private SwitchPreference mFingerprintVib;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CANDYSHOP;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fingerprint_settings);
        mFilePicker = (Preference) findPreference(FINGERPRINT_CUSTOM_ICON);

        mFingerprintVib = (SwitchPreference) findPreference(FINGERPRINT_VIB);
        mFingerprintVib.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.OMNI_FINGERPRINT_SUCCESS_VIB, 1) == 1));
        mFingerprintVib.setOnPreferenceChangeListener(this);

        boolean isFODDevice = getResources().getBoolean(com.android.internal.R.bool.config_needCustomFODView);
        if (!isFODDevice){
            removePreference(FINGERPRINT_CATEGORY);
        } else {
            final String customIconURI = Settings.System.getString(getContext().getContentResolver(),
                Settings.System.OMNI_CUSTOM_FP_ICON);

            if (!TextUtils.isEmpty(customIconURI)) {
                setPickerIcon(customIconURI);
                mFilePicker.setSummary(customIconURI);
            }

            mFilePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/png");

                    startActivityForResult(intent, GET_CUSTOM_FP_ICON);

                    return true;
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
        Intent resultData) {
        if (requestCode == GET_CUSTOM_FP_ICON && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                mFilePicker.setSummary(uri.toString());
                setPickerIcon(uri.toString());
                Settings.System.putString(getContentResolver(), Settings.System.OMNI_CUSTOM_FP_ICON,
                    uri.toString());
            }
        } else if (requestCode == GET_CUSTOM_FP_ICON && resultCode == Activity.RESULT_CANCELED) {
            mFilePicker.setSummary("");
            mFilePicker.setIcon(new ColorDrawable(Color.TRANSPARENT));
            Settings.System.putString(getContentResolver(), Settings.System.OMNI_CUSTOM_FP_ICON, "");
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFingerprintVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.OMNI_FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
            return true;
        }
        return false;
    }

    private void setPickerIcon(String uri) {
        try {
                ParcelFileDescriptor parcelFileDescriptor =
                    getContext().getContentResolver().openFileDescriptor(Uri.parse(uri), "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                Drawable d = new BitmapDrawable(getResources(), image);
                mFilePicker.setIcon(d);
            }
            catch (Exception e) {}
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider() {
            @Override
            public List < SearchIndexableResource > getXmlResourcesToIndex(Context context,
                boolean enabled) {
                ArrayList < SearchIndexableResource > result =
                    new ArrayList < SearchIndexableResource > ();

                SearchIndexableResource sir = new SearchIndexableResource(context);
                sir.xmlResId = R.xml.fingerprint_settings;
                result.add(sir);

                return result;
            }

            @Override
            public List < String > getNonIndexableKeys(Context context) {
                ArrayList < String > result = new ArrayList < String > ();
                return result;
            }
        };
}
