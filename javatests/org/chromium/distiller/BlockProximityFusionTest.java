// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.document.TextDocumentTestUtil;
import org.chromium.distiller.filters.heuristics.BlockProximityFusion;
import org.chromium.distiller.labels.DefaultLabels;

public class BlockProximityFusionTest extends DomDistillerJsTestCase {
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
    public void testMergeShortLeadingContent() {
        doTestMergeShortLeadingContent(BlockProximityFusion.PRE_FILTERING);
        doTestMergeShortLeadingContent(BlockProximityFusion.POST_FILTERING);
    }

    private void doTestMergeShortLeadingContent(BlockProximityFusion classifier) {
        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(SHORT_TEXT)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals("Classifier: " + classifier,
                1, document.getTextBlocks().size());
        assertTrue("Classifier: " + classifier,
                TextDocumentTestUtil.getContent(document).contains(SHORT_TEXT));
        assertTrue("Classifier: " + classifier,
                TextDocumentTestUtil.getContent(document).contains(LONG_TEXT));
    }

    // Both kinds of BlockProximityFusion should not merge an LI non-content followed by content.
    public void testDoesNotMergeShortLeadingLiNonContent() {
        doTestDoesNotMergeShortLeadingLiNonContent(BlockProximityFusion.PRE_FILTERING);
        doTestDoesNotMergeShortLeadingLiNonContent(BlockProximityFusion.POST_FILTERING);
    }

    private void doTestDoesNotMergeShortLeadingLiNonContent(BlockProximityFusion classifier) {
        TextDocument document = new TestTextDocumentBuilder()
                .addNonContentBlock(SHORT_TEXT, DefaultLabels.LI)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals("Classifier: " + classifier,
                2, document.getTextBlocks().size());
        assertFalse("Classifier: " + classifier,
                TextDocumentTestUtil.getContent(document).contains(SHORT_TEXT));
        assertTrue("Classifier: " + classifier,
                TextDocumentTestUtil.getContent(document).contains(LONG_TEXT));
    }

    // Both kinds of BlockProximityFusion should not merge a non-content followed by content.
    public void testDoesNotMergeShortLeadingNonContent() {
        doTestDoesNotMergeShortLeadingNonContent(BlockProximityFusion.PRE_FILTERING);
        doTestDoesNotMergeShortLeadingNonContent(BlockProximityFusion.POST_FILTERING);
    }

    public void doTestDoesNotMergeShortLeadingNonContent(BlockProximityFusion classifier) {
        TextDocument document = new TestTextDocumentBuilder()
                .addNonContentBlock(SHORT_TEXT)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals(2, document.getTextBlocks().size());
        assertFalse(TextDocumentTestUtil.getContent(document).contains(SHORT_TEXT));
        assertTrue(TextDocumentTestUtil.getContent(document).contains(LONG_TEXT));
    }

    // ***** Larger-document tests *****

    // Both kinds of BlockProximityFusion should merge a document full of content.
    public void testMergeLotsOfContent() {
        doTestMergeLotsOfContent(BlockProximityFusion.PRE_FILTERING);
        doTestMergeLotsOfContent(BlockProximityFusion.POST_FILTERING);
    }

    private void doTestMergeLotsOfContent(BlockProximityFusion classifier) {
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
                TextDocumentTestUtil.getContent(document).contains(LONG_LEADING_TEXT));
        assertTrue("Classifier: " + classifier,
                TextDocumentTestUtil.getContent(document).contains(SHORT_TEXT));
        assertTrue("Classifier: " + classifier,
                TextDocumentTestUtil.getContent(document).contains(LONG_TEXT));
    }

    // If only merging content blocks, non-content in the body of any kind is not merged.
    public void testSkipsNonContentInBody() {
        doTestSkipsNonContentInBody(BlockProximityFusion.PRE_FILTERING);
        doTestSkipsNonContentInBody(BlockProximityFusion.POST_FILTERING);
    }

    public void doTestSkipsNonContentInBody(BlockProximityFusion classifier) {
        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(LONG_LEADING_TEXT)
                .addContentBlock(LONG_LEADING_TEXT)
                .addNonContentBlock(SHORT_TEXT)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals(3, document.getTextBlocks().size());
        assertTrue(TextDocumentTestUtil.getContent(document).contains(LONG_LEADING_TEXT));
        assertFalse(TextDocumentTestUtil.getContent(document).contains(SHORT_TEXT));
        assertTrue(TextDocumentTestUtil.getContent(document).contains(LONG_TEXT));
    }

    // If "content" flag is ignored, a single non-content LI in the body is not merged.
    public void testPreFilteringSkipsNonContentListInBody() {
        BlockProximityFusion classifier = BlockProximityFusion.PRE_FILTERING;

        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(LONG_LEADING_TEXT)
                .addContentBlock(LONG_LEADING_TEXT)
                .addNonContentBlock(SHORT_TEXT, DefaultLabels.LI)
                .addContentBlock(LONG_TEXT)
                .build();
        classifier.process(document);

        assertEquals(3, document.getTextBlocks().size());
        assertTrue(TextDocumentTestUtil.getContent(document).contains(LONG_LEADING_TEXT));
        assertFalse(TextDocumentTestUtil.getContent(document).contains(SHORT_TEXT));
        assertTrue(TextDocumentTestUtil.getContent(document).contains(LONG_TEXT));
    }
}
