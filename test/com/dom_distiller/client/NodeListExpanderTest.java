// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;
import java.util.List;

public class NodeListExpanderTest extends DomDistillerTestCase {
    public void testNodeListExpander() {
        List<Element> divs = TestUtil.createDivTree();
        List<Node> leafNodes = new ArrayList<Node>();
        leafNodes.add(divs.get(3));
        leafNodes.add(divs.get(4));
        leafNodes.add(divs.get(5));
        leafNodes.add(divs.get(14));
        NodeTree subtree = NodeListExpander.expand(leafNodes);

        // This is TestUtil.expectedDivTreeHtml with the nodes that should not be included
        // commented out.
        assertEquals(
                "<div id=\"0\">" +
                    "<div id=\"1\">" +
                        "<div id=\"2\">" +
                            "<div id=\"3\"></div>" +
                            "<div id=\"4\"></div>" +
                        "</div>" +
                        "<div id=\"5\">" +
                            //"<div id=\"6\"></div>" +
                            //"<div id=\"7\"></div>" +
                        "</div>" +
                    "</div>" +
                    "<div id=\"8\">" +
                        //"<div id=\"9\">" +
                            //"<div id=\"10\"></div>" +
                            //"<div id=\"11\"></div>" +
                        //"</div>" +
                        "<div id=\"12\">" +
                            //"<div id=\"13\"></div>" +
                            "<div id=\"14\"></div>" +
                        "</div>" +
                    "</div>" +
                "</div>",
                TestUtil.getElementAsString(Element.as(subtree.cloneSubtree())));
    }

    public void testNodeListExpanderPruneTopChain() {
        List<Element> divs = TestUtil.createDivTree();
        List<Node> leafNodes = new ArrayList<Node>();
        leafNodes.add(divs.get(2));
        leafNodes.add(divs.get(3));
        NodeTree subtree = NodeListExpander.expand(leafNodes);

        // This is TestUtil.expectedDivTreeHtml with the nodes that should not be included
        // commented out.
        assertEquals("<div id=\"2\"><div id=\"3\"></div></div>",
                TestUtil.getElementAsString(Element.as(subtree.cloneSubtree())));
    }

}
