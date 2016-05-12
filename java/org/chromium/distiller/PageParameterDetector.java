// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;
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
 * Example: if the original URL is "http://www.foo.com/a/b-3.html", the page pattern is
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
    // The max number of paging documents for a document, beyond which the document is ignored and
    // pagination detection is aborted.
    // The original heuristic has this at 50, but it fails one of the URLs in the
    // multi-page-golden-data.sstable dataset: http://m.worldstarhiphop.com/apple/index.php?pg=13,
    // where there're always 100 pagination outlinks available regardless of which page you're
    // looking at.
    static final int MAX_PAGING_DOCS = 100;

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
         * @param url the URL to evaluate
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

    /**
     * Keeps track of the detection state:
     * - best PageParamInfo detected so far
     * - if multiple page patterns have been found.
     */
    private static class DetectionState {
        PageParamInfo mBestPageParamInfo = null;
        boolean mMultiPagePatterns = false;

        DetectionState() {
        }

        DetectionState(PageParamInfo pageParamInfo) {
            mBestPageParamInfo = pageParamInfo;
        }

        boolean isEmpty() {
            return mBestPageParamInfo == null;
        }

        boolean hasMultiPagePatterns() {
            return mMultiPagePatterns;
        }

        void update(DetectionState state) {
            mBestPageParamInfo = state.mBestPageParamInfo;
            mMultiPagePatterns = state.mMultiPagePatterns;
        }

        void compareAndUpdate(DetectionState state) {
            if (isEmpty()) {
                update(state);
                return;
            }

            // Compare both PageParamInfo's.
            int ret = mBestPageParamInfo.compareTo(state.mBestPageParamInfo);
            if (ret == -1) {  // The formal one is better.
                update(state);
            } else if (ret == 0) {  // Can't decide which one is better.
                mMultiPagePatterns = true;
            }
        }
    }

    // All the known bad page param names.
    private static Set<String> sBadPageParamNames = null;

    /**
     * Entry point for PageParameterDetector.
     * Creates a PageParamInfo based on outlinks and numeric text around them.
     *
     * @return PageParamInfo (see PageParamInfo.java), always.  If no page parameter is detected or
     * determined to be best, its mType is PageParamInfo.Type.UNSET.
     *
     * @param adjacentNumbersGroups all numeric content parsed from the document, grouped by their
     * adjacency and monotonicity (see MonotonicPageInfosGroups)
     * @param docUrl the current document URL that was parsed
     */
    public static PageParamInfo detect(MonotonicPageInfosGroups adjacentNumbersGroups,
            String docUrl) {
        ParsedUrl parsedDocUrl = ParsedUrl.create(docUrl);
        if (parsedDocUrl == null) return new PageParamInfo();  // Invalid document URL.
        parsedDocUrl.setUsername("");
        parsedDocUrl.setPassword("");

        DetectionState detectionState = new DetectionState();
        for (MonotonicPageInfosGroups.Group group : adjacentNumbersGroups.getGroups()) {
            if (group.mList.size() < 2) continue;

            DetectionState state = detectPageParamForMonotonicNumbers(group.mList,
                    group.mDeltaSign < 0, parsedDocUrl,
                    detectionState.isEmpty() ? "" : detectionState.mBestPageParamInfo.mPagePattern);
            if (state != null) detectionState.compareAndUpdate(state);
        }  // for all adjacentNumbersGroups.

        if (detectionState.isEmpty()) return new PageParamInfo();

        // For now, if there're multiple page patterns, we take the first one.
        // If this doesn't work for most sites, we might have to return nothing.
        if (detectionState.hasMultiPagePatterns() &&
                LogUtil.isLoggable(LogUtil.DEBUG_LEVEL_PAGING_INFO)) {
            LogUtil.logToConsole("Detected multiple page patterns");
        }

        final PageParamInfo bestPageParamInfo = detectionState.mBestPageParamInfo;
        bestPageParamInfo.determineNextPagingUrl(docUrl);
        return bestPageParamInfo;
    }

    /**
     * Detects the page parameter info for the given list of monotonic numbers.
     *
     * @return a populated detection state with the best PageParamInfo detected so far, null if none
     * is detected.
     *
     * @param monotonicNumbers list of numeric values which are either ascending or descending;
     * a descending list will be reversed and list elements could be mutated here.
     * @param isDescending true for a descending monotonicNumbers
     * @param parsedDocUrl ParsedUrl of the current document URL that was parsed
     * @param acceptedPagePattern the best accepted URL pattern if available so far, which contains
     * a PAGE_PARAM_PLACEHOLDER to replace the page parameter, and yields the best PageParamInfo.
     */
    private static DetectionState detectPageParamForMonotonicNumbers(
            List<PageParamInfo.PageInfo> monotonicNumbers, boolean isDescending,
            ParsedUrl parsedDocUrl, String acceptedPagePattern) {
        // Count number of outlinks.
        int outlinks = 0;
        for (PageParamInfo.PageInfo pageInfo : monotonicNumbers) {
            if (!pageInfo.mUrl.isEmpty()) outlinks++;
        }
        if (outlinks == 0) return null;

        if (isDescending) Collections.reverse(monotonicNumbers);

        // Some documents only have two partial pages, where each has a digital outlink to the
        // other.  But we need at least 2 URLs to extract page parameters.  To handle this case, use
        // current doc URL as the URL for the original plain text number.
        // Note we only do this when the known digital link's page number is 1 or 2.
        if (monotonicNumbers.size() == 2 && outlinks == 1 &&
                monotonicNumbers.get(0).mPageNum == 1 && monotonicNumbers.get(1).mPageNum == 2) {
            if (monotonicNumbers.get(0).mUrl.isEmpty()) {
                monotonicNumbers.set(0, new PageParamInfo.PageInfo(1, parsedDocUrl.toString()));
            } else {
                monotonicNumbers.set(1, new PageParamInfo.PageInfo(2, parsedDocUrl.toString()));
            }
            outlinks++;  // Increment to include current document URL.
        }

        // Now, extract the the page parameter.
        if (outlinks >= 2) {
            return extractPageParam(monotonicNumbers, parsedDocUrl, acceptedPagePattern);
        }

        return null;
    }

    /**
     * Extracts page parameter from given list of ascending PageParamInfo.PageInfo's.
     * From each PageInfo.mURL, detects the page parameter, and formulates the URL pattern which
     * contains a PAGE_PARAM_PLACEHOLDER to replace the page parameter.
     * For each URL pattern, validates it, compares the detected PageParamInfo with previously
     * detected one, and updates the better one in mPageParamInfo.
     *
     * @return a populated detection state with the best PageParamInfo detected among all page
     * patterns, null if none is detected.
     *
     * @param ascendingNumbers list of PageParamInfo.PageInfo's with ascending mPageNum's.
     * @param parsedDocUrl ParsedUrl of the current document URL that was parsed
     * @param acceptedPagePattern the best accepted URL pattern if available so far
    */
    private static DetectionState extractPageParam(List<PageParamInfo.PageInfo> ascendingNumbers,
            ParsedUrl parsedDocUrl, String acceptedPagePattern) {
        // Eliminate calendar date links.
        if (areCalendarDates(ascendingNumbers)) return null;

        String firstPageUrl = "";
        PageCandidatesMap pageCandidates = new PageCandidatesMap();
        // An array of ParsedUrl objects for URLs in ascendingNumbers' PageInfo's.
        ParsedUrl[] parsedUrls = new ParsedUrl[ascendingNumbers.size()];

        // First, try query components of URLs, looking out for first page URL.
        for (int i = 0; i < ascendingNumbers.size(); i++) {
            final PageParamInfo.PageInfo page = ascendingNumbers.get(i);
            if (!page.mUrl.isEmpty()) {
                ParsedUrl url = ParsedUrl.create(page.mUrl);
                parsedUrls[i] = url;
                if (url == null) continue;
                url.setUsername("");
                url.setPassword("");
                extractPageParamCandidatesFromQuery(url, page.mPageNum, i, pageCandidates);
                if (page.mPageNum == 1) firstPageUrl = page.mUrl;
            }
        }

        // If query components yield nothing, try paths of URLs.
        if (pageCandidates.map.isEmpty()) {
            for (int i = 0; i < ascendingNumbers.size(); i++) {
                final PageParamInfo.PageInfo page = ascendingNumbers.get(i);
                if (parsedUrls[i] != null) {
                    extractPageParamCandidatesFromPath(parsedUrls[i], page.mPageNum, i,
                            pageCandidates);
                }
            }
        }

        // Determine which URL page pattern is valid with a valid, and the best, PageParamInfo.
        DetectionState state = new DetectionState();
        for (Map.Entry<String, PageCandidatesMap.Info> entry : pageCandidates.map.entrySet()) {
            final String patternStr = entry.getKey();
            final PageCandidatesMap.Info info = entry.getValue();
            if (patternStr.equals(acceptedPagePattern) || info.mLinks.size() > MAX_PAGING_DOCS ||
                    !info.mPattern.isValidFor(parsedDocUrl)) {
                continue;
            }

            PageParamInfo pageParamInfo = PageParamInfo.evaluate(info.mPattern, info.mLinks,
                    ascendingNumbers, firstPageUrl);
            if (pageParamInfo == null) continue;

            // If feasible, insert current document URL as first page.
            // Otherwise, we enhance the heuristic: if current document URL fits the paging pattern
            // of the potential pagination URLs, consider it as first page too.
            final String docUrl = parsedDocUrl.getCleanHref();
            if (pageParamInfo.canInsertFirstPage(docUrl, ascendingNumbers)) {
                pageParamInfo.insertFirstPage(docUrl);
            } else if (info.mPattern.isPagingUrl(docUrl)) {
                final PageParamInfo.PageInfo firstPage = pageParamInfo.mAllPageInfo.get(0);
                if (firstPage.mPageNum == 2 && !firstPage.mUrl.equals(docUrl) &&
                        docUrl.length() < firstPage.mUrl.length()) {
                    pageParamInfo.insertFirstPage(docUrl);
                }
            }

            state.compareAndUpdate(new DetectionState(pageParamInfo));
        }  // for each URL pattern in pageCandidates.

        return state.isEmpty() ? null : state;
    }

    /**
     * Returns true if given list of page numbers are calendar dates.
     */
    private static boolean areCalendarDates(List<PageParamInfo.PageInfo> ascendingNumbers) {
        int possibleDateNum = 0;  // Match consecutive increasing numbers starting from 1.
        for (PageParamInfo.PageInfo page : ascendingNumbers) {
            if (page.mPageNum == possibleDateNum + 1) possibleDateNum++;
        }
        return possibleDateNum >= 28 && possibleDateNum <= 31;
    }

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

        for (int i = 0; i < queryParams.length; i++) {
            String[] nameValue = queryParams[i];
            PagePattern pattern = QueryParamPagePattern.create(url, i == 0, nameValue[0],
                    nameValue[1]);
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
        // PAGE_PARAM_PLACEHOLDER to formulate the page pattern, add it as page candidate.
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
     * Returns true if given name is blacklisted as a known bad page param name.
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
