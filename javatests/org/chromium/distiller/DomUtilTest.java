// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import java.util.Map;

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
}
