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
@Export()
public class MarkupParser implements Exportable {
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
         * The first image is the domainant (i.e. top or salient) one.
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
    @Export()
    public static class Image implements Exportable {
        public String url = "";
        public String secureUrl = "";
        public String type = "";
        public String caption = "";
        public int width = 0;
        public int height = 0;

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
        public String publishedTime = "";
        public String modifiedTime = "";
        public String expirationTime = "";
        public String section = "";
        // |authors| should be an empty array (not null) if there's no author.
        public String[] authors = null;

        public String getPublishedTime() { return publishedTime; }
        public String getModifiedTime() { return modifiedTime; }
        public String getExpirationTime() { return expirationTime; }
        public String getSection() { return section; }
        public String[] getAuthors() { return authors; }
    }

    private final List<Accessor> mAccessors;

    /**
     * The object that loads the different parsers, and retrieves requested information from one or
     * more of the parsers.
     */
    public MarkupParser(Element root) {
        mAccessors = new ArrayList<Accessor>();

        Accessor ogp = OpenGraphProtocolParser.parse(root);
        if (ogp != null) mAccessors.add(ogp);
        mAccessors.add(new SchemaOrgParserAccessor(root));
        mAccessors.add(new IEReadingViewParser(root));
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
}
