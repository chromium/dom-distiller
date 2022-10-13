// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;

public class IEReadingViewParserTest extends DomDistillerJsTestCase {

    public void testTitle() {
        String expectedTitle = "Testing title";
        mHead.appendChild(TestUtil.createTitle(expectedTitle));
        createMeta("title", expectedTitle);
        mBody.appendChild(TestUtil.createHeading(
                1, "start_h1: " + expectedTitle + " end_h1"));

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(expectedTitle, parser.getTitle());
    }

    public void testDateInOneElemWithOneClass() {
        String expectedDate = "Monday January 1st 2011 01:01";
        Element div = TestUtil.createDiv(0);
        div.addClassName("dateline");
        div.setInnerHTML(expectedDate);
        mRoot.appendChild(div);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(expectedDate, parser.getArticle().publishedTime);
    }

    public void testDateInSubstringClassName() {
        Element div = TestUtil.createDiv(0);
        div.addClassName("b4datelineaft");
        div.setInnerHTML("Monday January 1st 2011 01:01");
        mRoot.appendChild(div);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals("", parser.getArticle().publishedTime);
    }

    public void testDateInOneElemWithMultiClasses() {
        String expectedDate = "Tuesday February 2nd 2012 02:02";
        Element div = TestUtil.createDiv(0);
        div.addClassName("blah1");
        div.addClassName("dateline");
        div.addClassName("blah2");
        div.setInnerHTML(expectedDate);
        mRoot.appendChild(div);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(expectedDate, parser.getArticle().publishedTime);
    }

    public void testDateInOneBranch() {
        String expectedDate = "Wednesday Mar 3rd 2013 03:03";
        Element div1 = TestUtil.createDiv(1);
        div1.addClassName("blah11");
        div1.addClassName("blah12");
        div1.setInnerHTML("blah11 blah12");
        Element div2 = TestUtil.createDiv(2);
        div2.addClassName("blah21");
        div2.setInnerHTML("blah21 only");
        Element div3 = TestUtil.createDiv(3);
        div3.addClassName("blah31");
        div3.addClassName("dateline");
        div3.setInnerHTML(expectedDate);

        mRoot.appendChild(div1);
        div1.appendChild(div2);
        div2.appendChild(div3);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(expectedDate, parser.getArticle().publishedTime);
    }

    public void testDateInMultiBranches() {
        String expectedDate = "Thursday Apr 4th 2014 04:04";
        Element div1 = TestUtil.createDiv(1);
        div1.addClassName("blah11");
        div1.addClassName("blah12");
        div1.setInnerHTML("blah11 blah12");
        Element div2 = TestUtil.createDiv(2);
        div2.addClassName("blah12");
        div2.addClassName("dateline");
        div2.setInnerHTML(expectedDate);
        Element div3 = TestUtil.createDiv(3);
        div3.addClassName("blah31");
        div3.setInnerHTML("blah31 only");

        mHead.appendChild(div1);
        mBody.appendChild(div2);
        mRoot.appendChild(div3);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(expectedDate, parser.getArticle().publishedTime);
    }

    public void testDateInMeta() {
        String expectedDate = "Friday Apr 5th 2015 05:05";
        createMeta("displaydate", expectedDate);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(expectedDate, parser.getArticle().publishedTime);
    }

    public void testAuthorInSiblings() {
        mHead.appendChild(TestUtil.createTitle("testing author"));

        String expectedAuthor = "Jane Doe";
        Element div1 = TestUtil.createDiv(1);
        div1.addClassName("blah1");
        div1.setInnerHTML("blah1 only");
        Element div2 = TestUtil.createDiv(2);
        div2.addClassName("byline-name");
        div2.setInnerHTML(expectedAuthor);

        mHead.appendChild(div2);
        mHead.appendChild(div1);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        String[] authors = parser.getArticle().authors;
        assertEquals(1, authors.length);
        assertEquals(expectedAuthor, authors[0]);
    }

    public void testAuthorInBranch() {
        mHead.appendChild(TestUtil.createTitle("testing author"));

        String expectedAuthor = "Jane Doe";
        Element div1 = TestUtil.createDiv(1);
        div1.addClassName("blah1");
        div1.setInnerHTML("blah1 only");
        Element div2 = TestUtil.createDiv(2);
        div2.addClassName("byline-name");
        div2.setInnerHTML(expectedAuthor);

        mHead.appendChild(div1);
        div1.appendChild(div2);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        String[] authors = parser.getArticle().authors;
        assertEquals(1, authors.length);
        assertEquals(expectedAuthor, authors[0]);
    }

    public void testPublisherFromPublisherAttr() {
        String expectedPublisher = "Publisher Attribute";
        Element div = TestUtil.createDiv(0);
        div.setAttribute("publisher", expectedPublisher);
        mBody.appendChild(div);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(expectedPublisher, parser.getPublisher());
    }

    public void testPublisherFromSourceOrganizationrAttr() {
        String expectedPublisher = "Source Organization Attribute";
        Element div = TestUtil.createDiv(0);
        div.setAttribute("source_organization", expectedPublisher);
        mBody.appendChild(div);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(expectedPublisher, parser.getPublisher());
    }

    public void testCopyrightInMeta() {
        String expectedCopyright = "Friday Apr 5th 2015 05:05";
        createMeta("copyright", expectedCopyright);

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(expectedCopyright, parser.getCopyright());
    }

    public void testUncaptionedDominantImage() {
        Element root = TestUtil.createDiv(0);
        ImageElement img = TestUtil.createImage();
        String expectedUrl = "http://example.com/dominant_without_caption.jpeg";
        img.setSrc(expectedUrl);
        img.setWidth(600);
        img.setHeight(400);
        root.appendChild(img);

        IEReadingViewParser parser = new IEReadingViewParser(root);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedUrl, image.url);
        assertEquals("", image.secureUrl);
        assertEquals("", image.type);
        assertEquals("", image.caption);
        assertEquals(600, image.width);
        assertEquals(400, image.height);
    }

    public void testUncaptionedDominantImageWithInvalidSize() {
        Element root = TestUtil.createDiv(0);
        ImageElement img = TestUtil.createImage();
        String expectedUrl = "http://example.com/dominant_without_caption.jpeg";
        img.setSrc(expectedUrl);
        img.setWidth(100);
        img.setHeight(100);
        root.appendChild(img);

        IEReadingViewParser parser = new IEReadingViewParser(root);
        // Expect no dominant image.
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(0, images.length);
    }

    public void testCaptionedDominantImageWithSmallestAR() {
        Element root = TestUtil.createDiv(0);
        String expectedCaption = "Captioned Dominant Image with Smallest AR";
        Element figure = createFigureWithCaption(expectedCaption);
        ImageElement img = TestUtil.createImage();
        String expectedUrl = "http://example.com/captioned_smallest_dominant.jpeg";
        img.setSrc(expectedUrl);
        img.setWidth(400);
        img.setHeight(307);

        root.appendChild(figure);
        figure.appendChild(img);
        mBody.appendChild(root);

        IEReadingViewParser parser = new IEReadingViewParser(root);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedUrl, image.url);
        assertEquals("", image.secureUrl);
        assertEquals("", image.type);
        assertEquals(expectedCaption, image.caption);
        assertEquals(400, image.width);
        assertEquals(307, image.height);
    }

    public void testCaptionedDominantImageWithBiggestAR() {
        Element root = TestUtil.createDiv(0);
        String expectedCaption = "Captioned Dominant Image with Biggest AR";
        Element figure = createFigureWithCaption(expectedCaption);
        ImageElement img = TestUtil.createImage();
        String expectedUrl = "http://example.com/captioned_biggest_dominant.jpeg";
        img.setSrc(expectedUrl);
        img.setWidth(400);
        img.setHeight(134);

        root.appendChild(figure);
        figure.appendChild(img);
        mBody.appendChild(root);

        IEReadingViewParser parser = new IEReadingViewParser(root);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedUrl, image.url);
        assertEquals("", image.secureUrl);
        assertEquals("", image.type);
        assertEquals(expectedCaption, image.caption);
        assertEquals(400, image.width);
        assertEquals(134, image.height);
    }

    public void testCaptionedDominantImageWithInvalidSize() {
        Element root = TestUtil.createDiv(0);
        String expectedCaption = "Captioned Dominant Image with Invalid Size";
        Element figure = createFigureWithCaption(expectedCaption);
        ImageElement img = TestUtil.createImage();
        String expectedUrl = "http://example.com/captioned_dominant_with_wrong_dimensions.jpeg";
        img.setSrc(expectedUrl);
        img.setWidth(100);
        img.setHeight(100);

        root.appendChild(figure);
        figure.appendChild(img);
        mBody.appendChild(root);

        IEReadingViewParser parser = new IEReadingViewParser(root);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedUrl, image.url);
        assertEquals("", image.secureUrl);
        assertEquals("", image.type);
        assertEquals(expectedCaption, image.caption);
        assertEquals(100, image.width);
        assertEquals(100, image.height);
    }

    public void testUncaptionedInlineImageWithSmallestAR() {
        Element root = TestUtil.createDiv(0);
        Element figure = createDefaultDominantFigure();
        ImageElement img = TestUtil.createImage();
        String expectedUrl = "http://example.com/inline_without_caption.jpeg";
        img.setSrc(expectedUrl);
        img.setWidth(400);
        img.setHeight(307);

        root.appendChild(figure);
        root.appendChild(img);
        mBody.appendChild(root);

        IEReadingViewParser parser = new IEReadingViewParser(root);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(2, images.length);
        MarkupParser.Image image = images[1];
        assertEquals(expectedUrl, image.url);
        assertEquals("", image.secureUrl);
        assertEquals("", image.type);
        assertEquals("", image.caption);
        assertEquals(400, image.width);
        assertEquals(307, image.height);
    }

    public void testUncaptionedInlineImageWithBiggestAR() {
        Element root = TestUtil.createDiv(0);
        Element figure = createDefaultDominantFigure();
        ImageElement img = TestUtil.createImage();
        String expectedUrl = "http://example.com/inline_without_caption.jpeg";
        img.setSrc(expectedUrl);
        img.setWidth(400);
        img.setHeight(134);

        root.appendChild(figure);
        root.appendChild(img);
        mBody.appendChild(root);

        IEReadingViewParser parser = new IEReadingViewParser(root);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(2, images.length);
        MarkupParser.Image image = images[1];
        assertEquals(expectedUrl, image.url);
        assertEquals("", image.secureUrl);
        assertEquals("", image.type);
        assertEquals("", image.caption);
        assertEquals(400, image.width);
        assertEquals(134, image.height);
    }

    public void testCaptionedInlineImageWithInvalidSize() {
        Element root = TestUtil.createDiv(0);
        Element dominant = createDefaultDominantFigure();
        String expectedCaption = "Captioned Inline Image with Smallest AR";
        Element figure = createFigureWithCaption(expectedCaption);
        ImageElement img = TestUtil.createImage();
        String expectedUrl = "http://example.com/captioned_smallest_inline.jpeg";
        img.setSrc(expectedUrl);
        img.setWidth(400);
        img.setHeight(400);

        root.appendChild(dominant);
        root.appendChild(figure);
        figure.appendChild(img);
        mBody.appendChild(root);

        IEReadingViewParser parser = new IEReadingViewParser(root);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(2, images.length);
        MarkupParser.Image image = images[1];
        assertEquals(expectedUrl, image.url);
        assertEquals("", image.secureUrl);
        assertEquals("", image.type);
        assertEquals(expectedCaption, image.caption);
        assertEquals(400, image.width);
        assertEquals(400, image.height);
    }

    public void testOptOut() {
        createMeta("IE_RM_OFF", "true");

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(true, parser.optOut());
    }

    public void testOptIn() {
        createMeta("IE_RM_OFF", "false");

        IEReadingViewParser parser = new IEReadingViewParser(mRoot);
        assertEquals(false, parser.optOut());
    }

    private void createMeta(String name, String content) {
        mHead.appendChild(TestUtil.createMetaName(name, content));
    }

    private Element createFigureWithCaption(String caption) {
        Element figure = Document.get().createElement("FIGURE");
        Element figcaption = Document.get().createElement("FIGCAPTION");
        figcaption.setInnerHTML(caption);
        figure.appendChild(figcaption);
        return figure;
    }

    private Element createDefaultDominantFigure() {
        Element figure = createFigureWithCaption("Default Dominant Image");
        ImageElement image = TestUtil.createImage();
        image.setSrc("http://example.com/dominant.jpeg");
        image.setWidth(600);
        image.setHeight(400);

        figure.appendChild(image);
        return figure;
    }
}
