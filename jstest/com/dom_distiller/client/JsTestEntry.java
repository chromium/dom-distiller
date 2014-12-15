// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;

import java.util.Map;

public class JsTestEntry implements EntryPoint {
    public void onModuleLoad() {
    }

    @JsType
    interface TestSuiteResults {
        @JsProperty void setLog(String log);
        @JsProperty void setSuccess(boolean success);
    }

    private static native TestSuiteResults createResults() /*-{
        return new Object();
    }-*/;

    @JsExport
    public static TestSuiteResults run() {
        JsTestSuiteBuilder builder = GWT.<JsTestSuiteBuilder>create(JsTestSuiteBuilder.class);
        TestLogger logger = new TestLogger();
        Map<String, JsTestSuiteBase.TestCaseResults> results = builder.build().run(logger);
        TestSuiteResults response = createResults();
        response.setSuccess(true);
        for (Map.Entry<String, JsTestSuiteBase.TestCaseResults> testCaseEntry :
                results.entrySet()) {
            for (Map.Entry<String, JsTestSuiteBase.TestResult> testEntry :
                    testCaseEntry.getValue().getResults().entrySet()) {
                if (!testEntry.getValue().success()) {
                    logger.log(TestLogger.RESULTS,
                            testCaseEntry.getKey() + "." + testEntry.getKey() + " Failed.");
                    response.setSuccess(false);
                }
            }
        }

        response.setLog(logger.getLog());
        return response;
    }
}
