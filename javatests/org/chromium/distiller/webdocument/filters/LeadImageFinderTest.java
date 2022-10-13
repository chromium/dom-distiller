// Copyright 2015 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.webdocument.TestWebDocumentBuilder;
import org.chromium.distiller.webdocument.WebDocument;
import org.chromium.distiller.webdocument.WebImage;

public class LeadImageFinderTest extends DomDistillerJsTestCase {
    public void testEmptyDocument() {
        WebDocument document = new WebDocument();
        assertFalse(LeadImageFinder.process(document));
        assertTrue(document.getElements().isEmpty());
    }

    public void testRelevantLeadImage() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebImage wi = builder.addLeadImage();
        builder.addText("text 1").setIsContent(true);
        WebDocument document = builder.build();
        assertTrue(LeadImageFinder.process(document));
        assertTrue(wi.getIsContent());
    }

    public void testNoContent() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebImage wi = builder.addLeadImage();
        builder.addText("text 1").setIsContent(false);
        WebDocument document = builder.build();
        assertFalse(LeadImageFinder.process(document));
        assertFalse(wi.getIsContent());
    }

    public void testMultipleLeadImageCandidates() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebImage priority = builder.addLeadImage();
        WebImage ignored = builder.addLeadImage();
        builder.addText("text 1").setIsContent(true);
        WebDocument document = builder.build();
        assertTrue(LeadImageFinder.process(document));
        assertTrue(priority.getIsContent());
        assertFalse(ignored.getIsContent());
    }

    public void testIrrelevantLeadImage() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        builder.addText("text 1").setIsContent(true);
        builder.addText("text 2").setIsContent(true);
        WebImage priority = builder.addLeadImage();
        WebDocument document = builder.build();
        assertFalse(LeadImageFinder.process(document));
        assertFalse(priority.getIsContent());
    }

    public void testMultipleLeadImageCandidatesWithinTexts() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        builder.addText("text 1").setIsContent(true);
        WebImage priority = builder.addLeadImage();
        builder.addText("text 2").setIsContent(true);
        WebImage ignored = builder.addLeadImage();
        WebDocument document = builder.build();
        assertTrue(LeadImageFinder.process(document));
        assertTrue(priority.getIsContent());
        assertFalse(ignored.getIsContent());
    }

    public void testIrrelevantLeadImageWithContentImage() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebImage smallImage = builder.addImage();
        smallImage.setIsContent(true);
        WebImage largeImage = builder.addLeadImage();
        builder.addNestedText("text 1").setIsContent(true);
        WebDocument document = builder.build();
        assertFalse(LeadImageFinder.process(document));
        assertTrue(smallImage.getIsContent());
        assertFalse(largeImage.getIsContent());
    }

    public void testIrrelevantLeadImageAfterSingleText() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        WebImage wi = builder.addImage();
        builder.addNestedText("text 1").setIsContent(true);
        WebDocument document = builder.build();
        assertFalse(LeadImageFinder.process(document));
        assertFalse(wi.getIsContent());
    }
}
