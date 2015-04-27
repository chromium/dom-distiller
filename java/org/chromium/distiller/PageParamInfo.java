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
 * - list of pagination URLs with their page numbers
 * - coefficient and delta values of the linear formula formed by the pagination URLs:
 *     pageParamValue = coefficient * pageNum + delta.
 */
class PageParamInfo implements Cloneable {
    private static final int MIN_LINKS_TO_JUSTIFY_LINEAR_MAP = 2;

    static final int PAGE_NUM_ADJACENT_MASK = 1 << 0;
    static final int PAGE_NUM_CONSECUTIVE_MASK = 1 << 1;

    /**
     * Stores potential pagination info:
     * - page number represented as original plain text in document URL
     * - if the info is extracted from an anchor, its HRef.
     */
    static class PageInfo implements Comparable<PageInfo> {
        int mPageNum;
        String mUrl;

        PageInfo(int pageNum, String url) {
            mPageNum = pageNum;
            mUrl = url;
        }

        /**
         * Allows sorting of PageInfo by mPageNum.
         */
        @Override
        public int compareTo(PageInfo other) {
            if (mPageNum == other.mPageNum) return 0;
            return mPageNum < other.mPageNum ? -1 : 1;
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

    Type mType = Type.UNSET;
    List<PageInfo> mAllPageInfo;
    LinearFormula mFormula = null;

    PageParamInfo() {
        mAllPageInfo = new ArrayList<PageInfo>();
    }

    PageParamInfo(Type type, List<PageInfo> allPageInfo, LinearFormula formula) {
        mType = type;
        mAllPageInfo = allPageInfo;
        mFormula = formula;
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
            int result = arePageNumsAdjacentAndConsecutive(allLinkInfo, ascendingNumbers);
            if ((result & PAGE_NUM_ADJACENT_MASK) == 0) return null;

            LinearFormula linearFormula = getLinearFormula(allLinkInfo);

            // Type.PAGE_NUMBER: ascending numbers must be consecutive and form a page number
            // sequence.
            if ((result & PAGE_NUM_CONSECUTIVE_MASK) == 0) return null;
            if (!isPageNumberSequence(ascendingNumbers)) return null;

            List<PageInfo> allPageInfo = new ArrayList<PageInfo>();
            for (PageLinkInfo link : allLinkInfo) {
                allPageInfo.add(new PageInfo(link.mPageNum,
                        ascendingNumbers.get(link.mPosInAscendingList).mUrl));
            }
            return new PageParamInfo(Type.PAGE_NUMBER, allPageInfo, linearFormula);
        }

        // Most of news article have no more than 3 pages and the first page probably doesn't have
        // any page parameter.  If the first page url matches the the page pattern, we treat it as
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
                return new PageParamInfo(Type.PAGE_NUMBER, allPageInfo,
                        new LinearFormula(coefficient, delta));
            }
        }

        return null;
    }

    /**
     * Returns a new PageParamInfo that is a clone of this object.
     */
    public Object clone() {
        return new PageParamInfo(this.mType, this.mAllPageInfo, this.mFormula);
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

    @Override
    public String toString() {  // For debugging.
       String str = new String("Type: " + mType + "\nPageInfo: " + mAllPageInfo.size());
       for (PageInfo page : mAllPageInfo) {
           str += "\n  " + page.toString();
       }
       str += "\nformula: " + (mFormula == null ? "null" : mFormula.toString());
       return str;
    }

    /**
     * Detects if page numbers in list of PageLinkInfo's are adjacent, and if page numbers in list
     * of PageInfo's are consecutive.
     *
     * For adjacency, the page numbers in list of PageLinkInfo's must either be adjacent, or
     * separated by at most 1 plain text number which must represent the current page number in one
     * of the PageInfo's.
     * For consecutiveness, there must be at least one pair of consecutive number values in the list
     * of PageLinkInfo's, or between a PageLinkInfo and a plain text number.
     *
     * Returns a int value that is a combination of bits:
     * - bit for PAGE_PARAM_ADJACENT_MASK is set if allLinkInfo are adjacent
     * - bit for PAGE_PARAM_CONSECUTIVE_MASK is set if ascendingNumbers are consecutive.
     *
     * @param allLinkInfo the list of PageLinkInfo's to evaluate
     * @param ascendingNumbers list of PageInfo's with ascending mPageNum's
     */
    static int arePageNumsAdjacentAndConsecutive(List<PageLinkInfo> allLinkInfo,
            List<PageInfo> ascendingNumbers) {
        int result = 0;

        // Check if elements in allLinkInfo are adjacent or there's only 1 gap i.e. the gap is
        // current page number respresented in plain text.
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
                if (currPos <= lastPos || currPos != lastPos + 2 || gapPos != -1) return result;
                gapPos = currPos - 1;
            }
            // Make sure page param value, i.e. page number represented in plain text, is unique.
            if (!pageParamSet.add(linkInfo.mPageParamValue)) return result;
            lastPos = currPos;
        }  // for all LinkInfo's

        result |= PAGE_NUM_ADJACENT_MASK;

        // Now, determine if page numbers in ascendingNumbers are consecutive.

        // First, handle the gap.
        if (gapPos != -1) {
           if (gapPos <= 0 || gapPos >= ascendingNumbers.size() - 1) return result;
           // The "gap" should represent current page number in plain text.
           // Check if its adjacent page numbers are consecutive.
           // e.g. "[1] [5] 6 [7] [12]" is accepted; "[4] 8 [16]" is rejected.
           // This can eliminate links affecting the number of items on a page.
           final int currPageNum = ascendingNumbers.get(gapPos).mPageNum;
           if (ascendingNumbers.get(gapPos - 1).mPageNum == currPageNum - 1 &&
                   ascendingNumbers.get(gapPos + 1).mPageNum == currPageNum + 1) {
               return result | PAGE_NUM_CONSECUTIVE_MASK;
           }
           return result;
        }

        // There is no gap.  Check if at least one of the following cases is satisfied:
        // Case #1: "[1] [2] ..." or "1 [2] ... ".
        if ((firstPos == 0 || firstPos == 1) && ascendingNumbers.get(0).mPageNum == 1 &&
                ascendingNumbers.get(1).mPageNum == 2) {
            return result | PAGE_NUM_CONSECUTIVE_MASK;
        }
        // Case #2: "[1] 2 [3] ..." where [1] doesn't belong to current pattern.
        if (firstPos == 2 && ascendingNumbers.get(2).mPageNum == 3 &&
                ascendingNumbers.get(1).mUrl.isEmpty() && !ascendingNumbers.get(0).mUrl.isEmpty()) {
            return result | PAGE_NUM_CONSECUTIVE_MASK;
        }
        // Case #3: "... [n-1] [n]" or "... [n - 1] n".
        final int numbersSize = ascendingNumbers.size();
        if ((lastPos == numbersSize - 1 || lastPos == numbersSize - 2) &&
                ascendingNumbers.get(numbersSize - 2).mPageNum + 1 ==
                        ascendingNumbers.get(numbersSize - 1).mPageNum) {
            return result | PAGE_NUM_CONSECUTIVE_MASK;
        }
        // Case #4: "... [i-1] [i] [i+1] ...".
        for (int i = firstPos + 1; i < lastPos; i++) {
            if (ascendingNumbers.get(i - 1).mPageNum + 2 == ascendingNumbers.get(i + 1).mPageNum) {
                return result | PAGE_NUM_CONSECUTIVE_MASK;
            }
        }

        // Otherwise, there's no pair of consecutive values.
        return result;
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
     * parameter forumla could be determined.  Otherwise, returns null.
     *
     * @param allLinkInfo the list of PageLinkInfo's to evaluate
     */
    // TODO(kuan): As this gets rolled out, reassesss the necessity of non-1 coefficient support.
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
    private static boolean isPageNumberSequence(List<PageInfo> ascendingNumbers) {
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
