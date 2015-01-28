// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.TestTextDocumentBuilder;
import org.chromium.distiller.document.TextBlock;
import org.chromium.distiller.document.TextDocument;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

import java.util.List;

public class WebDocumentBuilderTest extends DomDistillerJsTestCase {

    private static final String TEXT1 = "Some really long text which should be content.";
    private static final String TEXT2 = "Another really long text thing which should be content.";
    private static final String TEXT3 = "And again a third long text for testing.";
    private WebDocumentBuilder mBuilder;
    private boolean mDocumentEnded;

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        mDocumentEnded = false;
        mBuilder = new WebDocumentBuilder();
        startElement(mBody);
    }

    private Element createSpanElement() {
        Element e = Document.get().createSpanElement();
        mBody.appendChild(e);
        return e;
    }

    private Element createDivElement() {
        Element e = Document.get().createDivElement();
        mBody.appendChild(e);
        return e;
    }

    private Element createElement(String tagName) {
        Element e = Document.get().createElement(tagName);
        mBody.appendChild(e);
        return e;
    }

    private Element createULElement() {
        Element e = Document.get().createULElement();
        mBody.appendChild(e);
        return e;
    }

    private Element createLIElement() {
        Element e = Document.get().createLIElement();
        mBody.appendChild(e);
        return e;
    }

    private AnchorElement createAnchorElement() {
        AnchorElement e = Document.get().createAnchorElement();
        mBody.appendChild(e);
        return e;
    }

    private Text createTextNode(String data) {
        Text e = Document.get().createTextNode(data);
        mBody.appendChild(e);
        return e;
    }

    public void testSpansAsInline() {
        // <span>
        //   TEXT1
        //   <span>
        //     TEXT2
        //   </span>
        //   TEXT3
        // </span>
        Element outerSpan = createSpanElement();
        startElement(outerSpan);
        addText(TEXT1);
        Element innerSpan = createSpanElement();
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
        Element span = createSpanElement();

        startElement(span);
        addText(TEXT1);
        Element div = createDivElement();
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
        Element div = createDivElement();
        startElement(div);
        addText(TEXT1);
        Element span = createDivElement();
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
        Element div = createDivElement();
        startElement(div);
        addText(TEXT1);
        Element span = createSpanElement();
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
        Element div = createDivElement();
        startElement(div);
        addText(TEXT1);
        Element h1 = createElement("h1");
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
        Element outerDiv = createDivElement();
        addText("\n"); // ignored
        startElement(outerDiv);
        addText("\n");
        addText(TEXT1);
        addText("\n");
        Element innerSpan = createSpanElement();
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
        List<TextBlock> textBlocks = getHandlerTextBlocks();
        assertEquals(1, textBlocks.size());
        assertEquals("\n" + TEXT1 + "\n\n" + TEXT2 + "\n\n" + TEXT3 + "\n",
                textBlocks.get(0).getText());
    }

    public void testNonWordCharacterMergedWithNextInlineTextBlock() {
        //
        // <div>
        //   -
        //   <span>TEXT1</span>
        //   <div>TEXT2</div>
        // </div>
        Element firstDiv = createDivElement();
        addText("\n"); // ignored
        startElement(firstDiv);
        addText("\n");
        addText("-");
        addText("\n");
        Element innerSpan = createSpanElement();
        startElement(innerSpan);
        addText(TEXT1);
        endElement(innerSpan);
        addText("\n");
        Element innerDiv = createDivElement();
        startElement(innerDiv);
        addText(TEXT2);
        endElement(innerDiv);
        addText("\n"); // ignored
        endElement(firstDiv);

        endBodyAndDocument();

        List<TextBlock> textBlocks = getHandlerTextBlocks();
        assertEquals(2, textBlocks.size());
        assertEquals("\n-\n" + TEXT1 + "\n", textBlocks.get(0).getText());
        assertEquals(TEXT2, textBlocks.get(1).getText());
    }

    public void testNonWordCharacterNotMergedWithNextBlockLevelTextBlock() {
        //
        // <div>
        //   -
        //   <div>TEXT1</div>
        //   <span>TEXT2</span>
        // </div>
        Element firstDiv = createDivElement();
        addText("\n"); // ignored
        startElement(firstDiv);
        addText("\n");
        addText("-");
        addText("\n");
        Element innerDiv = createDivElement();
        startElement(innerDiv);
        addText(TEXT1);
        endElement(innerDiv);
        addText("\n");
        Element innerSpan = createSpanElement();
        startElement(innerSpan);
        addText(TEXT2);
        endElement(innerSpan);
        addText("\n"); // ignored
        endElement(firstDiv);

        endBodyAndDocument();

        List<TextBlock> textBlocks = getHandlerTextBlocks();
        assertEquals(3, textBlocks.size());
        assertEquals("\n-\n", textBlocks.get(0).getText());
        assertEquals(TEXT1, textBlocks.get(1).getText());
        assertEquals("\n" + TEXT2 + "\n", textBlocks.get(2).getText());
    }

    // Simulates many social-bar/leading-link type UIs where lists are used for laying out images.
    public void testEmptyBlockNotMergedWithNextBlock() {
        // <ul>
        //   <li><a href="foo.html> </a>
        //   </li>
        //   <li>TEXT1
        //   </li>
        // </ul>
        Element ul = createULElement();
        startElement(ul);
        addText("\n");

        Element li = createLIElement();
        startElement(li);
        AnchorElement anchor = createAnchorElement();
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

        List<TextBlock> textBlocks = getHandlerTextBlocks();
        assertEquals(1, textBlocks.size());
        assertEquals(TEXT1 + "\n", textBlocks.get(0).getText());
    }

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
        mBuilder.startElement(e);
    }

    private void addText(String text) {
        mBuilder.textNode(createTextNode(text));
    }

    private void endElement(Element e) {
        mBuilder.endElement();
    }

    private void endBodyAndDocument() {
        assertFalse(mDocumentEnded);
        endElement(mBody);
        mDocumentEnded = true;
    }

    private List<TextBlock> getHandlerTextBlocks() {
        assertDocumentEnded();
        return mBuilder.toWebDocument().createTextDocumentView().getTextBlocks();
    }

    private void assertDocumentEnded() {
        assertTrue(mDocumentEnded);
    }

    private void assertBlock() {
        List<TextBlock> textBlocks = getHandlerTextBlocks();
        assertEquals(3, textBlocks.size());
        assertEquals(2, textBlocks.get(0).getTagLevel());
        assertEquals(3, textBlocks.get(1).getTagLevel());
        assertEquals(2, textBlocks.get(2).getTagLevel());
    }

    private void assertInline() {
        List<TextBlock> textBlocks = getHandlerTextBlocks();
        assertEquals(1, textBlocks.size());
        assertEquals(1, textBlocks.get(0).getTagLevel());
    }

    public void testRegression0() {
        String html = "<blockquote><p>“There are plenty of instances where provocation comes into" +
            " consideration, instigation comes into consideration, and I will be on the record" +
            " right here on national television and say that I am sick and tired of men" +
            " constantly being vilified and accused of things and we stop there,”" +
            " <a href=\"http://deadspin.com/i-do-not-believe-women-provoke-violence-says-stephen" +
            "-a-1611060016\" target=\"_blank\">Smith said.</a>  “I’m saying, “Can we go a step" +
            " further?” Since we want to dig all deeper into Chad Johnson, can we dig in deep" +
            " to her?”</p></blockquote>";
        Element div = Document.get().createDivElement();
        mBody.appendChild(div);
        div.setInnerHTML(html);
        TextDocument document = TestTextDocumentBuilder.fromPage(div);
        List<TextBlock> textBlocks = document.getTextBlocks();
        assertEquals(1, textBlocks.size());
        TextBlock tb = textBlocks.get(0);
        assertEquals(74, tb.getNumWords());
        assertTrue(0.1 > tb.getLinkDensity());
    }

    public void testRegression1() {
        String html = "<p>\n"
                + "<a href=\"example\" target=\"_top\"><u>More news</u></a> | \n"
                + "<a href=\"example\" target=\"_top\"><u>Search</u></a> | \n"
                + "<a href=\"example\" target=\"_top\"><u>Features</u></a> | \n"
                + "<a href=\"example\" target=\"_top\"><u>Blogs</u></a> | \n"
                + "<a href=\"example\" target=\"_top\"><u>Horse Health</u></a> | \n"
                + "<a href=\"example\" target=\"_top\"><u>Ask the Experts</u></a> | \n"
                + "<a href=\"example\" target=\"_top\"><u>Horse Breeding</u></a> | \n"
                + "<a href=\"example\" target=\"_top\"><u>Forms</u></a> | \n"
                + "<a href=\"example\" target=\"_top\"><u>Home</u></a> </p>\n";
        Element div = Document.get().createDivElement();
        mBody.appendChild(div);
        div.setInnerHTML(html);
        TextDocument document = TestTextDocumentBuilder.fromPage(div);
        List<TextBlock> textBlocks = document.getTextBlocks();
        assertEquals(1, textBlocks.size());
        TextBlock tb = textBlocks.get(0);
        assertEquals(14, tb.getNumWords());
        assertEquals(1.0, tb.getLinkDensity(), 0.01);
    }
}
