// Copyright 2015 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

public class MonotonicPageInfosGroupsTest extends DomDistillerJsTestCase {

    private final MonotonicPageInfosGroups mGroups = new MonotonicPageInfosGroups();

    @Override
    public void setUp() {
        mGroups.addGroup();
    }

    public void testBasicAscending() {
        final int[] allNums = { 1, 2, 3 };
        addNumbers(allNums);

        assertEquals(1, getNumGroups());
        verifyGroup(0, 1, allNums);
    }

    public void testBasicDesending() {
        final int[] allNums = { 3, 2, 1 };
        addNumbers(allNums);

        assertEquals(1, getNumGroups());
        verifyGroup(0, -1, allNums);
    }

    public void testOne() {
        final int[] allNums = { 1 };
        addNumbers(allNums);

        assertEquals(1, getNumGroups());
        verifyGroup(0, 0, allNums);
    }

    public void testTwoSame() {
        final int[] allNums = { 1, 1 };
        addNumbers(allNums);

        final int[] expectedNums = { 1 };
        assertEquals(1, getNumGroups());
        verifyGroup(0, 0, expectedNums);
    }

    public void testAscendingAndDescending1() {
        final int[] allNums = { 1, 2, 3, 3, 2, 1 };
        addNumbers(allNums);

        final int[] expectedNums0 = { 1, 2, 3 };
        final int[] expectedNums1 = { 3, 2, 1 };
        assertEquals(2, getNumGroups());
        verifyGroup(0, 1, expectedNums0);
        verifyGroup(1, -1, expectedNums1);
    }

    public void testAscendingAndDescending2() {
        final int[] allNums = { 1, 2, 3, 2, 1 };
        addNumbers(allNums);

        final int[] expectedNums0 = { 1, 2, 3 };
        final int[] expectedNums1 = { 3, 2, 1 };
        assertEquals(2, getNumGroups());
        verifyGroup(0, 1, expectedNums0);
        verifyGroup(1, -1, expectedNums1);
    }

    public void testAscendingAndDescending3() {
        final int[] allNums = { 1, 3, 5, 4, 2, 1, 10, 999, 0 };
        addNumbers(allNums);

        final int[] expectedNums0 = { 1, 3, 5 };
        final int[] expectedNums1 = { 5, 4, 2, 1 };
        final int[] expectedNums2 = { 1, 10, 999 };
        final int[] expectedNums3 = { 999, 0 };
        assertEquals(4, getNumGroups());
        verifyGroup(0, 1, expectedNums0);
        verifyGroup(1, -1, expectedNums1);
        verifyGroup(2, 1, expectedNums2);
        verifyGroup(3, -1, expectedNums3);
    }

    public void testDuplicateAscending1() {
        final int[] allNums = { 1, 1, 2, 3 };
        addNumbers(allNums);

        final int[] expectedNums = { 1, 2, 3 };
        assertEquals(1, getNumGroups());
        verifyGroup(0, 1, expectedNums);
    }

    public void testDuplicateAscending2() {
        final int[] allNums = { 1, 2, 2, 3 };
        addNumbers(allNums);

        final int[] expectedNums0 = { 1, 2 };
        final int[] expectedNums1 = { 2, 3 };
        assertEquals(2, getNumGroups());
        verifyGroup(0, 1, expectedNums0);
        verifyGroup(1, 1, expectedNums1);
    }

    public void testDuplicateAscending3() {
        final int[] allNums = { 1, 2, 3, 3 };
        addNumbers(allNums);

        final int[] expectedNums0 = { 1, 2, 3 };
        final int[] expectedNums1 = { 3 };
        assertEquals(2, getNumGroups());
        verifyGroup(0, 1, expectedNums0);
        verifyGroup(1, 0, expectedNums1);
    }

    public void testDuplicateDescending1() {
        final int[] allNums = { 3, 2, 1, 1 };
        addNumbers(allNums);

        final int[] expectedNums0 = { 3, 2, 1 };
        final int[] expectedNums1 = { 1 };
        assertEquals(2, getNumGroups());
        verifyGroup(0, -1, expectedNums0);
        verifyGroup(1, 0, expectedNums1);
    }

    public void testDuplicateDescending2() {
        final int[] allNums = { 3, 2, 2, 1 };
        addNumbers(allNums);

        final int[] expectedNums0 = { 3, 2 };
        final int[] expectedNums1 = { 2, 1 };
        assertEquals(2, getNumGroups());
        verifyGroup(0, -1, expectedNums0);
        verifyGroup(1, -1, expectedNums1);
    }

    public void testDuplicateDescending3() {
        final int[] allNums = { 3, 3, 2, 1 };
        addNumbers(allNums);

        final int[] expectedNums = { 3, 2, 1 };
        assertEquals(1, getNumGroups());
        verifyGroup(0, -1, expectedNums);
    }

    private void addNumbers(int[] allNums) {
        for (int i = 0; i < allNums.length; i++) {
            mGroups.addNumber(allNums[i], "");
        }
    }

    private int getNumGroups() {
        return mGroups.getGroups().size();
    }

    private void verifyGroup(int index, int expectedDeltaSign, int[] expectedNums) {
        MonotonicPageInfosGroups.Group group = mGroups.getGroups().get(index);
        assertEquals(expectedDeltaSign, group.mDeltaSign);
        assertEquals(expectedNums.length, group.mList.size());
        for (int i = 0; i < expectedNums.length; i++) {
            assertEquals(expectedNums[i], group.mList.get(i).mPageNum);
        }
    }

}
