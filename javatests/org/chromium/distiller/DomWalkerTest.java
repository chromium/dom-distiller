// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class DomWalkerTest extends DomDistillerJsTestCase {
    private static class VisitData {
        final int expectedId;
        final boolean shouldVisit;

        VisitData(int id, boolean visit) {
            expectedId = id;
            shouldVisit = visit;
        }
    }

    /**
     * Walks the node tree rooted at topNode. At each node, gets the next entry from data and
     * asserts that the id of the node matches the expectedId in the VisitData and then recurses
     * into that subtree if shouldVisit is true in that VisitData.
     *
     * Asserts that a node is visited for each entry in data.
     */
    private void doTestForValues(Node topNode, final List<VisitData> data) {
        final Stack<Node> path = new Stack<Node>();
        final Iterator<VisitData> it = data.iterator();
        new DomWalker(new DomWalker.Visitor() {
            @Override
            public void skip(Element e) {}

            @Override
            public boolean visit(Node n) {
                assertEquals(Node.ELEMENT_NODE, n.getNodeType());
                assertTrue(it.hasNext());
                VisitData vd = it.next();
                Element e = Element.as(n);
                int id = JavaScript.parseInt(e.getId());
                assertEquals(vd.expectedId, id);

                if (vd.shouldVisit) path.push(n);
                return vd.shouldVisit;
            }

            @Override
            public void exit(Node n) {
                assertTrue(n.equals(path.pop()));
            }
        }).walk(topNode);
        assertFalse(it.hasNext());
    }

    public void testTopNodeHasNextSiblingAndParent() {
        Node root = TestUtil.createDiv(0);
        Node firstChild = TestUtil.createDiv(1);
        Node secondChild = TestUtil.createDiv(2);
        root.appendChild(firstChild);
        root.appendChild(secondChild);

        doTestForValues(firstChild, Arrays.asList(
                new VisitData(1, true)
                ));
    }

    public void testDomWalker() {
        Node topNode = TestUtil.createDivTree().get(0);
        doTestForValues(topNode, Arrays.asList(
                new VisitData(0, true),
                new VisitData(1, true),
                new VisitData(2, true),
                new VisitData(3, true),
                new VisitData(4, true),
                new VisitData(5, true),
                new VisitData(6, true),
                new VisitData(7, true),
                new VisitData(8, true),
                new VisitData(9, true),
                new VisitData(10, true),
                new VisitData(11, true),
                new VisitData(12, true),
                new VisitData(13, true),
                new VisitData(14, true)
                ));

        doTestForValues(topNode, Arrays.asList(
                new VisitData(0, false)
                ));

        doTestForValues(topNode, Arrays.asList(
                new VisitData(0, true),
                new VisitData(1, false),
                new VisitData(8, false)
                ));

        doTestForValues(topNode, Arrays.asList(
                new VisitData(0, true),
                new VisitData(1, true),
                new VisitData(2, true),
                new VisitData(3, true),
                new VisitData(4, true),
                new VisitData(5, true),
                new VisitData(6, false),
                new VisitData(7, false),
                new VisitData(8, true),
                new VisitData(9, false),
                // These are skipped because children of 9 aren't visited.
                //new VisitData(10, false),
                //new VisitData(11, false),
                new VisitData(12, true),
                new VisitData(13, false),
                new VisitData(14, true)
                ));

    }
}
