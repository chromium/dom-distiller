// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.webdocument.WebEmbed;
import org.chromium.distiller.extractors.embeds.EmbedExtractor;
import org.chromium.distiller.extractors.embeds.TwitterExtractor;
import org.chromium.distiller.extractors.embeds.VimeoExtractor;
import org.chromium.distiller.extractors.embeds.YouTubeExtractor;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;

import java.util.List;

public class EmbedExtractorTest extends DomDistillerJsTestCase {

    public void testYouTubeExtractor() {
        Element youtube = TestUtil.createIframe();
        youtube.setAttribute("src", "http://www.youtube.com/embed/M7lc1UVf-VE?autoplay=1");

        EmbedExtractor extractor = new YouTubeExtractor();
        WebEmbed result = extractor.extract(youtube);

        // Check YouTube specific attributes
        assertNotNull(result);
        assertEquals("youtube", result.getType());
        assertEquals("M7lc1UVf-VE", result.getParams().get("videoid"));
        assertEquals("1", result.getParams().get("autoplay"));

        // Begin negative test:
        Element notYoutube = TestUtil.createIframe();
        notYoutube.setAttribute("src", "http://www.notyoutube.com/embed/M7lc1UVf-VE?autoplay=1");

        result = extractor.extract(notYoutube);
        assertNull(result);
    }

    public void testYouTubeExtractorID() {
        Element youtube = TestUtil.createIframe();
        youtube.setAttribute("src", "http://www.youtube.com/embed/M7lc1UVf-VE///?autoplay=1");

        EmbedExtractor extractor = new YouTubeExtractor();
        WebEmbed result = extractor.extract(youtube);

        // Check YouTube specific attributes
        assertNotNull(result);
        assertEquals("youtube", result.getType());
        assertEquals("M7lc1UVf-VE", result.getParams().get("videoid"));

        // Begin negative test.
        Element badYoutube = TestUtil.createIframe();
        badYoutube.setAttribute("src", "http://www.youtube.com/embed");

        result = extractor.extract(badYoutube);

        assertNull(result);
    }

    public void testVimeoExtractor() {
        Element vimeo = TestUtil.createIframe();
        vimeo.setAttribute("src", "http://player.vimeo.com/video/12345?portrait=0");

        EmbedExtractor extractor = new VimeoExtractor();
        WebEmbed result = extractor.extract(vimeo);

        // Check Vimeo specific attributes
        assertNotNull(result);
        assertEquals("vimeo", result.getType());
        assertEquals("12345", result.getParams().get("videoid"));
        assertEquals("0", result.getParams().get("portrait"));

        // Begin negative test:
        Element wrongDomain = TestUtil.createIframe();
        wrongDomain.setAttribute("src", "http://vimeo.com/video/09876?portrait=1");

        result = extractor.extract(wrongDomain);
        assertNull(result);
    }

    public void testVimeoExtractorID() {
        Element vimeo = TestUtil.createIframe();
        vimeo.setAttribute("src", "http://player.vimeo.com/video/12345/?portrait=0");

        EmbedExtractor extractor = new VimeoExtractor();
        WebEmbed result = extractor.extract(vimeo);

        // Check Vimeo specific attributes
        assertNotNull(result);
        assertEquals("vimeo", result.getType());
        assertEquals("12345", result.getParams().get("videoid"));

        // Begin negative test.
        Element badVimeo = TestUtil.createIframe();
        badVimeo.setAttribute("src", "http://player.vimeo.com/video");

        result = extractor.extract(badVimeo);

        assertNull(result);
    }

    public void testNotRenderedTwitterExtractor() {
        Element tweetBlock = Document.get().createBlockQuoteElement();
        tweetBlock.setAttribute("class", "twitter-tweet");
        Element temp = Document.get().createPElement();
        temp.appendChild(TestUtil.createAnchor("http://twitter.com/foo", "extra content"));
        tweetBlock.appendChild(temp);
        tweetBlock.appendChild(
                TestUtil.createAnchor("http://twitter.com/foo/bar/12345", "January 1, 1900"));

        EmbedExtractor extractor = new TwitterExtractor();
        WebEmbed result = extractor.extract(tweetBlock);

        // Check twitter specific attributes - namely twitter id
        assertNotNull(result);
        assertEquals("twitter", result.getType());
        assertEquals("12345", result.getParams().get("tweetid"));

        // Test trailing slash.
        tweetBlock = Document.get().createBlockQuoteElement();
        tweetBlock.setAttribute("class", "twitter-tweet");
        temp = Document.get().createPElement();
        temp.appendChild(TestUtil.createAnchor("http://twitter.com/foo", "extra content"));
        tweetBlock.appendChild(temp);
        tweetBlock.appendChild(
                TestUtil.createAnchor("http://twitter.com/foo/bar/12345///", "January 1, 1900"));

        result = extractor.extract(tweetBlock);

        // Check twitter specific attributes - namely twitter id
        assertNotNull(result);
        assertEquals("twitter", result.getType());
        assertEquals("12345", result.getParams().get("tweetid"));

        // Begin negative test:
        Element nontweet = Document.get().createBlockQuoteElement();
        nontweet.setAttribute("class", "arbitrary-class");
        temp = Document.get().createPElement();
        temp.appendChild(TestUtil.createAnchor("http://nottwitter.com/foo", "extra content"));
        nontweet.appendChild(temp);
        nontweet.appendChild(TestUtil.createAnchor("http://nottwitter.com/12345", "timestamp"));

        result = extractor.extract(nontweet);
        assertNull(result);
    }

    public void testRenderedTwitterExtractor() {
        IFrameElement twitter = TestUtil.createIframe();
        // Add iframe to body so its document is generated.
        mBody.appendChild(twitter);

        // This string represents a very simplified version of the twitter iframe embed structure.
        String iframeStructure = 
                "<div class=\"media-forward root standalone-tweet ltr\"" +
                    "data-iframe-title=\"Embedded Tweet\"" +
                    "data-scribe=\"page:tweet\">" +
                    "<blockquote data-tweet-id=\"1234567890\"" +
                        "data-scribe=\"section:subject\">" +
                        "<div class=\"cards-base cards-multimedia customisable-border\"" +
                            "data-scribe=\"component:card\"" +
                            "data-video-content-id=\"0987654321\">" +
                        "</div>" +
                        "<div class=\"header\">" +
                        "</div>" +
                        "<div class=\"content\" data-scribe=\"component:tweet\">" +
                        "</div>" +
                        "<div class=\"footer\" data-scribe=\"component:footer\">" +
                        "</div>" +
                    "</blockquote>" +
                "</div>";

        twitter.getContentDocument().getBody().setInnerHTML(iframeStructure);

        EmbedExtractor extractor = new TwitterExtractor();
        WebEmbed result = extractor.extract(twitter);

        // Check twitter specific attributes - namely twitter id
        assertNotNull(result);
        assertEquals("twitter", result.getType());
        assertEquals("1234567890", result.getParams().get("tweetid"));

        // Begin negative test:
        IFrameElement notTwitter = TestUtil.createIframe();
        mBody.appendChild(notTwitter);
        notTwitter.getContentDocument().getBody().setInnerHTML(
                iframeStructure.replaceAll("blockquote", "div"));

        result = extractor.extract(notTwitter);
        assertNull(result);

        // Test no important twitter content.
        notTwitter = TestUtil.createIframe();
        mBody.appendChild(notTwitter);
        notTwitter.getContentDocument().getBody().setInnerHTML(
                iframeStructure.replaceAll("data-tweet-id", "data-bad-id"));

        result = extractor.extract(notTwitter);
        assertNull(result);
    }
}
