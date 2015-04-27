// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

public class QueryParamPagePatternTest extends DomDistillerJsTestCase {
    private static final String PAGE_PARAM_VALUE = "8";

    public void testIsPagingUrl() {
        assertTrue(isPagingUrl("http://www.foo.com/a/b?queryA=v1&queryB=4&queryC=v3",
                               "http://www.foo.com/a/b?queryA=v1&queryB=[*!]&queryC=v3"));
        assertTrue(isPagingUrl("http://www.foo.com/a/b?queryA=v1&queryC=v3",
                               "http://www.foo.com/a/b?queryA=v1&queryB=[*!]&queryC=v3"));
        assertTrue(isPagingUrl("http://www.foo.com/a/b?queryB=2&queryC=v3",
                               "http://www.foo.com/a/b?queryB=[*!]&queryC=v3"));
        assertTrue(isPagingUrl("http://www.foo.com/a/b?queryC=v3",
                               "http://www.foo.com/a/b?queryB=[*!]&queryC=v3"));
        assertTrue(isPagingUrl("http://www.foo.com/a/b", "http://www.foo.com/a/b?page=[*!]"));
        assertTrue(isPagingUrl("http://www.foo.com/a/b?page=3",
                               "http://www.foo.com/a/b?page=[*!]"));
        assertTrue(isPagingUrl("http://www.foo.com/a/b/", "http://www.foo.com/a/b?page=[*!]"));
        assertTrue(isPagingUrl("http://www.foo.com/a/b.htm", "http://www.foo.com/a/b?page=[*!]"));
        assertTrue(isPagingUrl("http://www.foo.com/a/b.html", "http://www.foo.com/a/b?page=[*!]"));
        assertFalse(isPagingUrl("http://www.foo.com/a/b?queryA=v1&queryC=v3",
                                "http://www.foo.com/a/b?queryB=[*!]&queryC=v3"));
        assertFalse(isPagingUrl("http://www.foo.com/a/b?queryB=bar&queryC=v3",
                                "http://www.foo.com/a/b?queryB=[*!]&queryC=v3"));
        assertFalse(isPagingUrl("http://www.foo.com/a/b?queryC=v3&queryB=3",
                                "http://www.foo.com/a/b?queryB=[*!]&queryC=v3"));
        assertFalse(isPagingUrl("http://www.foo.com/a/b?queryA=v1",
                                "http://www.foo.com/a/b?queryA=v1&queryB=[*!]&queryC=v3"));
    }

    public void testIsPagePatternValid() {
        assertTrue(isPagePatternValid("http://www.google.com/forum-12",
                "http://www.google.com/forum-12?page=[*!]"));
        assertTrue(isPagePatternValid("http://www.google.com/forum-12?sid=12345",
                "http://www.google.com/forum-12?page=[*!]&sort=d"));
        assertFalse(isPagePatternValid("http://www.google.com/a/forum-12?sid=12345",
                "http://www.google.com/b/forum-12?page=[*!]&sort=d"));
        assertFalse(isPagePatternValid("http://www.google.com/forum-11?sid=12345",
                "http://www.google.com/forum-12?page=[*!]&sort=d"));
    }

    private static boolean isPagingUrl(String urlStr, String patternStr) {
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

    private static PageParameterDetector.PagePattern createPagePattern(String patternStr) {
        ParsedUrl url = ParsedUrl.create(patternStr);
        String[][] queryParams = url.getQueryParams();
        assertTrue(queryParams.length > 0);
        for (String[] nameValue : queryParams) {
            final String queryName = nameValue[0];
            final String queryValue = nameValue[1];
            if (queryValue.contains(PageParameterDetector.PAGE_PARAM_PLACEHOLDER)) {
                return QueryParamPagePattern.create(ParsedUrl.create(
                        url.replaceQueryValue(queryName, queryValue, PAGE_PARAM_VALUE)),
                        queryName, PAGE_PARAM_VALUE);
            }
        }
        return null;
    }

}
