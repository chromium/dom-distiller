// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import com.dom_distiller.proto.DomDistillerProtos;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.json.client.JSONObject;

import java.util.Arrays;

public class MarkupParserProtoTest extends DomDistillerTestCase {
    public void testCompleteInfoWithMultipleImages() {
        // Create the required properties for OpenGraphProtocol except for "image".
        createDefaultOGTitle();
        createDefaultOGType();
        createDefaultOGUrl();

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

        MarkupParser parser = new MarkupParser(mRoot);
        DomDistillerProtos.MarkupInfo markupInfo = parser.getMarkupInfo();
        assertEquals("dummy title", markupInfo.getTitle());
        assertEquals("", markupInfo.getType());
        assertEquals("http://dummy/url.html", markupInfo.getUrl());
        assertEquals(2, markupInfo.getImagesCount());
        DomDistillerProtos.MarkupImage markupImage = markupInfo.getImages(0);
        assertEquals(expectedUrl1, markupImage.getUrl());
        assertEquals(expectedSecureUrl1, markupImage.getSecureUrl());
        assertEquals(expectedType1, markupImage.getType());
        assertEquals(600, markupImage.getWidth());
        assertEquals(400, markupImage.getHeight());
        markupImage = markupInfo.getImages(1);
        assertEquals(expectedUrl2, markupImage.getUrl());
        assertEquals(expectedSecureUrl2, markupImage.getSecureUrl());
        assertEquals(expectedType2, markupImage.getType());
        assertEquals(1024, markupImage.getWidth());
        assertEquals(900, markupImage.getHeight());
    }

    public void testArticle() {
        // Create the required properties for OpenGraphProtocol except for "type".
        createDefaultOGTitle();
        createDefaultOGUrl();
        createDefaultOGImage();

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

        MarkupParser parser = new MarkupParser(mRoot);
        DomDistillerProtos.MarkupInfo markupInfo = parser.getMarkupInfo();
        assertEquals("Article", markupInfo.getType());
        DomDistillerProtos.MarkupArticle markupArticle = markupInfo.getArticle();
        assertEquals(expectedPublishedTime, markupArticle.getPublishedTime());
        assertEquals(expectedModifiedTime, markupArticle.getModifiedTime());
        assertEquals(expectedExpirationTime, markupArticle.getExpirationTime());
        assertEquals(expectedSection, markupArticle.getSection());
        assertEquals(2, markupArticle.getAuthorsCount());
        assertEquals(expectedAuthor1, markupArticle.getAuthors(0));
        assertEquals(expectedAuthor2, markupArticle.getAuthors(1));
    }

    private void createDefaultOGTitle() {
        createMeta("og:title", "dummy title");
    }

    private void createDefaultOGType() {
        createMeta("og:type", "website");
    }

    private void createDefaultOGUrl() {
        createMeta("og:url", "http://dummy/url.html");
    }

    private void createDefaultOGImage() {
        createMeta("og:image", "http://dummy/image.jpeg");
    }

    private void createMeta(String property, String content) {
        mHead.appendChild(TestUtil.createMetaProperty(property, content));
    }
}
