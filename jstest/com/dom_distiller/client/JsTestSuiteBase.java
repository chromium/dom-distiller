// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class JsTestSuiteBase {
    private static final boolean DEBUG = false;

    public interface TestCaseRunner {
        public void run(JsTestCase testCase) throws Exception;
    }

    public interface TestCaseFactory {
        public JsTestCase build() throws Exception;
    }

    public class TestResult {
        boolean success;
        Exception failure;
    }

    public class TestCaseResults {
        String testCaseName;
        TreeMap<String, TestResult> results;
        Exception testCaseFailure;

        TestCaseResults(String testCase) {
            testCaseName = testCase;
            results = new TreeMap<String, TestResult>();
        }

        public void setResult(String testName, TestResult result) {
            results.put(testName, result);
        }
    }

    public class TestCase {
        TestCaseFactory factory;
        String testCaseName;
        TreeMap<String, TestCaseRunner> tests;

        TestCase(TestCaseFactory factory, String name) {
            this.factory = factory;
            this.testCaseName = name;
            tests = new TreeMap<String, TestCaseRunner>();
        }

        public TestCase addTest(TestCaseRunner runner, String name) {
            tests.put(name, runner);
            return this;
        }

        public TestCaseResults run(TestLogger logger) {
            if (DEBUG) logger.log(TestLogger.WARNING, "Starting " + testCaseName);
            TestCaseResults results = new TestCaseResults(testCaseName);
            try {
                JsTestCase testCase = factory.build();
                for (Map.Entry<String, TestCaseRunner> test : tests.entrySet()) {
                    if (DEBUG) logger.log(TestLogger.WARNING, "Running " + test.getKey());
                    TestResult result = new TestResult();
                    try {
                        test.getValue().run(testCase);
                        result.success = true;
                    } catch (Exception e) {
                        result.failure = e;
                    }
                    results.setResult(test.getKey(), result);
                }
            } catch (Exception e) {
                results.testCaseFailure = e;
            }
            return results;
        }

        public Set<String> getTestNames() {
            return tests.keySet();
        }
    }

    private TreeMap<String, TestCase> testCases;

    public JsTestSuiteBase() {
        testCases = new TreeMap<String, TestCase>();
    }

    public TestCase addTestCase(TestCaseFactory factory, String name) {
        TestCase tc = new TestCase(factory, name);
        testCases.put(name, tc);
        return tc;
    }

    public Map<String, Set<String>> getTestNames() {
        TreeMap<String, Set<String>> names = new TreeMap<String, Set<String>>();
        for (Map.Entry<String, TestCase> e : testCases.entrySet()) {
            names.put(e.getKey(), e.getValue().getTestNames());
        }
        return names;
    }

    public Map<String, TestCaseResults> run(TestLogger logger) {
        TreeMap<String, TestCaseResults> results = new TreeMap<String, TestCaseResults>();
        for (Map.Entry<String, TestCase> caseEntries : testCases.entrySet()) {
            results.put(caseEntries.getKey(), caseEntries.getValue().run(logger));
        }
        for (Map.Entry<String, TestCaseResults> resultsEntry : results.entrySet()) {
            logger.log(TestLogger.RESULTS, "Results for " + resultsEntry.getKey());
            TestCaseResults caseResults = resultsEntry.getValue();
            for (Map.Entry<String, TestResult> testEntry : caseResults.results.entrySet()) {
                TestResult res = testEntry.getValue();
                logger.log(TestLogger.RESULTS,
                        testEntry.getKey() + ": " + (res.success ? "SUCCESS" : "FAILURE"));
                if (!res.success) {
                    logger.log(TestLogger.RESULTS, getExceptionString(res.failure));
                }
            }
        }
        return results;
    }

    private static String getExceptionString(Exception e) {
        return e.toString();
    }
}
