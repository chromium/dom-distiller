// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * This class detects the page parameter in the path of a potential pagination URL.  If detected,
 * it replaces the page param value with PageParameterDetector.PAGE_PARAM_PLACEHOLDER, then creates
 * and returns a new object.  This object can then be accessed via PageParameterDetector.PagePattern
 * interface to:
 * - validate the generated URL page pattern against the document URL
 * - determine if a URL is a paging URL based on the page pattern.
 * Example: if the original url is "http://www.foo.com/a/b-3.html", the page pattern is
 * "http://www.foo.com/a/b-[*!].html". (See comments at top of PageParameterDetector.java).
 */
public class PathComponentPagePattern implements PageParameterDetector.PagePattern {
    private final ParsedUrl mUrl;
    private final int mPageNumber;
    private final int mPlaceholderStart;
    private final String mUrlStr;
    // Start position of path component containing placeholder.
    private int mPlaceholderSegmentStart;
    // Page param path component in list of path components.
    private int mParamIndex = -1;
    private final String mPrefix; // The part of the page pattern before the placeholder.
    private String mSuffix = "";  // The part of the page pattern after the placeholder.

    /**
     * Returns a new PagePattern if url is valid and contains PAGE_PARAM_PLACEHOLDER.
     */
    static PageParameterDetector.PagePattern create(ParsedUrl url, int pathStart, int digitStart,
            int digitEnd) {
        try {
            return new PathComponentPagePattern(url, pathStart, digitStart, digitEnd);
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
     * Returns true if pattern and URL are sufficiently similar and the pattern's components are not
     * calendar digits.
     *
     * @param docUrl the current document URL
     */
    @Override
    public boolean isValidFor(ParsedUrl docUrl) {
        final int urlPathComponentsLen = docUrl.getPathComponents().length;
        final int patternPathComponentsLen = mUrl.getPathComponents().length;

        // Both the pattern and doc URL must have the similar path.
        if (urlPathComponentsLen > patternPathComponentsLen) return false;

        // If both doc URL and page pattern have only 1 component, their common prefix+suffix must
        // be at least half of the entire component in doc URL, e.g doc URL is
        // "foo.com/foo-bar-threads-132" and pattern is "foo.com/foo-bar-threads-132-[*!]".
        if (urlPathComponentsLen == 1 && patternPathComponentsLen == 1) {
            final String urlComponent = docUrl.getPathComponents()[0];
            final String patternComponent = mUrl.getPathComponents()[0];
            int commonPrefixLen = getLongestCommonPrefixLength(urlComponent, patternComponent);
            int commonSuffixLen = getLongestCommonSuffixLength(urlComponent, patternComponent,
                    commonPrefixLen);
            return (commonSuffixLen + commonPrefixLen) * 2 >= urlComponent.length();
        }

        if (!hasSamePathComponentsAs(docUrl)) return false;

        if (isCalendarPage()) return false;

        return true;
    }

    /**
     * Returns true if a URL matches this page pattern based on a pipeline of rules:
     * - suffix (part of pattern after page param placeholder) must be same, and
     * - different set of rules depending on if page param is at start of path component or not.
     *
     * @param url the URL to evalutate
     */
    @Override
    public boolean isPagingUrl(String url) {
        // Both url and pattern must have the same suffix, if available.
        if (!mSuffix.isEmpty() && !url.endsWith(mSuffix)) return false;

        return atStartOfPathComponent() ? isPagingUrlForStartOfPathComponent(url) :
                isPagingUrlForNotStartOfPathComponent(url);
    }

    private PathComponentPagePattern(ParsedUrl url, int pathStart, int digitStart, int digitEnd)
            throws IllegalArgumentException {
        final String urlStr = url.toString();
        if (isLastNumericPathComponentBad(urlStr, pathStart, digitStart, digitEnd)) {
            throw new IllegalArgumentException("Bad last numeric path component");
        }

        String valueStr = urlStr.substring(digitStart, digitEnd);
        int value = StringUtil.toNumber(valueStr);
        if (value < 0) {
            throw new IllegalArgumentException("Value in path component is an invalid number: " +
                    valueStr);
        }

        String pattern = urlStr.substring(0, digitStart) +
                PageParameterDetector.PAGE_PARAM_PLACEHOLDER + urlStr.substring(digitEnd);
        mUrl = ParsedUrl.create(pattern);
        if (mUrl == null) throw new IllegalArgumentException("Invalid URL: " + pattern);
        mUrlStr = pattern;
        mPageNumber = value;
        mPlaceholderStart = digitStart;
        mPlaceholderSegmentStart = mUrlStr.lastIndexOf('/', mPlaceholderStart);
        determineParamIndex();
        mPrefix = mUrlStr.substring(0, mPlaceholderSegmentStart);
        // Determine suffix, if available.
        final int urlLen = mUrlStr.length();
        int suffixLen = urlLen - mPlaceholderStart -
                PageParameterDetector.PAGE_PARAM_PLACEHOLDER_LEN;
        if (suffixLen != 0) mSuffix = mUrlStr.substring(urlLen - suffixLen);
    }

    private boolean atStartOfPathComponent() {
        return mUrlStr.charAt(mPlaceholderStart - 1) == '/';
    }

    private void determineParamIndex() {
        final String[] pathComponents = mUrl.getPathComponents();
        for (mParamIndex = 0; mParamIndex < pathComponents.length; mParamIndex++) {
            if (pathComponents[mParamIndex].contains(
                    PageParameterDetector.PAGE_PARAM_PLACEHOLDER)) {
                break;
            }
        }
    }

    /**
     * Returns true if, except for the path component containing the page param, the other path
     * components of doc URL are the same as pattern's.  But pattern may have more components, e.g.:
     * - doc URL is /thread/12, pattern is /thread/12/page/[*!]
     *   returns true because "thread" and "12" in doc URL match those in pattern
     * - doc URL is /thread/12/foo, pattern is /thread/12/page/[*!]/foo
     *   returns false because "foo" in doc URL doesn't match "page" in pattern whose page param
         path component comes after.
     * - doc URL is /thread/12/foo, pattern is /thread/12/[*!]/foo
     *   returns true because "foo" in doc URL would match "foo" in pattern whose page param path
     *   component is skipped when matching.
     */
    private boolean hasSamePathComponentsAs(ParsedUrl docUrl) {
        final String[] urlComponents = docUrl.getPathComponents();
        final String[] patternComponents = mUrl.getPathComponents();
        boolean passedParamComponent = false;
        for (int i = 0, j = 0; i < urlComponents.length && j < patternComponents.length; i++, j++) {
            if (i == mParamIndex && !passedParamComponent) {
                passedParamComponent = true;
                // Repeat current path component if doc URL has less components (as per comments
                // just above, doc URL may have less components).
                if (urlComponents.length < patternComponents.length) i--;
                continue;
            }
            if (!urlComponents[i].equalsIgnoreCase(patternComponents[j])) return false;
        }

        return true;
    }

    /**
     * Returns true if pattern is for a calendar page, e.g. 2012/01/[*!], which would be a
     * false-positive.
     */
    private boolean isCalendarPage() {
        if (mParamIndex < 2) return false;

        // Only if param is the entire path component.  This handles some cases erroneously
        // considered false-positives e.g. first page is
        // http://www.politico.com/story/2014/07/barack-obama-immigration-legal-questions-109467.html,
        // and second page is
        // http://www.politico.com/story/2014/07/barack-obama-immigration-legal-questions-109467_Page2.html,
        // would be considered false-positives otherwise because of "2014" and "07".
        final String[] patternComponents = mUrl.getPathComponents();
        if (patternComponents[mParamIndex].length() !=
                PageParameterDetector.PAGE_PARAM_PLACEHOLDER_LEN) {
            return false;
        }

        int month = StringUtil.toNumber(patternComponents[mParamIndex - 1]);
        if (month > 0 && month <= 12) {
            int year = StringUtil.toNumber(patternComponents[mParamIndex - 2]);
            if (year > 1970 && year < 3000) return true;
        }

        return false;
    }

    private static int getLongestCommonPrefixLength(String str1, String str2) {
        if (str1.isEmpty() || str2.isEmpty()) return 0;

        int limit = Math.min(str1.length(), str2.length());
        int i = 0;
        for (; i < limit; i++) {
            if (str1.charAt(i) != str2.charAt(i)) break;
        }
        return i;
    }

    private static int getLongestCommonSuffixLength(String str1, String str2, int startIndex) {
        int commonSuffixLen = 0;
        for (int i = str1.length() - 1, j = str2.length() - 1;
             i > startIndex && j > startIndex; i--, j--, commonSuffixLen++) {
            if (str1.charAt(i) != str2.charAt(i)) break;
        }
        return commonSuffixLen;
    }

    /**
     * Returns true if url is a paging URL based on the page pattern where the page param is at the
     * start of a path component.
     * If the page pattern is www.foo.com/a/[*!]/abc.html, expected doc URL is:
     * - www.foo.com/a/2/abc.html
     * - www.foo.com/a/abc.html
     * - www.foo.com/abc.html.
     */
    private boolean isPagingUrlForStartOfPathComponent(String url) {
        final int urlLen = url.length();
        final int suffixLen = mSuffix.length();
        final int suffixStart = url.length() - suffixLen;

        int prevComponentPos = mUrl.getPath().lastIndexOf('/',
                // We're only looking in the path, so the reverse search should start at the index
                // excluding the url's origin.
                mPlaceholderSegmentStart - 1 - mUrl.getOrigin().length());
        if (prevComponentPos != -1) {
            // Now, add back the url's origin to the index of previous path component.
            prevComponentPos += mUrl.getOrigin().length();
            if (prevComponentPos + suffixLen == urlLen) {
                // The url doesn't have page number param and previous path component, like
                // www.foo.com/abc.html.
                return url.regionMatches(0, mUrlStr, 0, prevComponentPos);
            }
        }

        // If both url and pattern have the same prefix, url must have nothing else.
        if (url.startsWith(mPrefix)) {
            int acceptLen = mPlaceholderSegmentStart + suffixLen;
            // The url doesn't have page number parameter, like www.foo.com/a/abc.html.
            if (acceptLen == urlLen) return true;
            if (acceptLen > urlLen) return false;

            // While we are here, the url must have page number param, so the url must have a '/'
            // at the pattern's path component start position.
            if (url.charAt(mPlaceholderSegmentStart) != '/') return false;

            return PageParameterDetector.isPlainNumber(url.substring(mPlaceholderSegmentStart + 1,
                    suffixStart));
        }

        return false;
    }

    /**
     * Returns true if url is a paging URL based on the page pattern where the page param is not at
     * the start of a path component.
     * If the page pattern is www.foo.com/a/abc-[*!].html, expected doc URL is:
     * - www.foo.com/a/abc-2.html
     * - www.foo.com/a/abc.html.
     */
    private boolean isPagingUrlForNotStartOfPathComponent(String url) {
        final int urlLen = url.length();
        final int suffixStart = urlLen - mSuffix.length();

        // The page param path component of both url and pattern must have the same prefix.
        if (!url.startsWith(mPrefix)) return false;

        // Find the first different character in page param path component just before
        // placeholder or suffix, then check if it's acceptable.
        int firstDiffPos = mPlaceholderSegmentStart;
        int maxPos = Math.min(mPlaceholderStart, suffixStart);
        for (; firstDiffPos < maxPos; firstDiffPos++) {
            if (url.charAt(firstDiffPos) != mUrlStr.charAt(firstDiffPos)) break;
        }
        if (firstDiffPos == suffixStart) {  // First different character is the suffix.
            if (firstDiffPos + 1 == mPlaceholderStart &&
                    isPageParamSeparator(mUrlStr.charAt(firstDiffPos))) {
                return true;
            }
        } else if (firstDiffPos == mPlaceholderStart) {  // First different character is page param.
            if (PageParameterDetector.isPlainNumber(url.substring(firstDiffPos, suffixStart))) {
                return true;
            }
        }

        return false;
    }

    private static RegExp sExtRegExp = null;  // Match trailing .(s)htm(l).
    private static RegExp sLastPathComponentRegExp = null;  // Match last path component.

    /**
     * Returns true if:
     * - the digitStart to digitEnd of urlStr is the last path component, and
     * - the entire path component is numeric, and
     * - the previous path component is a bad page param name.
     * E.g. "www.foo.com/tag/2" will return true because of the above reasons and "tag" is a bad
     * page param.
     */
    static boolean isLastNumericPathComponentBad(String urlStr, int pathStart, int digitStart,
            int digitEnd) {
        if (urlStr.charAt(digitStart - 1) == '/' && // Digit is at start of path component.
                pathStart < digitStart - 1) { // Not the first path component.
            String postMatch = urlStr.substring(digitEnd).toLowerCase();
            // Checks that this is the last path component, and trailing characters, if available,
            // are (s)htm(l) extensions.
            if (sExtRegExp == null) sExtRegExp = RegExp.compile("(.s?html?)?$", "i");
            if (sExtRegExp.test(postMatch)) {
                // Entire component is numeric, get previous path component.
               if (sLastPathComponentRegExp == null) {
                   sLastPathComponentRegExp = RegExp.compile("([^/]*)\\/$", "i");
                }
                MatchResult prevPathComponent = sLastPathComponentRegExp.exec(
                        urlStr.substring(pathStart + 1, digitStart));
                if (prevPathComponent != null && prevPathComponent.getGroupCount() > 1 &&
                        PageParameterDetector.isPageParamNameBad(prevPathComponent.getGroup(1))) {
                    return true;
                }
            }  // last numeric path component
        }

        return false;
    }

    /**
     * Returns true if given character is one of '-', '_', ';', ','.
     */
    private static native boolean isPageParamSeparator(Character c) /*-{
        return /[-_;,]/.test(c);
    }-*/;

}
