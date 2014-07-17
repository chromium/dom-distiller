// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class ContentExtractorTest extends DomDistillerTestCase {
    private static final String CONTENT_TEXT = "Lorem Ipsum Lorem Ipsum Lorem Ipsum.";
    private static final String TITLE_TEXT = "I am the document title";

    public void testDoesNotExtractTitle() {
        Element root = Document.get().getDocumentElement();
        root.appendChild(TestUtil.createTitle(TITLE_TEXT));

        Element body = Document.get().createElement("body");
        root.appendChild(body);

        Element titleDiv = TestUtil.createDiv(0);
        titleDiv.appendChild(TestUtil.createText(TITLE_TEXT));
        body.appendChild(titleDiv);
        Element contentDiv = TestUtil.createDiv(1);
        contentDiv.appendChild(TestUtil.createText(CONTENT_TEXT));
        body.appendChild(contentDiv);

        contentDiv = TestUtil.createDiv(2);
        contentDiv.appendChild(TestUtil.createText(CONTENT_TEXT));
        body.appendChild(contentDiv);

        contentDiv = TestUtil.createDiv(3);
        contentDiv.appendChild(TestUtil.createText(CONTENT_TEXT));

        body.appendChild(contentDiv);

        // Title hasn't been set yet, everything should be content.
        String extractedContent = ContentExtractor.extractContent();
        assertTrue(extractedContent + " must contain 'content':" + CONTENT_TEXT,
                extractedContent.contains(contentDiv.getInnerText()));
        assertTrue(
                extractedContent + " must contain 'title':" +TITLE_TEXT,
                extractedContent.contains(titleDiv.getInnerText()));

        // Now set the title and it should excluded from the content.
        Document.get().setTitle(TITLE_TEXT);
        extractedContent = ContentExtractor.extractContent();
        assertTrue(extractedContent + " must contain 'content':" + CONTENT_TEXT,
                extractedContent.contains(contentDiv.getInnerText()));
        assertFalse(
                extractedContent + " must not contain 'title':" +TITLE_TEXT,
                extractedContent.contains(titleDiv.getInnerText()));
    }
}
