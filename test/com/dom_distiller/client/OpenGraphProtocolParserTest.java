// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;

import com.google.gwt.junit.client.GWTTestCase;

public class OpenGraphProtocolParserTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.dom_distiller.DomDistillerJUnit";
    }

    public void testRequiredPropertiesAndDescriptionAndSiteName() {
        String expectedTitle = "Testing required OpenGraph Proptocol properties and optional Description of the document.";
        createMeta("og:title", expectedTitle);
        String expectedType = "video.movie";
        createMeta("og:type", expectedType);
        String expectedImage = "http://test/image.jpeg";
        createMeta("og:image", expectedImage);
        String expectedUrl = "http://test/test.html";
        createMeta("og:url", expectedUrl);
        String expectedDescr = "This test expects to retrieve the required OpenGraph Proptocol properties and optional Description of the document.";
        createMeta("og:description", expectedDescr);
        String expectedSiteName = "Google";
        createMeta("og:site_name", expectedSiteName);

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals(expectedTitle, parser.getTitle());
        assertEquals(expectedType, parser.getType());
        assertEquals(expectedUrl, parser.getUrl());
        assertEquals(expectedDescr, parser.getDescription());
        assertEquals(expectedSiteName, parser.getPublisher());
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedImage, image.image);
        assertEquals(null, image.url);
        assertEquals(null, image.secureUrl);
        assertEquals(null, image.type);
        assertEquals(0, image.width);
        assertEquals(0, image.height);
    }

    public void testNoRequiredImage() {
        createDefaultTitle();
        createDefaultType();
        createDefaultUrl();
        // Set an image structured property but not the root property.
        createMeta("og:image:url", "http://test/image.jpeg");

        assertEquals(null, OpenGraphProtocolParser.parse(mRoot));
    }

    public void testOneImage() {
        // Create the required properties except for "image".
        createDefaultTitle();
        createDefaultType();
        createDefaultUrl();

        // Create the image property and its structure.
        String expectedUrl = "http://test/image.jpeg";
        createMeta("og:image", expectedUrl);
        createMeta("og:image:url", expectedUrl);
        String expectedSecureUrl = "https://test/image.jpeg";
        createMeta("og:image:secure_url", expectedSecureUrl);
        String expectedType = "image/jpeg";
        createMeta("og:image:type", expectedType);
        createMeta("og:image:width", "600");
        createMeta("og:image:height", "400");
        
        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedUrl, image.image);
        assertEquals(expectedUrl, image.url);
        assertEquals(expectedSecureUrl, image.secureUrl);
        assertEquals(expectedType, image.type);
        assertEquals(600, image.width);
        assertEquals(400, image.height);
    }

    public void testCompleteMultipleImages() {
        // Create the required properties except for "image".
        createDefaultTitle();
        createDefaultType();
        createDefaultUrl();

        // Create the image properties and their structures.
        String expectedUrl1 = "http://test/image1.jpeg";
        createMeta("og:image", expectedUrl1);
        createMeta("og:image:url", expectedUrl1);
        String expectedSecureUrl1 = "https://test/image1.jpeg";
        createMeta("og:image:secure_url", expectedSecureUrl1);
        String expectedType1 = "image/jpeg";
        createMeta("og:image:type", expectedType1);
        createMeta("og:image:width", "600");
        createMeta("og:image:height", "400");

        String expectedUrl2 = "http://test/image2.jpeg";
        createMeta("og:image", expectedUrl2);
        createMeta("og:image:url", expectedUrl2);
        String expectedSecureUrl2 = "https://test/image2.jpeg";
        createMeta("og:image:secure_url", expectedSecureUrl2);
        String expectedType2 = "image/gif";
        createMeta("og:image:type", expectedType2);
        createMeta("og:image:width", "1024");
        createMeta("og:image:height", "900");

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(2, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedUrl1, image.image);
        assertEquals(expectedUrl1, image.url);
        assertEquals(expectedSecureUrl1, image.secureUrl);
        assertEquals(expectedType1, image.type);
        assertEquals(600, image.width);
        assertEquals(400, image.height);
        image = images[1];
        assertEquals(expectedUrl2, image.image);
        assertEquals(expectedUrl2, image.url);
        assertEquals(expectedSecureUrl2, image.secureUrl);
        assertEquals(expectedType2, image.type);
        assertEquals(1024, image.width);
        assertEquals(900, image.height);
    }

    public void testIncompleteMultipleImages() {
        // Create the required properties except for "image".
        createDefaultTitle();
        createDefaultType();
        createDefaultUrl();

        // Create the image properties and their structures.
        String expectedUrl1 = "http://test/image1.jpeg";
        createMeta("og:image", expectedUrl1);
        createMeta("og:image:url", expectedUrl1);
        String expectedSecureUrl1 = "https://test/image1.jpeg";
        createMeta("og:image:secure_url", expectedSecureUrl1);
        String expectedType1 = "image/jpeg";
        createMeta("og:image:type", expectedType1);

        // Intentionally insert a root image tag before the width and height
        // tags, so the width and height should belong to the 2nd image.
        String expectedUrl2 = "http://test/image2.jpeg";
        createMeta("og:image", expectedUrl2);
        createMeta("og:image:width", "600");
        createMeta("og:image:height", "400");

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(2, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedUrl1, image.image);
        assertEquals(expectedUrl1, image.url);
        assertEquals(expectedSecureUrl1, image.secureUrl);
        assertEquals(expectedType1, image.type);
        assertEquals(0, image.width);
        assertEquals(0, image.height);
        image = images[1];
        assertEquals(expectedUrl2, image.image);
        assertEquals(null, image.url);
        assertEquals(null, image.secureUrl);
        assertEquals(null, image.type);
        assertEquals(600, image.width);
        assertEquals(400, image.height);
    }

    public void testNoObjects() {
        // Create the required properties.
        createDefaultTitle();
        createDefaultType();
        createDefaultUrl();
        createDefaultImage();

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals(null, parser.getAuthor());
        assertEquals(null, parser.getArticle());
    }

    public void testProfile() {
        // Create the required properties except for "type".
        createDefaultTitle();
        createDefaultUrl();
        createDefaultImage();

        // Create the "profile" object.
        createMeta("og:type", "profile");
        createMeta("profile:first_name", "Jane");
        createMeta("profile:last_name", "Doe");
        
        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals("Jane Doe", parser.getAuthor());
    }

    public void testArticle() {
        // Create the required properties except for "type".
        createDefaultTitle();
        createDefaultUrl();
        createDefaultImage();

        // Create the "article" object.
        createMeta("og:type", "article");
        String expectedSection = "GWT Testing";
        createMeta("article:section", expectedSection);
        String expectedPublishedTime = "2014-04-01T01:23:59Z";
        createMeta("article:published_time", expectedPublishedTime);
        String expectedModifiedTime = "2014-04-02T02:23:59Z";
        createMeta("article:modified_time", expectedModifiedTime);
        String expectedExpirationTime = "2014-04-03T03:23:59Z";
        createMeta("article:expiration_time", expectedExpirationTime);
        String expectedAuthor1 = "http://blah/author1.html";
        createMeta("article:author", expectedAuthor1);
        String expectedAuthor2 = "http://blah/author2.html";
        createMeta("article:author", expectedAuthor2);

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        MarkupParser.Article article = parser.getArticle();
        assertEquals(expectedPublishedTime, article.publishedTime);
        assertEquals(expectedModifiedTime, article.modifiedTime);
        assertEquals(expectedExpirationTime, article.expirationTime);
        assertEquals(expectedSection, article.section);
        assertEquals(2, article.authors.length);
        assertEquals(expectedAuthor1, article.authors[0]);
        assertEquals(expectedAuthor2, article.authors[1]);
    }

    public void testOGAndProfilePrefixesInHtmlTag() {
        // Set prefix attribute in HTML tag.
        mRoot.setAttribute("prefix",
                "tstog: http://ogp.me/ns# tstpf: http://ogp.me/ns/profile#");

        // Create the required properties and description.
        String expectedTitle = "Testing customized OG and profile prefixes";
        createMeta("tstog:title", expectedTitle);
        createMeta("tstog:type", "profile");
        String expectedUrl = "http://test/url.html";
        createMeta("tstog:url", expectedUrl);
        String expectedImage = "http://test/image.jpeg";
        createMeta("tstog:image", expectedImage);
        String expectedDescr = "This tests the use of customized OG and profile prefixes";
        createMeta("tstog:description", expectedDescr);
        String expectedSiteName = "Google";
        createMeta("tstog:site_name", expectedSiteName);

        // Create the image structure.
        createMeta("tstog:image:url", expectedImage);
        String expectedSecureUrl = "https://test/image.jpeg";
        createMeta("tstog:image:secure_url", expectedSecureUrl);
        String expectedImageType = "image/jpeg";
        createMeta("tstog:image:type", expectedImageType);
        createMeta("tstog:image:width", "600");
        createMeta("tstog:image:height", "400");

        // Create the "profile" object.
        createMeta("tstpf:first_name", "Jane");
        createMeta("tstpf:last_name", "Doe");

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals(expectedTitle, parser.getTitle());
        assertEquals("profile", parser.getType());
        assertEquals(expectedUrl, parser.getUrl());
        assertEquals(expectedDescr, parser.getDescription());
        assertEquals(expectedSiteName, parser.getPublisher());
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedImage, image.image);
        assertEquals(expectedImage, image.url);
        assertEquals(expectedSecureUrl, image.secureUrl);
        assertEquals(expectedImageType, image.type);
        assertEquals(600, image.width);
        assertEquals(400, image.height);
        assertEquals("Jane Doe", parser.getAuthor());
    }

    public void testArticlePrefixInHeadTag() {
        // Set prefix attribute in HEAD tag.
        mHead.setAttribute("prefix",
                "tstog: http://ogp.me/ns# tsta: http://ogp.me/ns/article#");

        // Create the required properties.
        createCustomizedTitle();
        createCustomizedUrl();
        createCustomizedImage();
        createMeta("tstog:type", "article");

        // Create the "article" object.
        String expectedSection = "GWT Testing";
        createMeta("tsta:section", expectedSection);
        String expectedPublishedTime = "2014-04-01T01:23:59Z";
        createMeta("tsta:published_time", expectedPublishedTime);
        String expectedModifiedTime = "2014-04-02T02:23:59Z";
        createMeta("tsta:modified_time", expectedModifiedTime);
        String expectedExpirationTime = "2014-04-03T03:23:59Z";
        createMeta("tsta:expiration_time", expectedExpirationTime);
        String expectedAuthor1 = "http://blah/author1.html";
        createMeta("tsta:author", expectedAuthor1);
        String expectedAuthor2 = "http://blah/author2.html";
        createMeta("tsta:author", expectedAuthor2);

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals("article", parser.getType());
        MarkupParser.Article article = parser.getArticle();
        assertEquals(expectedPublishedTime, article.publishedTime);
        assertEquals(expectedModifiedTime, article.modifiedTime);
        assertEquals(expectedExpirationTime, article.expirationTime);
        assertEquals(expectedSection, article.section);
        assertEquals(2, article.authors.length);
        assertEquals(expectedAuthor1, article.authors[0]);
        assertEquals(expectedAuthor2, article.authors[1]);
    }

    public void testIncorrectPrefix() {
        // Set prefix attribute in HTML tag.
        mRoot.setAttribute("prefix", "tstog: http://ogp.me/ns#");

        // Create the required properties.
        createCustomizedTitle();
        createCustomizedType();
        createCustomizedUrl();
        createCustomizedImage();
        
        // Create the description property with the common "og" prefix, instead
        // of the customized "tstog" prefix.
        createMeta("og:description", "this description should be ignored");

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals(null, parser.getDescription());
    }

    public void testOGAndProfileXmlns() {
        // Set xmlns attributes in HTML tag.
        mRoot.setAttribute("xmlns:tstog", "http://ogp.me/ns#");
        mRoot.setAttribute("xmlns:tstpf", "http://ogp.me/ns/profile#");

        // Create the required properties and description.
        String expectedTitle = "Testing customized OG and profile xmlns";
        createMeta("tstog:title", expectedTitle);
        createMeta("tstog:type", "profile");
        String expectedUrl = "http://test/url.html";
        createMeta("tstog:url", expectedUrl);
        String expectedImage = "http://test/image.jpeg";
        createMeta("tstog:image", expectedImage);
        String expectedDescr = "This tests the use of customized OG and profile xmlns";
        createMeta("tstog:description", expectedDescr);
        String expectedSiteName = "Google";
        createMeta("tstog:site_name", expectedSiteName);

        // Create the image structure.
        createMeta("tstog:image:url", expectedImage);
        String expectedSecureUrl = "https://test/image.jpeg";
        createMeta("tstog:image:secure_url", expectedSecureUrl);
        String expectedImageType = "image/jpeg";
        createMeta("tstog:image:type", expectedImageType);
        createMeta("tstog:image:width", "600");
        createMeta("tstog:image:height", "400");

        // Create the "profile" object.
        createMeta("tstpf:first_name", "Jane");
        createMeta("tstpf:last_name", "Doe");

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals(expectedTitle, parser.getTitle());
        assertEquals("profile", parser.getType());
        assertEquals(expectedUrl, parser.getUrl());
        assertEquals(expectedDescr, parser.getDescription());
        assertEquals(expectedSiteName, parser.getPublisher());
        MarkupParser.Image[] images = parser.getImages();
        assertEquals(1, images.length);
        MarkupParser.Image image = images[0];
        assertEquals(expectedImage, image.image);
        assertEquals(expectedImage, image.url);
        assertEquals(expectedSecureUrl, image.secureUrl);
        assertEquals(expectedImageType, image.type);
        assertEquals(600, image.width);
        assertEquals(400, image.height);
        assertEquals("Jane Doe", parser.getAuthor());
    }

    public void testArticleXmlns() {
        // Set xmlns attributes in HTML tag.
        mRoot.setAttribute("xmlns:tstog", "http://ogp.me/ns#");
        mRoot.setAttribute("xmlns:tsta", "http://ogp.me/ns/article#");

        // Create the required properties.
        createCustomizedTitle();
        createCustomizedUrl();
        createCustomizedImage();
        createMeta("tstog:type", "article");

        // Create the "article" object.
        String expectedSection = "GWT Testing";
        createMeta("tsta:section", expectedSection);
        String expectedPublishedTime = "2014-04-01T01:23:59Z";
        createMeta("tsta:published_time", expectedPublishedTime);
        String expectedModifiedTime = "2014-04-02T02:23:59Z";
        createMeta("tsta:modified_time", expectedModifiedTime);
        String expectedExpirationTime = "2014-04-03T03:23:59Z";
        createMeta("tsta:expiration_time", expectedExpirationTime);
        String expectedAuthor1 = "http://blah/author1.html";
        createMeta("tsta:author", expectedAuthor1);
        String expectedAuthor2 = "http://blah/author2.html";
        createMeta("tsta:author", expectedAuthor2);

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals("article", parser.getType());
        MarkupParser.Article article = parser.getArticle();
        assertEquals(expectedPublishedTime, article.publishedTime);
        assertEquals(expectedModifiedTime, article.modifiedTime);
        assertEquals(expectedExpirationTime, article.expirationTime);
        assertEquals(expectedSection, article.section);
        assertEquals(2, article.authors.length);
        assertEquals(expectedAuthor1, article.authors[0]);
        assertEquals(expectedAuthor2, article.authors[1]);
    }

    public void testIncorrectXmlns() {
        // Set prefix attribute in HTML tag.
        mRoot.setAttribute("xmlns:tstog", "http://ogp.me/ns#");

        // Create the required properties.
        createCustomizedTitle();
        createCustomizedType();
        createCustomizedUrl();
        createCustomizedImage();

        // Create the description property with the common "og" prefix, instead
        // of the customized "tstog" prefix.
        createMeta("og:description", "this description should be ignored");

        OpenGraphProtocolParser parser = OpenGraphProtocolParser.parse(mRoot);
        assertTrue(parser != null);
        assertEquals(null, parser.getDescription());
    }

    @Override
    protected void gwtSetUp() throws Exception {
        // Get root element.
        mRoot = Document.get().getDocumentElement();

        // Get <head> element.
        NodeList<Element> heads = mRoot.getElementsByTagName("HEAD");
        if (heads.getLength() != 1)
            throw new Exception("There shouldn't be more than 1 <head> tag");
        mHead = heads.getItem(0);

        // Remove all attributes that specify prefix or namespace, so that each
        // testcase starts with clean HTML and HEAD tags.  Otherwise. a testcase
        // may run with the attributes set in a previous testcase, resulting in
        // unexpected results.
        mRoot.removeAttribute("prefix");
        mHead.removeAttribute("prefix");
        mRoot.removeAttribute("xmlns:tstog");
        mRoot.removeAttribute("xmlns:tstpf");
        mRoot.removeAttribute("xmlns:tsta");

        // Remove all meta tags, otherwise a testcase may run with the meta tags
        // set up in a previous testcase, resulting in unexpected results.
        NodeList<Element> allMeta = mRoot.getElementsByTagName("META");
        for (int i = allMeta.getLength() - 1; i >= 0; i--) {
            allMeta.getItem(i).removeFromParent();
        }
    }

    private void createDefaultTitle() {
        createMeta("og:title", "dummy title");
    }

    private void createCustomizedTitle() {
        createMeta("tstog:title", "dummy title");
    }

    private void createDefaultType() {
        createMeta("og:type", "website");
    }

    private void createCustomizedType() {
        createMeta("tstog:type", "website");
    }

    private void createDefaultUrl() {
        createMeta("og:url", "http://dummy/url.html");
    }

    private void createCustomizedUrl() {
        createMeta("tstog:url", "http://dummy/url.html");
    }

    private void createDefaultImage() {
        createMeta("og:image", "http://dummy/image.jpeg");
    }

    private void createCustomizedImage() {
        createMeta("tstog:image", "http://dummy/image.jpeg");
    }

    private void createDescription(String description, String prefix) {
        createMeta(prefix + ":description", description);
    }

    private void createMeta(String property, String content) {
        mHead.appendChild(TestUtil.createMetaProperty(property, content));
    }

    private Element mRoot;
    private Element mHead;
}
