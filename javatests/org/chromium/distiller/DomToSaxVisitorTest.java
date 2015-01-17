// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class DomToSaxVisitorTest extends DomDistillerJsTestCase {
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
