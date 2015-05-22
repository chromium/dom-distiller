// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.DomWalker;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

import java.util.List;

public class DomConverterTest extends DomDistillerJsTestCase {
    private void runTest(String innerHtml, String expectedHtml) throws Throwable {
        Element container = Document.get().createDivElement();
        mBody.appendChild(container);
        container.setInnerHTML(innerHtml);
        FakeWebDocumentBuilder builder = new FakeWebDocumentBuilder();
        DomConverter filteringDomVisitor = new DomConverter(builder);
        new DomWalker(filteringDomVisitor).walk(container);
        String expectedDocument = "<div>" + expectedHtml + "</div>";
        assertEquals(expectedDocument, builder.getDocumentString().toLowerCase());
    }

    public void testVisibleText() throws Throwable {
        String html = "visible text";
        runTest(html, html);
    }

    public void testVisibleElement() throws Throwable {
        String html = "<div>visible element</div>";
        runTest(html, html);
    }

    public void testDisplayNone() throws Throwable {
        runTest("<div style=\"display:none\">diplay none</div>", "");
    }

    public void testVisibilityHidden() throws Throwable {
        runTest("<div style=\"visibility:hidden\">visibility hidden</div>", "");
    }

    public void testInvisibleInVisible() throws Throwable {
        String html = "<div>visible parent" +
                          "<div style=\"display:none\">invisible child</div>" +
                      "</div>";
        runTest(html, "<div>visible parent</div>");
    }

    public void testVisibleInInvisible() throws Throwable {
        String html = "<div style=\"visibility:hidden\">invisible parent" +
                          "<div>visible child</div>" +
                      "</div>";
        runTest(html, "");
    }

    public void testVisibleInVisible() throws Throwable {
        String html = "<div>visible parent" +
                          "<div>visible child</div>" +
                      "</div>";
        runTest(html, html);
    }

    public void testInvisibleInInvisible() throws Throwable {
        String html = "<div style=\"visibility:hidden\">invisible parent" +
                          "<div style=\"display:none\">invisible child</div>" +
                      "</div>";
        runTest(html, "");
    }

    public void testDifferentChildrenInVisible() throws Throwable {
        String html = "<div>visible parent" +
                          "<div style=\"display:none\">invisible child0</div>" +
                          "<div>visible child1" +
                              "<div style=\"visibility:hidden\">invisible grandchild</div>" +
                          "</div>" +
                          "<div style=\"visibility:hidden\">invisible child2</div>" +
                      "</div>";
        runTest(html, "<div>visible parent<div>visible child1</div></div>");
    }

    public void testDifferentChildrenInInvisible() throws Throwable {
        String html = "<div style=\"visibility:hidden\">invisible parent" +
                          "<div style=\"display:none\">invisible child0</div>" +
                          "<div>visible child1" +
                              "<div style=\"display:none\">invisible grandchild</div>" +
                          "</div>" +
                          "<div style=\"visibility:hidden\">invisible child2</div>" +
                      "</div>";
        runTest(html, "");
    }

    // Note: getComputedStyle() doesn't work correctly when running ant test.dev or test.prod,
    // but it works in production mode in the browser, so the test is in chrome's components/
    // dom_distiller/content/distiller_page_web_contents_browsertest.cc::VisibilityDetection().

    public void testDataTable() throws Throwable {
        String html = "<table align=\"left\" role=\"grid\">" + // role=grid make this a data table.
                          "<tbody align=\"left\">" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                              "</tr>" +
                          "</tbody>" +
                      "</table>";
        runTest(html, "<datatable/>");
    }

    public void testNonDataTable() throws Throwable {
        String html = "<table align=\"left\">" +
                          "<tbody align=\"left\">" +
                              "<tr>" +
                                  "<td>row1col1</td>" +
                                  "<td>row1col2</td>" +
                              "</tr>" +
                          "</tbody>" +
                      "</table>";
        runTest(html, html);
    }

    public void testIgnorableElementsNonDataTable() throws Throwable {
        runTest("<style></style>", "");
        runTest("<link></link>", "");
        runTest("<script></script>", "");
        runTest("<noscript></noscript>", "");
        runTest("<applet></applet>", "");
        runTest("<object></object>", "");
        runTest("<option></option>", "");
        runTest("<embed></embed>", "");
    }

    public void testElementOrder() {
        Element container = Document.get().createDivElement();
        container.setInnerHTML("Text content <img src=\"http://example.com/1.jpg\"> more content");

        WebDocumentBuilder builder = new WebDocumentBuilder();
        DomConverter converter = new DomConverter(builder);
        new DomWalker(converter).walk(container);

        WebDocument doc = builder.toWebDocument();
        List<WebElement> elements = doc.getElements();

        assertEquals(3, elements.size());
        assertTrue(elements.get(0) instanceof WebText);
        assertTrue(elements.get(1) instanceof WebImage);
        assertTrue(elements.get(2) instanceof WebText);
    }

    public void testLineBreak() throws Throwable {
        String html = "text<br>split<br/>with<br/>lines";
        runTest(html, "text\nsplit\nwith\nlines");
    }
}
