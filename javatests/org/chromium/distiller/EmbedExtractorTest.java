// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import com.google.gwt.dom.client.Style;
import org.chromium.distiller.webdocument.WebEmbed;
import org.chromium.distiller.webdocument.WebImage;
import org.chromium.distiller.extractors.embeds.EmbedExtractor;
import org.chromium.distiller.extractors.embeds.TwitterExtractor;
import org.chromium.distiller.extractors.embeds.VimeoExtractor;
import org.chromium.distiller.extractors.embeds.YouTubeExtractor;
import org.chromium.distiller.extractors.embeds.ImageExtractor;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.ImageElement;

public class EmbedExtractorTest extends DomDistillerJsTestCase {

    /**
     * 5x5 red dot image
     */
    final String IMAGE_BASE64 = "data:image/png;base64,iVBORw0KGgo" +
            "AAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/" +
            "w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";

    public void testYouTubeExtractor() {
        Element youtube = TestUtil.createIframe();
        youtube.setAttribute("src", "http://www.youtube.com/embed/M7lc1UVf-VE?autoplay=1");

        EmbedExtractor extractor = new YouTubeExtractor();
        WebEmbed result = (WebEmbed) extractor.extract(youtube);

        // Check YouTube specific attributes
        assertNotNull(result);
        assertEquals("youtube", result.getType());
        assertEquals("M7lc1UVf-VE", result.getId());
        assertEquals("1", result.getParams().get("autoplay"));

        // Begin negative test:
        Element notYoutube = TestUtil.createIframe();
        notYoutube.setAttribute("src", "http://www.notyoutube.com/embed/M7lc1UVf-VE?autoplay=1");

        result = (WebEmbed) extractor.extract(notYoutube);
        assertNull(result);
    }

    public void testYouTubeExtractorID() {
        Element youtube = TestUtil.createIframe();
        youtube.setAttribute("src", "http://www.youtube.com/embed/M7lc1UVf-VE///?autoplay=1");

        EmbedExtractor extractor = new YouTubeExtractor();
        WebEmbed result = (WebEmbed) extractor.extract(youtube);

        // Check YouTube specific attributes
        assertNotNull(result);
        assertEquals("youtube", result.getType());
        assertEquals("M7lc1UVf-VE", result.getId());

        // Begin negative test.
        Element badYoutube = TestUtil.createIframe();
        badYoutube.setAttribute("src", "http://www.youtube.com/embed");

        result = (WebEmbed) extractor.extract(badYoutube);

        assertNull(result);
    }

    public void testVimeoExtractor() {
        Element vimeo = TestUtil.createIframe();
        vimeo.setAttribute("src", "http://player.vimeo.com/video/12345?portrait=0");

        EmbedExtractor extractor = new VimeoExtractor();
        WebEmbed result = (WebEmbed) extractor.extract(vimeo);

        // Check Vimeo specific attributes
        assertNotNull(result);
        assertEquals("vimeo", result.getType());
        assertEquals("12345", result.getId());
        assertEquals("0", result.getParams().get("portrait"));

        // Begin negative test:
        Element wrongDomain = TestUtil.createIframe();
        wrongDomain.setAttribute("src", "http://vimeo.com/video/09876?portrait=1");

        result = (WebEmbed) extractor.extract(wrongDomain);
        assertNull(result);
    }

    public void testVimeoExtractorID() {
        Element vimeo = TestUtil.createIframe();
        vimeo.setAttribute("src", "http://player.vimeo.com/video/12345/?portrait=0");

        EmbedExtractor extractor = new VimeoExtractor();
        WebEmbed result = (WebEmbed) extractor.extract(vimeo);

        // Check Vimeo specific attributes
        assertNotNull(result);
        assertEquals("vimeo", result.getType());
        assertEquals("12345", result.getId());

        // Begin negative test.
        Element badVimeo = TestUtil.createIframe();
        badVimeo.setAttribute("src", "http://player.vimeo.com/video");

        result = (WebEmbed) extractor.extract(badVimeo);

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
        WebEmbed result = (WebEmbed) extractor.extract(tweetBlock);

        // Check twitter specific attributes - namely twitter id
        assertNotNull(result);
        assertEquals("twitter", result.getType());
        assertEquals("12345", result.getId());

        // Test trailing slash.
        tweetBlock = Document.get().createBlockQuoteElement();
        tweetBlock.setAttribute("class", "twitter-tweet");
        temp = Document.get().createPElement();
        temp.appendChild(TestUtil.createAnchor("http://twitter.com/foo", "extra content"));
        tweetBlock.appendChild(temp);
        tweetBlock.appendChild(
                TestUtil.createAnchor("http://twitter.com/foo/bar/12345///", "January 1, 1900"));

        result = (WebEmbed) extractor.extract(tweetBlock);

        // Check twitter specific attributes - namely twitter id
        assertNotNull(result);
        assertEquals("twitter", result.getType());
        assertEquals("12345", result.getId());

        // Begin negative test:
        Element nontweet = Document.get().createBlockQuoteElement();
        nontweet.setAttribute("class", "arbitrary-class");
        temp = Document.get().createPElement();
        temp.appendChild(TestUtil.createAnchor("http://nottwitter.com/foo", "extra content"));
        nontweet.appendChild(temp);
        nontweet.appendChild(TestUtil.createAnchor("http://nottwitter.com/12345", "timestamp"));

        result = (WebEmbed) extractor.extract(nontweet);
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
        WebEmbed result = (WebEmbed) extractor.extract(twitter);

        // Check twitter specific attributes - namely twitter id
        assertNotNull(result);
        assertEquals("twitter", result.getType());
        assertEquals("1234567890", result.getId());

        // Begin negative test:
        IFrameElement notTwitter = TestUtil.createIframe();
        mBody.appendChild(notTwitter);
        notTwitter.getContentDocument().getBody().setInnerHTML(
                iframeStructure.replaceAll("blockquote", "div"));

        result = (WebEmbed) extractor.extract(notTwitter);
        assertNull(result);

        // Test no important twitter content.
        notTwitter = TestUtil.createIframe();
        mBody.appendChild(notTwitter);
        notTwitter.getContentDocument().getBody().setInnerHTML(
                iframeStructure.replaceAll("data-tweet-id", "data-bad-id"));

        result = (WebEmbed) extractor.extract(notTwitter);
        assertNull(result);
    }

    public void testImageExtractorWithWidthHeightAttributes() {
        ImageElement image = TestUtil.createImage();
        image.setSrc(IMAGE_BASE64);
        image.setAttribute("width", "32");
        image.setAttribute("height", "32");
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(32, result.getWidth());
        assertEquals(32, result.getHeight());
    }

    public void testImageExtractorWithNoAttributes() {
        ImageElement image = TestUtil.createImage();
        mBody.appendChild(image);
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(0, result.getWidth());
        assertEquals(0, result.getHeight());
    }

    public void testImageExtractorWithSettingDimension() {
        ImageElement image = TestUtil.createImage();
        image.setSrc(IMAGE_BASE64);
        mBody.appendChild(image);
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(5, result.getWidth());
        assertEquals(5, result.getHeight());
    }

    public void testImageExtractorWithOneAttribute() {
        ImageElement image = TestUtil.createImage();
        image.setSrc(IMAGE_BASE64);
        image.setWidth(32);
        mBody.appendChild(image);
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(32, result.getWidth());
        assertEquals(32, result.getHeight());
    }

    public void testImageExtractorWithHeightCSS() {
        ImageElement image = TestUtil.createImage();
        image.setSrc(IMAGE_BASE64);
        image.getStyle().setHeight(100, Style.Unit.PX);
        mBody.appendChild(image);
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(100, result.getWidth());
        assertEquals(100, result.getHeight());
    }

    public void testImageExtractorWithWidthHeightCSSPX() {
        ImageElement image = TestUtil.createImage();
        image.setSrc(IMAGE_BASE64);
        image.getStyle().setHeight(100, Style.Unit.PX);
        image.getStyle().setWidth(48, Style.Unit.PX);
        mBody.appendChild(image);
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(48, result.getWidth());
        assertEquals(100, result.getHeight());
    }

    public void testImageExtractorWithWidthAttributeHeightCSS() {
        ImageElement image = TestUtil.createImage();
        image.setSrc(IMAGE_BASE64);
        image.getStyle().setHeight(100, Style.Unit.PX);
        image.setAttribute("width", "144");
        mBody.appendChild(image);
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(144, result.getWidth());
        assertEquals(100, result.getHeight());
    }

    public void testImageExtractorWithAttributesCSS() {
        ImageElement image = TestUtil.createImage();
        image.setSrc(IMAGE_BASE64);
        image.setAttribute("width", "32");
        image.setAttribute("height", "32");
        image.getStyle().setHeight(48, Style.Unit.PX);
        image.getStyle().setWidth(48, Style.Unit.PX);
        mBody.appendChild(image);
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(48, result.getWidth());
        assertEquals(48, result.getHeight());
    }

    public void testImageExtractorWithAttributesCSSHeightCMAndWidthAttrb() {
        ImageElement image = TestUtil.createImage();
        image.setSrc(IMAGE_BASE64);
        image.getStyle().setHeight(1, Style.Unit.CM);
        image.setWidth(50);
        mBody.appendChild(image);
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(38, result.getHeight());
        assertEquals(50, result.getWidth());
    }

    public void testImageExtractorWithAttributesCSSHeightCM() {
        ImageElement image = TestUtil.createImage();
        image.setSrc(IMAGE_BASE64);
        image.getStyle().setHeight(1, Style.Unit.CM);
        mBody.appendChild(image);
        EmbedExtractor extractor = new ImageExtractor();
        WebImage result = (WebImage) extractor.extract(image);
        assertNotNull(result);
        assertEquals(38, result.getHeight());
        assertEquals(38, result.getWidth());
    }
}
