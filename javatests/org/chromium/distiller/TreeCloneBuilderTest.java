// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import java.util.ArrayList;
import java.util.List;

public class TreeCloneBuilderTest extends DomDistillerJsTestCase {
    public void testFullBuilder() {
        List<Element> divs = TestUtil.createDivTree();
        List<Node> leafNodes = new ArrayList<Node>();
        leafNodes.add(divs.get(3));
        leafNodes.add(divs.get(4));
        leafNodes.add(divs.get(5));
        leafNodes.add(divs.get(14));
        Node root = TreeCloneBuilder.buildTreeClone(leafNodes);

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
                TestUtil.removeAllDirAttributes(Element.as(root).getString()));
    }

    public void testSingleNodeList() {
        List<Node> leaf = new ArrayList<Node>();
        leaf.add(Document.get().createTextNode("some content"));

        Node root = TreeCloneBuilder.buildTreeClone(leaf);
        
        assertEquals(0, root.getChildCount());
        assertEquals(Text.as(leaf.get(0)).getData(), Text.as(root).getData());
    }

    public void testCloneElement() {
        Element element = Document.get().createDivElement();
        Node clone = TreeCloneBuilder.cloneNode(element);

        assertEquals("<div dir=\"auto\"></div>", Element.as(clone).getString());
    }

    public void testCloneTextNode() {
        Node n = Document.get().createTextNode("some content");
        Node clone = TreeCloneBuilder.cloneNode(n);

        Element container = Document.get().createDivElement();
        container.appendChild(clone);

        assertEquals("some content", container.getInnerHTML());
    }
}
