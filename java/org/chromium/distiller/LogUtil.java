// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.proto.DomDistillerProtos;
import org.chromium.distiller.proto.DomDistillerProtos.TimingEntry;
import org.chromium.distiller.proto.DomDistillerProtos.TimingInfo;

public class LogUtil {
    // All statically initialized fields in this class should be primitives or Strings. Otherwise, a
    // costly (because it is called many, many times) static initializer method will be created.
    public static final int DEBUG_LEVEL_NONE = 0;
    public static final int DEBUG_LEVEL_BOILER_PIPE_PHASES = 1;
    public static final int DEBUG_LEVEL_VISIBILITY_INFO = 2;
    public static final int DEBUG_LEVEL_PAGING_INFO = 3;
    public static final int DEBUG_LEVEL_TIMING_INFO = 4;

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

    private static String sLogBuilder = "";

    /**
     * Debug level requested by the client for logging to include while distilling.
     */
    private static int sDebugLevel = DEBUG_LEVEL_NONE;

    /**
     * Whether the log should be included in
     * {@link DomDistillerProtos.DomDistillerResult}.
     */
    private static boolean sIncludeLog = false;

    /**
     * Whether the log should be suppressed. If this flag is true, there will be no output to
     * the JS console. This is used when running the JS Tests in Chromium, where the log is
     * retreived using {@link #getAndClearLog} instead.
     */
    private static boolean sSuppressConsoleOutput;

    public static boolean isLoggable(int level) {
        return sDebugLevel >= level;
    }

    /**
     * Log a string to console unless {@link #sSuppressLogOutput} is true. The log string is always
     * added to the log builder.
     */
    public static void logToConsole(String str) {
        if (str == null) {
            str = "";
        }

        if (str.contains("[0;") || str.contains("[1;")) {
            str += kReset;
        }

        if (!sSuppressConsoleOutput) {
            jsLogToConsole(str);
        }

        sLogBuilder += str + "\n";
    }

    static void setSuppressConsoleOutput(boolean suppress) {
        sSuppressConsoleOutput = suppress;
    }

    static int getDebugLevel() {
        return sDebugLevel;
    }

    static void setDebugLevel(int level) {
        sDebugLevel = level;
    }

    static String getAndClearLog() {
        String log = sLogBuilder;
        sLogBuilder = "";
        return log;
    }

    /**
     * Log a string to the javascript console, if it exists, i.e. if it's defined correctly.
     */
    private static native void jsLogToConsole(String str) /*-{
        if ($wnd.console == null ||
                (typeof($wnd.console.log) != 'function' && typeof($wnd.console.log) != 'object')) {
            return;
        }
        $wnd.console.log(str);
    }-*/;

    public static void addTimingInfo(double startTime, TimingInfo timinginfo, String name) {
        if (timinginfo != null) {
            TimingEntry entry =  timinginfo.addOtherTimes();
            entry.setName(name);
            entry.setTime(DomUtil.getTime() - startTime);
        }
    }
}
