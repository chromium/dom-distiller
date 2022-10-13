// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

public class SimpleTest extends JsTestCase {
    public void testSuccess() {
        assertTrue(true);
        assertTrue("Failure message", true);
    }
}
