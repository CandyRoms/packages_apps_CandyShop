/*
 * Copyright (C) 2017-2019 crDroid Android Project
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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.candy.candyshop.R;

public class ProximitySensor implements SensorEventListener {

    private static final boolean DEBUG = false;
    private static final String TAG = "ProximitySensor";

    // Maximum time for the hand to cover the sensor: 1s
    private static final int HANDWAVE_MAX_DELTA_NS = 1000 * 1000 * 1000;

    // Minimum time until the device is considered to have been in the pocket: 2s
    private static final int POCKET_MIN_DELTA_NS = 2000 * 1000 * 1000;

    private SensorManager mSensorManager;
    private Sensor mSensorProximity;
    private Context mContext;
    private ExecutorService mExecutorService;
    private long mEntryTimestamp;

    private boolean mSawNear = false;
    private long mInPocketTime = 0;

    private boolean mHandwaveEnabled;
    private boolean mPocketEnabled;

    public ProximitySensor(Context context) {
        mContext = context;

        mHandwaveEnabled = Utils.handwaveGestureEnabled(mContext);
        mPocketEnabled = Utils.pocketGestureEnabled(mContext);
        if (!mHandwaveEnabled && !mPocketEnabled) {
            disable();
        }

        final boolean wakeup = context.getResources().getBoolean(
                com.android.internal.R.bool.config_deviceHaveWakeUpProximity);
        mSensorManager = (SensorManager)
                mContext.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            String customProximity = mContext.getResources().getString(
                    R.string.config_custom_proximity);
            if (!customProximity.isEmpty()) {
                mSensorManager = mContext.getSystemService(SensorManager.class);
                mSensorProximity = Utils.getSensor(mSensorManager, customProximity);
            } else {
                mSensorProximity = mSensorManager.getDefaultSensor(
                        Sensor.TYPE_PROXIMITY, wakeup);
            }
        }
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    private Future<?> submit(Runnable runnable) {
        return mExecutorService.submit(runnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean isNear = event.values[0] < mSensorProximity.getMaximumRange();
        if (mSawNear && !isNear) {
            if (shouldPulse(event.timestamp)) {
                Utils.launchDozePulse(mContext);
            }
        } else {
            mInPocketTime = event.timestamp;
        }
        mSawNear = isNear;
    }

    private boolean shouldPulse(long timestamp) {
        long delta = timestamp - mInPocketTime;

        if (mHandwaveGestureEnabled && mPocketGestureEnabled) {
            return true;
        } else if (mHandwaveGestureEnabled && !mPocketGestureEnabled) {
            return delta < POCKET_MIN_DELTA_NS;
        } else if (!mHandwaveGestureEnabled && mPocketGestureEnabled) {
            return delta >= POCKET_MIN_DELTA_NS;
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        /* Empty */
    }

    // Switching screen OFF - we enable the sensor
    protected void enable() {
        if (mHandwaveEnabled || mPocketEnabled) {
            if (DEBUG) Log.d(TAG, "Enabling");
            mEntryTimestamp = SystemClock.elapsedRealtime();
           // We save user settings so at next screen ON call (enable())
            // we don't need to read them again from the Settings provider
            mSensorManager.registerListener(this, mSensorProximity,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // Switching screen ON - we disable the sensor
    protected void disable() {
        if (DEBUG) Log.d(TAG, "Disabling");
            mSensorManager.unregisterListener(this, mSensorProximity);
    }
}
