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

import android.graphics.Bitmap;

/**
 *
 */
public class CallCounts implements Comparable<CallCounts> {
    private String phoneNumber;
    private int callCount;
    private String callerName;
    private int phoneType;
    private Bitmap bitmap;

    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CallCounts(String number) {
        this.phoneNumber = number;
        callCount = 1;
        callerName = "";
        phoneType = 0;
        bitmap = null;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void increment() {
        ++callCount;
    }

    public void incrementBy(int num) {
        callCount += num;
    }

    public String getDisplay() {
        String title = phoneNumber;
        if (!"".equalsIgnoreCase(callerName)) {
            title = callerName;
        }
        return title;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getCallCount() {
        return callCount;
    }

    @Override
    public String toString() {
        return "CallCounts{" +
                "phoneNumber=" + phoneNumber +
                ", callCount=" + callCount +
                ", callerName=" + callerName +
                '}';
    }


    public int compareTo(CallCounts o) {
        int thisCount = this.callCount;
        int anotherCount = o.getCallCount();
        int res;
        if (thisCount > anotherCount) {
            res = -1;
        } else if (thisCount != anotherCount) {
            res = 1;
        } else {
            String thisNumber = this.phoneNumber;
            String anotherNumber = o.getPhoneNumber();
            res = thisNumber.compareTo(anotherNumber);
        }
        return res;
    }
}
