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

package com.phonepony;

/**
 *
 */
public class Log {
    private static final boolean PRODUCTION_MODE = false;

    private Log() {
    }

    public static int v(String tag, String msg) {
        if (PRODUCTION_MODE) {
            return android.util.Log.v(tag, msg);
        }
        return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (PRODUCTION_MODE) {
            return android.util.Log.v(tag, msg, tr);
        }
        return 0;
    }

    public static int d(String tag, String msg) {
        if (PRODUCTION_MODE) {
            return android.util.Log.d(tag, msg);
        }
        return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (PRODUCTION_MODE) {
            return android.util.Log.d(tag, msg, tr);
        }
        return 0;
    }

    public static int i(String tag, String msg) {
        if (PRODUCTION_MODE) {
            return android.util.Log.i(tag, msg);
        }
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (PRODUCTION_MODE) {
            return android.util.Log.i(tag, msg, tr);
        }
        return 0;
    }

    public static int w(String tag, String msg) {
        if (PRODUCTION_MODE) {
            return android.util.Log.w(tag, msg);
        }
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (PRODUCTION_MODE) {
            return android.util.Log.w(tag, msg, tr);
        }
        return 0;
    }

    public static boolean isLoggable(String tag, int level) {
        //noinspection SimplifiableIfStatement
        if (PRODUCTION_MODE) {
            return android.util.Log.isLoggable(tag, level);
        }
        return false;
    }

    public static int w(String tag, Throwable tr) {
        if (PRODUCTION_MODE) {
            return android.util.Log.w(tag, tr);
        }
        return 0;
    }

    public static int e(String tag, String msg) {
        if (PRODUCTION_MODE) {
            return android.util.Log.e(tag, msg);
        }
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (PRODUCTION_MODE) {
            return android.util.Log.e(tag, msg, tr);
        }
        return 0;
    }

    public static String getStackTraceString(Throwable tr) {
        if (PRODUCTION_MODE) {
            return android.util.Log.getStackTraceString(tr);
        }
        return "";
    }

    public static int println(int priority, String tag, String msg) {
        if (PRODUCTION_MODE) {
            return android.util.Log.println(priority, tag, msg);
        }
        return 0;
    }

}
