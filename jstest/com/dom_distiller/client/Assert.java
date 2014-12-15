// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

/**
 * A set of assertion methods for tests.
 */
public class Assert {
    public static void assertTrue(boolean condition) {
        if (!condition) fail(format(null, true, condition));
    }

    public static void assertTrue(String message, boolean condition) {
        if (!condition) fail(format(message, true, condition));
    }

    private static void fail(String message) {
        throw new AssertionFailedException(message);
    }

    private static String format(String message, Object expected, Object actual) {
        return "Expected=" + expected + " Actual=" + actual +
            (message == null ? "" : ": " + message);
    }

    private static class AssertionFailedException extends RuntimeException {
        AssertionFailedException(String message) {
            super(message);
        }
    }
}
