// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

public class LogUtil {
    /**
     * Log a string to console, first to javascript console, and if it fails,
     * then to regular system console. 
     */
    public static void logToConsole(String str) {
        // Try to log to javascript console, which is only available when
        // running in production mode in browser; otherwise, log to regular
        // system console.
        if (!jsLogToConsole(str))
            System.out.println(str);
    }

    /**
     * Log a string to the javascript console, if it exists, i.e. if it's defined correctly.
     * Returns true if logging was successful.
     * The check is necessary to prevent crash/hang when running "ant test.dev" or "ant test.prod".
     */
    private static native boolean jsLogToConsole(String str) /*-{
        if ($wnd.console == null ||
                (typeof($wnd.console.log) != 'function' && typeof($wnd.console.log) != 'object')) {
            return false;
        }
        $wnd.console.log(str);
        return true;
    }-*/;

}
