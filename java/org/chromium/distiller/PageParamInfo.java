// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class stores information about the page parameter detected from potential pagination URLs
 * with numeric anchor text:
 * - type of page parameter detected
 * - URL pattern which contains a PageParameterDetector.PAGE_PARAM_PLACEHOLDER to replace the page
 *   parameter
 * - list of pagination URLs with their page numbers
 * - coefficient and delta values of the linear formula formed by the pagination URLs:
 *     pageParamValue = coefficient * pageNum + delta
 * - next paging URL.
 */
class PageParamInfo {
    private static final int MIN_LINKS_TO_JUSTIFY_LINEAR_MAP = 2;

    /**
     * Stores potential pagination info:
     * - page number represented as original plain text in document URL
     * - if the info is extracted from an anchor, its HRef.
     */
    static class PageInfo {
        int mPageNum;
        String mUrl;

        PageInfo(int pageNum, String url) {
            mPageNum = pageNum;
            mUrl = url;
        }

        @Override
        public String toString() {  // For debugging.
            return new String("pg" + mPageNum + ": " + mUrl);
        }
    }

    /**
     * Stores the coefficient and delta values of the linear formula:
     *   pageParamValue = coefficient * pageNum + delta.
     * See getLinearFormula() for details.
     */
    static class LinearFormula {
        final int mCoefficient;
        final int mDelta;

        LinearFormula(int coefficient, int delta) {
            mCoefficient = coefficient;
            mDelta = delta;
        }

        @Override
        public String toString() {  // For debugging.
            return new String("coefficient=" + mCoefficient + ", delta=" + mDelta);
        }
    }

    /**
     * Types of page parameter values in paging URLs.
     */
    static enum Type {
        UNSET,        // Initialized type to indicate empty PageParamInfo.
        PAGE_NUMBER,  // Value is a page number.
        UNKNOWN,      // None of the above.
    }

    /**
     * Class returned by getPageNumbersState() after it has checked if the given list of
     * PageLinkInfo's and PageInfo's are adjacent and consecutive, and if there's a gap in the list.
     */
    static class PageNumbersState {
        boolean mIsAdjacent = false;
        boolean mIsConsecutive = false;
        String mNextPagingUrl = "";
    }

    Type mType = Type.UNSET;
    String mPagePattern = "";
    List<PageInfo> mAllPageInfo;
    LinearFormula mFormula = null;
    String mNextPagingUrl = "";

    PageParamInfo() {
        mAllPageInfo = new ArrayList<PageInfo>();
    }

    /**
     * PageParamInfo initialized according to given parameters.
     *
     * @param type must not be Type.UNSET.
     * @param allPageInfo can be null.
     * @param formula can be null.
     * @param pagePattern must be non-empty string.
     * @param nextPagingUrl a non-empty string or "".
     */
    PageParamInfo(Type type, String pagePattern, List<PageInfo> allPageInfo, LinearFormula formula,
           String nextPagingUrl) {
        mType = type;
        mPagePattern = pagePattern;
        mAllPageInfo = allPageInfo;
        mFormula = formula;
        mNextPagingUrl = nextPagingUrl;
    }

    /**
     * Evaluates if the given list of PageLinkInfo's is a list of paging URLs:
     * - page numbers in list of PageLinkInfo's must be adjacent
     * - page numbers in list of ascending numbers must either
     *   - be consecutive and form a page number sequence, or
     *   - must construct a linear map with a linear formula: page_parameter = a * page_number + b
     * - if there's only 1 PageLinkInfo, the first ascending number must be page 1, first page URL
     *   must match page pattern, and the only outlink must be 2nd or 3rd page.
     *
     * Returns a populated PageParamInfo if evaluated true.  Otherwise, returns null.
     *
     * @param allLinkInfo the list of PageLinkInfo's to evaluate
     * @param pagePattern the URL pattern to use
     * @param ascendingNumbers list of PageInfo's with ascending mPageNum's
     * @param firstPageUrl the URL of the PageInfo with mPageNum=1
     */
    static PageParamInfo evaluate(PageParameterDetector.PagePattern pagePattern,
            List<PageLinkInfo> allLinkInfo, List<PageInfo> ascendingNumbers, String firstPageUrl) {
        if (allLinkInfo.size() >= MIN_LINKS_TO_JUSTIFY_LINEAR_MAP) {
            PageNumbersState state = getPageNumbersState(allLinkInfo, ascendingNumbers);
            if (!state.mIsAdjacent) return null;

            // Type.PAGE_NUMBER: ascending numbers must be consecutive and form a page number
            // sequence.
            if (!state.mIsConsecutive) return null;
            if (!isPageNumberSequence(ascendingNumbers, state)) return null;

            LinearFormula linearFormula = getLinearFormula(allLinkInfo);

            List<PageInfo> allPageInfo = new ArrayList<PageInfo>();
            for (PageLinkInfo link : allLinkInfo) {
                allPageInfo.add(new PageInfo(link.mPageNum,
                        ascendingNumbers.get(link.mPosInAscendingList).mUrl));
            }
            return new PageParamInfo(Type.PAGE_NUMBER, pagePattern.toString(), allPageInfo,
                    linearFormula, state.mNextPagingUrl);
        }

        // Most of news article have no more than 3 pages and the first page probably doesn't have
        // any page parameter.  If the first page URL matches the the page pattern, we treat it as
        // the first page of this pattern.
        if (allLinkInfo.size() == 1 && !firstPageUrl.isEmpty()) {
            final PageLinkInfo onlyLink = allLinkInfo.get(0);
            boolean secondPageIsOutlink = onlyLink.mPageNum == 2 &&
                    onlyLink.mPosInAscendingList == 1;
            boolean thirdPageIsOutlink = onlyLink.mPageNum == 3 &&
                    onlyLink.mPosInAscendingList == 2 &&
                    // onlyLink's pos is 2 (evaluated right before), so ascendingNumbers has >= 3
                    // elements; check if previous element is previous page.
                    ascendingNumbers.get(1).mPageNum == 2;
            // 1 PageLinkInfo means ascendingNumbers has >= 1 element.
            if (ascendingNumbers.get(0).mPageNum == 1 &&
                    (secondPageIsOutlink || thirdPageIsOutlink) &&
                    pagePattern.isPagingUrl(firstPageUrl)) {
                // Has valid PageParamInfo, create and populate it.
                int coefficient;
                int delta = onlyLink.mPageParamValue - onlyLink.mPageNum;
                if (delta == 0 || delta == 1) {
                    coefficient = 1;
                } else {
                    coefficient = onlyLink.mPageParamValue;
                    delta = 0;
                }
                List<PageInfo> allPageInfo = new ArrayList<PageInfo>();
                allPageInfo.add(new PageInfo(1, firstPageUrl));
                allPageInfo.add(new PageInfo(onlyLink.mPageNum,
                        ascendingNumbers.get(onlyLink.mPosInAscendingList).mUrl));
                return new PageParamInfo(Type.PAGE_NUMBER, pagePattern.toString(), allPageInfo,
                        new LinearFormula(coefficient, delta),
                        // If third page is outlink, the next page would be that 3rd page.
                        thirdPageIsOutlink ? allPageInfo.get(1).mUrl : "");
            }
        }

        return null;
    }

    /**
     * Evaluates which PageParamInfo is better based on the properties of PageParamInfo.
     * We prefer the one if the list of PageLinkInfo forms a linear formula, see getLinearFormula().
     *
     * Returns 1 if this is better, -1 if other is better and 0 if they are equal.
     *
    */
    int compareTo(PageParamInfo other) {
        // We prefer the one where the LinkInfo array forms a linear formula, see isLinearFormula.
        if (mFormula != null && other.mFormula == null) return 1;
        if (mFormula == null && other.mFormula != null) return -1;

        if (mType == other.mType) return 0;

        // For different page param types, we prefer PAGE_NUMBER.
        if (mType == Type.PAGE_NUMBER) return 1;
        if (other.mType == Type.PAGE_NUMBER) return -1;
        // Can't decide as they have unknown page type.
        return 0;
    }

    /**
     * @return true if the given URL, which is the current document URL, can be inserted as first
     * page.
     *
     * Often times, the first page of paginated content does not have a page parameter to identify
     * itself, so it is hard for us to cluster the first page into a paginated cluster.  However,
     * while parsing the first page, we do have some extra signals that can help decide if the
     * current page is the first page of its cluster.
     *
     * @param docUrl the current document URL that was parsed.
     * @param ascendingNumbers the list of PageInfo's with ascending mPageNum's.
     */
    boolean canInsertFirstPage(String docUrl, List<PageInfo> ascendingNumbers) {
        // Not enough info to determine whether the URL is fit to be first page.
        if (mAllPageInfo.size() < 2) return false;

        // Already detected first page, no need to add another one.
        if (mAllPageInfo.get(0).mPageNum == 1) return false;

        // If the current document URL is not shorter than other paginated page URL, it should have
        // a page parameter to identify itself.  This means we could detect it as part of paginated
        // cluster while parsing other members.
        // On the other hand, it is still possible to be the last page of cluster.
        if (docUrl.length() >= mAllPageInfo.get(0).mUrl.length()) return false;

        // The other paginated members must be page 2 through last of paginated content, and the
        // current document isn't detected as any one of them.
        for (int i = 0; i < mAllPageInfo.size(); i++) {
            if (mAllPageInfo.get(i).mPageNum != i + 2) return false;
            if (mAllPageInfo.get(i).mUrl.equals(docUrl)) return false;
        }

        // If there is a digital outlink with anchor text "1", don't insert this URL as first page,
        // because the first page rarely has an outlink with anchor text "1" pointing to other
        // pages.  Normally this is the last page of paginated cluster.
        for (PageInfo link : ascendingNumbers) {
            if (link.mPageNum == 1 && !link.mUrl.isEmpty() && !link.mUrl.equals(docUrl)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Inserts the given URL, which is the current document URL, as first page.  Only call this
     * if canInsertFirstPage() returns true.
     *
     * @param docUrl the current document URL that was parsed.
     */
    void insertFirstPage(String docUrl) {
        mAllPageInfo.add(0, new PageParamInfo.PageInfo(1, docUrl));
    }

    /**
     * Determines the next paging URL for the given document URL.
     *
     * @param docUrl the current document URL that was parsed.
     */
    void determineNextPagingUrl(String docUrl) {
        if (!mNextPagingUrl.isEmpty() || mAllPageInfo.isEmpty()) return;

        // If document URL is among mAllPageInfo, the next page is the one after.
        boolean hasDocUrl = false;
        for (PageInfo page : mAllPageInfo) {
            if (hasDocUrl) {
                mNextPagingUrl = page.mUrl;
                return;
            }
            if (page.mUrl.equals(docUrl)) hasDocUrl = true;
        }
    }

    @Override
    public String toString() {  // For debugging.
       String str = new String("Type: " + mType + "\nPageInfo: " + mAllPageInfo.size());
       str += "\npattern: " + mPagePattern;
       for (PageInfo page : mAllPageInfo) {
           str += "\n  " + page.toString();
       }
       str += "\nformula: " + (mFormula == null ? "null" : mFormula.toString()) +
              "\nnextPagingUrl: " + mNextPagingUrl;
       return str;
    }

    /**
     * Detects if page numbers in list of PageLinkInfo's are adjacent, if page numbers in list
     * of PageInfo's are consecutive, and if there's a gap in the list.
     *
     * For adjacency, the page numbers in list of PageLinkInfo's must either be adjacent, or
     * separated by at most 1 plain text number which must represent the current page number in one
     * of the PageInfo's.
     * For consecutiveness, there must be at least one pair of consecutive number values in the list
     * of PageLinkInfo's, or between a PageLinkInfo and a plain text number.
     *
     * Returns a populated PageNumbersState.
     *
     * @param allLinkInfo the list of PageLinkInfo's to evaluate
     * @param ascendingNumbers list of PageInfo's with ascending mPageNum's
     */
    static PageNumbersState getPageNumbersState(List<PageLinkInfo> allLinkInfo,
            List<PageInfo> ascendingNumbers) {
        PageNumbersState state = new PageNumbersState();

        // Check if elements in allLinkInfo are adjacent or there's only 1 gap i.e. the gap is
        // current page number represented in plain text.
        int firstPos = -1;
        int lastPos = -1;
        int gapPos = -1;
        Set<Integer> pageParamSet = new HashSet<Integer>();  // To check that page number is unique.
        for (PageLinkInfo linkInfo : allLinkInfo) {
            final int currPos = linkInfo.mPosInAscendingList;
            if (lastPos == -1) {
                firstPos = currPos;
            } else if (currPos != lastPos + 1) {
                // If position is not strictly ascending, or the gap size is > 1 (e.g. "[3] [4] 5 6
                // [7]"), or there's more than 1 gap (e.g. "[3] 4 [5] 6 [7]"), allLinkInfo is not
                // adjacent.
                if (currPos <= lastPos || currPos != lastPos + 2 || gapPos != -1) return state;
                gapPos = currPos - 1;
            }
            // Make sure page param value, i.e. page number represented in plain text, is unique.
            if (!pageParamSet.add(linkInfo.mPageParamValue)) return state;
            lastPos = currPos;
        }  // for all LinkInfo's

        state.mIsAdjacent = true;

        // Now, determine if page numbers in ascendingNumbers are consecutive.

        // First, handle the gap.
        if (gapPos != -1) {
           if (gapPos <= 0 || gapPos >= ascendingNumbers.size() - 1) return state;
           // The "gap" should represent current page number in plain text.
           // Check if its adjacent page numbers are consecutive.
           // e.g. "[1] [5] 6 [7] [12]" is accepted; "[4] 8 [16]" is rejected.
           // This can eliminate links affecting the number of items on a page.
           final int currPageNum = ascendingNumbers.get(gapPos).mPageNum;
           if (ascendingNumbers.get(gapPos - 1).mPageNum == currPageNum - 1 &&
                   ascendingNumbers.get(gapPos + 1).mPageNum == currPageNum + 1) {
               state.mIsConsecutive = true;
               state.mNextPagingUrl = ascendingNumbers.get(gapPos + 1).mUrl;
           }
           return state;
        }

        // There is no gap.  Check if at least one of the following cases is satisfied:
        // Case #1: "[1] [2] ..." or "1 [2] ... ".
        if ((firstPos == 0 || firstPos == 1) && ascendingNumbers.get(0).mPageNum == 1 &&
                ascendingNumbers.get(1).mPageNum == 2) {
            state.mIsConsecutive = true;
            return state;
        }
        // Case #2: "[1] 2 [3] ..." where [1] doesn't belong to current pattern.
        if (firstPos == 2 && ascendingNumbers.get(2).mPageNum == 3 &&
                ascendingNumbers.get(1).mUrl.isEmpty() && !ascendingNumbers.get(0).mUrl.isEmpty()) {
            state.mIsConsecutive = true;
            return state;
        }
        // Case #3: "... [n-1] [n]" or "... [n - 1] n".
        final int numbersSize = ascendingNumbers.size();
        if ((lastPos == numbersSize - 1 || lastPos == numbersSize - 2) &&
                ascendingNumbers.get(numbersSize - 2).mPageNum + 1 ==
                        ascendingNumbers.get(numbersSize - 1).mPageNum) {
            state.mIsConsecutive = true;
            return state;
        }
        // Case #4: "... [i-1] [i] [i+1] ...".
        for (int i = firstPos + 1; i < lastPos; i++) {
            if (ascendingNumbers.get(i - 1).mPageNum + 2 == ascendingNumbers.get(i + 1).mPageNum) {
                state.mIsConsecutive = true;
                return state;
            }
        }

        // Otherwise, there's no pair of consecutive values.
        return state;
    }

    /**
     * Determines if the list of PageLinkInfo's form a linear formula:
     *   pageParamValue = coefficient * pageNum + delta (delta == -coefficient or delta == 0).
     *
     * The coefficient and delta are calculated from the page parameter values and page numbers of 2
     * PageLinkInfo's, and then validated against the remaining PageLinkInfo's.
     * The order of page numbers doesn't matter.
     *
     * Returns LinearFormula, containing the coefficient and delta, if the page
     * parameter formula could be determined.  Otherwise, returns null.
     *
     * @param allLinkInfo the list of PageLinkInfo's to evaluate
     */
    // TODO(kuan): As this gets rolled out, reassess the necessity of non-1 coefficient support.
    private static LinearFormula getLinearFormula(List<PageLinkInfo> allLinkInfo) {
        if (allLinkInfo.size() < MIN_LINKS_TO_JUSTIFY_LINEAR_MAP) return null;

        final PageLinkInfo firstLink = allLinkInfo.get(0);
        final PageLinkInfo secondLink = allLinkInfo.get(1);

        if (allLinkInfo.size() == 2 && Math.max(firstLink.mPageNum, secondLink.mPageNum) > 4) {
            return null;
        }

        int deltaX = secondLink.mPageNum - firstLink.mPageNum;
        if (deltaX == 0) return null;

        int deltaY = secondLink.mPageParamValue - firstLink.mPageParamValue;
        int coefficient = deltaY / deltaX;
        if (coefficient == 0) return null;

        int delta = firstLink.mPageParamValue - coefficient * firstLink.mPageNum;
        if (delta != 0 && delta != -coefficient) return null;

        // Check if the remaining elements are on the same linear map.
        for (int i = 2; i < allLinkInfo.size(); i++) {
            final PageLinkInfo link = allLinkInfo.get(i);
            if (link.mPageParamValue != coefficient * link.mPageNum + delta) return null;
        }

        return new LinearFormula(coefficient, delta);
    }

    /**
     * Returns true if page numbers in list of PageInfo's form a sequence, based on a pipeline of
     * rules:
     * - first PageInfo must have a URL unless it is the first page
     * - there's only one plain number without URL in list
     * - if only two pages, they must be siblings - 2nd page number must follow 1st
     * - page numbers must be adjacent and consecutive; otherwise, 2 non-consecutive numbers must be
     *   head/tail or have URLs.
     *
     * @param ascendingNumbers list of PageInfo's with ascending mPageNum's
     */
    private static boolean isPageNumberSequence(List<PageInfo> ascendingNumbers,
            PageNumbersState state) {
        if (ascendingNumbers.size() <= 1) return false;

        // The first one must have a URL unless it is the first page.
        final PageInfo firstPage = ascendingNumbers.get(0);
        if (firstPage.mPageNum != 1 && firstPage.mUrl.isEmpty()) return false;

        // There's only one plain number without URL in ascending numbers group.
        boolean hasPlainNum = false;
        for (PageInfo page : ascendingNumbers) {
            if (page.mUrl.isEmpty()) {
                if (hasPlainNum) return false;
                hasPlainNum = true;
            } else if (hasPlainNum && state.mNextPagingUrl.isEmpty()) {
                state.mNextPagingUrl = page.mUrl;
            }
        }

        // If there are only two pages, they must be siblings.
        if (ascendingNumbers.size() == 2) {
            return firstPage.mPageNum + 1 == ascendingNumbers.get(1).mPageNum;
        }

        // Check if page numbers in ascendingNumbers are adjacent and consecutive.
        for (int i = 1; i < ascendingNumbers.size(); i++) {
            // If two adjacent numbers are not consecutive, we accept them only when:
            // 1) one of them is head/tail, like [1],[n-i][n-i+1]..[n] or [1],[2], [3]...[i], [n].
            // 2) both of them have URLs.
            final PageInfo currPage = ascendingNumbers.get(i);
            final PageInfo prevPage = ascendingNumbers.get(i - 1);
            if (currPage.mPageNum - prevPage.mPageNum != 1) {
                if (i != 1 && i != ascendingNumbers.size() - 1) return false;
                if (currPage.mUrl.isEmpty() || prevPage.mUrl.isEmpty()) return false;
            }
        }

        return true;
    }

}
