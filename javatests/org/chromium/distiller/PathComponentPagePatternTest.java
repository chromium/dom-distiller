// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class PathComponentPagePatternTest extends DomDistillerJsTestCase {
    private static final String PAGE_PARAM_VALUE = "8";
    private static final RegExp sDigitsRegExp = RegExp.compile("(\\d+)", "gi");

    public void testIsPagingUrl() {
        assertTrue(isPagingUrl("http://www.foo.com/a/abc-2.html",
                               "http://www.foo.com/a/abc-[*!].html"));
        assertTrue(isPagingUrl("http://www.foo.com/a/abc.html",
                               "http://www.foo.com/a/abc-[*!].html"));
        assertTrue(isPagingUrl("http://www.foo.com/a/abc", "http://www.foo.com/a/abc-[*!]"));
        assertTrue(isPagingUrl("http://www.foo.com/a/abc-2", "http://www.foo.com/a/abc-[*!]"));
        assertTrue(isPagingUrl("http://www.foo.com/a/b-c-3", "http://www.foo.com/a/b-[*!]-c-3"));
        assertTrue(isPagingUrl("http://www.foo.com/a-c-3", "http://www.foo.com/a-[*!]-c-3"));
        assertTrue(isPagingUrl("http://www.foo.com/a-p-1-c-3", "http://www.foo.com/a-p-[*!]-c-3"));
        assertFalse(isPagingUrl("http://www.foo.com/a/abc-page", "http://www.foo.com/a/abc-[*!]"));
        assertFalse(isPagingUrl("http://www.foo.com/a/2", "http://www.foo.com/a/abc-[*!]"));
        assertFalse(isPagingUrl("http://www.foo.com/a", "http://www.foo.com/a/abc-[*!]"));
        assertFalse(isPagingUrl("http://www.foo.com/a/abc.html",
                                "http://www.foo.com/a/abc[*!].html"));

        assertTrue(isPagingUrl("http://www.foo.com/a/page/2", "http://www.foo.com/a/page/[*!]"));
        assertTrue(isPagingUrl("http://www.foo.com/a", "http://www.foo.com/a/page/[*!]"));
        assertTrue(isPagingUrl("http://www.foo.com/a/page/2/abc.html",
                               "http://www.foo.com/a/page/[*!]/abc.html"));
        assertTrue(isPagingUrl("http://www.foo.com/a/abc.html",
                               "http://www.foo.com/a/page/[*!]/abc.html"));
        assertTrue(isPagingUrl("http://www.foo.com/a/abc.html",
                               "http://www.foo.com/a/[*!]/abc.html"));
        assertTrue(isPagingUrl("http://www.foo.com/a/2/abc.html",
                               "http://www.foo.com/a/[*!]/abc.html"));
        assertTrue(isPagingUrl("http://www.foo.com/abc.html",
                               "http://www.foo.com/a/[*!]/abc.html"));
        assertTrue(isPagingUrl("http://www.foo.com/a/page/2page",
                               "http://www.foo.com/a/page/[*!]page"));
        assertFalse(isPagingUrl("http://www.foo.com/a/page/2",
                                "http://www.foo.com/a/page/[*!]page"));
        assertFalse(isPagingUrl("http://www.foo.com/a/page/b", "http://www.foo.com/a/page/[*!]"));
        assertFalse(isPagingUrl("http://www.foo.com/m/page/2", "http://www.foo.com/p/page/[*!]"));
    }

    public void testIsPagePatternValid() {
        assertTrue(isPagePatternValid("http://www.google.com/forum-12",
                "http://www.google.com/forum-12/page/[*!]"));
        assertTrue(isPagePatternValid("http://www.google.com/forum-12",
                "http://www.google.com/forum-12/[*!]"));
        assertTrue(isPagePatternValid("http://www.google.com/forum-12",
                "http://www.google.com/forum-12/page-[*!]"));

        assertTrue(isPagePatternValid("http://www.google.com/forum-12/food",
                "http://www.google.com/forum-12/food/for/bar/[*!]"));
        assertTrue(isPagePatternValid("http://www.google.com/forum-12-food",
                "http://www.google.com/forum-12-food-[*!]"));

        assertFalse(isPagePatternValid("http://www.google.com/forum-12/food",
                "http://www.google.com/forum-12/food/2012/01/[*!]"));
        assertFalse(isPagePatternValid("http://www.google.com/forum-12/food/2012/01/01",
                "http://www.google.com/forum-12/food/2012/01/[*!]"));

        assertTrue(isPagePatternValid("http://www.google.com/thread/12",
                "http://www.google.com/thread/12/page/[*!]"));
        assertFalse(isPagePatternValid("http://www.google.com/thread/12/foo",
                "http://www.google.com/thread/12/page/[*!]/foo"));
        assertTrue(isPagePatternValid("http://www.google.com/thread/12/foo",
                "http://www.google.com/thread/12/[*!]/foo"));
    }

    public void testIsLastNumericPathComponentBad() {
        // Path component is not numeric i.e. contains non-digits.
        String url = "http://www.foo.com/a2";
        int digitStart = url.indexOf("2");
        assertFalse(isLastNumericPathComponentBad(url, digitStart));

        // Numeric path component is first.
        url = "http://www.foo.com/2";
        digitStart = url.indexOf("2");
        assertFalse(isLastNumericPathComponentBad(url, digitStart));

        // Numeric path component follows a path component that is not a bad page param name.
        url = "http://www.foo.com/good/2";
        digitStart = url.indexOf("2");
        assertFalse(isLastNumericPathComponentBad(url, digitStart));

        // Numeric path component follows a path component that is a bad page param name.
        url = "http://www.foo.com/wiki/2";
        digitStart = url.indexOf("2");
        assertTrue(isLastNumericPathComponentBad(url, digitStart));

        // (s)htm(l) extension doesn't follow digit.
        url = "http://www.foo.com/2a";
        digitStart = url.indexOf("2");
        assertFalse(isLastNumericPathComponentBad(url, digitStart));

        // .htm follows digit, previous path component is not a bad page param name.
        url = "http://www.foo.com/good/2.htm";
        digitStart = url.indexOf("2");
        assertFalse(isLastNumericPathComponentBad(url, digitStart));

        // .html follows digit, previous path component is a bad page param name.
        url = "http://www.foo.com/wiki/2.html";
        digitStart = url.indexOf("2");
        assertTrue(isLastNumericPathComponentBad(url, digitStart));

        // .shtml follows digit, previous path component is not a bad page param name, but the one
        // before that is.
        url = "http://www.foo.com/wiki/good/2.shtml";
        digitStart = url.indexOf("2");
        assertFalse(isLastNumericPathComponentBad(url, digitStart));
    }

    private static boolean isPagingUrl(String urlStr, String patternStr) {
        ParsedUrl url = ParsedUrl.create(urlStr);
        PageParameterDetector.PagePattern pattern = createPagePattern(patternStr);
        assertTrue(pattern != null);
        return pattern.isPagingUrl(urlStr);
    }

    private static boolean isPagePatternValid(String urlStr, String patternStr) {
        ParsedUrl url = ParsedUrl.create(urlStr);
        assertTrue(url != null);
        PageParameterDetector.PagePattern pattern = createPagePattern(patternStr);
        assertTrue(pattern != null);
        return pattern.isValidFor(url);
    }

    private static boolean isLastNumericPathComponentBad(String url, int digitStart) {
        return PathComponentPagePattern.isLastNumericPathComponentBad(url, 18, digitStart,
                digitStart + 1);
    }

    private static PageParameterDetector.PagePattern createPagePattern(String patternStr) {
        int pathStart = patternStr.indexOf('/');
        int digitStart = patternStr.indexOf(PageParameterDetector.PAGE_PARAM_PLACEHOLDER);
        sDigitsRegExp.setLastIndex(digitStart);
        String oriUrlStr = patternStr.replace(PageParameterDetector.PAGE_PARAM_PLACEHOLDER,
                PAGE_PARAM_VALUE);
        MatchResult match = sDigitsRegExp.exec(oriUrlStr);
        if (match == null) return null;
        return PathComponentPagePattern.create(ParsedUrl.create(oriUrlStr), pathStart, digitStart,
                sDigitsRegExp.getLastIndex());
    }

}
