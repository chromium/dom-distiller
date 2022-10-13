// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

/**
 * Base test case for all JS tests.
 */
public class JsTestCase extends Assert {
    public void setUp() throws Exception {}
    public void tearDown() throws Exception {}

    protected void disableAssertConsoleTrace() {
        setDumpTraceOnFailure(false);
    }
}
