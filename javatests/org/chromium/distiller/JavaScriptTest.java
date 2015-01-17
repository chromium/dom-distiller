// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

public class JavaScriptTest extends DomDistillerJsTestCase {
    public void testParseFloat() {
        assertEquals(1.0, JavaScript.parseFloat("1.0"), 1e-10);
        assertEquals(1.0, JavaScript.parseFloat("1.0f"), 1e-10);
        assertEquals(0.0, JavaScript.parseFloat("0"), 1e-10);
        assertEquals(3.14, JavaScript.parseFloat("3.14"), 1e-10);
        assertEquals(3.14159265359, JavaScript.parseFloat("3.14159265359"), 1e-10);
        assertTrue(Double.isNaN(JavaScript.parseFloat("")));
        assertTrue(Double.isNaN(JavaScript.parseFloat("sdfg1")));
    }

    public void testParseInt() {
        assertEquals(1, JavaScript.parseInt("1"));
        assertEquals(0, JavaScript.parseInt("0"));
        assertEquals(3, JavaScript.parseInt("3.14"));
        assertEquals(0, JavaScript.parseInt(""));
        assertEquals(0, JavaScript.parseInt("f1"));
    }
}
