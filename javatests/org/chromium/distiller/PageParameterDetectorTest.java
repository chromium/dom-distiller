// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

public class PageParameterDetectorTest extends DomDistillerJsTestCase {

    public void testIsLastNumericPathComponentBad() {
        // Path component is not numeric i.e. contains non-digits.
        String url = "http://www.foo.com/a2";
        int digitStart = url.indexOf("2");
        assertFalse(PageParameterDetector.isLastNumericPathComponentBad(url, 18, digitStart,
                digitStart + 1));

        // Numeric path component is first.
        url = "http://www.foo.com/2";
        digitStart = url.indexOf("2");
        assertFalse(PageParameterDetector.isLastNumericPathComponentBad(url, 18, digitStart,
                digitStart + 1));

        // Numeric path component follows a path component that is not a bad page param name.
        url = "http://www.foo.com/good/2";
        digitStart = url.indexOf("2");
        assertFalse(PageParameterDetector.isLastNumericPathComponentBad(url, 18, digitStart,
                digitStart + 1));

        // Numeric path component follows a path component that is a bad page param name.
        url = "http://www.foo.com/wiki/2";
        digitStart = url.indexOf("2");
        assertTrue(PageParameterDetector.isLastNumericPathComponentBad(url, 18, digitStart,
                digitStart + 1));

        // (s)htm(l) extension doesn't follow digit.
        url = "http://www.foo.com/2a";
        digitStart = url.indexOf("2");
        assertFalse(PageParameterDetector.isLastNumericPathComponentBad(url, 18, digitStart,
                digitStart + 1));

        // .htm follows digit, previous path component is not a bad page param name.
        url = "http://www.foo.com/good/2.htm";
        digitStart = url.indexOf("2");
        assertFalse(PageParameterDetector.isLastNumericPathComponentBad(url, 18, digitStart,
                digitStart + 1));

        // .html follows digit, previous path component is a bad page param name.
        url = "http://www.foo.com/wiki/2.html";
        digitStart = url.indexOf("2");
        assertTrue(PageParameterDetector.isLastNumericPathComponentBad(url, 18, digitStart,
                digitStart + 1));

        // .shtml follows digit, previous path component is not a bad page param name, but the one
        // before that is.
        url = "http://www.foo.com/wiki/good/2.shtml";
        digitStart = url.indexOf("2");
        assertFalse(PageParameterDetector.isLastNumericPathComponentBad(url, 18, digitStart,
                digitStart + 1));
    }

}
