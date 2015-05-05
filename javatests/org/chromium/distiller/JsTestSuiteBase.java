// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class JsTestSuiteBase {
    private boolean debug = false;

    public interface TestCaseRunner {
        public void run(JsTestCase testCase) throws Throwable;
    }

    public interface TestCaseFactory {
        public JsTestCase build() throws Exception;
    }

    public class TestResult {
        private Throwable failure;
        private boolean isSkipped;

        public boolean success() {
            return failure == null && !isSkipped;
        }

        public boolean skipped() {
            return isSkipped;
        }

        public Throwable getException() {
            return failure;
        }

        public void setException(Throwable e) {
            failure = e;
        }

        public void setSkipped() {
            isSkipped = true;
        }
    }

    public class TestCaseResults {
        private final String testCaseName;
        private TreeMap<String, TestResult> results;
        private Exception testCaseFailure;

        TestCaseResults(String testCase) {
            testCaseName = testCase;
            results = new TreeMap<String, TestResult>();
        }

        public void setCaseFailure(Exception e) {
            testCaseFailure = e;
        }

        public void setResult(String testName, TestResult result) {
            results.put(testName, result);
        }

        public Map<String, TestResult> getResults() {
            return results;
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

        public TestCaseResults run(TestLogger logger, TestFilter filter, boolean shuffle) {
            if (debug) logger.log(TestLogger.WARNING, "Starting " + testCaseName);
            TestCaseResults results = new TestCaseResults(testCaseName);
            try {
                List<Map.Entry<String, TestCaseRunner>> list =
                        new ArrayList<Map.Entry<String, TestCaseRunner>>(tests.entrySet());
                if (shuffle) {
                    TestUtil.shuffle(list);
                }
                for (Map.Entry<String, TestCaseRunner> test : list) {
                    Assert.setDumpTraceOnFailure(true);
                    TestResult result = new TestResult();
                    if (!filter.test(testCaseName, test.getKey())) {
                        if (debug) logger.log(TestLogger.WARNING, "Skipping " + test.getKey());
                        result.setSkipped();
                    } else {
                        if (debug) logger.log(TestLogger.WARNING, "Running " + test.getKey());
                        try {
                            JsTestCase testCase = factory.build();
                            testCase.setUp();
                            try {
                                test.getValue().run(testCase);
                            } finally {
                                testCase.tearDown();
                            }
                        } catch (Throwable e) {
                            result.setException(e);
                        }
                    }
                    results.setResult(test.getKey(), result);
                }
            } catch (Exception e) {
                results.setCaseFailure(e);
            }
            return results;
        }

        public Set<String> getTestNames() {
            return tests.keySet();
        }
    }

    private class TestFilter {
        private final List<RegExp> pos;
        private final List<RegExp> neg;

        private RegExp convert(String q) {
            // This is a simplified conversion, and doesn't handle all
            // possible glob strings correctly.
            q = q.replace(".", "\\.");
            q = q.replace("*", ".*");
            q = q.replace("?", ".");
            return RegExp.compile("^" + q + "$");
        }

        TestFilter(String filter) {
            pos = new ArrayList<RegExp>();
            neg = new ArrayList<RegExp>();

            if (filter == null) return;

            String[] segments = filter.split("-");
            if (segments.length > 2) {
                LogUtil.logToConsole("[ERROR] filter \"" + filter + "\" is malformed.");
            }
            for (int i = 0; i < segments.length; i++) {
                String[] filters = segments[i].split(":");
                for (int j = 0; j < filters.length; j++) {
                    if (filters[j].length() == 0) continue;
                    RegExp r = convert(filters[j]);
                    if (i == 0) {
                        pos.add(r);
                    } else {
                        neg.add(r);
                    }
                }
            }
        }

        public boolean test(String className, String methodName) {
            boolean ans = false;
            if (pos.size() == 0) ans = true;
            for (int i = 0; i < pos.size(); i++) {
                if (pos.get(i).test(className + "." + methodName)) {
                    ans = true;
                }
            }
            for (int i = 0; i < neg.size(); i++) {
                if (neg.get(i).test(className + "." + methodName)) {
                    ans = false;
                }
            }
            return ans;
        }
    }

    private TreeMap<String, TestCase> testCases;

    public JsTestSuiteBase() {
        testCases = new TreeMap<String, TestCase>();
        debug = "1".equals(Window.Location.getParameter("debug"));
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

    public Map<String, TestCaseResults> run(TestLogger logger, String filterString) {
        return run(logger, filterString, false);
    }

    public Map<String, TestCaseResults> run(
            TestLogger logger, String filterString, boolean shuffle) {
        TreeMap<String, TestCaseResults> results = new TreeMap<String, TestCaseResults>();
        TestFilter filter = new TestFilter(filterString);
        List<Map.Entry<String, TestCase>> list =
                new ArrayList<Map.Entry<String, TestCase>>(testCases.entrySet());
        if (shuffle) {
            TestUtil.shuffle(list);
        }
        for (Map.Entry<String, TestCase> caseEntries : list) {
            results.put(caseEntries.getKey(),
                        caseEntries.getValue().run(logger, filter, shuffle));
        }
        for (Map.Entry<String, TestCaseResults> resultsEntry : results.entrySet()) {
            logger.log(TestLogger.RESULTS, "Results for " + resultsEntry.getKey());
            TestCaseResults caseResults = resultsEntry.getValue();
            for (Map.Entry<String, TestResult> testEntry : caseResults.getResults().entrySet()) {
                TestResult res = testEntry.getValue();
                if (res.skipped()) {
                    logger.log(TestLogger.RESULTS, "    " + testEntry.getKey() + ": SKIPPED");
                } else if (res.success()) {
                    logger.log(TestLogger.RESULTS, "    " + testEntry.getKey() + ": PASSED");
                } else {
                    logger.log(TestLogger.RESULTS, "    " + testEntry.getKey() + ": FAILED");
                    logExceptionString(logger, res.getException());
                }
            }
        }
        return results;
    }

    private void logExceptionString(TestLogger logger, Throwable e) {
        StackTraceElement[] stack = e.getStackTrace();
        // The first stack frames are gwt internal exception creation functions that just make it
        // harder to see the real error. Skip them.
        int start = e instanceof JavaScriptException ? 1 : 4;
        if (debug) {
           start = 0;
        }
        int end = stack.length;
        logger.log(TestLogger.RESULTS, e.getMessage());
        for (int i = start; i < end; i++) {
            StackTraceElement ste = stack[i];
            if (debug) logger.log(TestLogger.WARNING, ste.toString());
            logger.log(TestLogger.RESULTS, "at " + stackFrameString(ste));
        }
    }

    public static String stackFrameString(StackTraceElement ste) {
        String mangledName = ste.getMethodName();
        mangledName = mangledName.split("__")[0];
        String demangledName =
                mangledName.replaceAll("_1", "~~").replaceAll("_", ".").replaceAll("~~", "_");
        return demangledName + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")";
    }
}
