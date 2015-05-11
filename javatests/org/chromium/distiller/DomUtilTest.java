// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.webdocument.WebTable;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.Map;
import java.util.List;

public class DomUtilTest extends DomDistillerJsTestCase {
    public void testGetAttributes() {
        Element e = Document.get().createDivElement();
        e.setInnerHTML("<div style=\"width:50px; height:100px\" id=\"f\" class=\"sdf\"></div>");
        e = Element.as(e.getChildNodes().getItem(0));
        JsArray<Node> jsAttrs = DomUtil.getAttributes(e);
        assertEquals(3, jsAttrs.length());
        assertEquals("style", jsAttrs.get(0).getNodeName());
        assertEquals("width:50px; height:100px", jsAttrs.get(0).getNodeValue());
        assertEquals("id", jsAttrs.get(1).getNodeName());
        assertEquals("f", jsAttrs.get(1).getNodeValue());
        assertEquals("class", jsAttrs.get(2).getNodeName());
        assertEquals("sdf", jsAttrs.get(2).getNodeValue());
    }

    public void testGetFirstElementWithClassName() {
        Element rootDiv = TestUtil.createDiv(0);

        Element div1 = TestUtil.createDiv(1);
        div1.addClassName("abcd");
        rootDiv.appendChild(div1);

        Element div2 = TestUtil.createDiv(2);
        div2.addClassName("test");
        div2.addClassName("xyz");
        rootDiv.appendChild(div2);

        Element div3 = TestUtil.createDiv(2);
        div3.addClassName("foobar foo");
        rootDiv.appendChild(div3);

        assertEquals(div1, DomUtil.getFirstElementWithClassName(rootDiv, "abcd"));
        assertEquals(div2, DomUtil.getFirstElementWithClassName(rootDiv, "test"));
        assertEquals(div2, DomUtil.getFirstElementWithClassName(rootDiv, "xyz"));
        assertEquals(null, DomUtil.getFirstElementWithClassName(rootDiv, "bc"));
        assertEquals(null, DomUtil.getFirstElementWithClassName(rootDiv, "t xy"));
        assertEquals(null, DomUtil.getFirstElementWithClassName(rootDiv, "tes"));
        assertEquals(div3, DomUtil.getFirstElementWithClassName(rootDiv, "foo"));
    }

    public void testHasRootDomain() {
        // Positive tests.
        assertTrue(DomUtil.hasRootDomain("http://www.foo.bar/foo/bar.html", "foo.bar"));
        assertTrue(DomUtil.hasRootDomain("https://www.m.foo.bar/foo/bar.html", "foo.bar"));
        assertTrue(DomUtil.hasRootDomain("https://www.m.foo.bar/foo/bar.html", "www.m.foo.bar"));
        assertTrue(DomUtil.hasRootDomain("http://localhost/foo/bar.html", "localhost"));
        assertTrue(DomUtil.hasRootDomain("https://www.m.foo.bar.baz", "foo.bar.baz"));
        // Negative tests.
        assertFalse(DomUtil.hasRootDomain("https://www.m.foo.bar.baz", "x.foo.bar.baz"));
        assertFalse(DomUtil.hasRootDomain("https://www.foo.bar.baz", "foo.bar"));
        assertFalse(DomUtil.hasRootDomain("http://foo", "m.foo"));
        assertFalse(DomUtil.hasRootDomain("https://www.badfoobar.baz", "foobar.baz"));
        assertFalse(DomUtil.hasRootDomain("", "foo"));
        assertFalse(DomUtil.hasRootDomain("http://foo.bar", ""));
        assertFalse(DomUtil.hasRootDomain(null, "foo"));
        assertFalse(DomUtil.hasRootDomain("http://foo.bar", null));
    }

    public void testSplitUrlParams() {
        Map<String, String> result = DomUtil.splitUrlParams("param1=apple&param2=banana");
        assertEquals(2, result.size());
        assertEquals("apple", result.get("param1"));
        assertEquals("banana", result.get("param2"));

        result = DomUtil.splitUrlParams("123=abc");
        assertEquals(1, result.size());
        assertEquals("abc", result.get("123"));

        result = DomUtil.splitUrlParams("");
        assertEquals(0, result.size());

        result = DomUtil.splitUrlParams(null);
        assertEquals(0, result.size());
    }

    public void testNearestCommonAncestor() {
        Element div = TestUtil.createDiv(1);

        Element div2 = TestUtil.createDiv(2);
        div.appendChild(div2);

        Element currDiv = TestUtil.createDiv(3);
        div2.appendChild(currDiv);
        Element finalDiv1 = currDiv;

        currDiv = TestUtil.createDiv(4);
        div2.appendChild(currDiv);
        currDiv.appendChild(TestUtil.createDiv(5));

        assertEquals(div2, DomUtil.getNearestCommonAncestor(finalDiv1, currDiv.getChild(0)));
    }

    public void testNearestCommonAncestorIsRoot() {
        Element div = TestUtil.createDiv(1);

        Element div2 = TestUtil.createDiv(2);
        div.appendChild(div2);

        Element div3 = TestUtil.createDiv(3);
        div2.appendChild(div3);

        assertEquals(div, DomUtil.getNearestCommonAncestor(div, div3));
    }

    public void testNodeDepth() {
        Element div = TestUtil.createDiv(1);

        Element div2 = TestUtil.createDiv(2);
        div.appendChild(div2);

        Element div3 = TestUtil.createDiv(3);
        div2.appendChild(div3);

        assertEquals(2, DomUtil.getNodeDepth(div3));
    }

    public void testZeroOrNoNodeDepth() {
        Element div = TestUtil.createDiv(0);
        assertEquals(0, DomUtil.getNodeDepth(div));
        assertEquals(-1, DomUtil.getNodeDepth(null));
    }

    public void testGetOutputNodes() {
        Element div = Document.get().createDivElement();
        String html = "<p>" +
                          "<span>Some content</span>" +
                          "<img src=\"./image.png\">" +
                      "</p>";
        div.setInnerHTML(html);
        mBody.appendChild(div);

        List<Node> contentNodes = DomUtil.getOutputNodes(div);

        // Expected nodes: <div><p><span>#text<img>.
        assertEquals(5, contentNodes.size());

        Node n = contentNodes.get(0);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("DIV", Element.as(n).getNodeName());

        n = contentNodes.get(1);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("P", Element.as(n).getNodeName());

        n = contentNodes.get(2);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("SPAN", Element.as(n).getNodeName());

        n = contentNodes.get(3);
        assertEquals(Node.TEXT_NODE, n.getNodeType());

        n = contentNodes.get(4);
        assertEquals(Node.ELEMENT_NODE, n.getNodeType());
        assertEquals("IMG", Element.as(n).getNodeName());
    }

    public void testGetOutputNodesWithHiddenChildren() {
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

        List<Node> contentNodes = DomUtil.getOutputNodes(webTable.getTableElement());

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

    public void testGetOutputNodesNestedTable() {
        Element table = Document.get().createTableElement();
        String html = "<tbody><tr>" +
            "<td><table><tbody><tr><td>nested</td></tr></tbody></table></td>" +
            "<td>outer</td>" +
            "</tr></tbody>";
        table.setInnerHTML(html);
        mBody.appendChild(table);
        WebTable webTable = new WebTable(table);

        List<Node> contentNodes = DomUtil.getOutputNodes(webTable.getTableElement());

        assertEquals(11, contentNodes.size());
    }

    public void testMakeAllLinksAbsolute() {
        final String html =
            "<!DOCTYPE html>" +
            "<html><head><base href=\"http://example.com/\"></head><body>" +
            "<a href=\"link\"></a>" +
            "<img src=\"image\" srcset=\"image200 200w, image400 400w\">" +
            "<video src=\"video\" poster=\"poster\">" +
            "<source src=\"source\">" +
            "<track src=\"track\"></track>" +
            "</video>" +
            "</body></html>";

        final String expected =
            "<a href=\"http://example.com/link\"></a>" +
            "<img src=\"http://example.com/image\">" +
            "<video src=\"http://example.com/video\" poster=\"http://example.com/poster\">" +
            "<source src=\"http://example.com/source\">" +
            "<track src=\"http://example.com/track\"></track>" +
            "</video>";

        Document doc = DomUtil.createHTMLDocument(Document.get());
        Element root = doc.getDocumentElement();
        root.setInnerHTML(html);
        DomUtil.makeAllLinksAbsolute(root);
        assertEquals(expected, doc.getBody().getInnerHTML());
    }

    public void testStripTableBackgroundColorAttributes() {
        String tableWithBGColorHTML =
            "<table bgcolor=\"red\">" +
                "<tbody>" +
                    "<tr bgcolor=\"red\">" +
                        "<th bgcolor=\"red\">text</th>" +
                        "<th bgcolor=\"red\">text</th>" +
                    "</tr><tr bgcolor=\"red\">" +
                        "<td bgcolor=\"red\">text</td>" +
                        "<td bgcolor=\"red\">text</td>" +
                    "</tr>" +
                "</tbody>" +
            "</table>";

        final String expected =
            "<table>" +
                "<tbody>" +
                    "<tr>" +
                        "<th>text</th>" +
                        "<th>text</th>" +
                    "</tr><tr>" +
                        "<td>text</td>" +
                        "<td>text</td>" +
                    "</tr>" +
                "</tbody>" +
            "</table>";

        mBody.setInnerHTML(tableWithBGColorHTML);
        DomUtil.stripTableBackgroundColorAttributes(mBody);
        assertEquals(expected, mBody.getInnerHTML());

        // We also test stripping the table element directly to ensure that
        // stripTableBackgroundColorAttributes deals with root elements properly.
        mBody.setInnerHTML(tableWithBGColorHTML);
        DomUtil.stripTableBackgroundColorAttributes(mBody.getFirstChildElement());
        assertEquals(expected, mBody.getInnerHTML());
    }
}
