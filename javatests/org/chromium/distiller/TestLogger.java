// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

public class TestLogger {
    public static final int RESULTS = -1;
    public static final int ERROR = 0;
    public static final int WARNING = 1;
    public static final int INFO = 2;
    public static final int DEBUG = 3;

    private int logLevel = WARNING;

    private StringBuilder logBuffer = new StringBuilder();

    public void log(int logLevel, String message) {
        if (logLevel <= this.logLevel) {
            logBuffer.append(message + "\n");
            LogUtil.logToConsole(message);
        }
    }

    public String getLog() {
        return LogUtil.getAndClearLog();
    }

    public static class NullLogger extends TestLogger {
        @Override
        public void log(int logLevel, String message) {
        }
    }

}
