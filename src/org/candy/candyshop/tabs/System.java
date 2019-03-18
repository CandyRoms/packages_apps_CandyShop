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

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.Utils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.internal.util.candy.AwesomeAnimationHelper;
import com.android.internal.util.candy.CandyUtils;
import com.android.settingslib.CustomDialogPreference;
import org.candy.candyshop.preference.SystemSettingSeekBarPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class System extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String MISC_CATEGORY = "system_category";
	private static final String TAG = "System";
    private static final String SCREEN_OFF_ANIMATION = "screen_off_animation";
    private static final String KEY_TOAST_ANIMATION = "toast_animation";
    private static final String POWER_MENU_ANIMATIONS = "power_menu_animations";
    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";

    private ListPreference mFlashlightOnCall;
    private ListPreference mScreenOffAnimation;
    private ListPreference mToastAnimation;
    private ListPreference mPowerMenuAnimations;

    protected Context mContext;


    private int[] mAnimations;
    private String[] mAnimationsStrings;
    private String[] mAnimationsNum;

    Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system);
        final PreferenceScreen prefSet = getPreferenceScreen();

        mContext = getActivity().getApplicationContext();
        final ContentResolver resolver = getActivity().getContentResolver();

        // Screen Off Animations
        mScreenOffAnimation = (ListPreference) findPreference(SCREEN_OFF_ANIMATION);
        int screenOffStyle = Settings.Global.getInt(resolver,
                 Settings.Global.SCREEN_OFF_ANIMATION, 0);
        mScreenOffAnimation.setValue(String.valueOf(screenOffStyle));
        mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntry());
        mScreenOffAnimation.setOnPreferenceChangeListener(this);

        mToastAnimation = (ListPreference) findPreference(KEY_TOAST_ANIMATION);
        mToastAnimation.setSummary(mToastAnimation.getEntry());
        int CurrentToastAnimation = Settings.System.getInt(getContentResolver(), Settings.System.TOAST_ANIMATION, 1);
        mToastAnimation.setValueIndex(CurrentToastAnimation); //set to index of default value
        mToastAnimation.setSummary(mToastAnimation.getEntries()[CurrentToastAnimation]);
        mToastAnimation.setOnPreferenceChangeListener(this);

        mAnimations = AwesomeAnimationHelper.getAnimationsList();
        int animqty = mAnimations.length;
        mAnimationsStrings = new String[animqty];
        mAnimationsNum = new String[animqty];
        for (int i = 0; i < animqty; i++) {
            mAnimationsStrings[i] = AwesomeAnimationHelper.getProperName(getActivity().getApplicationContext(), mAnimations[i]);
            mAnimationsNum[i] = String.valueOf(mAnimations[i]);

        mPowerMenuAnimations = (ListPreference) findPreference(POWER_MENU_ANIMATIONS);
        mPowerMenuAnimations.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.POWER_MENU_ANIMATIONS, 0)));
        mPowerMenuAnimations.setSummary(mPowerMenuAnimations.getEntry());
        mPowerMenuAnimations.setOnPreferenceChangeListener(this);
        }

        mFlashlightOnCall = (ListPreference) findPreference(FLASHLIGHT_ON_CALL);
        Preference FlashOnCall = findPreference("flashlight_on_call");
        if (!CandyUtils.deviceSupportsFlashLight(getActivity())) {
            prefSet.removePreference(FlashOnCall);
        } else {
        int flashlightValue = Settings.System.getInt(getContentResolver(),
                Settings.System.FLASHLIGHT_ON_CALL, 0);
        mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
        mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
        mFlashlightOnCall.setOnPreferenceChangeListener(this);
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

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mScreenOffAnimation) {
            Settings.Global.putInt(getContentResolver(),
                    Settings.Global.SCREEN_OFF_ANIMATION, Integer.valueOf((String) newValue));
            int valueIndex = mScreenOffAnimation.findIndexOfValue((String) newValue);
            mScreenOffAnimation.setSummary(mScreenOffAnimation.getEntries()[valueIndex]);
            return true;
        } else if (preference == mToastAnimation) {
            int index = mToastAnimation.findIndexOfValue((String) newValue);
            Settings.System.putString(getContentResolver(), Settings.System.TOAST_ANIMATION, (String) newValue);
            mToastAnimation.setSummary(mToastAnimation.getEntries()[index]);
            mToast.makeText(mContext, "Taste the Sweetness TM", Toast.LENGTH_SHORT).show();
            return true;
        } else if (preference == mPowerMenuAnimations) {
            Settings.System.putInt(getContentResolver(), Settings.System.POWER_MENU_ANIMATIONS,
                    Integer.valueOf((String) newValue));
            mPowerMenuAnimations.setValue(String.valueOf(newValue));
            mPowerMenuAnimations.setSummary(mPowerMenuAnimations.getEntry());
            return true;
        } else if (preference == mFlashlightOnCall) {
            int flashlightValue = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(getContentResolver(),
                    Settings.System.FLASHLIGHT_ON_CALL, flashlightValue);
            mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
            mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CANDYSHOP;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.system;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
            };
}

