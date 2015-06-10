// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;

public class ContentExtractorTest extends DomDistillerJsTestCase {
    private static final String CONTENT_TEXT = "Lorem Ipsum Lorem Ipsum Lorem Ipsum.";
    private static final String TITLE_TEXT = "I am the document title";

    public void testDoesNotExtractTitleInContent() {
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
                extractedContent.contains(DomUtil.getInnerText(contentDiv)));
        assertTrue(
                extractedContent + " must contain 'title':" + TITLE_TEXT,
                extractedContent.contains(DomUtil.getInnerText(titleDiv)));

        // Now set the title and it should excluded from the content.
        mHead.appendChild(TestUtil.createTitle(TITLE_TEXT));
        extractor = new ContentExtractor(mRoot);
        extractedContent = extractor.extractContent();
        assertTrue(extractedContent + " must contain 'content':" + CONTENT_TEXT,
                extractedContent.contains(DomUtil.getInnerText(contentDiv)));
        assertFalse(
                extractedContent + " must not contain 'title':" + TITLE_TEXT,
                extractedContent.contains(DomUtil.getInnerText(titleDiv)));
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
        assertEquals("<div><span>" + CONTENT_TEXT + "</span> " +
                     "<span>" + CONTENT_TEXT + "</span>\n" +
                     "<span>" + CONTENT_TEXT + "</span> </div>",
                TestUtil.removeAllDirAttributes(extractedContent));
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

    public void testImageWithSrcset() {
        final String html =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<img src=\"image\" srcset=\"image200 200w, image400 400w\">" +
            "<table role=\"grid\"><tbody><tr><td>" +
                "<img src=\"image\" srcset=\"image200 200w, image400 400w\">" +
                "</td></tr></tbody></table>" +
            "<p>" + CONTENT_TEXT + "</p>";

        final String expected =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<img src=\"http://example.com/image\">" +
            "<table role=\"grid\"><tbody><tr><td>" +
                "<img src=\"http://example.com/image\">" +
            "</td></tr></tbody></table>" +
            "<p>" + CONTENT_TEXT + "</p>";

        mHead.setInnerHTML("<base href=\"http://example.com/\">");
        mBody.setInnerHTML(html);

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();

        assertEquals(expected,
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    private void createMeta(String property, String content) {
        mHead.appendChild(TestUtil.createMetaProperty(property, content));
    }

    public void testRemoveFontColorAttributes() {
        Element outerFontTag = Document.get().createElement("FONT");
        outerFontTag.setAttribute("COLOR", "blue");
        mBody.appendChild(outerFontTag);

        String text = "<font color=\"red\">" + CONTENT_TEXT + "</font>";

        outerFontTag.appendChild(TestUtil.createSpan(text));
        outerFontTag.appendChild(TestUtil.createText(" "));
        outerFontTag.appendChild(TestUtil.createSpan(text));
        outerFontTag.appendChild(TestUtil.createText("\n"));
        outerFontTag.appendChild(TestUtil.createSpan(text));
        outerFontTag.appendChild(TestUtil.createText(" "));

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<font><span><font>" + CONTENT_TEXT + "</font></span> " +
                     "<span><font>" + CONTENT_TEXT + "</font></span>\n" +
                     "<span><font>" + CONTENT_TEXT + "</font></span> </font>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    private void assertExtractor(String expected, String html) {
        mBody.setInnerHTML("");
        Element div = TestUtil.createDiv(0);
        mBody.appendChild(div);

        div.setInnerHTML(html);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals(expected, TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testOnlyProcessArticleElement() {
        final String article = "<p>" + CONTENT_TEXT + "</p><p>" + CONTENT_TEXT + "</p>";

        final String html = "<h1>" + CONTENT_TEXT + "</h1><div>" + article + "</div>";
        final String expected = "<h1>" + CONTENT_TEXT + "</h1>" + article;

        // Make sure everything is there before using the fast path.
        assertExtractor(expected, html);

        final String htmlArticle =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<article>" + article + "</article>";

        assertExtractor(article, htmlArticle);
    }

    public void testOnlyProcessArticleElementMultiple() {
        final String article = "<p>" + CONTENT_TEXT + "</p><p>" + CONTENT_TEXT + "</p>";

        final String htmlArticle =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<article>" + article + "</article>" +
            "<article>" + article + "</article>";
        final String expected = "<h1>" + CONTENT_TEXT + "</h1>" + article + article;

        // The existence of multiple articles disables the fast path.
        assertExtractor(expected, htmlArticle);
    }

    public void testOnlyProcessOGArticle() {
        final String article = "<p>" + CONTENT_TEXT + "</p><p>" + CONTENT_TEXT + "</p>";

        final String htmlArticle =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<div itemscope itemtype=\"http://schema.org/Article\">" + article + "</div>";

        assertExtractor(article, htmlArticle);
    }

    public void testOnlyProcessOGArticleNews() {
        final String article = "<p>" + CONTENT_TEXT + "</p><p>" + CONTENT_TEXT + "</p>";

        final String htmlArticle =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<div itemscope itemtype=\"http://schema.org/NewsArticle\">" + article + "</div>";

        assertExtractor(article, htmlArticle);
    }

    public void testOnlyProcessOGArticleBlog() {
        final String article = "<p>" + CONTENT_TEXT + "</p><p>" + CONTENT_TEXT + "</p>";

        final String htmlArticle =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<div itemscope itemtype=\"http://schema.org/BlogPosting\">" + article + "</div>";

        assertExtractor(article, htmlArticle);
    }

    public void testOnlyProcessOGArticleNested() {
        final String paragraph = "<p>" + CONTENT_TEXT + "</p>";
        final String article = paragraph + paragraph;

        final String htmlArticle =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<div itemscope itemtype=\"http://schema.org/Article\">" +
                paragraph +
                "<div itemscope itemtype=\"http://schema.org/Article\">" + paragraph + "</div>" +
            "</div>";

        assertExtractor(article, htmlArticle);
    }

    public void testOnlyProcessOGNonArticleMovie() {
        final String article = "<p>" + CONTENT_TEXT + "</p><p>" + CONTENT_TEXT + "</p>";

        final String htmlArticle =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<div itemscope itemtype=\"http://schema.org/Movie\">" + article + "</div>";
        final String expected = "<h1>" + CONTENT_TEXT + "</h1>" + article;

        // Non-article schema.org types should not use the fast path.
        assertExtractor(expected, htmlArticle);
    }
}
