// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * This tests the super-sourced java.util.Pattern/Matcher.
 *
 * When running test.dev, these tests will actually use the real java versions of the tested
 * classes. When running test.prod it will use the super-sourced versions. This has the added
 * benefit of ensuring that the behavior that the tests expect are actually the behavior of the real
 * java implementations.
 */
public class RegExpTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testMatcherCaseSensitiveFind() {
        Pattern pattern = Pattern.compile("match");
        String goodString = "sdfmatchsdf";
        String badString1 = "sdfxxxxxsdf";
        String badString2 = "sdfMatchsdf";
        assertTrue(pattern.matcher(goodString).find());
        assertFalse(pattern.matcher(badString1).find());
        assertFalse(pattern.matcher(badString2).find());
    }

    public void testMatcherCaseInsensitiveFind() {
        Pattern pattern = Pattern.compile("match", Pattern.CASE_INSENSITIVE);
        String goodString1 = "sdfMaTchsdf";
        String goodString2 = "sdfmatchsdf";
        String badString = "sdfxxxxxsdf";
        assertTrue(pattern.matcher(goodString1).find());
        assertTrue(pattern.matcher(goodString2).find());
        assertFalse(pattern.matcher(badString).find());
    }

    public void testMatcherMultipleFind() {
        Pattern pattern = Pattern.compile("match");
        String multiple = "123match123match123match";
        Matcher matcher = pattern.matcher(multiple);
        assertTrue(matcher.find());
        assertEquals(3, matcher.start());
        assertEquals(8, matcher.end());

        assertTrue(matcher.find());
        assertEquals(11, matcher.start());
        assertEquals(16, matcher.end());

        assertTrue(matcher.find());
        assertEquals(19, matcher.start());
        assertEquals(24, matcher.end());

        assertFalse(matcher.find());
    }

    public void testMatcherMatches() {
        Pattern pattern = Pattern.compile("match");
        String goodString = "match";
        String badString = "match123";
        assertTrue(pattern.matcher(goodString).matches());
        assertFalse(pattern.matcher(badString).matches());
    }

    public void testMatcherGroup() {
        Pattern pattern = Pattern.compile("ma(tch)");
        String testString = "match";
        Matcher matcher = pattern.matcher(testString);
        assertTrue(matcher.find());
        assertEquals("match", matcher.group());
        assertEquals("match", matcher.group(0));
        assertEquals("tch", matcher.group(1));
    }

    public void testMatcherReplaceAll() {
        Pattern pattern = Pattern.compile("match");
        String multiple = "123match123match123match";
        Matcher matcher = pattern.matcher(multiple);
        String replaced = matcher.replaceAll("456");
        assertEquals("123456123456123456", replaced);
    }
}
