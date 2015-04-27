// Copyright 2015 The Chromium Authors. All rights reserved.
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
 *   anchor text of those outlinks is numeric.
 *
 * Definitions:
 * A paging document is one of the partial pages.
 * "digital" means the text contains only digits.
 * A page pattern is a paging URL whose page parameter value is replaced with a place holder
 * (PAGE_PARAM_PLACEHOLDER).
 * Example: if the original url is "http://www.foo.com/a/b-3.html", the page pattern is
 * "http://www.foo.com/a/b-[*!].html".
 *
 * This class extracts the page parameter from a document's outlinks.
 * The basic idea:
 *   #1. Collect groups of adjacent plain text numbers and outlinks with digital anchor text.
 *   #2. For each group, determine the relationship between digital anchor texts and digital parts
 *       (either a query value or a path component) in URL.  If one part of a URL is always a linear
 *        map from its digital anchor text, we guess the part is the page parameter of the URL.
 *
 * As an example, consider a document http://a/b?c=1&p=10, which contains the following digital
 * outlinks:
 *   <a href=http://a/b?c=1&p=20>3</a>
 *   <a href=http://a/b?c=1&p=30>4</a>
 *   <a href=http://a/b?c=1&p=40>5</a>
 * This class finds that the "p" parameter is always equal to "anchor text" * 10 - 10, and so
 * guesses it is the page parameter.  The associated page pattern is http://a/b?c=1&p=[*!].
 */
public class PageParameterDetector {
    static final String PAGE_PARAM_PLACEHOLDER = "[*!]";
    static final int PAGE_PARAM_PLACEHOLDER_LEN = PAGE_PARAM_PLACEHOLDER.length();

    /**
     * The interface that page pattern handlers must implement to detect page parameter from
     * potential pagination URLs.
     */
    interface PagePattern {
       /**
        * Returns the string of the URL page pattern.
        */
        String toString();

       /**
        * Returns the page number extracted from the URL during creation of object that implements
        * this interface.
        */
        int getPageNumber();
        
        /**
         * Validates this page pattern according to the current document URL through a pipeline of
         * rules.
         *
         * Returns true if page pattern is valid.
         *
         * @param docUrl the current document URL
         */
        boolean isValidFor(ParsedUrl docUrl);

        /**
         * Returns true if a URL matches this page pattern based on a pipeline of rules.
         *
         * @param url the URL to evalutate
         */
        boolean isPagingUrl(String url);
    }

    /**
     * Stores a map of URL pattern to its associated list of PageLinkInfo's.
     */
    private static class PageCandidatesMap {
        private static class Info {
            private final PagePattern mPattern;
            private final List<PageLinkInfo> mLinks;

            Info(PagePattern pattern, PageLinkInfo link) {
                mPattern = pattern;
                mLinks = new ArrayList<PageLinkInfo>();
                mLinks.add(link);
            }
        }

        private final Map<String, Info> map = new HashMap<String, Info>();

        /**
         * Adds urlPattern with its PageLinkInfo into the map.  If the urlPattern already exists,
         * adds the link to the list of LinkInfo's.  Otherwise, creates a new map entry.
         */
        private void add(PagePattern pattern, PageLinkInfo link) {
            final String patternStr = pattern.toString();
            if (map.containsKey(patternStr)) {
                map.get(patternStr).mLinks.add(link);
            } else {
                map.put(patternStr, new Info(pattern, link));
            }
        }
    }

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
     * PageLinkInfo's, and hence 3 page parameter candidates.
     *
     * @param url ParsedUrl of the URL to process
     * @param pageNum the page number as represented in original plain text
     * @param posInAscendingNumbers position of this page number in the list of ascending numbers
     * @param pageCandidates the map of URL pattern to its associated list of PageLinkInfo's
     */
    private static void extractPageParamCandidatesFromQuery(ParsedUrl url, int pageNum,
            int posInAscendingNumbers, PageCandidatesMap pageCandidates) {
        String[][] queryParams = url.getQueryParams();
        if (queryParams.length == 0) return;  // No query.

        for (String[] nameValue : queryParams) {
            PagePattern pattern = QueryParamPagePattern.create(url, nameValue[0], nameValue[1]);
            if (pattern != null) {
                pageCandidates.add(pattern,
                        new PageLinkInfo(pageNum, pattern.getPageNumber(), posInAscendingNumbers));
            }
        }
    }

    private static RegExp sDigitsRegExp = null;  // Match at least 1 digit.

    /**
     * Extracts page parameter candidates from the path part of given URL (without query components)
     * and adds the associated links into pageCandidates which is keyed by page pattern.
     *
     * A page parameter candidate is one where a path component contains consecutive digits which
     * can be converted to a plain number (>= 0).
     * E.g. a URL path with 3 path components that contain plain numbers will generate 3 URL page
     * patterns with 3 PageLinkInfo's, and hence 3 page parameter candidates.
     *
     * @param url ParsedUrl of the URL to process
     * @param pageNum the page number as represented in original plain text
     * @param posInAscendingNumbers position of this page number in the list of ascending numbers
     * @param pageCandidates the map of URL pattern to its associated list of PageLinkInfo's
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
            PagePattern pattern = PathComponentPagePattern.create(url, pathStart, matchStart,
                    matchEnd);
            if (pattern != null) {
                pageCandidates.add(pattern,
                        new PageLinkInfo(pageNum, pattern.getPageNumber(), posInAscendingNumbers));
            }
        }  // while there're matches
    }

    /**
     * Returns true if given name is backlisted as a known bad page param name.
     */
    static boolean isPageParamNameBad(String name) {
        initBadPageParamNames();
        return sBadPageParamNames.contains(name.toLowerCase());
    }

    /**
     * Returns true if given string can be converted to a number >= 0.
     */
    static boolean isPlainNumber(String str) {
        return StringUtil.toNumber(str) >= 0;
    }

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
    }

}
