// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

/**
 * A set of assertion methods for tests.
 */
public class Assert {
    public static void assertArrayEquals(byte[] expecteds, byte[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(null, expecteds, actuals));
    }

    public static void assertArrayEquals(char[] expecteds, char[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(null, expecteds, actuals));
    }

    public static void assertArrayEquals(int[] expecteds, int[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(null, expecteds, actuals));
    }

    public static void assertArrayEquals(long[] expecteds, long[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(null, expecteds, actuals));
    }

    public static void assertArrayEquals(Object[] expecteds, Object[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(null, expecteds, actuals));
    }

    public static void assertArrayEquals(short[] expecteds, short[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(null, expecteds, actuals));
    }

    public static void assertArrayEquals(String message, byte[] expecteds, byte[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(message, expecteds, actuals));
    }

    public static void assertArrayEquals(String message, char[] expecteds, char[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(message, expecteds, actuals));
    }

    public static void assertArrayEquals(String message, int[] expecteds, int[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(message, expecteds, actuals));
    }

    public static void assertArrayEquals(String message, long[] expecteds, long[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(message, expecteds, actuals));
    }

    public static void assertArrayEquals(String message, Object[] expecteds, Object[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(message, expecteds, actuals));
    }

    public static void assertArrayEquals(String message, short[] expecteds, short[] actuals) {
        if (!java.util.Arrays.equals(expecteds, actuals)) fail(format(message, expecteds, actuals));
    }

    public static void assertEquals(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) fail(format(null, expected, actual));
    }

    public static void assertEquals(long expected, long actual) {
        if (expected != actual) fail(format(null, expected, actual));
    }

    public static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual))
            fail(format(null, expected, actual));
    }

    public static void assertEquals(String message, double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) fail(format(message, expected, actual));
    }

    public static void assertEquals(String message, long expected, long actual) {
        if (expected != actual) fail(format(message, expected, actual));
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual))
            fail(format(message, expected, actual));
    }

    public static void assertFalse(boolean condition) {
        if (condition) fail(format(null, false, condition));
    }

    public static void assertFalse(String message, boolean condition) {
        if (condition) fail(format(message, false, condition));
    }

    public static void assertNotNull(Object object) {
        if (object == null) fail(format(null, "not null", object));
    }

    public static void assertNotNull(String message, Object object) {
        if (object == null) fail(format(message, "not null", object));
    }

    public static void assertNotSame(Object unexpected, Object actual) {
        if (unexpected == actual) fail(format(null, unexpected, actual));
    }

    public static void assertNotSame(String message, Object unexpected, Object actual) {
        if (unexpected == actual) fail(format(message, unexpected, actual));
    }

    public static void assertNull(Object object) {
        if (object != null) fail(format(null, null, object));
    }

    public static void assertNull(String message, Object object) {
        if (object != null) fail(format(message, null, object));
    }

    public static void assertSame(Object expected, Object actual) {
        if (expected != actual) fail(format(null, expected, actual));
    }

    public static void assertSame(String message, Object expected, Object actual) {
        if (expected != actual) fail(format(message, expected, actual));
    }

    public static void assertTrue(boolean condition) {
        if (!condition) fail(format(null, true, condition));
    }

    public static void assertTrue(String message, boolean condition) {
        if (!condition) fail(format(message, true, condition));
    }

    public static void fail() {
        throw new AssertionFailedException("");
    }

    public static void fail(String message) {
        throw new AssertionFailedException(message);
    }

    private static String format(String message, Object expected, Object actual) {
        return "Expected=" + expected + " Actual=" + actual +
            (message == null ? "" : ": " + message);
    }

    private static boolean dumpTraceOnFailure = false;

    private static class AssertionFailedException extends RuntimeException {
        AssertionFailedException(String message) {
            super(message);
            if (dumpTraceOnFailure) consoleTrace();
        }
    }

    public static void setDumpTraceOnFailure(boolean v) {
        dumpTraceOnFailure = v;
    }

    // The console trace will contain clickable links to source files (w/ line number offset).
    public static native void consoleTrace() /*-{
        console.trace();
    }-*/;
}
