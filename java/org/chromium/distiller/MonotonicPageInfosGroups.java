// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores all numeric content (both outlinks and plain text pieces) parsed from the
 * document, grouped by their monotonicity and adjacency.  Basically, it's a list of groups of
 * monotonic and adjacent PageParamInfo.PageInfo's (with or without links) in the document.
 * It's being used by PageParameterParser and PageParameterDetector.
 */
class MonotonicPageInfosGroups {
    static class Group {
        final List<PageParamInfo.PageInfo> mList =
                new ArrayList<PageParamInfo.PageInfo>();
        int mDeltaSign = 0;  // 1: increase, -1: decrease, 0: remain the same.
    }

    private final List<Group> mGroups = new ArrayList<Group>();
    private PageParamInfo.PageInfo mPrevPageInfo = null;

    List<Group> getGroups() { return mGroups; }

    /**
     * Add a new group because a non-plain-number has been encountered in the document being
     * parsed i.e. there's a break in the adjacency of plain numbers.
     */
    void addGroup() {
        if (mGroups.isEmpty() || !getLastGroup().mList.isEmpty()) {
            mGroups.add(new Group());
            mPrevPageInfo = null;  // Break in adjacency nukes the previous PageInfo.
        }
    }

    /**
     * Add the given PageParamInfo.PageInfo, ensuring the group stays monotonic:
     * - add in the current group if the page number is strictly increasing or decreasing
     * - otherwise, start a new group.
     */
    void addPageInfo(PageParamInfo.PageInfo pageInfo) {
        Group group = getLastGroup();
        if (group.mList.isEmpty()) {
            group.mList.add(pageInfo);
            mPrevPageInfo = pageInfo;
            return;
        }

        int delta = pageInfo.mPageNum - mPrevPageInfo.mPageNum;
        int deltaSign = delta == 0 ? 0 : (delta < 0 ? -1 : 1);
        if (deltaSign != group.mDeltaSign) {
            // group.mDeltaSign = 0 means the group only has 1 entry, and hence no ordering yet;
            // the new deltaSign would determine the ordering.
            // Otherwise, the group has been strictly ascending/descending until this number, in
            // which case, start a new group:
            // - with this number if it's the same as previous (deltaSign = 0)
            // - with the previous, followed by this, if both are different numbers.
            if (group.mDeltaSign != 0) {
                group = addGroupButRetainPrev();
                if (deltaSign != 0) group.mList.add(mPrevPageInfo);
            }
        } else if (deltaSign == 0) {
            // This number is same as previous (i.e. the only entry in the group), and will be added
            // (below), so clear the group to avoid duplication.
            group.mList.clear();
        }
        group.mList.add(pageInfo);
        mPrevPageInfo = pageInfo;
        group.mDeltaSign = deltaSign;
    }

    /**
     * Add a PageParamInfo.PageInfo for the given page number and URL, ensuring the group stays
     * monotonic.
     */
    void addNumber(int number, String url) {
        addPageInfo(new PageParamInfo.PageInfo(number, url));
    }

    void cleanup() {
        // Remove last empty adjacent number group.
        if (!mGroups.isEmpty() && getLastGroup().mList.isEmpty()) {
            mGroups.remove(mGroups.size() - 1);
        }
    }

    // Only call this if mGroups is not empty.
    private Group getLastGroup() {
        return mGroups.get(mGroups.size() - 1);
    }

    /**
     * Adds a new group because the new number is no longer monotonic (strictly increasing/
     * decreasing) as in the current group.
     *
     * @return the newly added group.
     */
    private Group addGroupButRetainPrev() {
        Group group = new Group();
        mGroups.add(group);
        return group;
    }
}
