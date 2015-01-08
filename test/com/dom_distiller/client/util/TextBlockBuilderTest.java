// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client.util;

import com.dom_distiller.client.DomDistillerTestCase;
import com.dom_distiller.client.TestTextDocumentBuilder;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextBlockBuilderTest extends DomDistillerTestCase {
    private static void addText(TextBlockBuilder builder, String text, int tagLevel) {
        builder.textNode(Document.get().createTextNode(text), tagLevel);
    }

    public void testSimpleBlocks() {
        TextBlockBuilder builder = new TextBlockBuilder();

        TextBlock block = builder.build(0);
        assertNull(block);

        addText(builder, "Two words.", 0);
        block = builder.build(0);
        assertEquals(2, block.getNumWords());
        assertEquals(0, block.getNumWordsInAnchorText());
        assertEquals("Two words.", block.getText());
        assertEquals(1, block.getAllTextNodes().size());
        assertEquals(0, block.getLabels().size());
        assertEquals(0, block.getOffsetBlocksStart());
        assertEquals(0, block.getOffsetBlocksEnd());

        addText(builder, "More", 0);
        addText(builder, " than", 0);
        addText(builder, " two", 0);
        addText(builder, " words.", 0);
        block = builder.build(1);
        assertEquals(4, block.getNumWords());
        assertEquals(0, block.getNumWordsInAnchorText());
        assertEquals("More than two words.", block.getText());
        assertEquals(4, block.getAllTextNodes().size());
        assertEquals(1, block.getOffsetBlocksStart());
        assertEquals(1, block.getOffsetBlocksEnd());

        assertNull(builder.build(0));
        assertNull(builder.build(0));
    }

    public void testBlockWithAnchors() {
        TextBlockBuilder builder = new TextBlockBuilder();

        addText(builder, "one", 0);
        builder.enterAnchor();
        addText(builder, "two", 0);
        addText(builder, " three", 0);
        builder.exitAnchor();
        TextBlock block = builder.build(0);
        assertEquals(3, block.getNumWords());
        assertEquals(2, block.getNumWordsInAnchorText());
        assertEquals("one two three ", block.getText());

        builder.enterAnchor();
        addText(builder, "one", 0);
        block = builder.build(0);
        assertEquals(1, block.getNumWords());
        assertEquals(1, block.getNumWordsInAnchorText());
        assertEquals(" one", block.getText());

        // Should still be in the previous anchor.
        addText(builder, "one", 0);
        block = builder.build(0);
        assertEquals(1, block.getNumWords());
        assertEquals(1, block.getNumWordsInAnchorText());
        assertEquals("one", block.getText());
    }

    public void testComplicatedText() {
        TextBlockBuilder builder = new TextBlockBuilder();

        addText(builder, "JULIE'S CALAMARI", 0);
        assertEquals(2, builder.build(0).getNumWords());
        addText(builder, "all-purpose flour", 0);
        assertEquals(2, builder.build(0).getNumWords());
        addText(builder, "1/2 cups flour", 0);
        assertEquals(3, builder.build(0).getNumWords());
        addText(builder, "email foo@bar.com", 0);
        assertEquals(2, builder.build(0).getNumWords());
        addText(builder, "$2.38 million", 0);
        assertEquals(2, builder.build(0).getNumWords());
        addText(builder, "goto website.com", 0);
        assertEquals(2, builder.build(0).getNumWords());
        addText(builder, "Deal expires:7d:04h:23m", 0);
        assertEquals(2, builder.build(0).getNumWords());
    }

    public void testWhitespaceAroundAnchor() {
        TextBlockBuilder builder = new TextBlockBuilder();
        addText(builder, "The ", 0);
        builder.enterAnchor();
        addText(builder, "Overview", 0);
        builder.exitAnchor();
        addText(builder, " is", 0);
        TextBlock tb = builder.build(0);
        assertEquals("The  Overview  is", tb.getText());
    }

    public void testWhitespaceNodes() {
        TextBlockBuilder builder = new TextBlockBuilder();

        addText(builder, "one", 0);
        TextBlock tb = builder.build(0);
        assertEquals(tb.getFirstNonWhitespaceTextNode(), tb.getLastNonWhitespaceTextNode());

        addText(builder, " ", 0);
        addText(builder, "one", 0);
        addText(builder, " ", 0);
        tb = builder.build(0);
        assertEquals(tb.getFirstNonWhitespaceTextNode(), tb.getLastNonWhitespaceTextNode());
        assertEquals(3, tb.getAllTextNodes().size());
        assertFalse(tb.getAllTextNodes().get(0).equals(tb.getFirstNonWhitespaceTextNode()));
        assertFalse(tb.getAllTextNodes().get(2).equals(tb.getFirstNonWhitespaceTextNode()));

        addText(builder, "one", 0);
        addText(builder, "two", 0);
        tb = builder.build(0);
        assertFalse(tb.getFirstNonWhitespaceTextNode().equals(tb.getLastNonWhitespaceTextNode()));
    }

    public void testRegression0() {
        TextBlockBuilder builder = new TextBlockBuilder();
        String html = "<blockquote><p>“There are plenty of instances where provocation comes into" +
            " consideration, instigation comes into consideration, and I will be on the record" +
            " right here on national television and say that I am sick and tired of men" +
            " constantly being vilified and accused of things and we stop there,”" +
            " <a href=\"http://deadspin.com/i-do-not-believe-women-provoke-violence-says-stephen" +
            "-a-1611060016\" target=\"_blank\">Smith said.</a>  “I’m saying, “Can we go a step" +
            " further?” Since we want to dig all deeper into Chad Johnson, can we dig in deep" +
            " to her?”</p></blockquote>";
        Element div = Document.get().createDivElement();
        div.setInnerHTML(html);
        TextDocument document = TestTextDocumentBuilder.fromPage(div);
        List<TextBlock> textBlocks = document.getTextBlocks();
        assertEquals(1, textBlocks.size());
        TextBlock tb = textBlocks.get(0);
        assertEquals(74, tb.getNumWords());
        assertTrue(0.1 > tb.getLinkDensity());
    }

    public void testRegression1() {
        TextBlockBuilder builder = new TextBlockBuilder();
        String html =
            "<p>\n" +
            "<a href=\"example\" target=\"_top\"><u>More news</u></a> | \n" +
            "<a href=\"example\" target=\"_top\"><u>Search</u></a> | \n" +
            "<a href=\"example\" target=\"_top\"><u>Features</u></a> | \n" +
            "<a href=\"example\" target=\"_top\"><u>Blogs</u></a> | \n" +
            "<a href=\"example\" target=\"_top\"><u>Horse Health</u></a> | \n" +
            "<a href=\"example\" target=\"_top\"><u>Ask the Experts</u></a> | \n" +
            "<a href=\"example\" target=\"_top\"><u>Horse Breeding</u></a> | \n" +
            "<a href=\"example\" target=\"_top\"><u>Forms</u></a> | \n" +
            "<a href=\"example\" target=\"_top\"><u>Home</u></a> </p>\n";
        Element div = Document.get().createDivElement();
        div.setInnerHTML(html);
        TextDocument document = TestTextDocumentBuilder.fromPage(div);
        List<TextBlock> textBlocks = document.getTextBlocks();
        assertEquals(1, textBlocks.size());
        TextBlock tb = textBlocks.get(0);
        assertEquals(14, tb.getNumWords());
        assertEquals(1.0, tb.getLinkDensity(), 0.01);
    }

}
