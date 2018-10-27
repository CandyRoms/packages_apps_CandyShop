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

package org.candy.candyshop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class AboutCandy extends DialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup nullParent = null;
        View view = null;
        if (inflater != null) {
            view = inflater.inflate(R.layout.about_candy, nullParent);
        }
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();

        FrameLayout gerrit = null;
        if (view != null) {
            gerrit = view.findViewById(R.id.gerrit);
        }
        if (gerrit != null) {
            gerrit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gerrit.bbqdroid.org"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout candyroms_github = null;
        if (view != null) {
            candyroms_github = view.findViewById(R.id.candyroms_github);
        }
        if (candyroms_github != null) {
            candyroms_github.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/CandyRoms"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout candydev_github = null;
        if (view != null) {
            candydev_github = view.findViewById(R.id.candydev_github);
        }
        if (candydev_github != null) {
            candydev_github.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/CandyDevices"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout candyroms_tg = null;
        if (view != null) {
            candyroms_tg = view.findViewById(R.id.candyroms_tg);
        }
        if (candyroms_tg != null) {
            candyroms_tg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/CandyRoms"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout opengapps = null;
        if (view != null) {
            opengapps = view.findViewById(R.id.opengapps);
        }
        if (opengapps != null) {
            opengapps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://opengapps.org"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout magisk = null;
        if (view != null) {
            magisk = view.findViewById(R.id.magisk);
        }
        if (magisk != null) {
            magisk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/topjohnwu/Magisk/releases"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout candyroms = null;
        if (view != null) {
            candyroms = view.findViewById(R.id.candyroms);
        }
        if (candyroms != null) {
            candyroms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/108100228397827258211"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout dan = null;
        if (view != null) {
            dan = view.findViewById(R.id.dan);
        }
        if (dan != null) {
            dan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+DanCartier"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout tony = null;
        if (view != null) {
            tony = view.findViewById(R.id.tony);
        }
        if (tony != null) {
            tony.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/102341413337882269722"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout kevin = null;
        if (view != null) {
            kevin = view.findViewById(R.id.kevin);
        }
        if (kevin != null) {
            kevin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+KevinPrinciotta"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout nick = null;
        if (view != null) {
            nick = view.findViewById(R.id.nick);
        }
        if (nick != null) {
            nick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+Cloud9ner"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout bhi = null;
        if (view != null) {
            bhi = view.findViewById(R.id.bhi);
        }
        if (bhi != null) {
            bhi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+bhimananddharmatti"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout mourya = null;
        if (view != null) {
            mourya = view.findViewById(R.id.mourya);
        }
        if (mourya != null) {
            mourya.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/102171551866121426915"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout rajat = null;
        if (view != null) {
            rajat = view.findViewById(R.id.rajat);
        }
        if (rajat != null) {
            rajat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+RajatGupta1998"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout sanjay = null;
        if (view != null) {
            sanjay = view.findViewById(R.id.sanjay);
        }
        if (sanjay != null) {
            sanjay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+SanjayShrestha53"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        FrameLayout chad = null;
        if (view != null) {
            chad = view.findViewById(R.id.chad);
        }
        if (chad != null) {
            chad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/+ChadCormierRoussel"));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        dialog.show();

        return dialog;
    }

    @Override
    public void onDestroyView() {
        if(getDialog()!=null && getRetainInstance()) {
            getDialog().setDismissMessage(null);

        }
        super.onDestroyView();
    }
}
