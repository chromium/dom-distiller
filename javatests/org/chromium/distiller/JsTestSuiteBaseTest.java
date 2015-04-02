// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

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

        suite.run(new TestLogger.NullLogger(), null);
    }

    public void testStackTraceString() {
        assertTrue("org.chromium.distiller.ClassName.functionName(FileName.java:123)".equals(
                JsTestSuiteBase.stackFrameString(new StackTraceElement(null,
                        "org_chromium_distiller_ClassName_functionName__Ljava_lang_String_2V",
                        "FileName.java", 123))));
    }

    public void testJsTestSuiteFilter() {
        JsTestSuiteBase suite = new JsTestSuiteBase();
        suite.addTestCase(null, "TestClass")
            .addTest(null, "test1")
            .addTest(null, "test2");
        suite.addTestCase(null, "Test2Class")
            .addTest(null, "test1");

        Map<String, JsTestSuiteBase.TestCaseResults> results = suite.run(
                new TestLogger.NullLogger(), "TestClass.*");
        assertFalse(results.get("TestClass").getResults().get("test1").skipped());
        assertFalse(results.get("TestClass").getResults().get("test2").skipped());
        assertTrue(results.get("Test2Class").getResults().get("test1").skipped());

        results = suite.run(new TestLogger.NullLogger(), "*NotTestClass*");
        assertTrue(results.get("TestClass").getResults().get("test1").skipped());
        assertTrue(results.get("TestClass").getResults().get("test2").skipped());
        assertTrue(results.get("Test2Class").getResults().get("test1").skipped());

        results = suite.run(new TestLogger.NullLogger(), "TestClass.test1");
        assertFalse(results.get("TestClass").getResults().get("test1").skipped());
        assertTrue(results.get("TestClass").getResults().get("test2").skipped());
        assertTrue(results.get("Test2Class").getResults().get("test1").skipped());

        results = suite.run(new TestLogger.NullLogger(), "TestClass.*-*.test1");
        assertTrue(results.get("TestClass").getResults().get("test1").skipped());
        assertFalse(results.get("TestClass").getResults().get("test2").skipped());
        assertTrue(results.get("Test2Class").getResults().get("test1").skipped());

        results = suite.run(new TestLogger.NullLogger(), ":*.test1::*.test2:");
        assertFalse(results.get("TestClass").getResults().get("test1").skipped());
        assertFalse(results.get("TestClass").getResults().get("test2").skipped());
        assertFalse(results.get("Test2Class").getResults().get("test1").skipped());

        results = suite.run(new TestLogger.NullLogger(), "T*C*-*tes?2");
        assertFalse(results.get("TestClass").getResults().get("test1").skipped());
        assertTrue(results.get("TestClass").getResults().get("test2").skipped());
        assertFalse(results.get("Test2Class").getResults().get("test1").skipped());

        results = suite.run(new TestLogger.NullLogger(), "TestClass*:*test1-*test2");
        assertFalse(results.get("TestClass").getResults().get("test1").skipped());
        assertTrue(results.get("TestClass").getResults().get("test2").skipped());
        assertFalse(results.get("Test2Class").getResults().get("test1").skipped());

        results = suite.run(new TestLogger.NullLogger(), "-*1");
        assertTrue(results.get("TestClass").getResults().get("test1").skipped());
        assertFalse(results.get("TestClass").getResults().get("test2").skipped());
        assertTrue(results.get("Test2Class").getResults().get("test1").skipped());

        results = suite.run(new TestLogger.NullLogger(), "-*.e*");
        assertFalse(results.get("TestClass").getResults().get("test1").skipped());
        assertFalse(results.get("TestClass").getResults().get("test2").skipped());
        assertFalse(results.get("Test2Class").getResults().get("test1").skipped());

        results = suite.run(new TestLogger.NullLogger(), "-TestClass.test1:TestClass.test2");
        assertTrue(results.get("TestClass").getResults().get("test1").skipped());
        assertTrue(results.get("TestClass").getResults().get("test2").skipped());
        assertFalse(results.get("Test2Class").getResults().get("test1").skipped());

        suite.addTestCase(null, "Test3Class")
            .addTest(null, "test1")
            .addTest(null, "test2")
            .addTest(null, "test3");

        results = suite.run(new TestLogger.NullLogger(),
                            "Test?Class.test*-Test2Class.test1:Test*Class.test3");
        assertTrue(results.get("TestClass").getResults().get("test1").skipped());
        assertTrue(results.get("TestClass").getResults().get("test2").skipped());
        assertTrue(results.get("Test2Class").getResults().get("test1").skipped());
        assertFalse(results.get("Test3Class").getResults().get("test1").skipped());
        assertFalse(results.get("Test3Class").getResults().get("test2").skipped());
        assertTrue(results.get("Test3Class").getResults().get("test3").skipped());

        results = suite.run(new TestLogger.NullLogger(),
                            "TestClass.*:Test?Class.test1:Test3Class.test3-TestClass.test1:Test3Class.test*");
        assertTrue(results.get("TestClass").getResults().get("test1").skipped());
        assertFalse(results.get("TestClass").getResults().get("test2").skipped());
        assertFalse(results.get("Test2Class").getResults().get("test1").skipped());
        assertTrue(results.get("Test3Class").getResults().get("test1").skipped());
        assertTrue(results.get("Test3Class").getResults().get("test2").skipped());
        assertTrue(results.get("Test3Class").getResults().get("test3").skipped());
    }
}
