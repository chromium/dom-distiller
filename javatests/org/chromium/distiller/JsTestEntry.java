// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;
import com.google.gwt.user.client.Window;

import java.util.Map;

public class JsTestEntry implements EntryPoint {
    public void onModuleLoad() {
    }

    @JsType
    interface TestSuiteResults {
        @JsProperty void setLog(String log);
        @JsProperty void setSuccess(boolean success);
        @JsProperty void setNumTests(int i);
        @JsProperty void setFailed(int i);
        @JsProperty void setSkipped(int i);
    }

    private static native TestSuiteResults createResults() /*-{
        return new Object();
    }-*/;

    @JsExport
    public static TestSuiteResults run() {
        String filter = Window.Location.getParameter("filter");
        return runWithFilter(filter);
    }

    @JsExport
    public static TestSuiteResults runWithFilter(String filter) {
        int debugLevel = JavaScript.parseInt(Window.Location.getParameter("debug_level"));
        LogUtil.setDebugLevel(debugLevel);
        boolean consoleLog = !"0".equals(Window.Location.getParameter("console_log"));
        LogUtil.setSuppressConsoleOutput(!consoleLog);
        JsTestSuiteBuilder builder = GWT.<JsTestSuiteBuilder>create(JsTestSuiteBuilder.class);
        TestLogger logger = new TestLogger();
        Map<String, JsTestSuiteBase.TestCaseResults> results = builder.build().run(logger, filter);
        return createTestSuiteResults(results, logger);
    }

    @JsExport
    public static void printTestNames() {
        JsTestSuiteBuilder builder = GWT.<JsTestSuiteBuilder>create(JsTestSuiteBuilder.class);
        Map<String, JsTestSuiteBase.TestCaseResults> results = builder.build().run(
                new TestLogger.NullLogger(), "$^");
        for (Map.Entry<String, JsTestSuiteBase.TestCaseResults> testCaseEntry :
                results.entrySet()) {
            for (Map.Entry<String, JsTestSuiteBase.TestResult> testEntry :
                    testCaseEntry.getValue().getResults().entrySet()) {
                LogUtil.logToConsole(testCaseEntry.getKey() + "." + testEntry.getKey());
            }
        }
    }

    private static TestSuiteResults createTestSuiteResults(
            Map<String, JsTestSuiteBase.TestCaseResults> results, TestLogger logger) {
        int numTests = 0, failed = 0, skipped = 0;
        TestSuiteResults response = createResults();
        for (Map.Entry<String, JsTestSuiteBase.TestCaseResults> testCaseEntry :
                results.entrySet()) {
            for (Map.Entry<String, JsTestSuiteBase.TestResult> testEntry :
                    testCaseEntry.getValue().getResults().entrySet()) {
                numTests++;
                if (testEntry.getValue().skipped()) {
                    skipped++;
                } else if (!testEntry.getValue().success()) {
                    failed++;
                }
            }
        }

        response.setSuccess(failed == 0);
        response.setNumTests(numTests);
        response.setFailed(failed);
        response.setSkipped(skipped);
        // Make the test log contain all LogUtil logs instead of just TestLogger.
        response.setLog(LogUtil.getAndClearLog());
        return response;
    }
}
