// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

/**
 * This class loads the different parsers that are based on different markup specifications, and
 * allows retrieval of different distillation-related markup properties from a document.
 * It retrieves the requested properties from one or more parsers.  If necessary, it may merge the
 * information from multiple parsers.
 * Currently, three parsers are supported: OpenGraphProtocolParser, IEReadingViewParser and
 * SchemaOrgParser.  For now, OpenGraphProtocolParser takes precedence, because it uses specific
 * meta tags and hence extracts information the fastest; it also demands conformance to rules.  If
 * the rules are broken or the properties retrieved are null or empty, we try with
 * IEReadingViewParser, and so forth.
 * The properties that matter to distilled content are:
 * - individual properties: title, page type, page url, description, publisher, author, copyright
 * - dominant and inline images and their properties: url, secure_url, type, caption, width, height
 * - article and its properties: section name, published time, modified time, expiration time,
 *   authors.
 */
 // TODO(kuan): for some properties, e.g. dominant and inline images, we might want to retrieve from
 // multiple parsers; IEReadingViewParser provides more information as it scans all images in the
 // document.  If we do so, we would need to merge the multiple versions in a meaningful way.
 // TODO(kuan): I'll only start on SchemaOrgParser after IEReadingViewParser is done.
@Export()
public class MarkupParser implements Exportable {
    /**
     * The interface that all parsers must implement so that MarkupParser can retrieve their
     * properties.
     */
    public interface Parser {
        /**
         * Returns the markup title of the document.
         */
        public String getTitle();

        /**
         * Returns the markup type of the document.
         */
        public String getType();

        /**
         * Returns the markup url of the document.
         */
        public String getUrl();

        /**
         * Returns the properties of all markup images in the document.  The first image is the
         * domainant (i.e. top or salient) one.
         */
        public Image[] getImages();

        /**
         * Returns the markup description of the document.
         */
        public String getDescription();

        /**
         * Returns the markup publisher of the document.
         */
        public String getPublisher();

        /**
         * Returns the markup copyright of the document.
         */
        public String getCopyright();

        /**
         * Returns the full name of the markup author.
         */
        public String getAuthor();

        /**
         * Returns the properties of the markup "article" object.
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
    @Export()
    public static class Image implements Exportable {
        public String image = null;
        public String url = null;
        public String secureUrl = null;
        public String type = null;
        public String caption = null;
        public int width = 0;
        public int height = 0;

        public String getImage() { return image; }
        public String getUrl() { return url; }
        public String getSecureUrl() { return secureUrl; }
        public String getType() { return type; }
        public String getCaption() { return caption; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
    }

    /**
     * The object that contains the properties of an article document.
     */
    @Export()
    public static class Article implements Exportable {
        public String publishedTime = null;
        public String modifiedTime = null;
        public String expirationTime = null;
        public String section = null;
        public String[] authors = null;

        public String getPublishedTime() { return publishedTime; }
        public String getModifiedTime() { return modifiedTime; }
        public String getExpirationTime() { return expirationTime; }
        public String getSection() { return section; }
        public String[] getAuthors() { return authors; }
    }

    private final List<Parser> mParsers;

    /**
     * The object that loads the different parsers, and retrieves requested information from one or
     * more of the parsers.
     */
    public MarkupParser(Element root) {
        mParsers = new ArrayList<Parser>();

        Parser ogp = OpenGraphProtocolParser.parse(root);
        if (ogp != null) mParsers.add(ogp);
        Parser ie = new IEReadingViewParser(root);
        if (ie != null) mParsers.add(ie);
    }
    
    public String getTitle() {
        String title = null;
        for (int i = 0; i < mParsers.size(); i++) {
            title = mParsers.get(i).getTitle();
            if (title != null && !title.isEmpty()) break;
        }
        return title;
    }

    public String getType() {
        String type = null;
        for (int i = 0; i < mParsers.size(); i++) {
            type = mParsers.get(i).getType();
            if (type != null && !type.isEmpty()) break;
        }
        return type;
    }

    public String getUrl() {
        String url = null;
        for (int i = 0; i < mParsers.size(); i++) {
            url = mParsers.get(i).getUrl();
            if (url != null && !url.isEmpty()) break;
        }
        return url;
    }

    public Image[] getImages() {
        Image[] images = null;
        for (int i = 0; i < mParsers.size(); i++) {
            images = mParsers.get(i).getImages();
            if (images != null && images.length > 0) break;
        }
        return images;
    }

    public String getDescription() {
        String description = null;
        for (int i = 0; i < mParsers.size(); i++) {
            description = mParsers.get(i).getDescription();
            if (description != null && !description.isEmpty()) break;
        }
        return description;
    }

    public String getPublisher() {
        String publisher = null;
        for (int i = 0; i < mParsers.size(); i++) {
            publisher = mParsers.get(i).getPublisher();
            if (publisher != null && !publisher.isEmpty()) break;
        }
        return publisher;
    }

    public String getCopyright() {
        String copyright = null;
        for (int i = 0; i < mParsers.size(); i++) {
            copyright = mParsers.get(i).getCopyright();
            if (copyright != null && !copyright.isEmpty()) break;
        }
        return copyright;
    }

    public String getAuthor() {
        String author = null;
        for (int i = 0; i < mParsers.size(); i++) {
            author = mParsers.get(i).getAuthor();
            if (author != null && !author.isEmpty()) break;
        }
        return author;
    }

    public Article getArticle() {
        Article article = null;
        for (int i = 0; i < mParsers.size(); i++) {
            article = mParsers.get(i).getArticle();
            if (article != null) break;
        }
        return article;
    }
}
