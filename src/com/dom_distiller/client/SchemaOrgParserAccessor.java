// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.dom_distiller.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;

/**
 * This class instantiates SchemaOrgParser and implements MarkupParser.Parser interface to provide
 * access to properties that SchemaOrgParser has parsed.
 */
public class SchemaOrgParserAccessor implements MarkupParser.Parser {
    private final SchemaOrgParser parser;

    /**
     * The object that instantiates SchemaOrgParser and implements its MarkupParser.Parser
     * interface.
     */
    public SchemaOrgParserAccessor(Element root) {
        parser = new SchemaOrgParser(root);
    }

    @Override
    public String getTitle() {
        String title = "";
        List<SchemaOrgParser.ArticleItem> articles = parser.getArticleItems();

        // Get the "headline" property of the first article that has it.
        for (int i = 0; i < articles.size() && title.isEmpty(); i++) {
            title = articles.get(i).getStringProperty(SchemaOrgParser.HEADLINE_PROP);
        }

        // If there's no "headline" property, use "name" property.
        for (int i = 0; i < articles.size() && title.isEmpty(); i++) {
            title = articles.get(i).getStringProperty(SchemaOrgParser.NAME_PROP);
        }

        return title;
    }

    @Override
    public String getType() {
        // TODO(kuan): consolidate/standardize types returned from all 3 parsers in MarkupParser.
        // Returns ARTICLe if there's an article.
        return parser.getArticleItems().isEmpty() ? "" : SchemaOrgParser.Type.ARTICLE.toString();
    }

    @Override
    public String getUrl() {
        List<SchemaOrgParser.ArticleItem> articles = parser.getArticleItems();
        return articles.isEmpty() ? "" :
                articles.get(0).getStringProperty(SchemaOrgParser.URL_PROP);
    }

    @Override
    public MarkupParser.Image[] getImages() {
        List<MarkupParser.Image> images = new ArrayList<MarkupParser.Image>();

        // First, get images from ArticleItem's.
        List<SchemaOrgParser.ArticleItem> articleItems = parser.getArticleItems();
        MarkupParser.Image imageOfArticle = null;
        SchemaOrgParser.ImageItem associatedImageOfArticle = null;

        for (int i = 0; i < articleItems.size(); i++) {
            SchemaOrgParser.ArticleItem articleItem = articleItems.get(i);
            // If article has an associated image or the "image" property, remember them for now;
            // they'll be added to the list later when the position in the list can be determined.
            if (associatedImageOfArticle == null) {
                associatedImageOfArticle = articleItem.getRepresentativeImageItem();
                if (associatedImageOfArticle != null) continue;
            }
            MarkupParser.Image image = articleItem.getImage();
            if (image == null) continue;
            if (imageOfArticle == null) {
                imageOfArticle = image;
            } else {
                images.add(image);
            }
        }

        // Then, get images from ImageItem's.
        List<SchemaOrgParser.ImageItem> imageItems = parser.getImageItems();
        boolean hasRepresentativeImage = false;

        for (int i = 0; i < imageItems.size(); i++) {
            SchemaOrgParser.ImageItem imageItem = imageItems.get(i);
            MarkupParser.Image image = imageItem.getImage();
            // Insert |image| at beginning of list if it's the first image that's representative of
            // page or it's the associated image of the first article.
            if (!hasRepresentativeImage && (imageItem == associatedImageOfArticle ||
                                            imageItem.isRepresentativeOfPage())) {
                hasRepresentativeImage = true;
                images.add(0, image);
            } else {
                images.add(image);
            }
        }

        // Prepend |imageOfArticle| to list if there's no image representative of page; append it
        // otherwise.
        if (imageOfArticle != null) {
            if (!hasRepresentativeImage) images.add(0, imageOfArticle);
            else images.add(imageOfArticle);
        }

        if (images.isEmpty()) return null;

        return images.toArray(new MarkupParser.Image[images.size()]);
    }

    @Override
    public String getDescription() {
        List<SchemaOrgParser.ArticleItem> articles = parser.getArticleItems();
        return articles.isEmpty() ?  "" :
                articles.get(0).getStringProperty(SchemaOrgParser.DESCRIPTION_PROP);
    }

    @Override
    public String getPublisher() {
        // Returns either the "publisher" or "copyrightHolder" property of the first article.
        String publisher = "";
        List<SchemaOrgParser.ArticleItem> articles = parser.getArticleItems();
        if (!articles.isEmpty()) {
            SchemaOrgParser.ArticleItem article = articles.get(0);
            publisher = article.getPersonOrOrganizationName(SchemaOrgParser.PUBLISHER_PROP);
            if (publisher.isEmpty()) {
                publisher = article.getPersonOrOrganizationName(
                        SchemaOrgParser.COPYRIGHT_HOLDER_PROP);
            }
        }
        return publisher;
    }

    @Override
    public String getCopyright() {
        List<SchemaOrgParser.ArticleItem> articles = parser.getArticleItems();
        return articles.isEmpty() ? "" : articles.get(0).getCopyright();
    }

    @Override
    public String getAuthor() {
        String author = "";
        List<SchemaOrgParser.ArticleItem> articles = parser.getArticleItems();
        if (!articles.isEmpty()) {
             SchemaOrgParser.ArticleItem article = articles.get(0);
             author = article.getPersonOrOrganizationName(SchemaOrgParser.AUTHOR_PROP);
             // If there's no "author" property, use "creator" property.
             if (author.isEmpty()) {
                 author = article.getPersonOrOrganizationName(SchemaOrgParser.CREATOR_PROP);
             }
        }
        // Otherwise, use "rel=author" tag.
        return author.isEmpty() ? parser.getAuthorFromRel() : author;
    }

    @Override
    public MarkupParser.Article getArticle() {
        List<SchemaOrgParser.ArticleItem> articles = parser.getArticleItems();
        return articles.isEmpty() ? null : articles.get(0).getArticle();
    }

    @Override
    public boolean optOut() {
        return false;
    }
}
