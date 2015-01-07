// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client.util;

import com.dom_distiller.client.DomDistillerTestCase;

import com.google.gwt.dom.client.Document;

import de.l3s.boilerpipe.document.TextBlock;

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
        assertEquals(1, block.getAllTextElements().size());
        assertEquals(0, block.getLabels().size());
        assertEquals(0, block.getOffsetBlocksStart());
        assertEquals(0, block.getOffsetBlocksEnd());

        addText(builder, "More", 0);
        addText(builder, "than", 0);
        addText(builder, "two", 0);
        addText(builder, "words.", 0);
        block = builder.build(1);
        assertEquals(2, block.getNumWords());
        assertEquals(0, block.getNumWordsInAnchorText());
        assertEquals("More than two words.", block.getText());
        assertEquals(4, block.getAllTextElements().size());
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
        addText(builder, "three", 0);
        builder.exitAnchor();
        TextBlock block = builder.build(0);
        assertEquals(3, block.getNumWords());
        assertEquals(2, block.getNumWordsInAnchorText());
        assertEquals("one two three", block.getText());

        builder.enterAnchor();
        addText(builder, "one", 0);
        block = builder.build(0);
        assertEquals(1, block.getNumWords());
        assertEquals(1, block.getNumWordsInAnchorText());
        assertEquals("one", block.getText());

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
}
