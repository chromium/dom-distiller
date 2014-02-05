// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.google.gwt.junit.client.GWTTestCase;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.DOM;

import org.xml.sax.Attributes;

public class DomToSaxParserTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testGetAttributes() {
        Element e = Document.get().createDivElement();
        e.setInnerHTML("<div style=\"width:50px; height:100px\" id=\"f\" class=\"sdf\"></div>");
        e = Element.as(e.getChildNodes().getItem(0));
        JsArray<Node> jsAttrs = DomToSaxParser.getAttributes(e);
        assertEquals(3, jsAttrs.length());
    }

    public void testGetSaxAttributes() {
        Element e = Document.get().createDivElement();
        e.setInnerHTML("<div style=\"width:50px; height:100px\" id=\"f\" class=\"sdf\"></div>");
        e = Element.as(e.getChildNodes().getItem(0));
        Attributes attrs = DomToSaxParser.getSaxAttributes(e);
        assertEquals(3, attrs.getLength());
        assertEquals("f", attrs.getValue("id"));
        assertEquals("sdf", attrs.getValue("class"));
    }

    private void runDomParserTest(String innerHtml) throws Throwable {
        Element container = Document.get().createDivElement();
        container.setInnerHTML(innerHtml);
        SimpleContentHandler contentHandler = new SimpleContentHandler();
        DomToSaxParser.parse(container, contentHandler);
        String expectedDocument = "<div>" + innerHtml + "</div>";
        assertEquals(expectedDocument, contentHandler.getDocumentString().toLowerCase());
    }

    public void testDomParserText() throws Throwable {
        runDomParserTest("foo");
    }

    public void testDomParserElement() throws Throwable {
        runDomParserTest("<div></div>");
    }

    public void testDomParserSiblings() throws Throwable {
        runDomParserTest("<div></div><div></div>");
    }

    public void testDomParserAttribute() throws Throwable {
        runDomParserTest("<div style=\"width:50px; height:100px\" id=\"f\" class=\"sdf\"></div>");
    }

    public void testDomParserSimplePage() throws Throwable {
        String simpleHtml =
                "<div id=\"a\" class=\"foo\">foo<a href=\"x.com/#1\">x.com</a>bar</div>" +
                "<div>baz</div>";
        runDomParserTest(simpleHtml);
    }
}
