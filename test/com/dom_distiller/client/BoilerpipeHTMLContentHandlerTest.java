// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.sax.BoilerpipeHTMLContentHandler;

import java.util.List;

public class BoilerpipeHTMLContentHandlerTest extends DomDistillerTestCase {

    private static final String TEXT1 = "Some really long text which should be content.";
    private static final String TEXT2 = "Another really long text thing which should be content.";
    private static final String TEXT3 = "And again a third long text for testing.";
    private BoilerpipeHTMLContentHandler mHandler;

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        mHandler = new BoilerpipeHTMLContentHandler();
        mHandler.startDocument();
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

    public void testKeepsWhitespaceWithinTextBlock() {
        //
        // <div>
        //   TEXT1
        //
        //   <span>
        //     TEXT2
        //   </span>
        //   TEXT3
        // </div>
        //
        Element outerDiv = Document.get().createDivElement();
        addText("\n"); // ignored
        startElement(outerDiv);
        addText("\n");
        addText(TEXT1);
        addText("\n");
        Element innerSpan = Document.get().createSpanElement();
        startElement(innerSpan);
        addText("\n");
        addText(TEXT2);
        addText("\n");
        endElement(innerSpan);
        addText("\n");
        addText(TEXT3);
        addText("\n");
        endElement(outerDiv);
        addText("\n"); // ignored
        endBodyAndDocument();
        List<TextBlock> textBlocks = mHandler.toTextDocument().getTextBlocks();
        assertEquals(1, textBlocks.size());
        assertEquals(TEXT1 + " " + TEXT2 + " " + TEXT3, textBlocks.get(0).getText());
        assertEquals(3, textBlocks.get(0).getNonWhitespaceTextElements().size());
        assertEquals(
                "\n" +
                "TEXT1\n" +
                "\n" +
                "TEXT2\n" +
                "\n" +
                "TEXT3\n",
                joinTextNodes(textBlocks.get(0).getAllTextElements()));
    }

    public void testDiscardsWhitespaceBetweenTextBlocks() {
        //
        // <div>
        //   TEXT1
        // </div>
        //
        // <div>
        //   TEXT2
        //   TEXT3</div>
        //
        Element firstDiv = Document.get().createDivElement();
        addText("\n"); // ignored
        startElement(firstDiv);
        addText("\n");
        addText(TEXT1);
        addText("\n");
        endElement(firstDiv);

        addText("\n \n"); // ignored

        Element secondDiv = Document.get().createDivElement();
        startElement(secondDiv);
        addText("\n");
        addText(TEXT2);
        addText("\n");
        addText(TEXT3);
        endElement(secondDiv);

        addText("\n"); // ignored
        addText("\n"); // ignored
        endBodyAndDocument();

        List<TextBlock> textBlocks = mHandler.toTextDocument().getTextBlocks();
        assertEquals(2, textBlocks.size());
        assertEquals(TEXT1, textBlocks.get(0).getText());
        assertEquals(1, textBlocks.get(0).getNonWhitespaceTextElements().size());
        assertEquals(
                "\n" +
                "TEXT1\n",
                joinTextNodes(textBlocks.get(0).getAllTextElements()));

        assertEquals(TEXT2 + " " + TEXT3, textBlocks.get(1).getText());
        assertEquals(2, textBlocks.get(1).getNonWhitespaceTextElements().size());
        assertEquals(
                "\n" +
                "TEXT2\n" +
                "TEXT3",
                joinTextNodes(textBlocks.get(1).getAllTextElements()));
    }

    public void testNonWordCharcterMergedWithNextInlineTextBlock() {
        //
        // <div>
        //   -
        //   <span>TEXT1</span>
        //   <div>TEXT2</div>
        // </div>
        Element firstDiv = Document.get().createDivElement();
        addText("\n"); // ignored
        startElement(firstDiv);
        addText("\n");
        addText("-");
        addText("\n");
        Element innerSpan = Document.get().createSpanElement();
        startElement(innerSpan);
        addText(TEXT1);
        endElement(innerSpan);
        addText("\n");
        Element innerDiv = Document.get().createDivElement();
        startElement(innerDiv);
        addText(TEXT2);
        endElement(innerDiv);
        addText("\n"); // ignored
        endElement(firstDiv);

        endBodyAndDocument();

        List<TextBlock> textBlocks = mHandler.toTextDocument().getTextBlocks();
        assertEquals(2, textBlocks.size());
        assertEquals("- " + TEXT1, textBlocks.get(0).getText());
        assertEquals(2, textBlocks.get(0).getNonWhitespaceTextElements().size());
        assertEquals(
                "\n" +
                "-\n" +
                "TEXT1\n",
                joinTextNodes(textBlocks.get(0).getAllTextElements()));

        assertEquals(TEXT2, textBlocks.get(1).getText());
        assertEquals(1, textBlocks.get(1).getNonWhitespaceTextElements().size());
        assertEquals("TEXT2",
                    joinTextNodes(textBlocks.get(1).getAllTextElements()));
    }

    public void testNonWordCharcterNotMergedWithNextBlockLevelTextBlock() {
        //
        // <div>
        //   -
        //   <div>TEXT1</div>
        //   <span>TEXT2</span>
        // </div>
        Element firstDiv = Document.get().createDivElement();
        addText("\n"); // ignored
        startElement(firstDiv);
        addText("\n");
        addText("-");
        addText("\n");
        Element innerDiv = Document.get().createDivElement();
        startElement(innerDiv);
        addText(TEXT1);
        endElement(innerDiv);
        addText("\n");
        Element innerSpan = Document.get().createSpanElement();
        startElement(innerSpan);
        addText(TEXT2);
        endElement(innerSpan);
        addText("\n"); // ignored
        endElement(firstDiv);

        endBodyAndDocument();

        List<TextBlock> textBlocks = mHandler.toTextDocument().getTextBlocks();
        assertEquals(3, textBlocks.size());
        assertEquals("-", textBlocks.get(0).getText());
        assertEquals(1, textBlocks.get(0).getNonWhitespaceTextElements().size());
        assertEquals(
                "\n" +
                "-\n",
                joinTextNodes(textBlocks.get(0).getAllTextElements()));

        assertEquals(TEXT1, textBlocks.get(1).getText());
        assertEquals(1, textBlocks.get(1).getNonWhitespaceTextElements().size());
        assertEquals(
                "TEXT1",
                joinTextNodes(textBlocks.get(1).getAllTextElements()));

        assertEquals(TEXT2, textBlocks.get(2).getText());
        assertEquals(1, textBlocks.get(2).getNonWhitespaceTextElements().size());
        assertEquals(
                "\n" +
                "TEXT2\n",
                joinTextNodes(textBlocks.get(2).getAllTextElements()));
    }

    // Simulates many social-bar/leading-link type UIs where lists are used for laying out images.
    public void testEmptyBlockNotMergedWithNextBlock() {
        // <ul>
        //   <li><a href="foo.html> </a>
        //   </li>
        //   <li>TEXT1
        //   </li>
        // </ul>
        Element ul = Document.get().createULElement();
        startElement(ul);
        addText("\n");

        Element li = Document.get().createLIElement();
        startElement(li);
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setHref("foo.html");
        startElement(anchor);
        addText(" ");
        endElement(anchor);
        addText("\n");
        endElement(li);
        addText("\n");

        startElement(li);
        addText(TEXT1);
        addText("\n");
        endElement(li);
        endElement(ul);

        endBodyAndDocument();

        List<TextBlock> textBlocks = mHandler.toTextDocument().getTextBlocks();
        assertEquals(1, textBlocks.size());
        assertEquals(TEXT1, textBlocks.get(0).getText());
        assertEquals(1, textBlocks.get(0).getNonWhitespaceTextElements().size());
        assertEquals(
                "TEXT1\n",
                joinTextNodes(textBlocks.get(0).getAllTextElements()));    }

    private static String joinTextNodes(List<Node> elements) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < elements.size(); i++) {
            String value = elements.get(i).getNodeValue();
            // Convert back to the constant names to make tests easier to parse.
            value = value.replaceAll(TEXT1, "TEXT1").
                    replaceAll(TEXT2, "TEXT2").
                    replaceAll(TEXT3, "TEXT3");
            sb.append(value);
        }
        return sb.toString();
    }

    private void startElement(Element e) {
        mHandler.startElement(e);
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
