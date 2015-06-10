// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageParamInfoTest extends DomDistillerJsTestCase {

    public void testGetPageNumbersState() {
        {
            final int[] allNums = { 1, 2 };
            final int[] selectedNums = { 1, 2 };
            PageParamInfo.PageNumbersState state = getPageNumbersState(selectedNums, allNums);
            assertTrue(state.mIsAdjacent);
            assertTrue(state.mIsConsecutive);
        }
        {
            final int[] allNums = { 1, 2, 3 };
            final int[] selectedNums = { 2, 3 };
            PageParamInfo.PageNumbersState state = getPageNumbersState(selectedNums, allNums);
            assertTrue(state.mIsAdjacent);
            assertTrue(state.mIsConsecutive);
        }
        {
            final int[] allNums = { 1, 5, 6, 7, 10 };
            final int[] selectedNums = { 1, 5, 7, 10 };
            PageParamInfo.PageNumbersState state = getPageNumbersState(selectedNums, allNums);
            assertTrue(state.mIsAdjacent);
            assertTrue(state.mIsConsecutive);
        }
        {
            final int[] allNums = { 10, 25, 50 };
            final int[] selectedNums = { 10, 25, 50 };  // No consecutive pairs.
            PageParamInfo.PageNumbersState state = getPageNumbersState(selectedNums, allNums);
            assertTrue(state.mIsAdjacent);
            assertFalse(state.mIsConsecutive);
        }
        {
            final int[] allNums = { 23, 24, 30 };
            // This list doesn't satisfy consecutive rule. There should be "22" on the left of "23",
            // or "25" on the right of "24", or "29" on the left of "30".
            final int[] selectedNums = { 23, 24, 30 };
            PageParamInfo.PageNumbersState state = getPageNumbersState(selectedNums, allNums);
            assertTrue(state.mIsAdjacent);
            assertFalse(state.mIsConsecutive);
        }
        {
            final int[] allNums = { 1, 2, 3, 4, 5 };
            final int[] selectedNums = { 1, 3, 5 };  // Two gaps.
            PageParamInfo.PageNumbersState state = getPageNumbersState(selectedNums, allNums);
            assertFalse(state.mIsAdjacent);
            assertFalse(state.mIsConsecutive);
        }
        {
            final int[] allNums = { 2, 3, 4, 5 };
            final int[] selectedNums = { 2, 5 };  // A gap of 2 numbers.
            PageParamInfo.PageNumbersState state = getPageNumbersState(selectedNums, allNums);
            assertFalse(state.mIsAdjacent);
            assertFalse(state.mIsConsecutive);
        }
    }

    public void testInsertFirstPage() {
        PageParamInfo info = new PageParamInfo();
        info.mType = PageParamInfo.Type.PAGE_NUMBER;

        {
            final String testUrl = "http://www.google.com/article/bar";
            final PageParamContentInfoEx[] allContentInfo = {
                NumberInPlainText(1),
                NumericOutlink("http://www.google.com/article/bar?page=2", 2, true),
                NumericOutlink("http://www.google.com/article/bar?page=3", 3, true),
            };
            boolean canInsert = canInsertFirstPage(testUrl, allContentInfo, info);
            assertTrue(canInsert);
            assertEquals(2, info.mAllPageInfo.size());

            info.insertFirstPage(testUrl);
            // The current document is inserted as first page.
            assertEquals(3, info.mAllPageInfo.size());
            PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
            assertEquals(1, page.mPageNum);
            assertEquals("http://www.google.com/article/bar", page.mUrl);
            page = info.mAllPageInfo.get(1);
            assertEquals(2, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=2", page.mUrl);
            page = info.mAllPageInfo.get(2);
            assertEquals(3, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=3", page.mUrl);
        }

        {
            // Current document url has same length as other paginated pages, so it shouldn't be
            // inserted as first page.
            final String testUrl = "http://www.google.com/article/bar?page=1";
            final PageParamContentInfoEx[] allContentInfo = {
                NumberInPlainText(1),
                NumericOutlink("http://www.google.com/article/bar?page=2", 2, true),
                NumericOutlink("http://www.google.com/article/bar?page=3", 3, true),
            };
            boolean canInsert = canInsertFirstPage(testUrl, allContentInfo, info);
            assertFalse(canInsert);
            assertEquals(2, info.mAllPageInfo.size());
        }

        {
            // Current document url is the last page, so shouldn't be inserted as first page.
            final String testUrl = "http://www.google.com/article/bar?page=4";
            final PageParamContentInfoEx[] allContentInfo = {
                NumericOutlink("http://www.google.com/article/bar", 1, false),
                NumericOutlink("http://www.google.com/article/bar?page=2&s=11", 2, true),
                NumericOutlink("http://www.google.com/article/bar?page=3&s=11", 3, true),
                NumberInPlainText(4),
            };
            boolean canInsert = canInsertFirstPage(testUrl, allContentInfo, info);
            assertFalse(canInsert);
            assertEquals(2, info.mAllPageInfo.size());
        }

        {
            // Page one has an outlink to itself, should be inserted as first page.
            final String testUrl = "http://www.google.com/article/bar";
            final PageParamContentInfoEx[] allContentInfo = {
                NumericOutlink("http://www.google.com/article/bar", 1, false),
                NumericOutlink("http://www.google.com/article/bar?page=2", 2, true),
                NumericOutlink("http://www.google.com/article/bar?page=3", 3, true),
            };
            boolean canInsert = canInsertFirstPage(testUrl, allContentInfo, info);
            assertTrue(canInsert);
            assertEquals(2, info.mAllPageInfo.size());

            info.insertFirstPage(testUrl);
            assertEquals(3, info.mAllPageInfo.size());
            PageParamInfo.PageInfo page = info.mAllPageInfo.get(0);
            assertEquals(1, page.mPageNum);
            assertEquals("http://www.google.com/article/bar", page.mUrl);
            page = info.mAllPageInfo.get(1);
            assertEquals(2, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=2", page.mUrl);
            page = info.mAllPageInfo.get(2);
            assertEquals(3, page.mPageNum);
            assertEquals("http://www.google.com/article/bar?page=3", page.mUrl);
        }
    }

    private static PageParamInfo.PageNumbersState getPageNumbersState(int[] selectedNums,
            int[] allNums) {
        List<PageParamInfo.PageInfo> ascendingNumbers = new ArrayList<PageParamInfo.PageInfo>();
        Map<Integer, Integer> numberToPos = new HashMap<Integer, Integer>();

        for (int i = 0; i < allNums.length; i++) {
            final int number = allNums[i];
            numberToPos.put(number, i);
            ascendingNumbers.add(new PageParamInfo.PageInfo(number, ""));
        }

        List<PageLinkInfo> allLinkInfo = new ArrayList<PageLinkInfo>();
        for (int i = 0; i < selectedNums.length; i++) {
            final int number = selectedNums[i];
            allLinkInfo.add(new PageLinkInfo(number, number, numberToPos.get(number)));
        }

        return PageParamInfo.getPageNumbersState(allLinkInfo, ascendingNumbers);
    }

    private static class PageParamContentInfoEx {
        PageParamContentInfo mContentInfo;
        boolean mIsPageInfo;

        PageParamContentInfoEx(PageParamContentInfo contentInfo, boolean isPageInfo) {
            mContentInfo = contentInfo;
            mIsPageInfo = isPageInfo;
        }
    }

    private static PageParamContentInfoEx NumberInPlainText(int number) {
        return new PageParamContentInfoEx(PageParamContentInfo.NumberInPlainText(number), false);
    }

    private static PageParamContentInfoEx NumericOutlink(String targetUrl, int number,
            boolean isPageInfo) {
        return new PageParamContentInfoEx(PageParamContentInfo.NumericOutlink(targetUrl, number),
            isPageInfo);
    }

    private static boolean canInsertFirstPage(String docUrl,
            PageParamContentInfoEx[] allContentInfo, PageParamInfo pageParamInfo) {
        List<PageParamInfo.PageInfo> ascendingNumbers = new ArrayList<PageParamInfo.PageInfo>();
        pageParamInfo.mAllPageInfo.clear();

        for (int i = 0; i < allContentInfo.length; i++) {
            PageParamContentInfoEx ex = allContentInfo[i];
            switch (ex.mContentInfo.mType) {
                case NUMBER_IN_PLAIN_TEXT:
                    ascendingNumbers.add(new PageParamInfo.PageInfo(ex.mContentInfo.mNumber, ""));
                    break;
                case NUMERIC_OUTLINK:
                    PageParamInfo.PageInfo page = new PageParamInfo.PageInfo(
                            ex.mContentInfo.mNumber, ex.mContentInfo.mTargetUrl);
                    ascendingNumbers.add(page);
                    if (ex.mIsPageInfo) pageParamInfo.mAllPageInfo.add(page);
                    break;
                default:
                    break;
            }
        }

        return pageParamInfo.canInsertFirstPage(docUrl, ascendingNumbers);
    }

}
