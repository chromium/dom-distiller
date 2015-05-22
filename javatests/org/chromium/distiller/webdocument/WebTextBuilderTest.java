// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument;

import org.chromium.distiller.DomDistillerJsTestCase;

import com.google.gwt.dom.client.Document;

public class WebTextBuilderTest extends DomDistillerJsTestCase {
    private static void addText(WebTextBuilder builder, String text, int tagLevel) {
        builder.textNode(Document.get().createTextNode(text), tagLevel);
    }

    public void testSimpleBlocks() {
        WebTextBuilder builder = new WebTextBuilder();

        WebText block = builder.build(0);
        assertNull(block);

        addText(builder, "Two words.", 0);
        block = builder.build(0);
        assertEquals(2, block.getNumWords());
        assertEquals(0, block.getNumLinkedWords());
        assertEquals("Two words.", block.getText());
        assertEquals(1, block.getTextNodes().size());
        assertEquals(0, block.getLabels().size());
        assertEquals(0, block.getOffsetBlock());

        addText(builder, "More", 0);
        addText(builder, " than", 0);
        addText(builder, " two", 0);
        addText(builder, " words.", 0);
        block = builder.build(1);
        assertEquals(4, block.getNumWords());
        assertEquals(0, block.getNumLinkedWords());
        assertEquals("More than two words.", block.getText());
        assertEquals(4, block.getTextNodes().size());
        assertEquals(1, block.getOffsetBlock());

        assertNull(builder.build(0));
        assertNull(builder.build(0));
    }

    public void testBlockWithAnchors() {
        WebTextBuilder builder = new WebTextBuilder();

        addText(builder, "one", 0);
        builder.enterAnchor();
        addText(builder, "two", 0);
        addText(builder, " three", 0);
        builder.exitAnchor();
        WebText block = builder.build(0);
        assertEquals(3, block.getNumWords());
        assertEquals(2, block.getNumLinkedWords());
        assertEquals("one two three ", block.getText());

        builder.enterAnchor();
        addText(builder, "one", 0);
        block = builder.build(0);
        assertEquals(1, block.getNumWords());
        assertEquals(1, block.getNumLinkedWords());
        assertEquals(" one", block.getText());

        // Should still be in the previous anchor.
        addText(builder, "one", 0);
        block = builder.build(0);
        assertEquals(1, block.getNumWords());
        assertEquals(1, block.getNumLinkedWords());
        assertEquals("one", block.getText());
    }

    public void testComplicatedText() {
        WebTextBuilder builder = new WebTextBuilder();

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
        WebTextBuilder builder = new WebTextBuilder();
        addText(builder, "The ", 0);
        builder.enterAnchor();
        addText(builder, "Overview", 0);
        builder.exitAnchor();
        addText(builder, " is", 0);
        WebText tb = builder.build(0);
        assertEquals("The  Overview  is", tb.getText());
    }

    public void testWhitespaceNodes() {
        WebTextBuilder builder = new WebTextBuilder();

        addText(builder, "one", 0);
        WebText tb = builder.build(0);
        assertEquals(tb.getFirstNonWhitespaceTextNode(), tb.getLastNonWhitespaceTextNode());

        addText(builder, " ", 0);
        addText(builder, "one", 0);
        addText(builder, " ", 0);
        tb = builder.build(0);
        assertEquals(tb.getFirstNonWhitespaceTextNode(), tb.getLastNonWhitespaceTextNode());
        assertEquals(3, tb.getTextNodes().size());
        assertFalse(tb.getTextNodes().get(0).equals(tb.getFirstNonWhitespaceTextNode()));
        assertFalse(tb.getTextNodes().get(2).equals(tb.getFirstNonWhitespaceTextNode()));

        addText(builder, "one", 0);
        addText(builder, "two", 0);
        tb = builder.build(0);
        assertFalse(tb.getFirstNonWhitespaceTextNode().equals(tb.getLastNonWhitespaceTextNode()));
    }

    public void testBRElement() {
        WebTextBuilder builder = new WebTextBuilder();
        addText(builder, "words", 0);
        builder.lineBreak(Document.get().createBRElement());
        addText(builder, "split", 0);
        builder.lineBreak(Document.get().createBRElement());
        addText(builder, "with", 0);
        builder.lineBreak(Document.get().createBRElement());
        addText(builder, "lines", 0);

        WebText webText = builder.build(0);
        assertEquals(7, webText.getTextNodes().size());
        assertEquals("words\nsplit\nwith\nlines", webText.getText());
    }
}
