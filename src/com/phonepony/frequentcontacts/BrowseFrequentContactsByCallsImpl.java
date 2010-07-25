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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

public final class BrowseFrequentContactsByCallsImpl implements IBrowseFrequentContacts {

    public static final String TAG = "BrowseFrequentContactsByCallsImpl";

    public Intent getActionIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return intent;
    }

    public Cursor getPhoneNumbersCursor(Context context, long time) {

        Uri uri = CallLog.Calls.CONTENT_URI;

        String[] projection = new String[]{
                CallLog.Calls.NUMBER
        };

        String selection = CallLog.Calls.TYPE + " == " + CallLog.Calls.OUTGOING_TYPE + " AND " + CallLog.Calls.DATE + ">?";
        String lastDate = String.valueOf(System.currentTimeMillis() - time);
        String sortOrder = CallLog.Calls.DATE + " DESC";
        return context.getContentResolver().query(uri, projection, selection, new String[]{lastDate}, sortOrder);
    }

    public int getBadge() {
        return R.drawable.badge_action_call;
    }
}
