// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

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
        JsTestSuiteBuilder builder = GWT.<JsTestSuiteBuilder>create(JsTestSuiteBuilder.class);
        TestLogger logger = new TestLogger();
        Map<String, JsTestSuiteBase.TestCaseResults> results = builder.build().run(logger, filter);

        return createTestSuiteResults(results, logger);
    }

    private static TestSuiteResults createTestSuiteResults(
            Map<String, JsTestSuiteBase.TestCaseResults> results, TestLogger logger) {
        int numTests = 0, failed = 0, skipped = 0;
        TestSuiteResults response = createResults();
        response.setSuccess(true);
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

        response.setNumTests(numTests);
        response.setFailed(failed);
        response.setSkipped(skipped);
        response.setLog(logger.getLog());
        return response;
    }
}
