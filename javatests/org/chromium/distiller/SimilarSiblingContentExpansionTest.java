// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.document.TextDocument;
import org.chromium.distiller.filters.heuristics.SimilarSiblingContentExpansion;
import org.chromium.distiller.labels.DefaultLabels;

public class SimilarSiblingContentExpansionTest extends DomDistillerJsTestCase {

    public void testSimpleExpansion() {
        mBody.setInnerHTML(
                "<div>" +
                "<div>text</div>" +
                "<div>text</div>" +
                "</div>"
                );
        TextDocument doc = TestTextDocumentBuilder.fromPage(mRoot);

        assertEquals(2, doc.getTextBlocks().size());
        doc.getTextBlocks().get(0).setIsContent(true);

        assertFalse(doc.getTextBlocks().get(1).isContent());
        new SimilarSiblingContentExpansion.Builder()
                .maxLinkDensity(0)
                .maxBlockDistance(3)
                .build()
                .process(doc);
        assertTrue(doc.getTextBlocks().get(1).isContent());
    }

    public void testRequireSameTag() {
        mBody.setInnerHTML(
                "<div>" +
                "<div>text</div>" +
                "<p>text</p>" +
                "</div>"
                );
        TextDocument doc = TestTextDocumentBuilder.fromPage(mRoot);

        assertEquals(2, doc.getTextBlocks().size());
        doc.getTextBlocks().get(0).setIsContent(true);

        assertFalse(doc.getTextBlocks().get(1).isContent());
        new SimilarSiblingContentExpansion.Builder()
                .maxLinkDensity(0)
                .maxBlockDistance(3)
                .build()
                .process(doc);
        assertFalse(doc.getTextBlocks().get(1).isContent());

        new SimilarSiblingContentExpansion.Builder()
                .allowMixedTags()
                .maxLinkDensity(0)
                .maxBlockDistance(3)
                .build()
                .process(doc);
        assertTrue(doc.getTextBlocks().get(1).isContent());
    }

    public void testDoNotCrossTitles() {
        mBody.setInnerHTML(
                "<div>" +
                "<div>text</div>" +
                "<p>title</p>" +
                "<div>text</div>" +
                "</div>"
                );
        TextDocument doc = TestTextDocumentBuilder.fromPage(mRoot);

        assertEquals(3, doc.getTextBlocks().size());
        doc.getTextBlocks().get(1).addLabel(DefaultLabels.TITLE);
        doc.getTextBlocks().get(2).setIsContent(true);

        assertFalse(doc.getTextBlocks().get(0).isContent());
        new SimilarSiblingContentExpansion.Builder()
                .maxLinkDensity(0)
                .maxBlockDistance(3)
                .build()
                .process(doc);
        assertFalse(doc.getTextBlocks().get(0).isContent());

        new SimilarSiblingContentExpansion.Builder()
                .allowCrossTitles()
                .maxLinkDensity(0)
                .maxBlockDistance(3)
                .build()
                .process(doc);
        assertTrue(doc.getTextBlocks().get(0).isContent());
    }

    public void testDoNotCrossHeadings() {
        mBody.setInnerHTML(
                "<div>" +
                "<div>text</div>" +
                "<p>heading</p>" +
                "<div>text</div>" +
                "</div>"
                );
        TextDocument doc = TestTextDocumentBuilder.fromPage(mRoot);

        assertEquals(3, doc.getTextBlocks().size());
        doc.getTextBlocks().get(1).addLabel(DefaultLabels.HEADING);
        doc.getTextBlocks().get(2).setIsContent(true);

        assertFalse(doc.getTextBlocks().get(0).isContent());
        new SimilarSiblingContentExpansion.Builder()
                .maxLinkDensity(0)
                .maxBlockDistance(3)
                .build()
                .process(doc);
        assertFalse(doc.getTextBlocks().get(0).isContent());

        new SimilarSiblingContentExpansion.Builder()
                .allowCrossHeadings()
                .maxLinkDensity(0)
                .maxBlockDistance(3)
                .build()
                .process(doc);
        assertTrue(doc.getTextBlocks().get(0).isContent());
    }

    public void testExpansionMaxDistance() {
        mBody.setInnerHTML(
                "<div>" +
                "<div>text</div>" +
                "<p>text</p>" +
                "<div>text</div>" +
                "</div>"
                );
        TextDocument doc = TestTextDocumentBuilder.fromPage(mRoot);

        assertEquals(3, doc.getTextBlocks().size());
        doc.getTextBlocks().get(0).setIsContent(true);

        assertFalse(doc.getTextBlocks().get(1).isContent());
        assertFalse(doc.getTextBlocks().get(2).isContent());
        new SimilarSiblingContentExpansion.Builder()
                .maxLinkDensity(0)
                .maxBlockDistance(1)
                .build()
                .process(doc);
        assertFalse(doc.getTextBlocks().get(1).isContent());
        assertFalse(doc.getTextBlocks().get(2).isContent());

        new SimilarSiblingContentExpansion.Builder()
                .maxLinkDensity(0)
                .maxBlockDistance(2)
                .build()
                .process(doc);
        assertTrue(doc.getTextBlocks().get(2).isContent());
    }

    public void testMaxLinkDensity() {
        mBody.setInnerHTML(
                "<div>" +
                "<div>text</div>" +
                "<div>text <a href='http://example.com'>link</a></div>" +
                "</div>"
                );
        TextDocument doc = TestTextDocumentBuilder.fromPage(mRoot);

        assertEquals(2, doc.getTextBlocks().size());
        doc.getTextBlocks().get(0).setIsContent(true);

        assertTrue(doc.getTextBlocks().get(1).getLinkDensity() > 0.4);
        assertTrue(doc.getTextBlocks().get(1).getLinkDensity() < 0.6);

        assertFalse(doc.getTextBlocks().get(1).isContent());
        new SimilarSiblingContentExpansion.Builder()
                .maxLinkDensity(0.4)
                .maxBlockDistance(1)
                .build()
                .process(doc);
        assertFalse(doc.getTextBlocks().get(1).isContent());

        new SimilarSiblingContentExpansion.Builder()
                .maxLinkDensity(0.6)
                .maxBlockDistance(1)
                .build()
                .process(doc);
        assertTrue(doc.getTextBlocks().get(1).isContent());
    }
}
