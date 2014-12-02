// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.dom_distiller.proto.DomDistillerProtos.TimingEntry;
import com.dom_distiller.proto.DomDistillerProtos.TimingInfo;

public class LogUtil {

    public static final int DEBUG_LEVEL_NONE = 0;
    public static final int DEBUG_LEVEL_BOILER_PIPE_PHASES = 1;
    public static final int DEBUG_LEVEL_VISIBILITY_INFO = 2;
    public static final int DEBUG_LEVEL_PAGING_INFO = 3;
    public static final int DEBUG_LEVEL_TIMING_INFO = 4;

    private static final StringBuilder LOG_BUILDER = new StringBuilder();

    /**
     * Debug level requested by the client for logging to include while distilling.
     */
    private static int sDebugLevel = DEBUG_LEVEL_NONE;

    /**
     * Whether the log should be included in
     * {@link com.dom_distiller.proto.DomDistillerProtos.DomDistillerResult}.
     */
    private static boolean sIncludeLog = false;

    public static boolean isLoggable(int level) {
        return sDebugLevel >= level;
    }

    /**
     * Log a string to console, first to javascript console, and if it fails,
     * then to regular system console.
     */
    public static void logToConsole(String str) {
        // Try to log to javascript console, which is only available when
        // running in production mode in browser; otherwise, log to regular
        // system console.
        if (str.contains("[0;") || str.contains("[1;"))
            str += kReset;

        if (!jsLogToConsole(str))
            System.out.println(str);

        LOG_BUILDER.append(str).append("\n");
    }

    static int getDebugLevel() {
        return sDebugLevel;
    }

    static void setDebugLevel(int level) {
        sDebugLevel = level;
    }

    static String getAndClearLog() {
        String log = LOG_BUILDER.toString();
        // Clears the log, but re-uses the StringBuilder to reduce amount of copying for next use.
        LOG_BUILDER.setLength(0);
        return log;
    }

    public static final String kBlack = "\033[0;30m";
    public static final String kWhite = "\033[1;37m";

    public static final String kDarkGray = "\033[1;30m";
    public static final String kLightGray = "\033[0;37m";

    public static final String kBlue = "\033[0;34m";
    public static final String kLightBlue = "\033[1;34m";

    public static final String kGreen = "\033[0;32m";
    public static final String kLightGreen = "\033[1;32m";

    public static final String kCyan = "\033[0;36m";
    public static final String kLightCyan = "\033[1;36m";

    public static final String kRed = "\033[0;31m";
    public static final String kLightRed = "\033[1;31m";

    public static final String kPurple = "\033[0;35m";
    public static final String kLightPurple = "\033[1;35m";

    public static final String kBrown = "\033[0;33m";
    public static final String kYellow = "\033[1;33m";

    public static final String kReset = "\033[0m";

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

    public static void addTimingInfo(double startTime, TimingInfo timinginfo, String name) {
        if (timinginfo != null) {
            TimingEntry entry =  timinginfo.addOtherTimes();
            entry.setName(name);
            entry.setTime(DomUtil.getTime() - startTime);
        }
    }
}
