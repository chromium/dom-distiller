// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

/**
 */
public class Assert {
    public void assertTrue(boolean condition) {
        if (!condition) fail(format(null, true, condition));
    }

    public void assertTrue(String message, boolean condition) {
        if (!condition) fail(format(message, true, condition));
    }

    private void fail(String message) {
        throw new AssertionFailedException(message);
    }

    private String format(String message, Object expected, Object actual) {
        return "Expected=" + expected + " Actual=" + actual +
            (message == null ? "" : ": " + message);
    }

    private class AssertionFailedException extends RuntimeException {
        AssertionFailedException(String message) {
            super(message);
        }
    }

}
