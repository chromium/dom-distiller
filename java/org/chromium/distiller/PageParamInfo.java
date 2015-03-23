// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores information about the page parameter detected from potential pagination URLs
 * with numeric anchor text:
 * - type of page parameter detected
 * - list of pagination URLs with their page numbers
 * - coefficient and delta values of the linear formula formed by the pagination URLs:
 *     pageParamValue = coefficient * pageNum + delta
 * - detected single page URL.
 */
class PageParamInfo implements Cloneable {

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
     * See PageParamterDetector.getPageParamLinearFormula() for details.
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
    List<PageInfo> mAllPageInfo = new ArrayList<PageInfo>();
    LinearFormula mFormula = null;
    String mSinglePageUrl = "";

    /**
     * Returns a new PageParamInfo that is a clone of this object.
     */
    public Object clone() {
        PageParamInfo info = new PageParamInfo();
        info.mType = this.mType;
        info.mAllPageInfo = this.mAllPageInfo;
        info.mFormula = this.mFormula;
        info.mSinglePageUrl = this.mSinglePageUrl;
        return info;
    }
   
    /**
     * Evaluates which PageParamInfo is better based on the properties of PageParamInfo.
     * We prefer the one if the list of PageParameterDetector.LinkInfo forms a linear formula, see
     * PageParamterDetector.getPageParamLinearFormula().
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
       str += "\nsinglePageUrl: " + mSinglePageUrl +
               "\nformula: " + (mFormula == null ? "null" : mFormula.toString());
       return str;
    }

}
