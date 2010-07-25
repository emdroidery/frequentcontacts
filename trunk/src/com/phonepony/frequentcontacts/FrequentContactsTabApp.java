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

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/**
 *
 */
public class FrequentContactsTabApp extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent().setClass(this, WidgetTabActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.info_str)).
                setIndicator(getString(R.string.info_str),
                        getResources().getDrawable(R.drawable.ic_tab_info)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, BrowseFrequentContactsActivity.class);
        intent.putExtra(Constants.ACTIVITY_TYPE_NAME, Constants.ACTIVITY_TYPE.CALLS.name());
        spec = tabHost.newTabSpec(getString(R.string.calls_str)).
                setIndicator(getString(R.string.calls_str),
                        getResources().getDrawable(R.drawable.ic_tab_dialer)).setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, BrowseFrequentContactsActivity.class);
        intent.putExtra(Constants.ACTIVITY_TYPE_NAME, Constants.ACTIVITY_TYPE.SMS.name());
        spec = tabHost.newTabSpec(getString(R.string.sms_str)).
                setIndicator(getString(R.string.sms_str),
                        getResources().getDrawable(R.drawable.ic_tab_recent)).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}
