// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

/**
 * This class stores information about the link (anchor) after PageParameterDetector has detected
 * the page parameter:
 * - the page number (as represented by the original plain text) for the link
 * - the original page parameter numeric component in the URL (this component would be replaced
 *   by PageParameterDetector.PAGE_PARAM_PLACEHOLDER in the URL pattern)
 * - the position of this link in the list of ascending numbers.
 */
class PageLinkInfo {
    int mPageNum;
    int mPageParamValue;
    int mPosInAscendingList;

    PageLinkInfo(int pageNum, int pageParamValue, int posInAscendingList) {
        mPageNum = pageNum;
        mPageParamValue = pageParamValue;
        mPosInAscendingList = posInAscendingList;
    }
}
