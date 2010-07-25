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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import com.phonepony.Log;


/**
 * A widget provider.  We have a string that we pull from a preference in order to show
 * the configuration settings and the current time when the widget was updated.  We also
 * register a BroadcastReceiver for time-changed and timezone-changed broadcasts, and
 * update then too.
 * <p/>
 * <p>See also the following files:
 * <ul>
 * <li>ExampleAppWidgetConfigure.java</li>
 * <li>ExampleBroadcastReceiver.java</li>
 * <li>res/layout/appwidget_configure.xml</li>
 * <li>res/layout/appwidget_provider.xml</li>
 * <li>res/xml/appwidget_provider.xml</li>
 * </ul>
 */
public class FrequentContactsSmsWidget extends AppWidgetProvider {
    // log tag
    private static final String TAG = "FrequentContactsSmsWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(Constants.ACTIVITY_TYPE_NAME, Constants.ACTIVITY_TYPE.SMS.name());
        context.startService(intent);
    }


}