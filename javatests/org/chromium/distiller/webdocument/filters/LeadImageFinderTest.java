// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.webdocument.TestWebTextBuilder;
import org.chromium.distiller.webdocument.TestWebDocumentBuilder;
import org.chromium.distiller.webdocument.WebDocument;
import org.chromium.distiller.webdocument.WebElement;
import org.chromium.distiller.webdocument.WebImage;
import org.chromium.distiller.webdocument.WebText;
import org.chromium.distiller.webdocument.WebTable;

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
        WebImage wi = builder.addImage();
        builder.addNestedText("text 1").setIsContent(true);
        WebDocument document = builder.build();
        assertFalse(LeadImageFinder.process(document));
        assertFalse(wi.getIsContent());
    }
}
