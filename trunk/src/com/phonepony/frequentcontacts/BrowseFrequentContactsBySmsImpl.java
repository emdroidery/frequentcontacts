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

public final class BrowseFrequentContactsBySmsImpl implements IBrowseFrequentContacts {

    public Intent getActionIntent(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
        return intent;
    }

    public Cursor getPhoneNumbersCursor(Context context, long time) {

        Uri SmsContentUri = Uri.parse("content://sms");
        Uri SmsSentBoxUri = Uri.withAppendedPath(SmsContentUri, "sent");

        String[] projection = new String[]{
                "address"
        };

        String selection = "date" + ">?";
        String lastDate = String.valueOf(System.currentTimeMillis() - time);
        String sortOrder = "date" + " DESC";

        Cursor cursor = context.getContentResolver().query(SmsSentBoxUri, projection, selection, new String[]{lastDate}, sortOrder);

        return cursor;
    }

    public int getBadge() {
        return R.drawable.badge_action_sms;
    }
}
