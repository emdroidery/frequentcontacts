/*
 * Copyright (C) 2010 The Phone Pony Project
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

package com.phonepony.frequentcontacts;


import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import com.phonepony.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class UpdateService extends IntentService {
    private static final String TAG = "UpdateService";

    private static HashMap<String, BrowseFrequentContactsBase> activities = new HashMap<String, BrowseFrequentContactsBase>();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateService() {
        super("UpdateService");
    }

    private RemoteViews setNumber(List<CallCounts> counts, int i, int appwidget_text, int appwidget_image, BrowseFrequentContactsBase activity) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.contact_link);
        String callerName = "";
        if (counts.size() > i) {
            CallCounts count = counts.get(i);
            callerName = count.getDisplay();
            PendingIntent intent = PendingIntent.getActivity(this, 0, activity.getActionIntent(count.getPhoneNumber()), PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(appwidget_text, intent);
            Bitmap bitmap = count.getBitmap();
            views.setImageViewBitmap(appwidget_image, bitmap);
        }
        views.setTextViewText(appwidget_text, callerName);
        return views;
    }

    private int getEmptyCall(CallCounts[] counts) {
        int length = counts.length;
        for (int i = 0; i < length; ++i) {
            if (null == counts[i]) {
                return i;
            }
        }
        return -1;
    }

    private List<CallCounts> rearrangeBySavedLayout(final List<CallCounts> counts, String prefKey) {

        int place = -1, length = counts.size(), free_place = -1;
        CallCounts call, temp;
        CallCounts[] newCounts = new CallCounts[length];
        SharedPreferences pref = getSharedPreferences(prefKey, 0);
        for (int i = 0; i < length; ++i) {
            call = counts.get(i);
            place = pref.getInt(call.getPhoneNumber(), -1);
            free_place = getEmptyCall(newCounts);
            if (-1 == place) {
                newCounts[free_place] = call;
            } else {
                temp = newCounts[place];
                if (null != temp) {
                    free_place = getEmptyCall(newCounts);
                    newCounts[free_place] = temp;
                }
                newCounts[place] = call;
            }
        }
        return Arrays.asList(newCounts);
    }

    private void saveLayout(List<CallCounts> counts, String prefKey) {
        SharedPreferences.Editor editor = getSharedPreferences(prefKey, 0).edit();
        editor.clear();
        int length = counts.size();
        for (int i = 0; i < length; ++i) {
            editor.putInt(counts.get(i).getPhoneNumber(), i);
        }
        editor.commit();
    }

    private RemoteViews updateAppWidget(BrowseFrequentContactsBase baseClass) {
        Log.d(TAG, "updateAppWidget");

        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        List<CallCounts> counts = baseClass.getCallCounts(this);

        String prefKey = baseClass.getClass().getName();

        try {
            counts = rearrangeBySavedLayout(counts.subList(0, 8), prefKey);
        } catch (Throwable t) {
        }

        saveLayout(counts, prefKey);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget_provider);

        /**
         * Removing initial loading views
         */

        views.removeAllViews(R.id.appwidget_contact0);
        views.removeAllViews(R.id.appwidget_contact1);
        views.removeAllViews(R.id.appwidget_contact2);
        views.removeAllViews(R.id.appwidget_contact3);
        views.removeAllViews(R.id.appwidget_contact4);
        views.removeAllViews(R.id.appwidget_contact5);
        views.removeAllViews(R.id.appwidget_contact6);
        views.removeAllViews(R.id.appwidget_contact7);

        views.addView(R.id.appwidget_contact0, setNumber(counts, 0, R.id.appwidget_text, R.id.appwidget_image, baseClass));
        views.addView(R.id.appwidget_contact1, setNumber(counts, 1, R.id.appwidget_text, R.id.appwidget_image, baseClass));
        views.addView(R.id.appwidget_contact2, setNumber(counts, 2, R.id.appwidget_text, R.id.appwidget_image, baseClass));
        views.addView(R.id.appwidget_contact3, setNumber(counts, 3, R.id.appwidget_text, R.id.appwidget_image, baseClass));

        views.addView(R.id.appwidget_contact4, setNumber(counts, 4, R.id.appwidget_text, R.id.appwidget_image, baseClass));
        views.addView(R.id.appwidget_contact5, setNumber(counts, 5, R.id.appwidget_text, R.id.appwidget_image, baseClass));
        views.addView(R.id.appwidget_contact6, setNumber(counts, 6, R.id.appwidget_text, R.id.appwidget_image, baseClass));
        views.addView(R.id.appwidget_contact7, setNumber(counts, 7, R.id.appwidget_text, R.id.appwidget_image, baseClass));

        return views;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("UpdateService", "onStart()");

        BrowseFrequentContactsBase baseClass = null;
        String type = intent.getStringExtra(Constants.ACTIVITY_TYPE_NAME);
        String classComponentName = null;
        boolean exists = false;

        Log.d("UpdateService", "type: " + type);

        if (activities.containsKey(type)) {
            baseClass = activities.get(type);
            exists = true;
        }

        if (type.equalsIgnoreCase(Constants.ACTIVITY_TYPE.CALLS.name())) {
            if (baseClass == null) {
                baseClass = new BrowseFrequentContactsBase(new BrowseFrequentContactsByCallsImpl());
            }
            classComponentName = FrequentContactsCallsWidget.class.getName();
        } else if (type.equalsIgnoreCase(Constants.ACTIVITY_TYPE.SMS.name())) {
            if (baseClass == null) {
                baseClass = new BrowseFrequentContactsBase(new BrowseFrequentContactsBySmsImpl());
            }
            classComponentName = FrequentContactsSmsWidget.class.getName();
        } else {
            Log.d("UpdateService", "undefined baseClass type: " + type);
            return;
        }

        if (!exists) {
            activities.put(type, baseClass);
        }

        RemoteViews updateViews = updateAppWidget(baseClass);
        Log.d("UpdateService", "update built");
        ComponentName thisWidget = new ComponentName(this, classComponentName);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, updateViews);
        Log.d("UpdateService", "widget updated");
    }

}
