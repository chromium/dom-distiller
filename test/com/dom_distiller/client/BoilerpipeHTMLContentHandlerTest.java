// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.dom_distiller.client.sax.AttributesImpl;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.junit.client.GWTTestCase;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.sax.BoilerpipeHTMLContentHandler;

import java.util.List;

public class BoilerpipeHTMLContentHandlerTest extends GWTTestCase {

    private static final String TEXT1 = "Some really long text which should be content.";
    private static final String TEXT2 = "Another really long text thing which should be content.";
    private static final String TEXT3 = "And again a third long text for testing.";
    private BoilerpipeHTMLContentHandler mHandler;
    private Element mBody;

    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        mHandler = new BoilerpipeHTMLContentHandler();
        mHandler.startDocument();
        mBody = Document.get().createElement("body");
        startElement(mBody);
    }

    public void testSpansAsInline() {
        // <span>
        //   TEXT1
        //   <span>
        //     TEXT2
        //   </span>
        //   TEXT3
        // </span>
        Element outerSpan = Document.get().createSpanElement();
        startElement(outerSpan);
        addText(TEXT1);
        Element innerSpan = Document.get().createSpanElement();
        startElement(innerSpan);
        addText(TEXT2);
        endElement(innerSpan);
        addText(TEXT3);
        endElement(outerSpan);

        endBodyAndDocument();

        assertInline();
    }

    public void testDivsAsInline() {
        // <span>
        //   TEXT1
        //   <div style="display: inline;">
        //     TEXT2
        //   </div>
        //   TEXT3
        // </span>
        Element span = Document.get().createSpanElement();
        startElement(span);
        addText(TEXT1);
        Element div = Document.get().createDivElement();
        div.setAttribute("style", "display: inline;");
        startElement(div);
        addText(TEXT2);
        endElement(div);
        addText(TEXT3);
        endElement(span);

        endBodyAndDocument();

        assertInline();
    }

    public void testDivsAsBlocks() {
        // <div>
        //   TEXT1
        //   <div>
        //     TEXT2
        //   </div>
        //   TEXT3
        // </div>
        Element div = Document.get().createDivElement();
        startElement(div);
        addText(TEXT1);
        Element span = Document.get().createDivElement();
        startElement(span);
        addText(TEXT2);
        endElement(span);
        addText(TEXT3);
        endElement(div);

        endBodyAndDocument();

        assertBlock();
    }

    public void testSpansAsBlocks() {
        // <div>
        //   TEXT1
        //   <span style="display: block;">
        //     TEXT2
        //   </span>
        //   TEXT3
        // </div>
        Element div = Document.get().createDivElement();
        startElement(div);
        addText(TEXT1);
        Element span = Document.get().createSpanElement();
        span.setAttribute("style", "display: block;");
        startElement(span);
        addText(TEXT2);
        endElement(span);
        addText(TEXT3);
        endElement(div);

        endBodyAndDocument();

        assertBlock();
    }

    public void testHeadingsAsBlocks() {
        // <div>
        //   TEXT1
        //   <h1>
        //     TEXT2
        //   </h1>
        //   TEXT3
        // </div>
        Element div = Document.get().createDivElement();
        startElement(div);
        addText(TEXT1);
        Element h1 = Document.get().createElement("h1");
        startElement(h1);
        addText(TEXT2);
        endElement(h1);
        addText(TEXT3);
        endElement(div);

        endBodyAndDocument();

        assertBlock();
    }

    private void startElement(Element e) {
        mHandler.startElement(e, new AttributesImpl());
    }

    private void addText(String text) {
        mHandler.textNode(Document.get().createTextNode(text));
    }

    private void endElement(Element e) {
        mHandler.endElement(e);
    }

    private void endBodyAndDocument() {
        endElement(mBody);
        mHandler.endDocument();
    }

    private void assertBlock() {
        List<TextBlock> textBlocks = mHandler.toTextDocument().getTextBlocks();
        assertEquals(3, textBlocks.size());
        assertEquals(2, textBlocks.get(0).getTagLevel());
        assertEquals(3, textBlocks.get(1).getTagLevel());
        assertEquals(2, textBlocks.get(2).getTagLevel());
    }

    private void assertInline() {
        List<TextBlock> textBlocks = mHandler.toTextDocument().getTextBlocks();
        assertEquals(1, textBlocks.size());
        assertEquals(1, textBlocks.get(0).getTagLevel());
    }
}
