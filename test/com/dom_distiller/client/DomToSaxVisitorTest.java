// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.junit.client.GWTTestCase;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import com.dom_distiller.client.sax.Attributes;

public class DomToSaxVisitorTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testGetAttributes() {
        Element e = Document.get().createDivElement();
        e.setInnerHTML("<div style=\"width:50px; height:100px\" id=\"f\" class=\"sdf\"></div>");
        e = Element.as(e.getChildNodes().getItem(0));
        JsArray<Node> jsAttrs = DomUtil.getAttributes(e);
        assertEquals(3, jsAttrs.length());
    }

    public void testGetSaxAttributes() {
        Element e = Document.get().createDivElement();
        e.setInnerHTML("<div style=\"width:50px; height:100px\" id=\"f\" class=\"sdf\"></div>");
        e = Element.as(e.getChildNodes().getItem(0));
        Attributes attrs = DomToSaxVisitor.getSaxAttributes(e);
        assertEquals(3, attrs.getLength());
        assertEquals("f", attrs.getValue("id"));
        assertEquals("sdf", attrs.getValue("class"));
    }

    private void runDomVisitorTest(String innerHtml) throws Throwable {
        Element container = Document.get().createDivElement();
        container.setInnerHTML(innerHtml);
        SimpleContentHandler contentHandler = new SimpleContentHandler();
        DomToSaxVisitor visitor = new DomToSaxVisitor(contentHandler);
        new DomWalker(visitor).walk(container);
        String expectedDocument = "<div>" + innerHtml + "</div>";
        assertEquals(expectedDocument, contentHandler.getDocumentString().toLowerCase());
    }

    public void testDomVisitorText() throws Throwable {
        runDomVisitorTest("foo");
    }

    public void testDomVisitorElement() throws Throwable {
        runDomVisitorTest("<div></div>");
    }

    public void testDomVisitorSiblings() throws Throwable {
        runDomVisitorTest("<div></div><div></div>");
    }

    public void testDomVisitorAttribute() throws Throwable {
        runDomVisitorTest("<div style=\"width:50px; height:100px\" id=\"f\" class=\"sdf\"></div>");
    }

    public void testDomVisitorSimplePage() throws Throwable {
        String simpleHtml =
                "<div id=\"a\" class=\"foo\">foo<a href=\"x.com/#1\">x.com</a>bar</div>" +
                "<div>baz</div>";
        runDomVisitorTest(simpleHtml);
    }
}
