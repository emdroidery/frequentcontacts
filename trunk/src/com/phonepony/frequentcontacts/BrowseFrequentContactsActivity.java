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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.phonepony.Log;

import java.util.List;

/**
 *
 */
public class BrowseFrequentContactsActivity extends Activity {

    public static final String TAG = "BrowseFrequentContactsActivity";

    private GridView mContactView;
    private ContactsListAdapter mAdapter;
    private BrowseFrequentContactsBase mBrowseFrequentContactsBase;
    private List<CallCounts> mCallCounts;
    private static final int PROCESSING_DIALOG = 0;

    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Processing");
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        return dialog;
    }

    /**
     * This function assumes that mCallCounts were calculated in background by additional thread
     */
    private void updateResultsInUi() {
        mAdapter.clear();
        for (CallCounts callCount : mCallCounts) {
            mAdapter.add(callCount);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "Activity State: onCreate()");

        super.onCreate(savedInstanceState);
        String type = getIntent().getStringExtra(Constants.ACTIVITY_TYPE_NAME);

        if (type.equalsIgnoreCase(Constants.ACTIVITY_TYPE.CALLS.name())) {
            mBrowseFrequentContactsBase = new BrowseFrequentContactsBase(new BrowseFrequentContactsByCallsImpl());
        } else if (type.equalsIgnoreCase(Constants.ACTIVITY_TYPE.SMS.name())) {
            mBrowseFrequentContactsBase = new BrowseFrequentContactsBase(new BrowseFrequentContactsBySmsImpl());
        } else {
            Log.d("UpdateService", "undefined activity type: " + type);
            return;
        }

        setContentView(R.layout.browse_frequent_contact);
        mContactView = (GridView) findViewById(R.id.contactList);
        mAdapter = new ContactsListAdapter(this);
        mContactView.setAdapter(mAdapter);
        mContactView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    CallCounts callCounts = (CallCounts) adapterView.getItemAtPosition(i);
                    Intent callIntent = mBrowseFrequentContactsBase.getActionIntent(callCounts.getPhoneNumber());
                    startActivity(callIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to invoke call", e);
                }
            }
        });

        showDialog(PROCESSING_DIALOG);
        final Handler uiThreadCallback = new Handler();

        final Runnable runInUIThread = new Runnable() {
            public void run() {
                updateResultsInUi();
                removeDialog(PROCESSING_DIALOG);
            }
        };

        new Thread() {
            @Override
            public void run() {
                refreshPhoneList();
                uiThreadCallback.post(runInUIThread);
            }
        }.start();
    }

    private void refreshPhoneList() {
        mCallCounts = mBrowseFrequentContactsBase.getCallCounts(this);
    }
}
