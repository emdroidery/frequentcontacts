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
import android.provider.ContactsContract;

import java.util.*;

public class BrowseFrequentContactsBase {

    public static final String TAG = "BrowseFrequentContactsBase";

    private IBrowseFrequentContacts mBrowseFrequentContacts;

    private static final long MILLIS_IN_MONTH = 1000l * 60 * 60 * 24 * 30;
    private static final int MAX_CONTACTS = 100;

    public BrowseFrequentContactsBase(IBrowseFrequentContacts browseFrequentContacts) {
        mBrowseFrequentContacts = browseFrequentContacts;
    }

    public Intent getActionIntent(String number) {
        return mBrowseFrequentContacts.getActionIntent(number);
    }

    public List<CallCounts> getCallCounts(Context context) {

        Cursor cursor = mBrowseFrequentContacts.getPhoneNumbersCursor(context, MILLIS_IN_MONTH);
        HashMap<String, CallCounts> callCounter = new HashMap<String, CallCounts>();
        if (cursor.moveToFirst()) {
            do {
                String number = cursor.getString(0);
                CallCounts count = callCounter.get(number);
                if (count == null) {
                    callCounter.put(number, new CallCounts(number));
                } else {
                    count.increment();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        List<CallCounts> callCounts = new ArrayList<CallCounts>(callCounter.values());
        setContactsDetails(context, callCounts);
        callCounts = aggregate(callCounts);
        Collections.sort(callCounts);
        callCounts = new ArrayList<CallCounts>(callCounts.subList(0, Math.min(callCounts.size(), MAX_CONTACTS)));
        return callCounts;
    }

    /**
     * The given array might contain duplicate entries because of (+972) forms as described in
     * setContactsDetails() method. We need to aggregate their calls counts.
     *
     * @param callCounts
     * @return the new array
     */
    private static List<CallCounts> aggregate(Collection<CallCounts> callCounts) {
        HashMap<String, CallCounts> countsCollection = new HashMap<String, CallCounts>();
        for (CallCounts callCount : callCounts) {
            if (countsCollection.containsKey(callCount.getPhoneNumber())) {
                CallCounts counts = countsCollection.get(callCount.getPhoneNumber());
                counts.incrementBy(callCount.getCallCount());
            } else {
                countsCollection.put(callCount.getPhoneNumber(), callCount);
            }
        }
        return new ArrayList<CallCounts>(countsCollection.values());
    }

    /**
     * @param context
     * @return A cursor for for accessing the contact list.
     */
    private boolean setContactsDetails(Context context, Collection<CallCounts> callCounts) {

        //return creator.generatePhoneNumberIcon(bitmap, phoneType, R.drawable.badge_action_call);
        PhoneBitmapCreator creator = new PhoneBitmapCreator(context);
        for (CallCounts callCount : callCounts) {
            Uri lookupByPhoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(callCount.getPhoneNumber()));
            Cursor cursorInPhoneLookup = context.getContentResolver().query(lookupByPhoneUri, null, null, null, null);
            if (cursorInPhoneLookup.moveToFirst()) {
                String name = cursorInPhoneLookup.getString(cursorInPhoneLookup.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                int type = cursorInPhoneLookup.getInt(cursorInPhoneLookup.getColumnIndex(ContactsContract.PhoneLookup.TYPE));

                String storedNumber = cursorInPhoneLookup.getString(cursorInPhoneLookup.getColumnIndex(ContactsContract.PhoneLookup.NUMBER));

                String lookupKey = cursorInPhoneLookup.getString(cursorInPhoneLookup.getColumnIndex(ContactsContract.PhoneLookup.LOOKUP_KEY));
                Uri contactLookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                Uri contactUri = ContactsContract.Contacts.lookupContact(context.getContentResolver(), contactLookupUri);
                /**
                 * If code gets here, this phone number associated with some contact.
                 * We need to determine now if the dialed number has the same form as stored number
                 * (+972 can be associated with same contact but be different number). If not, we need to change it to stored
                 * number.
                 */
                if (callCount.getPhoneNumber().compareTo(storedNumber) != 0) {
                    callCount.setPhoneNumber(storedNumber);
                }
                callCount.setBitmap(creator.generatePhoneNumberIcon(contactUri, type, mBrowseFrequentContacts.getBadge()));
                callCount.setCallerName(name);
                callCount.setPhoneType(type);
            } else {
                callCount.setBitmap(creator.generateRandomPhoneIcon());
            }
            cursorInPhoneLookup.close();
        }
        return true;
    }

}