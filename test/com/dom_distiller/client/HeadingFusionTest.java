// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.filters.heuristics.HeadingFusion;
import de.l3s.boilerpipe.labels.DefaultLabels;

import java.util.LinkedList;

public class HeadingFusionTest extends DomDistillerTestCase {
    private static final String HEADING_TEXT =
            "Heading";
    private static final String LONG_TEXT =
            "Leading text that's used to start a document but just to offset a " +
            "few text blocks. This will allow testing in-page merges.";
    private static final String SHORT_TEXT = "I might be a header.";

    public void testHeadingFused() throws Exception {
        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(HEADING_TEXT, DefaultLabels.HEADING)
                .addContentBlock(LONG_TEXT)
                .addContentBlock(SHORT_TEXT)
                .build();

        assertTrue(new HeadingFusion().process(document));

        assertEquals(2, document.getTextBlocks().size());
        assertFalse(document.getTextBlocks().get(0).hasLabel(DefaultLabels.HEADING));
        assertFalse(
                document.getTextBlocks().get(0).hasLabel(DefaultLabels.BOILERPLATE_HEADING_FUSED));
        assertTrue(document.getContent().contains(HEADING_TEXT));
        assertTrue(document.getContent().contains(LONG_TEXT));
        assertTrue(document.getContent().contains(SHORT_TEXT));
    }

    public void testBoilerplateHeadingFused() throws Exception {
        TextDocument document = new TestTextDocumentBuilder()
                .addNonContentBlock(HEADING_TEXT, DefaultLabels.HEADING)
                .addContentBlock(LONG_TEXT)
                .addContentBlock(SHORT_TEXT)
                .build();

        assertTrue(new HeadingFusion().process(document));

        assertEquals(2, document.getTextBlocks().size());
        assertFalse(document.getTextBlocks().get(0).hasLabel(DefaultLabels.HEADING));
        assertTrue(
                document.getTextBlocks().get(0).hasLabel(DefaultLabels.BOILERPLATE_HEADING_FUSED));
        assertTrue(document.getContent().contains(HEADING_TEXT));
        assertTrue(document.getContent().contains(LONG_TEXT));
        assertTrue(document.getContent().contains(SHORT_TEXT));
    }

    public void testHeadingBeforeBoilerplate() throws Exception {
        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(HEADING_TEXT, DefaultLabels.HEADING)
                .addNonContentBlock(LONG_TEXT)
                .addContentBlock(SHORT_TEXT)
                .build();

        assertTrue(new HeadingFusion().process(document));
        assertEquals(3, document.getTextBlocks().size());
        assertFalse(document.getTextBlocks().get(0).isContent());
    }

    public void testTitleNotFused() throws Exception {
        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(HEADING_TEXT, DefaultLabels.HEADING, DefaultLabels.TITLE)
                .addContentBlock(LONG_TEXT)
                .addContentBlock(SHORT_TEXT)
                .build();

        assertFalse(new HeadingFusion().process(document));

        assertEquals(3, document.getTextBlocks().size());
    }
}
