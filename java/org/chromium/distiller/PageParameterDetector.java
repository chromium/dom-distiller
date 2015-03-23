// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Background:
 *   The long article/news/forum thread/blog document may be partitioned into several partial pages
 *   by webmaster.  Each partial page has outlinks pointing to the adjacent partial pages.  The
 *   anchor text of those outlinks is numeric.  Meanwhile, there may be a page which contains the
 *   whole content, called "single page".
 *
 * Definitions:
 * A single page document is a document that contains the whole content.
 * A paging document is one of the partial pages.
 * "digital" means the text contains only digits.
 * A page pattern is a paging URL whose page parameter value is replaced with a place holder
 * (PAGE_PARAM_PLACEHOLDER).
 * Example: if the original url is "http: *www.foo.com/a/b-3.html", the page pattern is
 * "http: *www.foo.com/a/b-[*!].html".
 *
 * This class extracts the page parameter from a document's outlinks.
 * The basic idea:
 *   #1. Collect groups of adjacent plain text numbers and outlinks with digital anchor text.
 *   #2. For each group, determine the relationship between digital anchor texts and digital parts
 *       (either a query value or a path component) in URL.  If one part of a URL is always a linear
 *        map from its digital anchor text, we guess the part is the page parameter of the URL.
 *
 * As an example, consider a document http: *a/b?c=1&p=10, which contains the following digital
 * outlinks:
 *   <a href=http: *a/b?c=1&p=20>3</a>
 *   <a href=http: *a/b?c=1&p=30>4</a>
 *   <a href=http: *a/b?c=1&p=40>5</a>
 *   <a href=http: *a/b?c=1&p=all>single page</a>
 * This class finds that the "p" parameter is always equal to "anchor text" * 10 - 10, and so
 * guesses it is the page parameter.  The associated page pattern is http: *a/b?c=1&p=[*!].
 * Then, this class extracts the single page based on page parameter info.  The single page url is
 * http: *a/b?c=1&p=all.
 */
public class PageParameterDetector {
    private static final String PAGE_PARAM_PLACEHOLDER = "[*!]";

    /**
     * Stores information about the link (anchor) after the page parameter is detected:
     * - the page number (as represented by the original plain text) for the link
     * - the original page parameter numeric component in the URL (this component would be replaced
     *   by PAGE_PARAM_PLACEHOLDER in the URL pattern)
     * - the position of this link in the list of ascending numbers.
     */
    static class LinkInfo {
        private int mPageNum;
        private int mPageParamValue;
        private int mPosInAscendingList;

        LinkInfo(int pageNum, int pageParamValue, int posInAscendingList) {
            mPageNum = pageNum;
            mPageParamValue = pageParamValue;
            mPosInAscendingList = posInAscendingList;
        }
    }  // LinkInfo

    /**
     * Stores a map of URL pattern to its associated list of LinkInfo's.
     */
    private static class PageCandidatesMap {
        private final Map<String, List<LinkInfo>> map = new HashMap<String, List<LinkInfo>>();

        /**
         * Adds urlPattern with its LinkInfo into the map.  If the urlPattern already exists, adds
         * the link to the list of LinkInfo's.  Otherwise, creates a new map entry.
         */
        private void add(String urlPattern, LinkInfo link) {
            if (map.containsKey(urlPattern)) {
                map.get(urlPattern).add(link);
            } else {
                List<LinkInfo> links = new ArrayList<LinkInfo>();
                links.add(link);
                map.put(urlPattern, links);
            }
        }

    }  // PageCandidatesMap

    // All the known bad page param names.
    private static Set<String> sBadPageParamNames = null;

    /**
     * Extracts page parameter candidates from the query part of given URL and adds the associated
     * links into pageCandidates which is keyed by page pattern.
     *
     * A page parameter candidate is one where:
     * - the name of a query name-value component is not one of sBadPageParamNames, and
     * - the value of the query component is a plain number (>= 0).
     * E.g. a URL query with 3 plain number query values will generate 3 URL page patterns with 3
     * LinkInfo's, and hence 3 page parameter candidates.
     *
     * @param url ParsedUrl of the URL to process
     * @param pageNum the page number as represented in original plain text
     * @param posInAscendingNumbers position of this page number in the list of ascending numbers
     * @param pageCandidates the map of URL pattern to its associated list of LinkInfo's
     */
    private static void extractPageParamCandidatesFromQuery(ParsedUrl url, int pageNum,
            int posInAscendingNumbers, PageCandidatesMap pageCandidates) {
        String[][] queryParams = url.getQueryParams();
        if (queryParams.length == 0) return;  // No query.

        for (String[] nameValue : queryParams) {
            final String queryName = nameValue[0];
            final String queryValue = nameValue[1];
            if (!queryName.isEmpty() && !queryValue.isEmpty() &&
                    StringUtil.isStringAllDigits(queryValue) && !isPageParamNameBad(queryName)) {
                int value = StringUtil.toNumber(queryValue);
                if (value >= 0) {
                    pageCandidates.add(
                            url.replaceQueryValue(queryName, queryValue, PAGE_PARAM_PLACEHOLDER),
                            new LinkInfo(pageNum, value, posInAscendingNumbers));
                }
            }
        }
    }  // extractPageParamCandidatesFromQuery

    private static RegExp sDigitsRegExp = null;  // Match at least 1 digit.

    /**
     * Extracts page parameter candidates from the path part of given URL (without query components)
     * and adds the associated links into pageCandidates which is keyed by page pattern.
     *
     * A page parameter candidate is one where a path component contains consecutive digits which
     * can be converted to a plain number (>= 0).
     * E.g. a URL path with 3 path components that contain plain numbers will generate 3 URL page
     * patterns with 3 LinkInfo's, and hence 3 page parameter candidates.
     *
     * @param url ParsedUrl of the URL to process
     * @param pageNum the page number as represented in original plain text
     * @param posInAscendingNumbers position of this page number in the list of ascending numbers
     * @param pageCandidates the map of URL pattern to its associated list of LinkInfo's
     */

    private static void extractPageParamCandidatesFromPath(ParsedUrl url, int pageNum,
            int posInAscendingNumbers, PageCandidatesMap pageCandidates) {
        String path = url.getTrimmedPath();
        if (path.isEmpty() || !StringUtil.containsDigit(path)) return;

        // Extract digits (either one or consecutive) from path, replace the digit(s) with
        // PAGE_PARAM_PLACEHOLDER to fomulate the page pattern, add it as page candidate.
        final String urlStr = url.toString();
        final int pathStart = url.getOrigin().length();
        if (sDigitsRegExp == null) sDigitsRegExp = RegExp.compile("(\\d+)", "gi");
        sDigitsRegExp.setLastIndex(pathStart);
        while (true) {
            MatchResult match = sDigitsRegExp.exec(urlStr);
            if (match == null) break;

            final int matchEnd = sDigitsRegExp.getLastIndex();
            final int matchStart = matchEnd - match.getGroup(1).length();

            if (isLastNumericPathComponentBad(urlStr, pathStart, matchStart, matchEnd)) continue;

            int value = StringUtil.toNumber(urlStr.substring(matchStart, matchEnd));
            if (value >= 0) {
                pageCandidates.add(urlStr.substring(0, matchStart) + PAGE_PARAM_PLACEHOLDER +
                        urlStr.substring(matchEnd),
                        new LinkInfo(pageNum, value, posInAscendingNumbers));
            }
        }  // while there're matches
    }  // extractPageParamCandidatesFromPath

    /**
     * Returns true if given name is backlisted as a known bad page param name.
     */
    private static boolean isPageParamNameBad(String name) {
        initBadPageParamNames();
        return sBadPageParamNames.contains(name.toLowerCase());
    }  // isPageParamNameBad

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
    static boolean isLastNumericPathComponentBad(String urlStr, int pathStart,
            int digitStart, int digitEnd) {
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
                        isPageParamNameBad(prevPathComponent.getGroup(1))) {
                    return true;
                }
            }  // last numeric path component
        }

        return false;
    }  // isLastNumericPathComponentBad

    /**
     * If sBadPageParamNames is null, initialize it with all the known bad page param names, in
     * alphabetical order.
     */
    private static void initBadPageParamNames() {
        if (sBadPageParamNames != null) return;

        sBadPageParamNames = new HashSet<String>();
        sBadPageParamNames.add("baixar-gratis");
        sBadPageParamNames.add("category");
        sBadPageParamNames.add("content");
        sBadPageParamNames.add("day");
        sBadPageParamNames.add("date");
        sBadPageParamNames.add("definition");
        sBadPageParamNames.add("etiket");
        sBadPageParamNames.add("film-seyret");
        sBadPageParamNames.add("key");
        sBadPageParamNames.add("keys");
        sBadPageParamNames.add("keyword");
        sBadPageParamNames.add("label");
        sBadPageParamNames.add("news");
        sBadPageParamNames.add("q");
        sBadPageParamNames.add("query");
        sBadPageParamNames.add("rating");
        sBadPageParamNames.add("s");
        sBadPageParamNames.add("search");
        sBadPageParamNames.add("seasons");
        sBadPageParamNames.add("search_keyword");
        sBadPageParamNames.add("search_query");
        sBadPageParamNames.add("sortby");
        sBadPageParamNames.add("subscriptions");
        sBadPageParamNames.add("tag");
        sBadPageParamNames.add("tags");
        sBadPageParamNames.add("video");
        sBadPageParamNames.add("videos");
        sBadPageParamNames.add("w");
        sBadPageParamNames.add("wiki");
    }  // initBadPageParamNames

}
