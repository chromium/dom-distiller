// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.regexp.shared.RegExp;

/**
 * This class detects the page parameter in the query of a potential pagination URL.  If detected,
 * it replaces the page param value with PageParameterDetector.PAGE_PARAM_PLACEHOLDER, then creates
 * and returns a new object.  This object can then be called via PageParameterDetector.PagePattern
 * interface to:
 * - validate the generated URL page pattern against the document URL
 * - determine if a URL is a paging URL based on the page pattern.
 * Example: if the original url is "http://www.foo.com/a/b/?page=2&query=a", the page pattern is
 * "http://www.foo.com/a/b?page=[*!]&query=a". (See comments at top of PageParameterDetector.java).
 */
public class QueryParamPagePattern implements PageParameterDetector.PagePattern {
    private final ParsedUrl mUrl;
    private final int mPageNumber;
    private final int mPlaceholderStart;
    private final String mUrlStr;
    private final int mQueryStart;
    // Start position of query param containing placeholder.
    private int mPlaceholderSegmentStart;
    private final String mPrefix;  // The part of the page pattern before the placeholder.
    private String mSuffix = "";  // The part of the page pattern after the placeholder.
    // This is not mSuffix.length(), see their initializations in constructor.
    private final int mSuffixLen;

    /**
     * Returns a new QueryParamPagePattern if url is valid and page param is in the query.
     */
    static PageParameterDetector.PagePattern create(ParsedUrl url, String queryName,
            String queryValue) {
        try {
            return new QueryParamPagePattern(url, queryName, queryValue);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return mUrlStr;
    }

    @Override
    public int getPageNumber() {
        return mPageNumber;
    }

    /**
     * Returns true if page pattern and URL have the same path components.
     *
     * @param docUrl the current document URL
     */
    @Override
    public boolean isValidFor(ParsedUrl docUrl) {
        return docUrl.getTrimmedPath().equalsIgnoreCase(mUrl.getTrimmedPath());
    }

    private static RegExp sSlashOrHtmExtRegExp = null;  // Match either '/' or ".htm(l)".

    /**
     * Returns true if a URL matches this page pattern based on a pipeline of rules:
     * - suffix (part of pattern after page param placeholder) must be same, and
     * - scheme, host, and path must be same, and
     * - query params, except that for page number, must be same in order and value, and
     * - query value must be a plain number.
     *
     * @param url the URL to evalutate
     */
    @Override
    public boolean isPagingUrl(String url) {
        // Both url and pattern must have the same suffix, if available.
        if (mSuffixLen != 0 && !url.endsWith(mSuffix)) return false;

        final int suffixStart = url.length() - mSuffixLen;

        // The url matches the pattern only when:
        //   1. has same prefix (scheme, host, path)
        //   2. has same query params with same value (except page number query) in the same
        //      order.
        // Examples:
        // If page pattern is http://foo.com/a/b?queryA=v1&queryB=[*!]&queryC=v3
        // Returns true for:
        //   - http://foo.com/a/b/?queryA=v1&queryC=v3
        //   - http://foo.com/a/b/?queryA=v1&queyrB=4&queryC=v3
        // Otherwise, returns false.
        //
        // If page pattern is http://foo.com/a/b?page=[*!]&query=a
        // Returns true for:
        //   - http://foo.com/a/b?query=a
        //   - http://foo.com/a/b?page=2&query=a
        // Otherwise, returns false.
        //
        // If page pattern is http://foo.com/a/b?page=[*!]
        // Returns true for:
        //   - http://foo.com/a/b/
        //   - http://foo.com/a/b.html
        //   - http://foo.com/a/b.htm
        //   - http://foo.com/a/b?page=2
        // Otherwise, returns false.

        // Both url and pattern must have the same prefix.
        if (!url.startsWith(mPrefix)) return false;

        // If the url doesn't have page number query, it is fine.
        if (mPlaceholderSegmentStart == suffixStart) return true;

        // If the only difference in the page param between url and pattern is "/", ".htm" or
        // ".html", it is fine.
        String diffPart = url.substring(mPlaceholderSegmentStart, suffixStart).toLowerCase();
        if (sSlashOrHtmExtRegExp == null) {
            sSlashOrHtmExtRegExp = RegExp.compile("^\\/|(.html?)$", "i");
        }
        if (sSlashOrHtmExtRegExp.test(diffPart)) return true;

        // Both url and pattern must have the same query name.
        if (!url.regionMatches(mPlaceholderSegmentStart, mUrlStr, mPlaceholderSegmentStart,
                mPlaceholderStart - mPlaceholderSegmentStart)) {
            return false;
        }

        return PageParameterDetector.isPlainNumber(url.substring(mPlaceholderStart, suffixStart));
    }

    private QueryParamPagePattern(ParsedUrl url, String queryName, String queryValue)
            throws IllegalArgumentException {
        if (queryName.isEmpty()) throw new IllegalArgumentException("Empty query name");
        if (queryValue.isEmpty()) throw new IllegalArgumentException("Empty query value");
        if (!StringUtil.isStringAllDigits(queryValue)) {
            throw new IllegalArgumentException("Query value has non-digits: " + queryValue);
        }
        if (PageParameterDetector.isPageParamNameBad(queryName)) {
            throw new IllegalArgumentException("Query name is bad page param name: " + queryName);
        }

        int value = StringUtil.toNumber(queryValue);
        if (value < 0) {
            throw new IllegalArgumentException("Query value is an invalid number: " + queryValue);
        }

        String pattern = url.replaceQueryValue(queryName, queryValue,
                PageParameterDetector.PAGE_PARAM_PLACEHOLDER);
        mUrl = ParsedUrl.create(pattern);
        if (mUrl == null) throw new IllegalArgumentException("Invalid URL: " + pattern);
        mUrlStr = pattern;
        mPageNumber = value;
        mPlaceholderStart = pattern.indexOf(PageParameterDetector.PAGE_PARAM_PLACEHOLDER);
        mQueryStart = mUrlStr.lastIndexOf('?', mPlaceholderStart - 1);
        mPlaceholderSegmentStart = mUrlStr.lastIndexOf('&', mPlaceholderStart - 1);
        if (mPlaceholderSegmentStart == -1) {  // Page param is the first query.
            mPlaceholderSegmentStart = mQueryStart;
        }
        mPrefix = mUrlStr.substring(0, mPlaceholderSegmentStart);
        // Determine suffix, if available.
        final int urlLen = mUrlStr.length();
        mSuffixLen = urlLen - mPlaceholderStart - PageParameterDetector.PAGE_PARAM_PLACEHOLDER_LEN;
        if (mSuffixLen != 0) {
            mSuffix = mUrlStr.substring(urlLen - mSuffixLen + 1); // +1 to exclude '&' or '?'.
        }
    }

}
