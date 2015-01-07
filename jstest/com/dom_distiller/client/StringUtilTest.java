// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

public class StringUtilTest extends JsTestCase {
    public void testCountWords() {
        assertEquals(0, StringUtil.countWords(""));
        assertEquals(0, StringUtil.countWords("  -@# ';]"));
        assertEquals(1, StringUtil.countWords("word"));
        assertEquals(1, StringUtil.countWords("b'fore"));
        assertEquals(1, StringUtil.countWords(" _word.under_score_ "));
        assertEquals(2, StringUtil.countWords(" \ttwo\nwords"));
        assertEquals(2, StringUtil.countWords(" \ttwo @^@^&(@#$([][;;\nwords"));
        assertEquals(5, StringUtil.countWords("dør når på svært dårlig"));
        assertEquals(5, StringUtil.countWords("svært få dør av blåbærsyltetøy"));
    }

    public void testIsWhitespace() {
        assertTrue(StringUtil.isWhitespace(' '));
        assertTrue(StringUtil.isWhitespace('\t'));
        assertTrue(StringUtil.isWhitespace('\n'));
        assertTrue(StringUtil.isWhitespace('\u00a0'));
        assertFalse(StringUtil.isWhitespace('a'));
        assertFalse(StringUtil.isWhitespace('$'));
        assertFalse(StringUtil.isWhitespace('_'));
        assertFalse(StringUtil.isWhitespace('\u0460'));
    }

    public void testIsStringAllWhitespace() {
        assertTrue(StringUtil.isStringAllWhitespace(""));
        assertTrue(StringUtil.isStringAllWhitespace(" \t\r\n"));
        assertTrue(StringUtil.isStringAllWhitespace(" \u00a0     \t\t\t"));
        assertFalse(StringUtil.isStringAllWhitespace("a"));
        assertFalse(StringUtil.isStringAllWhitespace("     a  "));
        assertFalse(StringUtil.isStringAllWhitespace("\u00a0\u0460"));
        assertFalse(StringUtil.isStringAllWhitespace("\n\t_ "));
    }
}
