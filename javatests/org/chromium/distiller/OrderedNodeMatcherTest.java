// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Node;

import java.util.Arrays;
import java.util.List;

public class OrderedNodeMatcherTest extends DomDistillerJsTestCase {
    public void testOrderedNodeMatcher() {
        List<Node> matchNodes = Arrays.<Node>asList(
                TestUtil.createDiv(0),
                TestUtil.createDiv(1),
                TestUtil.createDiv(2),
                TestUtil.createDiv(3));
        OrderedNodeMatcher matcher = new OrderedNodeMatcher(matchNodes);

        for (int i = 0; i < matchNodes.size(); ++i) {
            assertFalse(matcher.isFinished());
            for (int j = 0; j < matchNodes.size(); ++j) {
                if (j != i) {
                    assertFalse(matcher.match(matchNodes.get(j)));
                }
            }
            assertTrue(matcher.match(matchNodes.get(i)));
        }
        assertTrue(matcher.isFinished());
    }
}
