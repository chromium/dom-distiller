// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.Map;
import java.util.Set;

public class JsTestSuiteBaseTest extends JsTestCase {
    public static class SimpleTest extends JsTestCase {
        public void testSuccess() {
            assertTrue(true);
        }

        public void testFailure() {
            assertTrue("Failure message", false);
        }
    }

    public static class OtherSimpleTest extends JsTestCase {
        public void testNothing() {}
    }

    public static class NullLogger extends TestLogger {
        @Override
        public void log(int logLevel, String message) {
        }
    }

    public void testJsTestSuiteSetup() {
        JsTestSuiteBase suite = new JsTestSuiteBase();
        suite.addTestCase(null, "TestClass")
            .addTest(null, "test1")
            .addTest(null, "test2");

        suite.addTestCase(null, "OtherTestClass")
            .addTest(null, "testOther1")
            .addTest(null, "testOther2")
            .addTest(null, "testOther3");

        Map<String, Set<String>> testCases = suite.getTestNames();

        assertTrue(testCases.size() == 2);
        assertTrue(testCases.containsKey("TestClass"));
        assertTrue(testCases.get("TestClass").size() == 2);
        assertTrue(testCases.get("TestClass").contains("test1"));
        assertTrue(testCases.get("TestClass").contains("test2"));
        assertTrue(testCases.containsKey("OtherTestClass"));
        assertTrue(testCases.get("OtherTestClass").size() == 3);
        assertTrue(testCases.get("OtherTestClass").contains("testOther1"));
        assertTrue(testCases.get("OtherTestClass").contains("testOther2"));
        assertTrue(testCases.get("OtherTestClass").contains("testOther3"));
    }

    public void testJsTestSuiteResults() {
        JsTestSuiteBase suite = new JsTestSuiteBase();
        suite.addTestCase(null, "TestClass")
            .addTest(null, "test1")
            .addTest(null, "test2");

        suite.addTestCase(null, "OtherTestClass")
            .addTest(null, "testOther1")
            .addTest(null, "testOther2")
            .addTest(null, "testOther3");

        suite.run(new NullLogger(), null);
    }

    public void testStackTraceString() {
        assertTrue("com.dom_distiller.client.ClassName.functionName(FileName.java:123)".equals(
                JsTestSuiteBase.stackFrameString(new StackTraceElement(null,
                        "com_dom_1distiller_client_ClassName_functionName__Ljava_lang_String_2V",
                        "FileName.java", 123))));
    }

    public void testJsTestSuiteFilter() {
        JsTestSuiteBase suite = new JsTestSuiteBase();
        suite.addTestCase(null, "TestClass")
            .addTest(null, "test1")
            .addTest(null, "test2");
        Map<String, JsTestSuiteBase.TestCaseResults> results = suite.run(
                new NullLogger(), ".*TestClass.*");
        assertFalse(results.get("TestClass").getResults().get("test1").skipped());
        assertFalse(results.get("TestClass").getResults().get("test2").skipped());

        results = suite.run(new NullLogger(), ".*NotTestClass.*");
        assertTrue(results.get("TestClass").getResults().get("test1").skipped());
        assertTrue(results.get("TestClass").getResults().get("test2").skipped());

        results = suite.run(new NullLogger(), ".*TestClass.test1");
        assertFalse(results.get("TestClass").getResults().get("test1").skipped());
        assertTrue(results.get("TestClass").getResults().get("test2").skipped());
    }
}
