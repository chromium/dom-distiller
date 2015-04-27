// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageParamInfoTest extends DomDistillerJsTestCase {

    public void testArePageNumsAdjacentAndConsecutive() {
        {
            final int[] allNums = { 1, 2 };
            final int[] selectedNums = { 1, 2 };
            int result = arePageNumsAdjacentAndConsecutive(selectedNums, allNums);
            assertTrue(isAdjacent(result));
            assertTrue(isConsecutive(result));
        }
        {
            final int[] allNums = { 1, 2, 3 };
            final int[] selectedNums = { 2, 3 };
            int result = arePageNumsAdjacentAndConsecutive(selectedNums, allNums);
            assertTrue(isAdjacent(result));
            assertTrue(isConsecutive(result));
        }
        {
            final int[] allNums = { 1, 5, 6, 7, 10 };
            final int[] selectedNums = { 1, 5, 7, 10 };
            int result = arePageNumsAdjacentAndConsecutive(selectedNums, allNums);
            assertTrue(isAdjacent(result));
            assertTrue(isConsecutive(result));
        }
        {
            final int[] allNums = { 10, 25, 50 };
            final int[] selectedNums = { 10, 25, 50 };  // No consecutive pairs.
            int result = arePageNumsAdjacentAndConsecutive(selectedNums, allNums);
            assertTrue(isAdjacent(result));
            assertFalse(isConsecutive(result));
        }
        {
            final int[] allNums = { 23, 24, 30 };
            // This list doesn't satisfy consecutive rule. There should be "22" on the left of "23",
            // or "25" on the right of "24", or "29" on the left of "30".
            final int[] selectedNums = { 23, 24, 30 };
            int result = arePageNumsAdjacentAndConsecutive(selectedNums, allNums);
            assertTrue(isAdjacent(result));
            assertFalse(isConsecutive(result));
        }
        {
            final int[] allNums = { 1, 2, 3, 4, 5 };
            final int[] selectedNums = { 1, 3, 5 };  // Two gaps.
            int result = arePageNumsAdjacentAndConsecutive(selectedNums, allNums);
            assertFalse(isAdjacent(result));
            assertFalse(isConsecutive(result));
        }
        {
            final int[] allNums = { 2, 3, 4, 5 };
            final int[] selectedNums = { 2, 5 };  // A gap of 2 numbers.
            int result = arePageNumsAdjacentAndConsecutive(selectedNums, allNums);
            assertFalse(isAdjacent(result));
            assertFalse(isConsecutive(result));
        }
    }

    private static int arePageNumsAdjacentAndConsecutive(int[] selectedNums, int[] allNums) {
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

        return PageParamInfo.arePageNumsAdjacentAndConsecutive(allLinkInfo, ascendingNumbers);
    }

    private static boolean isAdjacent(int result) {
        return (result & PageParamInfo.PAGE_NUM_ADJACENT_MASK) ==
                PageParamInfo.PAGE_NUM_ADJACENT_MASK;
    }

    private static boolean isConsecutive(int result) {
        return (result & PageParamInfo.PAGE_NUM_CONSECUTIVE_MASK) ==
                PageParamInfo.PAGE_NUM_CONSECUTIVE_MASK;
    }

}
