// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;

public class WebTableTest extends DomDistillerJsTestCase {
    public void testAddOutputNodes() {
        Element table = Document.get().createTableElement();
        String html = "<tbody>" +
                          "<tr>" +
                              "<td>row1col1</td>" +
                              "<td><img src=\"./table.png\"></td>" +
                          "</tr>" +
                      "</tbody>";
        table.setInnerHTML(html);
        WebTable webTable = new WebTable(table);

        ArrayList<Node> contentNodes = new ArrayList<Node>();
        webTable.addOutputNodes(contentNodes, false);

        // Expected nodes: <table><tbody><tr><td>#text<td><img>.
        assertEquals(7, contentNodes.size());

        Node n = contentNodes.get(0);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TABLE", Element.as(n).getNodeName());

        n = contentNodes.get(1);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TBODY", Element.as(n).getNodeName());

        n = contentNodes.get(2);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TR", Element.as(n).getNodeName());

        n = contentNodes.get(3);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TD", Element.as(n).getNodeName());
        n = contentNodes.get(4);
        assertEquals("#text", n.getNodeName());
        assertEquals("row1col1", n.getNodeValue());
        n = contentNodes.get(5);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TD", Element.as(n).getNodeName());
        n = contentNodes.get(6);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("IMG", Element.as(n).getNodeName());
    }

    public void testAddOutputNodesWithHiddenChildren() {
        Element table = Document.get().createTableElement();
        String html = "<tbody>" +
                          "<tr>" +
                              "<td>row1col1</td>" +
                              // Since the <img> is hidden, it should not be included in the final
                              // output.
                              "<td><img src=\"./table.png\" style=\"display:none\"></td>" +
                          "</tr>" +
                      "</tbody>";
        table.setInnerHTML(html);
        mBody.appendChild(table);
        WebTable webTable = new WebTable(table);

        ArrayList<Node> contentNodes = new ArrayList<Node>();
        webTable.addOutputNodes(contentNodes, false);

        // Expected nodes: <table><tbody><tr><td>#text<td>.
        assertEquals(6, contentNodes.size());

        Node n = contentNodes.get(0);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TABLE", Element.as(n).getNodeName());

        n = contentNodes.get(1);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TBODY", Element.as(n).getNodeName());

        n = contentNodes.get(2);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TR", Element.as(n).getNodeName());

        n = contentNodes.get(3);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TD", Element.as(n).getNodeName());
        n = contentNodes.get(4);
        assertEquals("#text", n.getNodeName());
        assertEquals("row1col1", n.getNodeValue());
        n = contentNodes.get(5);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("TD", Element.as(n).getNodeName());
    }

    public void testAddOutputNodesNestedTable() {
        Element table = Document.get().createTableElement();
        String html = "<tbody><tr>" +
            "<td><table><tbody><tr><td>nested</td></tr></tbody></table></td>" +
            "<td>outer</td>" +
            "</tr></tbody>";
        table.setInnerHTML(html);
        mBody.appendChild(table);
        WebTable webTable = new WebTable(table);

        ArrayList<Node> contentNodes = new ArrayList<Node>();
        webTable.addOutputNodes(contentNodes, false);

        assertEquals(11, contentNodes.size());
    }
}
