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

        OpenGraphProtocolParserAccessor parser = new OpenGraphProtocolParserAccessor(mRoot);
        assertTrue(parser != null);
        assertEquals(MARKUP_PARSER_TITLE, parser.getTitle());

        Document.get().setTitle(TITLE_TEXT);

        ContentExtractor extractor = new ContentExtractor(mRoot);
        assertEquals("OpenGraph title should be picked over document.title",
                MARKUP_PARSER_TITLE, extractor.extractTitle());
    }

    public void testImage() {
        // Test the absolute and different kinds of relative URLs for image sources,
        // and also add an extra comma (,) as malformed srcset syntax for robustness.
        // Also test images in WebImage and WebTable.
        // TODO(wychen): add images in WebText when it is supported.
        final String html =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<img id=\"a\" style=\"typo\" align=\"left\" src=\"image\" srcset=\"image200 200w, //example.org/image400 400w\">" +
            "<figure><picture>" +
                "<source srcset=\"image200 200w, //example.org/image400 400w\">" +
                "<source srcset=\"image100 100w, //example.org/image300 300w\">" +
                "<img>" +
            "</picture></figure>" +
            "<span class=\"lazy-image-placeholder\" data-src=\"/image\" " +
                "data-srcset=\"/image2x 2x\" data-width=\"20\" data-height=\"10\"></span>" +
            "<img id=\"b\" style=\"align: left\" alt=\"b\" data-dummy=\"c\" data-src=\"image2\">" +
            "<table role=\"grid\"><tbody><tr><td>" +
                "<img id=\"c\" style=\"a\" alt=\"b\" src=\"/image\" srcset=\"https://example.com/image2x 2x, /image4x 4x,\">" +
                "<img id=\"d\" style=\"a\" align=\"left\" src=\"/image2\">" +
                "</td></tr></tbody></table>" +
            "<p>" + CONTENT_TEXT + "</p>";

        final String expected =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<img src=\"http://example.com/path/image\" " +
                 "srcset=\"http://example.com/path/image200 200w, http://example.org/image400 400w\">" +
            "<figure><picture>" +
                "<source srcset=\"http://example.com/path/image200 200w, http://example.org/image400 400w\">" +
                "<source srcset=\"http://example.com/path/image100 100w, http://example.org/image300 300w\">" +
                "<img>" +
            "</picture></figure>" +
            "<img srcset=\"http://example.com/image2x 2x\" src=\"http://example.com/image\" " +
                "width=\"20\" height=\"10\">" +
            "<img alt=\"b\" src=\"http://example.com/path/image2\">" +
            "<table role=\"grid\"><tbody><tr><td>" +
                "<img alt=\"b\" src=\"http://example.com/image\" " +
                     "srcset=\"https://example.com/image2x 2x, http://example.com/image4x 4x, \">" +
                "<img src=\"http://example.com/image2\">" +
            "</td></tr></tbody></table>" +
            "<p>" + CONTENT_TEXT + "</p>";

        mHead.setInnerHTML("<base href=\"http://example.com/path/\">");
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

    public void testRemoveStyleAttributes() {
        String html =
            "<h1 style=\"font-weight: folder\">" +
                CONTENT_TEXT +
            "</h1>" +
            "<p style=\"\">" +
                CONTENT_TEXT +
            "</p>" +
            "<img style=\"align: left\" data-src=\"/test.png\">" +
            "<table style=\"position: absolute\">" +
                "<tbody style=\"font-size: 2\">" +
                    "<tr style=\"z-index: 0\">" +
                        "<th style=\"top: 0px\">" + CONTENT_TEXT +
                            "<img style=\"align: left\" src=\"/test.png\">" +
                        "</th>" +
                        "<th style=\"width: 20px\">" + CONTENT_TEXT + "</th>" +
                    "</tr><tr style=\"left: 0\">" +
                        "<td style=\"display: block\">" + CONTENT_TEXT + "</td>" +
                        "<td style=\"color: #123\">" + CONTENT_TEXT + "</td>" +
                    "</tr>" +
                "</tbody>" +
            "</table>";

        final String expected =
            "<h1>" +
                CONTENT_TEXT +
            "</h1>" +
            "<p>" +
                CONTENT_TEXT +
            "</p>" +
            "<img src=\"http://example.com/test.png\">" +
            "<table>" +
                "<tbody>" +
                    "<tr>" +
                        "<th>" + CONTENT_TEXT +
                            "<img src=\"http://example.com/test.png\">" +
                        "</th>" +
                        "<th>" + CONTENT_TEXT + "</th>" +
                    "</tr><tr>" +
                        "<td>" + CONTENT_TEXT + "</td>" +
                        "<td>" + CONTENT_TEXT + "</td>" +
                    "</tr>" +
                "</tbody>" +
            "</table>";

        mHead.setInnerHTML("<base href=\"http://example.com/\">");
        mBody.setInnerHTML(html);

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals(expected,
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testKeepingWidthAndHeightAttributes() {
        String html =
            "<h1>" +
                CONTENT_TEXT +
            "</h1>" +
            "<p>" +
                CONTENT_TEXT +
            "</p>" +
            "<img style=\"align: left\" src=\"/test.png\" " +
                    "width=\"200\" height=\"300\">" +
            "<img style=\"align: left\" src=\"/test.png\" " +
                    "width=\"200\">" +
            "<img style=\"align: left\" src=\"/test.png\">";

        final String expected =
            "<h1>" +
                CONTENT_TEXT +
            "</h1>" +
            "<p>" +
                CONTENT_TEXT +
            "</p>" +
            "<img src=\"http://example.com/test.png\" " +
                    "width=\"200\" height=\"300\">" +
            "<img src=\"http://example.com/test.png\" " +
                    "width=\"200\">" +
            "<img src=\"http://example.com/test.png\">";

        mHead.setInnerHTML("<base href=\"http://example.com/\">");
        mBody.setInnerHTML(html);

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals(expected,
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveOrderedList() {
        Element outerListTag = Document.get().createElement("OL");
        mBody.appendChild(outerListTag);

        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<OL>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                     "</OL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveOrderedListWithSpan() {
        String html =
            "<OL>" +
                "<LI><span>" + CONTENT_TEXT + "</span></LI>" +
                "<LI>" + CONTENT_TEXT + "</LI>" +
                "<LI>" + CONTENT_TEXT + "</LI>" +
                "<LI>" + CONTENT_TEXT + "</LI>" +
            "</OL>";
        mBody.setInnerHTML(html);

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals(html,
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveNestedOrderedList() {
        Element outerListTag = Document.get().createElement("OL");
        Element outerListItem = Document.get().createElement("LI");

        Element innerListTag = Document.get().createElement("OL");
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));

        outerListItem.appendChild(innerListTag);
        outerListTag.appendChild(outerListItem);
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));

        mBody.appendChild(outerListTag);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<OL>" +
                        "<LI>" +
                          "<OL>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                          "</OL>" +
                        "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                     "</OL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveNestedOrderedListWithOtherElementsInside() {
        Element outerListTag = Document.get().createElement("OL");
        Element outerListItem = Document.get().createElement("LI");
        outerListItem.appendChild(TestUtil.createText(CONTENT_TEXT));
        outerListItem.appendChild(TestUtil.createParagraph(CONTENT_TEXT));

        Element innerListTag = Document.get().createElement("OL");
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createParagraph(""));

        outerListItem.appendChild(innerListTag);
        outerListTag.appendChild(outerListItem);
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        outerListTag.appendChild(TestUtil.createParagraph(CONTENT_TEXT));

        mBody.appendChild(outerListTag);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<OL>" +
                        "<LI>" + CONTENT_TEXT +
                          "<p>" + CONTENT_TEXT + "</p>" +
                          "<OL>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                          "</OL>" +
                        "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<p>" + CONTENT_TEXT + "</p>" +
                     "</OL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveUnorderedList() {
        Element outerListTag = Document.get().createElement("UL");
        mBody.appendChild(outerListTag);

        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<UL>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                     "</UL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveNestedUnorderedList() {
        Element outerListTag = Document.get().createElement("UL");
        Element outerListItem = Document.get().createElement("LI");

        Element innerListTag = Document.get().createElement("UL");
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));

        outerListItem.appendChild(innerListTag);
        outerListTag.appendChild(outerListItem);
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));

        mBody.appendChild(outerListTag);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<UL>" +
                        "<LI>" +
                          "<UL>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                          "</UL>" +
                        "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                     "</UL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveNestedUnorderedListWithOtherElementsInside() {
        Element outerListTag = Document.get().createElement("UL");
        Element outerListItem = Document.get().createElement("LI");
        outerListItem.appendChild(TestUtil.createText(CONTENT_TEXT));
        outerListItem.appendChild(TestUtil.createParagraph(CONTENT_TEXT));

        Element innerListTag = Document.get().createElement("UL");
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        innerListTag.appendChild(TestUtil.createParagraph(""));

        outerListItem.appendChild(innerListTag);
        outerListTag.appendChild(outerListItem);
        outerListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        outerListTag.appendChild(TestUtil.createParagraph(CONTENT_TEXT));

        mBody.appendChild(outerListTag);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<UL>" +
                        "<LI>" + CONTENT_TEXT +
                          "<p>" + CONTENT_TEXT + "</p>" +
                          "<UL>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                          "</UL>" +
                        "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<p>" + CONTENT_TEXT + "</p>" +
                     "</UL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveUnorderedListWithNestedOrderedList() {
        Element unorderedListTag = Document.get().createElement("UL");
        Element li = Document.get().createElement("LI");
        Element orderedList = Document.get().createElement("OL");
        orderedList.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        orderedList.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        li.appendChild(orderedList);
        unorderedListTag.appendChild(li);
        unorderedListTag.appendChild(TestUtil.createListItem(CONTENT_TEXT));
        mBody.appendChild(unorderedListTag);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<UL>" +
                        "<LI>" +
                          "<OL>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                            "<LI>" + CONTENT_TEXT + "</LI>" +
                          "</OL>" +
                        "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                     "</UL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testMalformedListStructureWithExtraLITagEnd() {
        Element unorderedListTag = Document.get().createElement("UL");
        String html = "<LI>" +  CONTENT_TEXT + "</LI></LI><LI>" + CONTENT_TEXT + "</LI>";
        unorderedListTag.setInnerHTML(html);
        mBody.appendChild(unorderedListTag);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<UL>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                     "</UL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testMalformedListStructureWithExtraLITagStart() {
        Element unorderedListTag = Document.get().createElement("OL");
        String html = "<LI><LI>" + CONTENT_TEXT + "</LI><LI>" + CONTENT_TEXT + "</LI>";
        unorderedListTag.setInnerHTML(html);
        mBody.appendChild(unorderedListTag);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<OL>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                     "</OL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testMalformedListStructureWithExtraOLTagStart() {
        Element unorderedListTag = Document.get().createElement("OL");
        String html = "<OL><LI>" + CONTENT_TEXT + "</LI><LI>" + CONTENT_TEXT + "</LI>";
        unorderedListTag.setInnerHTML(html);
        mBody.appendChild(unorderedListTag);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<OL>" +
                        "<OL>" +
                          "<LI>" + CONTENT_TEXT + "</LI>" +
                          "<LI>" + CONTENT_TEXT + "</LI>" +
                        "</OL>" +
                     "</OL>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testMalformedListStructureWithoutLITag(){
        Element orderedListTag = Document.get().createElement("OL");
        String html = "<LI>" + CONTENT_TEXT + "</LI>" +
                       CONTENT_TEXT +
                      "<LI>" + CONTENT_TEXT + "</LI>";
        orderedListTag.setInnerHTML(html);
        mBody.appendChild(orderedListTag);
        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<OL>" +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                         CONTENT_TEXT +
                        "<LI>" + CONTENT_TEXT + "</LI>" +
                     "</OL>" ,
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveChildElementWithinBlockquote() {
        Element blockquote = Document.get().createElement("BLOCKQUOTE");
        mBody.appendChild(blockquote);

        blockquote.appendChild(TestUtil.createParagraph(CONTENT_TEXT
                + CONTENT_TEXT + CONTENT_TEXT + CONTENT_TEXT));

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<BLOCKQUOTE>" +
                       "<p>" + CONTENT_TEXT + CONTENT_TEXT
                        + CONTENT_TEXT + CONTENT_TEXT + "</p>" +
                     "</BLOCKQUOTE>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testPreserveChildrenElementsWithinBlockquote() {
        Element blockquote = Document.get().createElement("BLOCKQUOTE");
        mBody.appendChild(blockquote);

        blockquote.appendChild(TestUtil.createParagraph(CONTENT_TEXT));
        blockquote.appendChild(TestUtil.createParagraph(CONTENT_TEXT));
        blockquote.appendChild(TestUtil.createParagraph(CONTENT_TEXT));

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals("<BLOCKQUOTE>" +
                        "<p>" + CONTENT_TEXT + "</p>" +
                        "<p>" + CONTENT_TEXT + "</p>" +
                        "<p>" + CONTENT_TEXT + "</p>" +
                     "</BLOCKQUOTE>",
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testDiscardBlockquoteWithoutContent() {
        assertExtractor("", "<BLOCKQUOTE></BLOCKQUOTE>");
    }

    public void testPreservePre() {
        final String article = CONTENT_TEXT + CONTENT_TEXT + CONTENT_TEXT;
        final String html = "<h1>" + CONTENT_TEXT + "</h1><PRE><kbd>" + article + "</kbd></PRE>";

        assertExtractor(html, html);
    }

    private void assertExtractor(String expected, String html) {
        mBody.setInnerHTML(html);

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals(expected, TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testDropCap() {
        String html =
            "<h1>" +
                CONTENT_TEXT +
            "</h1>" +
            "<p>" +
                "<strong><span style=\"float: left\">T</span>est</strong>" +
                CONTENT_TEXT +
            "</p>";

        final String expected =
            "<h1>" +
                CONTENT_TEXT +
            "</h1>" +
            "<p>" +
                "<strong><span>T</span>est</strong>" +
                CONTENT_TEXT +
            "</p>";

        mBody.setInnerHTML(html);

        ContentExtractor extractor = new ContentExtractor(mRoot);
        String extractedContent = extractor.extractContent();
        assertEquals(expected,
                TestUtil.removeAllDirAttributes(extractedContent));
    }

    public void testBlockyArticle() {
        final String htmlArticle =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<span>" + CONTENT_TEXT + "</span>" +
            "<div><span>" + CONTENT_TEXT + "</span></div>" +
            "<p><em>" + CONTENT_TEXT + "</em></p>" +
            "<div><cite><span><span>" + CONTENT_TEXT + "</span></span></cite></div>" +
            "<div><span>" + CONTENT_TEXT + "</span><span>" + CONTENT_TEXT + "</span></div>" +
            "<main><span><blockquote><cite>" +
                "<span><span>" + CONTENT_TEXT + "</span></span><span>" + CONTENT_TEXT + "</span>" +
            "</cite></blockquote></span></main>";

        final String expected =
            "<h1>" + CONTENT_TEXT + "</h1>" +
            "<span>" + CONTENT_TEXT + "</span>" +
            "<div><span>" + CONTENT_TEXT + "</span></div>" +
            "<p><em>" + CONTENT_TEXT + "</em></p>" +
            "<div><cite><span><span>" + CONTENT_TEXT + "</span></span></cite></div>" +
            "<div><span>" + CONTENT_TEXT + "</span><span>" + CONTENT_TEXT + "</span></div>" +
            "<BLOCKQUOTE><cite>" +
                "<span><span>" + CONTENT_TEXT + "</span></span><span>" + CONTENT_TEXT + "</span>" +
            "</cite></BLOCKQUOTE>";

        assertExtractor(expected, htmlArticle);
    }

    public void testSandboxedIFrame() {
        final String html ="<iframe sandbox></iframe>";

        assertExtractor("", html);
    }

    public void testSpanArticle() {
        final String htmlArticle =
            "<span>" + CONTENT_TEXT + "</span>" +
            "<span>" + CONTENT_TEXT + "</span>" +
            "<span>" + CONTENT_TEXT + "</span>";

        final String expected = "<div>" + htmlArticle + "</div>";

        assertExtractor(expected, htmlArticle);
    }

    public void testUnwantedIFrame() {
        final String html =
            "<p>" + CONTENT_TEXT + "</p>" +
            "<iframe>dummy</iframe>" +
            "<p>" + CONTENT_TEXT + "</p>";

        final String expected =
            "<p>" + CONTENT_TEXT + "</p>" +
            "<p>" + CONTENT_TEXT + "</p>";

        assertExtractor(expected, html);
    }

    public void testStripUnwantedClassNames() {
        final String html =
            "<p class=\"test\">" + CONTENT_TEXT + "</p>" +
            "<p class=\"iscaption\">" + CONTENT_TEXT + "</p>";

        final String expected =
            "<p>" + CONTENT_TEXT + "</p>" +
            "<p class=\"caption\">" + CONTENT_TEXT + "</p>";

        assertExtractor(expected, html);
    }

    public void testHiddenArticle() {
        final String html =
            "<article>" +
                "<p class=\"visible\">" + CONTENT_TEXT + "</p>" +
                "<p class=\"hidden another\">" + CONTENT_TEXT + "</p>" +
                "<div class=\"nested hidden\"><p>" + CONTENT_TEXT + "</p></div>" +
                "<div class=\"invisible\"><p>" + CONTENT_TEXT + "</p></div>" +
                "<p class=\"last hidden\">" + CONTENT_TEXT + "</p>" +
            "</article>";

        mHead.setInnerHTML(
            "<meta name=\"viewport\" content=\"width=device-width\">" +
            "<style>.hidden {display: none} .invisible {display: none}</style>"
        );

        final String expected =
            "<p>" + CONTENT_TEXT + "</p>" +
            "<p>" + CONTENT_TEXT + "</p>" +
            "<p>" + CONTENT_TEXT + "</p>" +
            "<p>" + CONTENT_TEXT + "</p>";

        assertExtractor(expected, html);
    }
}
