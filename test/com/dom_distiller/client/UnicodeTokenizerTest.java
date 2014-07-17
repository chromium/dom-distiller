// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import de.l3s.boilerpipe.util.UnicodeTokenizer;

import java.util.Arrays;

public class UnicodeTokenizerTest extends DomDistillerTestCase {
    public void testTokenize() {
        assertArrayEquals(new String[]{"JULIE'S", "CALAMARI"},
                UnicodeTokenizer.tokenize("JULIE'S CALAMARI"));
        assertArrayEquals(new String[]{"all-purpose", "flour"},
                UnicodeTokenizer.tokenize("all-purpose flour"));
        assertArrayEquals(new String[]{"1/2", "cups", "flour"},
                UnicodeTokenizer.tokenize("1/2 cups flour"));
        assertArrayEquals(new String[]{"email", "foo@bar.com"},
                UnicodeTokenizer.tokenize("email foo@bar.com"));
        assertArrayEquals(new String[]{"$2.38", "million"},
                UnicodeTokenizer.tokenize("$2.38 million"));
        assertArrayEquals(new String[]{"goto", "website.com"},
                UnicodeTokenizer.tokenize("goto website.com"));
        assertArrayEquals(new String[]{"Deal", "expires:7d:04h:23m"},
                UnicodeTokenizer.tokenize("Deal expires:7d:04h:23m"));
    }

    private static void assertArrayEquals(String[] expected, String[] got) {
        assertTrue(
                "Expected: " + Arrays.toString(expected) + ", got: " + Arrays.toString(got),
                Arrays.equals(expected, got));
    }
}
