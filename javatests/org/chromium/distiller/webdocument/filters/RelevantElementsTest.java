// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller.webdocument.filters;

import org.chromium.distiller.DomDistillerJsTestCase;
import org.chromium.distiller.webdocument.TestWebTextBuilder;
import org.chromium.distiller.webdocument.TestWebDocumentBuilder;
import org.chromium.distiller.webdocument.WebDocument;
import org.chromium.distiller.webdocument.WebElement;
import org.chromium.distiller.webdocument.WebText;
import org.chromium.distiller.webdocument.WebTable;


public class RelevantElementsTest extends DomDistillerJsTestCase {
    public void testEmptyDocument() {
        WebDocument document = new WebDocument();
        assertFalse(RelevantElements.process(document));
        assertTrue(document.getElements().isEmpty());
    }

    public void testNoContent() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        builder.addText("text 1");
        builder.addText("text 2");
        builder.addTable("<tbody><tr><td>t1</td></tr></tbody>");
        WebDocument document = builder.build();
        assertFalse(RelevantElements.process(document));
        for (WebElement e : document.getElements()) {
            assertFalse(e.getIsContent());
        }
    }

    public void testRelevantTable() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        builder.addText("text 1").setIsContent(true);
        WebTable wt = builder.addTable("<tbody><tr><td>t1</td></tr></tbody>");
        WebDocument document = builder.build();
        assertTrue(RelevantElements.process(document));
        assertTrue(wt.getIsContent());
    }

    public void testNonRelevantTable() {
        TestWebDocumentBuilder builder = new TestWebDocumentBuilder();
        builder.addText("text 1").setIsContent(true);
        builder.addText("text 1");
        WebTable wt = builder.addTable("<tbody><tr><td>t1</td></tr></tbody>");
        WebDocument document = builder.build();
        assertFalse(RelevantElements.process(document));
        assertFalse(wt.getIsContent());
    }
}
