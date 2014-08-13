// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextBlock;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.filters.heuristics.BlockProximityFusion;
import de.l3s.boilerpipe.labels.DefaultLabels;

import java.util.LinkedList;

public class BlockProximityFusionTest extends DomDistillerTestCase {
    private static final String LONG_TEXT =
            "Lorem Ipsum Lorem Ipsum Lorem Ipsum. " +
            "Lorem Ipsum Lorem Ipsum Lorem Ipsum. " +
            "Lorem Ipsum Lorem Ipsum Lorem Ipsum.";
    private static final String LONG_LEADING_TEXT =
            "Leading text that's used to start a document but just to offset a " +
            "few text blocks. This will allow testing in-page merges.";
    private static final String SHORT_TEXT = "I might be a header.";


    // ***** Tests for special-handling of leading text *****

    // Both kinds of BlockProximityFusion should merge two content blocks.
    public void testMergeShortLeadingContent() throws BoilerpipeProcessingException {
        doTestMergeShortLeadingContent(BlockProximityFusion.CONTENT_AGNOSTIC);
        doTestMergeShortLeadingContent(BlockProximityFusion.CONTENT_ONLY_SAME_TAGLEVEL);
    }

    private void doTestMergeShortLeadingContent(BlockProximityFusion classifier) throws BoilerpipeProcessingException {
        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(SHORT_TEXT)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals("Classifier: " + classifier,
                1, document.getTextBlocks().size());
        assertTrue("Classifier: " + classifier,
                document.getContent().contains(SHORT_TEXT));
        assertTrue("Classifier: " + classifier,
                document.getContent().contains(LONG_TEXT));
    }

    // Both kinds of BlockProximityFusion should not merge an LI non-content followed by content.
    public void testDoesNotMergeShortLeadingLiNonContent() throws BoilerpipeProcessingException {
        doTestDoesNotMergeShortLeadingLiNonContent(BlockProximityFusion.CONTENT_AGNOSTIC);
        doTestDoesNotMergeShortLeadingLiNonContent(BlockProximityFusion.CONTENT_ONLY_SAME_TAGLEVEL);
    }

    private void doTestDoesNotMergeShortLeadingLiNonContent(BlockProximityFusion classifier) throws BoilerpipeProcessingException {
        TextDocument document = new TestTextDocumentBuilder()
                .addNonContentBlock(SHORT_TEXT, DefaultLabels.LI)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals("Classifier: " + classifier,
                2, document.getTextBlocks().size());
        assertFalse("Classifier: " + classifier,
                document.getContent().contains(SHORT_TEXT));
        assertTrue("Classifier: " + classifier,
                document.getContent().contains(LONG_TEXT));
    }

    // If only merging content blocks, leading non-content of any kind is not merged.
    public void testContentOnlyDoesNotMergeShortLeadingNonContent() throws BoilerpipeProcessingException {
        BlockProximityFusion classifier = BlockProximityFusion.CONTENT_ONLY_SAME_TAGLEVEL;

        TextDocument document = new TestTextDocumentBuilder()
                .addNonContentBlock(SHORT_TEXT)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals(2, document.getTextBlocks().size());
        assertFalse(document.getContent().contains(SHORT_TEXT));
        assertTrue(document.getContent().contains(LONG_TEXT));
    }

    // If "content" flag is ignored, a non-LI leading text (e.g. headline?) can be merged with subsequent content.
    public void testContentAgnosticMergeShortLeadingNonContent() throws BoilerpipeProcessingException {
        BlockProximityFusion classifier = BlockProximityFusion.CONTENT_AGNOSTIC;

        TextDocument document = new TestTextDocumentBuilder()
                .addNonContentBlock(SHORT_TEXT)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals(1, document.getTextBlocks().size());
        assertTrue(document.getContent().contains(SHORT_TEXT));
        assertTrue(document.getContent().contains(LONG_TEXT));
    }

    // ***** Larger-document tests *****

    // Both kinds of BlockProximityFusion should merge a document full of content.
    public void testMergeLotsOfContent() throws BoilerpipeProcessingException {
        doTestMergeLotsOfContent(BlockProximityFusion.CONTENT_AGNOSTIC);
        doTestMergeLotsOfContent(BlockProximityFusion.CONTENT_ONLY_SAME_TAGLEVEL);
    }

    private void doTestMergeLotsOfContent(BlockProximityFusion classifier) throws BoilerpipeProcessingException {
        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(LONG_LEADING_TEXT)
                .addContentBlock(LONG_LEADING_TEXT)
                .addContentBlock(SHORT_TEXT)
                .addContentBlock(LONG_TEXT)
                .addContentBlock(LONG_TEXT)
                .addContentBlock(SHORT_TEXT)
                .build();
        classifier.process(document);

        assertEquals("Classifier: " + classifier,
                1, document.getTextBlocks().size());
        assertTrue("Classifier: " + classifier,
                document.getContent().contains(LONG_LEADING_TEXT));
        assertTrue("Classifier: " + classifier,
                document.getContent().contains(SHORT_TEXT));
        assertTrue("Classifier: " + classifier,
                document.getContent().contains(LONG_TEXT));
    }

    // If only merging content blocks, non-content in the body of any kind is not merged.
    public void testContentOnlySkipsNonContentInBody() throws BoilerpipeProcessingException {
        BlockProximityFusion classifier = BlockProximityFusion.CONTENT_ONLY_SAME_TAGLEVEL;

        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(LONG_LEADING_TEXT)
                .addContentBlock(LONG_LEADING_TEXT)
                .addNonContentBlock(SHORT_TEXT)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals(3, document.getTextBlocks().size());
        assertTrue(document.getContent().contains(LONG_LEADING_TEXT));
        assertFalse(document.getContent().contains(SHORT_TEXT));
        assertTrue(document.getContent().contains(LONG_TEXT));
    }

    // If "content" flag is ignored, a single non-content in the body is merged with subsequent content.
    public void testContentAgnosticIncludesNonContentInBody() throws BoilerpipeProcessingException {
        BlockProximityFusion classifier = BlockProximityFusion.CONTENT_AGNOSTIC;

        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(LONG_LEADING_TEXT)
                .addContentBlock(LONG_LEADING_TEXT)
                .addNonContentBlock(SHORT_TEXT) // begins a new block but is included in the document.
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals(2, document.getTextBlocks().size());
        assertTrue(document.getContent().contains(LONG_LEADING_TEXT));
        assertTrue(document.getContent().contains(SHORT_TEXT));
        assertTrue(document.getContent().contains(LONG_TEXT));
    }

    // If "content" flag is ignored, a single non-content LI in the body is not merged.
    public void testContentAgnosticSkipsNonContentListInBody() throws BoilerpipeProcessingException {
        BlockProximityFusion classifier = BlockProximityFusion.CONTENT_AGNOSTIC;

        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(LONG_LEADING_TEXT)
                .addContentBlock(LONG_LEADING_TEXT)
                .addNonContentBlock(SHORT_TEXT, DefaultLabels.LI)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals(3, document.getTextBlocks().size());
        assertTrue(document.getContent().contains(LONG_LEADING_TEXT));
        assertFalse(document.getContent().contains(SHORT_TEXT));
        assertTrue(document.getContent().contains(LONG_TEXT));
    }
}
