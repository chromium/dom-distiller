// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.distiller;

import org.chromium.distiller.proto.DomDistillerProtos;
import org.chromium.distiller.proto.DomDistillerProtos.TimingInfo;

import com.google.gwt.dom.client.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * This class loads the different parsers that are based on different markup specifications, and
 * allows retrieval of different distillation-related markup properties from a document.
 * It retrieves the requested properties from one or more parsers.  If necessary, it may merge the
 * information from multiple parsers.
 * Currently, three parsers are supported: OpenGraphProtocolParser, IEReadingViewParser and
 * SchemaOrgParser.  For now, OpenGraphProtocolParser takes precedence, because it uses specific
 * meta tags and hence extracts information the fastest; it also demands conformance to rules.  If
 * the rules are broken or the properties retrieved are null or empty, we try with SchemaOrgParser,
 * then IEReadingViewParser.
 * The properties that matter to distilled content are:
 * - individual properties: title, page type, page url, description, publisher, author, copyright
 * - dominant and inline images and their properties: url, secure_url, type, caption, width, height
 * - article and its properties: section name, published time, modified time, expiration time,
 *   authors.
 */
 // TODO(kuan): for some properties, e.g. dominant and inline images, we might want to retrieve from
 // multiple parsers; IEReadingViewParser provides more information as it scans all images in the
 // document.  If we do so, we would need to merge the multiple versions in a meaningful way.
public class MarkupParser {
    public static final String ARTICLE_TYPE = "Article";

    /**
     * The interface that all parsers must implement so that MarkupParser can retrieve their
     * properties.
     */
    public interface Accessor {
        /**
         * Returns the markup title of the document, empty string if none.
         */
        public String getTitle();

        /**
         * Returns the markup type of the document, empty string if none.
         */
        public String getType();

        /**
         * Returns the markup url of the document, empty string if none.
         */
        public String getUrl();

        /**
         * Returns the properties of all markup images in the document, empty array if none.
         * The first image is the dominant (i.e. top or salient) one.
         */
        public Image[] getImages();

        /**
         * Returns the markup description of the document, empty string if none.
         */
        public String getDescription();

        /**
         * Returns the markup publisher of the document, empty string if none.
         */
        public String getPublisher();

        /**
         * Returns the markup copyright of the document, empty string if none.
         */
        public String getCopyright();

        /**
         * Returns the full name of the markup author, empty string if none.
         */
        public String getAuthor();

        /**
         * Returns the properties of the markup "article" object, null if none.
         */
        public Article getArticle();

        /**
         * Returns true if page owner has opted out of distillation.
         */
        public boolean optOut();
    }

    /**
     * The object that contains the properties of an image in the document.
     */
    public static class Image {
        public String url = "";
        public String secureUrl = "";
        public String type = "";
        public String caption = "";
        public int width = 0;
        public int height = 0;
    }

    /**
     * The object that contains the properties of an article document.
     */
    public static class Article {
        public String publishedTime = "";
        public String modifiedTime = "";
        public String expirationTime = "";
        public String section = "";
        // |authors| should be an empty array (not null) if there's no author.
        public String[] authors = null;
    }

    private final List<Accessor> mAccessors;

    private final TimingInfo mTimingInfo;

    /**
     * The object that loads the different parsers, and retrieves requested information from one or
     * more of the parsers.
     */
    public MarkupParser(Element root) {
        this(root, (TimingInfo) null);
    }

    public MarkupParser(Element root, TimingInfo timingInfo) {
        mTimingInfo = timingInfo;
        mAccessors = new ArrayList<Accessor>();
        double startTime = DomUtil.getTime();
        mAccessors.add(new OpenGraphProtocolParserAccessor(root, mTimingInfo));
        LogUtil.addTimingInfo(startTime, mTimingInfo, "OpenGraphProtocolParser");

        startTime = DomUtil.getTime();
        mAccessors.add(new SchemaOrgParserAccessor(root, mTimingInfo));
        LogUtil.addTimingInfo(startTime, mTimingInfo, "SchemaOrgParserAccessor");

        startTime = DomUtil.getTime();
        // TODO(wychen): Use eager evaluation in IEReadingViewParser, but only for profiling.
        mAccessors.add(new IEReadingViewParser(root));
        LogUtil.addTimingInfo(startTime, mTimingInfo, "IEReadingViewParser");
    }

    public String getTitle() {
        String title = "";
        for (int i = 0; i < mAccessors.size() && title.isEmpty(); i++) {
            title = mAccessors.get(i).getTitle();
        }
        return title;
    }

    public String getType() {
        String type = "";
        for (int i = 0; i < mAccessors.size() && type.isEmpty(); i++) {
            type = mAccessors.get(i).getType();
        }
        return type;
    }

    public String getUrl() {
        String url = "";
        for (int i = 0; i < mAccessors.size() && url.isEmpty(); i++) {
            url = mAccessors.get(i).getUrl();
        }
        return url;
    }

    public Image[] getImages() {
        Image[] images = null;
        for (int i = 0; i < mAccessors.size(); i++) {
            images = mAccessors.get(i).getImages();
            if (images.length > 0) break;
        }
        return images;
    }

    public String getDescription() {
        String description = "";
        for (int i = 0; i < mAccessors.size() && description.isEmpty(); i++) {
            description = mAccessors.get(i).getDescription();
        }
        return description;
    }

    public String getPublisher() {
        String publisher = "";
        for (int i = 0; i < mAccessors.size() && publisher.isEmpty(); i++) {
            publisher = mAccessors.get(i).getPublisher();
        }
        return publisher;
    }

    public String getCopyright() {
        String copyright = "";
        for (int i = 0; i < mAccessors.size() && copyright.isEmpty(); i++) {
            copyright = mAccessors.get(i).getCopyright();
        }
        return copyright;
    }

    public String getAuthor() {
        String author = "";
        for (int i = 0; i < mAccessors.size() && author.isEmpty(); i++) {
            author = mAccessors.get(i).getAuthor();
        }
        return author;
    }

    public Article getArticle() {
        Article article = null;
        for (int i = 0; i < mAccessors.size() && article == null; i++) {
            article = mAccessors.get(i).getArticle();
        }
        return article;
    }

    public boolean optOut() {
        boolean optOut = false;
        for (int i = 0; i < mAccessors.size() && !optOut; i++) {
            optOut = mAccessors.get(i).optOut();
        }
        return optOut;
    }

    public DomDistillerProtos.MarkupInfo getMarkupInfo() {
        DomDistillerProtos.MarkupInfo info = DomDistillerProtos.MarkupInfo.create();
        if (optOut()) return info;

        info.setTitle(getTitle());
        info.setType(getType());
        info.setUrl(getUrl());
        info.setDescription(getDescription());
        info.setPublisher(getPublisher());
        info.setCopyright(getCopyright());
        info.setAuthor(getAuthor());

        Article article = getArticle();
        if (article != null) {
            DomDistillerProtos.MarkupArticle articleInfo =
                    DomDistillerProtos.MarkupArticle.create();
            articleInfo.setPublishedTime(article.publishedTime);
            articleInfo.setModifiedTime(article.modifiedTime);
            articleInfo.setExpirationTime(article.expirationTime);
            articleInfo.setSection(article.section);
            for (int i = 0; i < article.authors.length; i++) {
                articleInfo.addAuthors(article.authors[i]);
            }
            info.setArticle(articleInfo);
        }

        Image[] images = getImages();
        for (int i = 0; i < images.length; i++) {
            Image image = images[i];
            DomDistillerProtos.MarkupImage imageInfo = info.addImages();
            imageInfo.setUrl(image.url);
            imageInfo.setSecureUrl(image.secureUrl);
            imageInfo.setType(image.type);
            imageInfo.setCaption(image.caption);
            imageInfo.setWidth(image.width);
            imageInfo.setHeight(image.height);
        }

        return info;
    }
}
