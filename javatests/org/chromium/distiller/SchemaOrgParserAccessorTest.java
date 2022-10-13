// Copyright 2014 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;

public class SchemaOrgParserAccessorTest extends DomDistillerJsTestCase {

    public void testImageWithEmbeddedPublisher() {
        String expectedUrl = "http://dummy/test_image_with_embedded_item.html";
        String expectedFormat = "jpeg";
        String expectedCaption = "A test for IMAGE with embedded publisher";
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/ImageObject\">" +
                "<h1 itemprop=\"headline\">Testcase for IMAGE" +
                "</h1>" +
                "<h2 itemprop=\"description\">Testing IMAGE with embedded publisher" +
                "</h2>" +
                "<a itemprop=\"contentUrl\" href=\"" + expectedUrl + "\">test results" +
                "</a>" +
                "<div id=\"2\" itemscope itemtype=\"http://schema.org/Organization\"" +
                    " itemprop=\"publisher\">Publisher: " +
                    "<span itemprop=\"name\">Whatever Image Incorporated" +
                    "</span>" +
                "</div>" +
                "<div id=\"3\">" +
                    "<span itemprop=\"copyrightYear\">1999-2022" +
                    "</span>" +
                    "<span itemprop=\"copyrightHolder\">Whoever Image Copyrighted" +
                    "</span>" +
                "</div>" +
                "<span itemprop=\"encodingFormat\">" + expectedFormat +
                "</span>" +
                "<span itemprop=\"caption\">" + expectedCaption +
                "</span>" +
                "<meta itemprop=\"representativeOfPage\" content=\"true\">" +
                "<meta itemprop=\"width\" content=\"600\">" +
                "<meta itemprop=\"height\" content=\"400\">" +
            "</div>";

        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("", parser.getType());
        assertEquals("", parser.getTitle());
        assertEquals("", parser.getDescription());
        assertEquals("", parser.getUrl());
        assertEquals("", parser.getPublisher());
        assertEquals(null, parser.getArticle());
        assertEquals("", parser.getAuthor());
        assertEquals("", parser.getCopyright());
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedUrl, image.url);
        assertEquals("", image.secureUrl);
        assertEquals(expectedFormat, image.type);
        assertEquals(expectedCaption, image.caption);
        assertEquals(600, image.width);
        assertEquals(400, image.height);
    }

    public void test2Images() {
        String expectedUrl1 = "http://dummy/test_1st image.html";
        String expectedPublisher1 = "Whatever 1st Image Incorporated";
        String expectedFormat1 = "jpeg";
        String expectedCaption1 = "A test for 1st IMAGE";
        String expectedUrl2 = "http://dummy/test_2nd image.html";
        String expectedFormat2 = "gif";
        String expectedCaption2 = "A test for 2nd IMAGE";
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/ImageObject\">" +
                "<h1 itemprop=\"headline\">Testcase for 1st IMAGE" +
                "</h1>" +
                "<h2 itemprop=\"description\">Testing 1st IMAGE" +
                "</h2>" +
                "<a itemprop=\"contentUrl\" href=\"" + expectedUrl1 + "\">1st test results" +
                "</a>" +
                "<div id=\"2\" itemprop=\"publisher\">" + expectedPublisher1 +
                "</div>" +
                "<div id=\"3\">" +
                    "<span itemprop=\"copyrightYear\">1000-1999" +
                    "</span>" +
                    "<span itemprop=\"copyrightHolder\">Whoever 1st Image Copyrighted" +
                    "</span>" +
                "</div>" +
                "<span itemprop=\"encodingFormat\">" + expectedFormat1 +
                "</span>" +
                "<span itemprop=\"caption\">" + expectedCaption1 +
                "</span>" +
                "<meta itemprop=\"representativeOfPage\" content=\"false\">" +
                "<meta itemprop=\"width\" content=\"400\">" +
                "<meta itemprop=\"height\" content=\"300\">" +
            "</div>" +
            "<div id=\"4\" itemscope itemtype=\"http://schema.org/ImageObject\">" +
                "<h3 itemprop=\"headline\">Testcase for 2nd IMAGE" +
                "</h3>" +
                "<h4 itemprop=\"description\">Testing 2nd IMAGE" +
                "</h4>" +
                "<a itemprop=\"contentUrl\" href=\"" + expectedUrl2 + "\">2nd test results" +
                "</a>" +
                "<div id=\"5\" itemprop=\"publisher\">Whatever 2nd Image Incorporated" +
                "</div>" +
                "<div id=\"6\">" +
                    "<span itemprop=\"copyrightYear\">2000-2999" +
                    "</span>" +
                    "<span itemprop=\"copyrightHolder\">Whoever 2nd Image Copyrighted" +
                    "</span>" +
                "</div>" +
                "<span itemprop=\"encodingFormat\">" + expectedFormat2 +
                "</span>" +
                "<span itemprop=\"caption\">" + expectedCaption2 +
                "</span>" +
                "<meta itemprop=\"representativeOfPage\" content=\"true\">" +
                "<meta itemprop=\"width\" content=\"1000\">" +
                "<meta itemprop=\"height\" content=\"600\">" +
            "</div>";

        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        // The basic properties of Thing should be from the first image that was
        // inserted.
        assertEquals("", parser.getType());
        assertEquals("", parser.getTitle());
        assertEquals("", parser.getDescription());
        assertEquals("", parser.getUrl());
        assertEquals("", parser.getPublisher());
        assertEquals(null, parser.getArticle());
        assertEquals("", parser.getAuthor());
        assertEquals("", parser.getCopyright());

        MarkupParser.Image[] images = parser.getImages();
        assertEquals(2, images.length);
        // The 2nd image that was inserted is representative of page, so the
        // images should be swapped in |images|.
        MarkupParser.Image image = images[0];
        assertEquals(expectedUrl2, image.url);
        assertEquals("", image.secureUrl);
        assertEquals(expectedFormat2, image.type);
        assertEquals(expectedCaption2, image.caption);
        assertEquals(1000, image.width);
        assertEquals(600, image.height);
        image = images[1];
        assertEquals(expectedUrl1, image.url);
        assertEquals("", image.secureUrl);
        assertEquals(expectedFormat1, image.type);
        assertEquals(expectedCaption1, image.caption);
        assertEquals(400, image.width);
        assertEquals(300, image.height);
    }

    public void testArticleWithEmbeddedAuthorAndPublisher() {
        String expectedTitle = "Testcase for ARTICLE";
        String expectedDescription = "Testing ARTICLE with embedded author and publisher";
        String expectedUrl = "http://dummy/test_article_with_embedded_items.html";
        String expectedImage = "http://dummy/test_article_with_embedded_items.jpeg";
        String expectedAuthor = "Whoever authored";
        String expectedPublisher = "Whatever Article Incorporated";
        String expectedDatePublished = "April 15, 2014";
        String expectedTimeModified = "2014-04-16T23:59";
        String expectedCopyrightYear = "2000-2014";
        String expectedCopyrightHolder = "Whoever Article Copyrighted";
        String expectedSection = "Romance thriller";
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/Article\">" +
                "<h1 itemprop=\"headline\">" + expectedTitle +
                "</h1>" +
                "<h2 itemprop=\"description\">" + expectedDescription +
                "</h2>" +
                "<a itemprop=\"url\" href=\"" + expectedUrl + "\">test results" +
                "</a>" +
                "<img itemprop=\"image\" src=\"" + expectedImage + "\">" +
                "<div id=\"2\" itemscope itemtype=\"http://schema.org/Person\"" +
                    " itemprop=\"author\">Author: " +
                    "<span itemprop=\"name\">" + expectedAuthor +
                    "</span>" +
                "</div>" +
                "<div id=\"3\" itemscope itemtype=\"http://schema.org/Organization\"" +
                    " itemprop=\"publisher\">Publisher: " +
                    "<span itemprop=\"name\">" + expectedPublisher +
                    "</span>" +
                "</div>" +
                "<span itemprop=\"datePublished\">" + expectedDatePublished +
                "</span>" +
                "<time itemprop=\"dateModified\" datetime=\"" + expectedTimeModified +
                    "\">April 16, 2014 11:59pm" +
                "</time>" +
                "<span itemprop=\"copyrightYear\">" + expectedCopyrightYear +
                "</span>" +
                "<span itemprop=\"copyrightHolder\">" + expectedCopyrightHolder +
                "</span>" +
                "<span itemprop=\"articleSection\">" + expectedSection +
                "</span>" +
            "</div>";

        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("Article", parser.getType());
        assertEquals(expectedTitle, parser.getTitle());
        assertEquals(expectedDescription, parser.getDescription());
        assertEquals(expectedUrl, parser.getUrl());
        assertEquals(expectedAuthor, parser.getAuthor());
        assertEquals(expectedPublisher, parser.getPublisher());
        assertEquals( "Copyright " + expectedCopyrightYear + " " + expectedCopyrightHolder,
                parser.getCopyright());
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        assertEquals(expectedImage, images[0].url);
        MarkupParser.Article article = parser.getArticle();
        assertEquals(expectedDatePublished, article.publishedTime);
        assertEquals(expectedTimeModified, article.modifiedTime);
        assertEquals("", article.expirationTime);
        assertEquals(expectedSection, article.section);
        assertEquals(1, article.authors.length);
        assertEquals(expectedAuthor, article.authors[0]);
    }

    public void testArticleWithEmbeddedAndTopLevelImages() {
        String expectedTitle = "Testcase for ARTICLE with Embedded and Top-Level IMAGEs";
        String expectedDescription = "Testing ARTICLE with embedded and top-level images";
        String expectedUrl = "http://dummy/test_article_with_embedded_and_toplevel_images.html";
        String expectedImage1 = "http://dummy/test_toplevel image.html";
        String expectedFormat1 = "gif";
        String expectedCaption1 = "A test for top-level IMAGE";
        String expectedImage2 = "http://dummy/test_article_with_embedded_and_toplevel_images.html";
        String expectedFormat2 = "jpeg";
        String expectedCaption2 = "A test for emedded IMAGE in ARTICLE";
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/ImageObject\">" +
                "<span itemprop=\"headline\">Title should be ignored" +
                "</span>" +
                "<span itemprop=\"description\">Testing top-level IMAGE" +
                "</span>" +
                "<a itemprop=\"url\" href=\"http://dummy/to_be_ignored_url.html\">test results" +
                "</a>" +
                "<a itemprop=\"contentUrl\" href=\"" + expectedImage1 + "\">top-level image" +
                "</a>" +
                "<span itemprop=\"encodingFormat\">" + expectedFormat1 +
                "</span>" +
                "<span itemprop=\"caption\">" + expectedCaption1 +
                "</span>" +
                "<meta itemprop=\"representativeOfPage\" content=\"true\">" +
                "<meta itemprop=\"width\" content=\"1000\">" +
                "<meta itemprop=\"height\" content=\"600\">" +
            "</div>" +
            "<div id=\"2\" itemscope itemtype=\"http://schema.org/Article\">" +
                "<span itemprop=\"headline\">" + expectedTitle +
                "</span>" +
                "<span itemprop=\"description\">" + expectedDescription +
                "</span>" +
                "<a itemprop=\"url\" href=\"" + expectedUrl + "\">test results" +
                "</a>" +
                "<img itemprop=\"image\" src=\"http://dummy/should_be_ignored_image.jpeg\">" +
                "<div id=\"3\" itemscope itemtype=\"http://schema.org/ImageObject\"" +
                    " itemprop=\"associatedMedia\">" +
                    "<a itemprop=\"url\" href=\"" + expectedImage2 + "\">associated image" +
                    "</a>" +
                    "<span itemprop=\"encodingFormat\">" + expectedFormat2 +
                    "</span>" +
                    "<span itemprop=\"caption\">" + expectedCaption2 +
                    "</span>" +
                    "<meta itemprop=\"representativeOfPage\" content=\"false\">" +
                    "<meta itemprop=\"width\" content=\"600\">" +
                    "<meta itemprop=\"height\" content=\"400\">" +
                "</div>" +
            "</div>";

        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("Article", parser.getType());
        assertEquals(expectedTitle, parser.getTitle());
        assertEquals(expectedDescription, parser.getDescription());
        assertEquals(expectedUrl, parser.getUrl());
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(2, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedImage2, image.url);
        assertEquals("", image.secureUrl);
        assertEquals(expectedFormat2, image.type);
        assertEquals(expectedCaption2, image.caption);
        assertEquals(600, image.width);
        assertEquals(400, image.height);
        image = images[1];
        assertEquals(expectedImage1, image.url);
        assertEquals("", image.secureUrl);
        assertEquals(expectedFormat1, image.type);
        assertEquals(expectedCaption1, image.caption);
        assertEquals(1000, image.width);
        assertEquals(600, image.height);
    }

    public void testItemscopeInHTMLTag() {
        setItemScopeAndType(mRoot, "Article");

        String expectedTitle = "Testcase for ItemScope in HTML tag";
        Element h = TestUtil.createHeading(1, expectedTitle);
        setItemProp(h, "headline");
        mBody.appendChild(h);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("Article", parser.getType());
        assertEquals(expectedTitle, parser.getTitle());
        assertTrue(parser.getArticle() != null);

        // Remove "itemscope" and "itemtype" attributes in <html> tag, so that
        // other testcases won't be affected.
        mRoot.removeAttribute("ITEMSCOPE");
        mRoot.removeAttribute("ITEMTYPE");
    }

    public void testSupportedWithUnsupportedItemprop() {
        String expectedTitle = "Testcase for Supported With Unsupported Itemprop";
        String expectedSection = "Testing";
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/Article\">" +
                "<span itemprop=\"headline\">" + expectedTitle +
                "</span>" +
                "<span itemprop=\"articleSection\">" + expectedSection +
                "</span>" +
                // Add unsupported AggregateRating to supported Article as itemprop.
                "<div id=\"2\" itemscope itemtype=\"http://schema.org/AggregateRating\"" +
                    " itemprop=\"aggregateRating\">Ratings: " +
                    "<span itemprop=\"ratingValue\">9.9" +
                    "</span>" +
                    // Add supported Person to unsupported AggregateRating as itemprop.
                    "<div id=\"3\" itemscope itemtype=\"http://schema.org/Person\"" +
                        " itemprop=\"author\">Author: " +
                        "<span itemprop=\"name\">Whoever authored" +
                        "</span>" +
                    "</div>" +
                "</div>" +
            "</div>";

        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("Article", parser.getType());
        assertEquals(expectedTitle, parser.getTitle());
        assertEquals("", parser.getDescription());
        assertEquals("", parser.getUrl());
        assertEquals("", parser.getAuthor());
        assertEquals("", parser.getPublisher());
        assertEquals("", parser.getCopyright());
        MarkupParser.Article article = parser.getArticle();
        assertEquals("", article.publishedTime);
        assertEquals("", article.modifiedTime);
        assertEquals("", article.expirationTime);
        assertEquals(expectedSection, article.section);
        assertEquals(0, article.authors.length);
    }

    public void testUnsupportedWithSupportedItemprop() {
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/Movie\">" +
                "<span itemprop=\"headline\">Testcase for Unsupported With Supported Itemprop" +
                "</span>" +
                // Add supported Person to unsupported Movie as itemprop.
                "<div id=\"3\" itemscope itemtype=\"http://schema.org/Person\"" +
                    " itemprop=\"publisher\">Publisher: " +
                    "<span itemprop=\"name\">Whoever published" +
                    "</span>" +
                "</div>" +
            "</div>";
        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("", parser.getType());
        assertEquals("", parser.getTitle());
        assertEquals("", parser.getDescription());
        assertEquals("", parser.getUrl());
        assertEquals("", parser.getAuthor());
        assertEquals("", parser.getPublisher());
        assertEquals("", parser.getCopyright());
        assertEquals(null, parser.getArticle());
        assertEquals(0, parser.getImages().length);
    }

    public void testUnsupportedWithNestedSupported() {
        String expectedTitle = "Testcase for ARTICLE nested in Unsupported Type";
        String expectedDescription = "Testing ARTICLE that is nested within unsupported type";
        String expectedUrl = "http://dummy/test_article_with_embedded_items.html";
        String expectedImage = "http://dummy/test_article_with_embedded_items.jpeg";
        String expectedAuthor = "Whoever authored";
        String expectedPublisher = "Whatever Article Incorporated";
        String expectedDatePublished = "April 15, 2014";
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/Movie\">" +
                "<span itemprop=\"headline\">Testcase for Unsupported With Supported Itemprop" +
                "</span>" +
                // Add supported Article to unsupported Movie as a non-itemprop.
                "<div id=\"2\" itemscope itemtype=\"http://schema.org/Article\">" +
                    "<span itemprop=\"headline\">" + expectedTitle +
                    "</span>" +
                    "<span itemprop=\"description\">" + expectedDescription +
                    "</span>" +
                    "<a itemprop=\"url\" href=\"" + expectedUrl + "\">test results" +
                    "</a>" +
                    "<img itemprop=\"image\" src=\"" + expectedImage + "\">" +
                    "<div id=\"3\" itemscope itemtype=\"http://schema.org/Person\"" +
                        " itemprop=\"author\">Author: " +
                        "<span itemprop=\"name\">" + expectedAuthor +
                        "</span>" +
                    "</div>" +
                    "<div id=\"4\" itemscope itemtype=\"http://schema.org/Organization\"" +
                        " itemprop=\"publisher\">Publisher: " +
                        "<span itemprop=\"name\">" + expectedPublisher +
                        "</span>" +
                    "</div>" +
                    "<span itemprop=\"datePublished\">" + expectedDatePublished +
                    "</span>" +
                "</div>" +
            "</div>";
        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("Article", parser.getType());
        assertEquals(expectedTitle, parser.getTitle());
        assertEquals(expectedDescription, parser.getDescription());
        assertEquals(expectedUrl, parser.getUrl());
        assertEquals(expectedAuthor, parser.getAuthor());
        assertEquals(expectedPublisher, parser.getPublisher());
        assertEquals("", parser.getCopyright());
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        assertEquals(expectedImage, images[0].url);
        MarkupParser.Article article = parser.getArticle();
        assertEquals(expectedDatePublished, article.publishedTime);
        assertEquals("", article.expirationTime);
        assertEquals(1, article.authors.length);
        assertEquals(expectedAuthor, article.authors[0]);
    }

    public void testSameItempropDifferentValues() {
        String expectedAuthor = "Author 1";
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/Article\">" +
                "<div id=\"2\" itemscope itemtype=\"http://schema.org/Person\"" +
                    " itemprop=\"author\">Authors: " +
                    "<span itemprop=\"name\">" + expectedAuthor +
                    "</span>" +
                    "<span itemprop=\"name\">Author 2" +
                    "</span>" +
                "</div>" +
            "</div>";

        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("Article", parser.getType());
        assertEquals(expectedAuthor, parser.getAuthor());
        MarkupParser.Article article = parser.getArticle();
        assertEquals(1, article.authors.length);
        assertEquals(expectedAuthor, article.authors[0]);
   }

    public void testItempropWithMultiProperties() {
        String expectedPerson = "Person foo";
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/Article\">" +
                "<div id=\"2\" itemscope itemtype=\"http://schema.org/Person\"" +
                    " itemprop=\"author publisher\">" +
                    "<span itemprop=\"name\">" + expectedPerson +
                    "</span>" +
                "</div>" +
            "</div>";

        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("Article", parser.getType());
        assertEquals(expectedPerson, parser.getAuthor());
        assertEquals(expectedPerson, parser.getPublisher());
        MarkupParser.Article article = parser.getArticle();
        assertEquals(1, article.authors.length);
        assertEquals(expectedPerson, article.authors[0]);
   }

    public void testAuthorPropertyFromDifferentSources() {
        // Test that "creator" property is used when "author" property doens't exist.
        String expectedCreator = "Whoever created";
        String htmlStr =
            "<div id=\"1\" itemscope itemtype=\"http://schema.org/Article\">" +
                "<div id=\"2\" itemscope itemtype=\"http://schema.org/Person\"" +
                    " itemprop=\"author\">Creator: " +
                    "<span itemprop=\"name\">" + expectedCreator +
                    "</span>" +
                "</div>" +
            "</div>";

        Element rootDiv = TestUtil.createDiv(0);
        rootDiv.setInnerHTML(htmlStr);
        mBody.appendChild(rootDiv);

        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("Article", parser.getType());
        assertEquals(expectedCreator, parser.getAuthor());
        MarkupParser.Article article = parser.getArticle();
        assertEquals(1, article.authors.length);
        assertEquals(expectedCreator, article.authors[0]);

        // Remove article item from parent, to clear the state for the next rel="author" test.
        rootDiv.removeFromParent();

        // Test that rel="author" attribute in an anchor element is used in the absence of "author"
        // or "creator" properties.
        String expectedAuthor = "Chromium Authors";
        AnchorElement link = TestUtil.createAnchor("http://dummy/rel_author.html", expectedAuthor);
        link.setRel("author");
        mBody.appendChild(link);

        parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals("", parser.getType());
        assertEquals(expectedAuthor, parser.getAuthor());
        assertEquals(null, parser.getArticle());

        // Remove anchor from parent, so that other testcases won't be affected.
        link.removeFromParent();
    }

    public void testGetTitleWhenTheMainArticleDoesntHaveHeadline() {
        String expectedTitle = "This is a headline";
        String elements =
            "<div itemscope itemtype=\"http://schema.org/Article\" " +
                "style=\"width: 200; height: 300\">" +
                "<span itemprop=\"name\">A headline</span>" +
            "</div>" +
            "<div itemscope itemtype=\"http://schema.org/Article\" " +
                "style=\"width: 200; height: 400\">" +
                "<span itemprop=\"name\">" + expectedTitle + "</span>" +
            "</div>" +
            "<div itemscope itemtype=\"http://schema.org/Article\" " +
                "style=\"width: 200; height: 300\">" +
                "<span itemprop=\"name headline\">Another headline</span>" +
            "</div>";
        mBody.setInnerHTML(elements);
        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals(expectedTitle, parser.getTitle());
    }

    public void testGetTitleWhenTheMainArticleHasHeadline() {
        String expectedTitle = "This is a headline";
        String elements =
            "<div itemscope itemtype=\"http://schema.org/Article\" " +
                "style=\"width: 200; height: 290\">" +
                "<span itemprop=\"name headline\">A headline</span>" +
            "</div>" +
            "<div itemscope itemtype=\"http://schema.org/Article\" " +
                "style=\"width: 100; height: 300\">" +
                "<span itemprop=\"name headline\">Another headline</span>" +
            "</div>" +
            "<div itemscope itemtype=\"http://schema.org/Article\" " +
                "style=\"width: 200; height: 300\">" +
                "<span itemprop=\"name headline\">" + expectedTitle + "</span>" +
            "</div>";
        mBody.setInnerHTML(elements);
        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals(expectedTitle, parser.getTitle());
    }

    public void testGetTitleWithNestedArticles() {
        String expectedTitle = "This is a headline";
        String elements =
            "<div itemscope itemtype=\"http://schema.org/Article\" " +
                "style=\"width: 200; height: 100\">" +
                "<span itemprop=\"name headline\">A headline</span>" +
                "<div itemscope itemtype=\"http://schema.org/Article\" " +
                "style=\"width: 400; height: 300\">" +
                    "<span itemprop=\"name headline\">" + expectedTitle + "</span>" +
                "</div>" +
            "</div>" +
            "<div itemscope itemtype=\"http://schema.org/Article\" " +
                "style=\"width: 200; height: 200\">" +
                "<span itemprop=\"name headline\">A headline</span>" +
            "</div>";
        mBody.setInnerHTML(elements);
        SchemaOrgParserAccessor parser = new SchemaOrgParserAccessor(mRoot);
        assertEquals(expectedTitle, parser.getTitle());
    }

    private void setItemScopeAndType(Element e, String type) {
        e.setAttribute("ITEMSCOPE", "");
        e.setAttribute("ITEMTYPE", "http://schema.org/" + type);
    }

    private void setItemProp(Element e, String name) {
        e.setAttribute("itemprop", name);
    }
}
