// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class ContentExtractorTest extends DomDistillerTestCase {
    private static final String CONTENT_TEXT = "Lorem Ipsum Lorem Ipsum Lorem Ipsum.";
    private static final String TITLE_TEXT = "I am the document title";

    public void testDoesNotExtractTitleInContent() {
        mRoot.appendChild(mBody);

        Element titleDiv = TestUtil.createDiv(0);
        titleDiv.appendChild(TestUtil.createText(TITLE_TEXT));
        mBody.appendChild(titleDiv);
        Element contentDiv = TestUtil.createDiv(1);
        contentDiv.appendChild(TestUtil.createText(CONTENT_TEXT));
        mBody.appendChild(contentDiv);

        contentDiv = TestUtil.createDiv(2);
        contentDiv.appendChild(TestUtil.createText(CONTENT_TEXT));
        mBody.appendChild(contentDiv);

        contentDiv = TestUtil.createDiv(3);
        contentDiv.appendChild(TestUtil.createText(CONTENT_TEXT));

        mBody.appendChild(contentDiv);

        // Title hasn't been set yet, everything should be content.
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertTrue(extractedContent + " must contain 'content':" + CONTENT_TEXT,
                extractedContent.contains(contentDiv.getInnerText()));
        assertTrue(
                extractedContent + " must contain 'title':" + TITLE_TEXT,
                extractedContent.contains(titleDiv.getInnerText()));

        // Now set the title and it should excluded from the content.
        mRoot.appendChild(TestUtil.createTitle(TITLE_TEXT));
        extractor = new ContentExtractor(mRoot);
        extractedContent = extractor.extractContent();
        assertTrue(extractedContent + " must contain 'content':" + CONTENT_TEXT,
                extractedContent.contains(contentDiv.getInnerText()));
        assertFalse(
                extractedContent + " must not contain 'title':" +TITLE_TEXT,
                extractedContent.contains(titleDiv.getInnerText()));
    }

    public void testExtractsEssentialWhitespace() {
        Element div = TestUtil.createDiv(0);
        mBody.appendChild(div);

        div.appendChild(TestUtil.createSpan(CONTENT_TEXT));
        div.appendChild(TestUtil.createText(" "));
        div.appendChild(TestUtil.createSpan(CONTENT_TEXT));
        div.appendChild(TestUtil.createText("\n"));
        div.appendChild(TestUtil.createSpan(CONTENT_TEXT));
        div.appendChild(TestUtil.createText(" "));

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<span>" + CONTENT_TEXT + "</span> " +
                     "<span>" + CONTENT_TEXT + "</span>\n" +
                     "<span>" + CONTENT_TEXT + "</span> ",
                extractedContent);
    }

    public void testPrefersMarkupParserOverDocumentTitle() {
        // Minimum fields for open-graph parser.
        final String MARKUP_PARSER_TITLE = "title from markup parser";
        createMeta("og:title", MARKUP_PARSER_TITLE);
        createMeta("og:type", "video.movie");
        createMeta("og:image", "http://test/image.jpeg");
        createMeta("og:url", "http://test/test.html");

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals(MARKUP_PARSER_TITLE, parser.getTitle());

        Document.get().setTitle(TITLE_TEXT);

        ContentExtractor extractor = new ContentExtractor(mRoot);
        assertEquals("OpenGraph title should be picked over document.title",
                MARKUP_PARSER_TITLE, extractor.extractTitle());
    }

    private void createMeta(String property, String content) {
        mHead.appendChild(TestUtil.createMetaProperty(property, content));
    }
}
