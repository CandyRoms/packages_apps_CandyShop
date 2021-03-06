/*
 * Copyright (C) 2017 The Dirty Unicorns Project
 * Copyright (C) 2018 CandyRoms
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

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.android.settings.R;

import java.util.Objects;

public class AboutCandy extends DialogFragment {

    public static final String TAG_ABOUT_CANDY = "AboutCandy";

    private Dialog dialog;
    private View view;

    public AboutCandy() {
    }

    public static AboutCandy newInstance() {
        return new AboutCandy ();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new Dialog(requireActivity(), R.style.AboutCandy);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.about_candy, container, false);

        if (view != null) {
            FrameLayout candyroms = view.findViewById(R.id.candyroms);
            setUriTarget("http://candyroms.org", candyroms);

            FrameLayout gerrit = view.findViewById(R.id.gerrit);
            setUriTarget("https://gerrit.bbqdroid.org", gerrit);

            FrameLayout candyroms_github = view.findViewById(R.id.candyroms_github);
            setUriTarget("https://github.com/CandyRoms", candyroms_github);

            FrameLayout candydev_github = view.findViewById(R.id.candydev_github);
            setUriTarget("https://github.com/CandyDevices", candydev_github);

            FrameLayout candyroms_tg = view.findViewById(R.id.candyroms_tg);
            setUriTarget("https://t.me/CandyRoms", candyroms_tg);

            FrameLayout opengapps = view.findViewById(R.id.opengapps);
            setUriTarget("https://opengapps.org", opengapps);

            FrameLayout magisk = view.findViewById(R.id.magisk);
            setUriTarget("https://github.com/topjohnwu/Magisk/releases", magisk);

            FrameLayout dan = view.findViewById(R.id.dan);
            setUriTarget("http://candyroms.org/nospamdan-dan-cartier", dan);

            FrameLayout kevin = view.findViewById(R.id.kevin);
            setUriTarget("http://candyroms.org/candymaker_d-kevin-princiotta", kevin);

            FrameLayout bhi = view.findViewById(R.id.bhi);
            setUriTarget("http://candyroms.org/bhi244-bhimanand-dharmatti", bhi);

            FrameLayout mourya = view.findViewById(R.id.mourya);
            setUriTarget("http://candyroms.org/miju12-mourya-baruah", mourya);

            FrameLayout rajat = view.findViewById(R.id.rajat);
            setUriTarget("http://candyroms.org/rajatgupta1998-rajat-gupta", rajat);

            FrameLayout sanjay = view.findViewById(R.id.sanjay);
            setUriTarget("http://candyroms.org/yaznas-sanjay-shrestha", sanjay);

            FrameLayout chad = view.findViewById(R.id.chad);
            setUriTarget("https://forum.xda-developers.com/member.php?u=1986924", chad);

            FrameLayout tony = view.findViewById(R.id.tony);
            setUriTarget("https://forum.xda-developers.com/member.php?u=4779996", tony);

            //FrameLayout nick = view.findViewById(R.id.nick);
            //setUriTarget("https://forum.xda-developers.com/member.php?u=6220524", nick);
        }
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = (inflater.inflate(R.layout.about_candy, null));

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    private void setUriTarget(final String uriTarget, final FrameLayout name) {
        if (name != null) {
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(uriTarget));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        if(getDialog()!=null && getRetainInstance()) {
            getDialog().setDismissMessage(null);

        }
        super.onDestroyView();
    }
}
