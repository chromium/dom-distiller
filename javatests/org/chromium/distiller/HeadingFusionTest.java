// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.document.TextDocumentTestUtil;
import org.chromium.distiller.filters.heuristics.HeadingFusion;
import org.chromium.distiller.labels.DefaultLabels;

public class HeadingFusionTest extends DomDistillerJsTestCase {
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
        assertTrue(TextDocumentTestUtil.getContent(document).contains(HEADING_TEXT));
        assertTrue(TextDocumentTestUtil.getContent(document).contains(LONG_TEXT));
        assertTrue(TextDocumentTestUtil.getContent(document).contains(SHORT_TEXT));
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
        assertTrue(TextDocumentTestUtil.getContent(document).contains(HEADING_TEXT));
        assertTrue(TextDocumentTestUtil.getContent(document).contains(LONG_TEXT));
        assertTrue(TextDocumentTestUtil.getContent(document).contains(SHORT_TEXT));
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
