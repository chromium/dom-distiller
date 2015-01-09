// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.document.TextDocumentStatistics;

/**
 * Tests for {@link TextDocumentStatistics}.
 */
public class TextDocumentStatisticsTest  extends DomDistillerJsTestCase {
    private static final String THREE_WORDS = "I love statistics";
    public void testOnlyContent() {
        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(THREE_WORDS)
                .addContentBlock(THREE_WORDS)
                .addContentBlock(THREE_WORDS)
                .build();
        assertEquals(9, TextDocumentStatistics.countWordsInContent(document));
    }

    public void testOnlyNonContent() {
        TextDocument document = new TestTextDocumentBuilder()
                .addNonContentBlock(THREE_WORDS)
                .addNonContentBlock(THREE_WORDS)
                .addNonContentBlock(THREE_WORDS)
                .build();
        assertEquals(0, TextDocumentStatistics.countWordsInContent(document));
    }

    public void testMixedContent() {
        TextDocument document = new TestTextDocumentBuilder()
                .addContentBlock(THREE_WORDS)
                .addNonContentBlock(THREE_WORDS)
                .addContentBlock(THREE_WORDS)
                .addNonContentBlock(THREE_WORDS)
                .build();
        assertEquals(6, TextDocumentStatistics.countWordsInContent(document));
    }
}
