// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.List;
import java.util.Iterator;
import java.util.Arrays;

import com.google.gwt.junit.client.GWTTestCase;

import com.google.gwt.dom.client.Node;

public class OrderedNodeMatcherTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

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
